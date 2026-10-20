"""Runs script test cases against the original engine DLLs.

Every case is a CNV fragment plus a list of script lines. Each line is run by
the real interpreter of every engine found in the DLL repository (through
tools/pikhost), and the results are written to a manifest that the emulator's
conformance test compares against.

    python tools/crosscheck/crosscheck.py engines
    python tools/crosscheck/crosscheck.py run                 # all cases, all engines -> manifest
    python tools/crosscheck/crosscheck.py run -k integer -e piklib8 --no-write
    python tools/crosscheck/crosscheck.py try cases/x.cnv "A^ADD(3)" -r A
    python tools/crosscheck/crosscheck.py verify              # manifest up to date with cases?

Needs Python 3.11+ (tomllib). Engines are 32-bit, the runner does not care:
it only talks to pikhost.exe over pipes.
"""

from __future__ import annotations

import argparse
import csv
import fnmatch
import hashlib
import json
import os
import subprocess
import sys
import tempfile
import tomllib
from dataclasses import dataclass, field
from datetime import datetime, timezone
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
CASES_DIR = HERE / "cases"
MANIFEST = HERE / "engine-results.json"
PIKHOST = ROOT / "tools" / "pikhost" / "build" / "pikhost.exe"
FORMAT = 1
ENGINE_ENCODING = "cp1250"
CASE_TIMEOUT = 20.0

# Engine DLL file name -> (family, EngineVariant on the emulator side).
FAMILIES = {
    "PIKLIB61.DLL": ("piklib61", "PIKLIB_6_1"),
    "PIKLIB71.DLL": ("piklib71", "PIKLIB_7_1"),
    "PIKLIB72.DLL": ("piklib72", "PIKLIB_7_2"),
    "PIKLIB8.DLL": ("piklib8", "PIKLIB_8"),
    "BLOOMOODLL.DLL": ("bloomoo", "BLOOMOO"),
    "BLOOMOOD.DLL": ("bloomoo", "BLOOMOO"),
    "BLOOMOOWEB.DLL": ("bloomoo-web", "BLOOMOO"),
    "BLOOMOOW.DLL": ("bloomoo-web", "BLOOMOO"),
}
# pikhost cannot bootstrap the CMC_Scene based engines yet (see tools/pikhost/README.md).
DEFAULT_FAMILIES = ("piklib72", "piklib8", "bloomoo", "bloomoo-web")


# ------------------------------------------------------------------ engines

@dataclass
class Engine:
    id: str
    family: str
    variant: str
    file: str
    sha1: str
    path: Path
    games: list[str] = field(default_factory=list)

    unsupported: str | None = None

    def describe(self) -> dict:
        info = {"family": self.family, "variant": self.variant, "file": self.file,
                "sha1": self.sha1, "games": sorted(self.games)}
        if self.unsupported:
            info["unsupported"] = self.unsupported
        return info


def discover_engines(repository: Path) -> list[Engine]:
    """Reads the repository manifest.csv and returns one Engine per distinct binary."""
    listing = repository / "manifest.csv"
    if not listing.is_file():
        raise SystemExit(f"no manifest.csv in {repository}")
    engines: dict[str, Engine] = {}
    with listing.open(encoding="utf-8-sig", newline="") as stream:
        for row in csv.DictReader(stream):
            original = row["original_name"].upper()
            if original not in FAMILIES:
                continue
            family, variant = FAMILIES[original]
            sha1 = row["sha1"].lower()
            # copied_to points at the machine the repository was built on; only the name counts.
            path = repository / Path(row["copied_to"].replace("\\", "/")).name
            game = Path(row["container_path"].replace("\\", "/")).stem
            engine = engines.get(sha1)
            if engine is None:
                if not path.is_file():
                    continue
                engine = Engine(f"{family}-{sha1[:12]}", family, variant, row["original_name"], sha1, path)
                engines[sha1] = engine
            if game not in engine.games:
                engine.games.append(game)
    return sorted(engines.values(), key=lambda e: (e.family, e.id))


def default_repository() -> Path:
    value = os.environ.get("CROSSCHECK_DLL_REPOSITORY")
    if not value:
        raise SystemExit("set CROSSCHECK_DLL_REPOSITORY or pass --repo")
    return Path(value)


# ------------------------------------------------------------------ cases

@dataclass
class Case:
    id: str
    description: str
    tags: list[str]
    cnv: str
    steps: list[str]
    read: list[str]
    source: str

    def digest(self) -> str:
        payload = json.dumps([self.cnv, self.steps, self.read], ensure_ascii=False)
        return hashlib.sha1(payload.encode("utf-8")).hexdigest()

    def describe(self) -> dict:
        return {"id": self.id, "description": self.description, "tags": self.tags,
                "source": self.source, "digest": self.digest(),
                "cnv": self.cnv, "steps": self.steps, "read": self.read}


def load_cases(directory: Path = CASES_DIR) -> list[Case]:
    cases: list[Case] = []
    seen: set[str] = set()
    for path in sorted(directory.glob("*.toml")):
        with path.open("rb") as stream:
            document = tomllib.load(stream)
        defaults = document.get("defaults", {})
        for entry in document.get("case", []):
            case = Case(
                id=entry["id"],
                description=entry.get("description", ""),
                tags=entry.get("tags", defaults.get("tags", [])),
                cnv=entry.get("cnv", defaults.get("cnv", "")).strip("\n") + "\n",
                steps=entry["steps"],
                read=entry.get("read", defaults.get("read", [])),
                source=path.name,
            )
            if case.id in seen:
                raise SystemExit(f"{path.name}: duplicate case id {case.id}")
            for step in case.steps:
                if "\n" in step:
                    raise SystemExit(f"{case.id}: a step is one script line; use a BEHAVIOUR for blocks")
            seen.add(case.id)
            cases.append(case)
    return cases


def select(items, patterns, key):
    if not patterns:
        return list(items)
    return [item for item in items if any(fnmatch.fnmatchcase(key(item), p) or p in key(item) for p in patterns)]


# ------------------------------------------------------------------ pikhost

def unescape(text: str) -> str:
    out, i = [], 0
    while i < len(text):
        c = text[i]
        if c == "\\" and i + 1 < len(text):
            out.append({"n": "\n", "r": "\r", "t": "\t"}.get(text[i + 1], text[i + 1]))
            i += 2
        else:
            out.append(c)
            i += 1
    return "".join(out)


def parse_value(payload: str):
    """'TYPE\\tVALUE' -> [type, value]; '-' -> None."""
    if payload == "-":
        return None
    kind, _, value = payload.partition("\t")
    return [unescape(kind), unescape(value)]


def run_case(engine: Engine, case: Case, workdir: Path) -> dict:
    """Runs one case in a fresh pikhost process; a crash only loses this case."""
    if not PIKHOST.is_file():
        raise SystemExit(f"{PIKHOST} not found - run tools/pikhost/build.ps1 first")
    # The CNV loader takes an ANSI path, so the file lives in an ASCII temp dir.
    cnv_path = workdir / f"{hashlib.sha1(case.id.encode()).hexdigest()[:16]}.cnv"
    cnv_path.write_bytes(case.cnv.encode(ENGINE_ENCODING))

    commands = [f"LOAD {cnv_path}"]
    commands += [f"GET {name}" for name in case.read]
    for step in case.steps:
        commands.append(f"RUN {step}")
        commands += [f"GET {name}" for name in case.read]
    commands.append("QUIT")
    payload = ("\n".join(commands) + "\n").encode(ENGINE_ENCODING)

    try:
        completed = subprocess.run([str(PIKHOST), str(engine.path)], input=payload,
                                   capture_output=True, timeout=CASE_TIMEOUT)
        lines = completed.stdout.decode(ENGINE_ENCODING, errors="replace").splitlines()
        timed_out = False
    except subprocess.TimeoutExpired as error:
        lines = (error.stdout or b"").decode(ENGINE_ENCODING, errors="replace").splitlines()
        timed_out = True
    return interpret(case, lines, timed_out)


def interpret(case: Case, lines: list[str], timed_out: bool) -> dict:
    """Folds pikhost replies back onto the case structure."""
    result: dict = {"status": "ok", "initial": {}, "steps": []}
    messages: list[str] = []
    replies = iter(lines)

    def next_reply(expected: str):
        for line in replies:
            if line.startswith("MSG "):
                messages.append(unescape(line[4:]))
                continue
            if line.startswith("CRASH "):
                fields = line.split()
                crash = {"code": fields[1], "rva": fields[2]}
                if len(fields) > 3:
                    crash["stack"] = fields[3].split(",")
                result.update(status="crash", crash=crash)
                return None
            if line.startswith("ERR "):
                result.update(status="error", error=line[4:])
                return None
            if line.startswith(expected):
                return line[len(expected):]
            result.update(status="error", error=f"unexpected reply {line!r}")
            return None
        if result["status"] == "ok":
            result.update(status="timeout" if timed_out else "error",
                          error=None if timed_out else "pikhost ended early")
        return None

    def read_state(target: dict) -> bool:
        for name in case.read:
            reply = next_reply("VAL ")
            if reply is None:
                return False
            target[name] = parse_value(reply)
        return True

    def done():
        if messages:
            result["messages"] = messages
        return result

    if next_reply("READY ") is None:
        result["stage"] = "bootstrap"
        return done()
    if next_reply("OK") is None:
        result["stage"] = "load"
        return done()
    if not read_state(result["initial"]):
        return done()
    for index, _ in enumerate(case.steps):
        step: dict = {"result": None, "state": {}}
        reply = next_reply("RES ")
        if reply is None:
            result["stage"] = f"step {index}"
            return done()
        step["result"] = parse_value(reply)
        result["steps"].append(step)
        if not read_state(step["state"]):
            return done()
    return done()


# ------------------------------------------------------------------ commands

PROBE = Case("probe", "", [], "OBJECT=A\nA:TYPE=INTEGER\nA:VALUE=1\n", ["A^ADD(1)"], ["A"], "")


def probe(engine: Engine, workdir: Path) -> str | None:
    """Why pikhost cannot drive this engine, or None when it can."""
    r = run_case(engine, PROBE, workdir)
    if r["status"] == "ok" and r["steps"] and r["steps"][0]["state"].get("A") == ["INTEGER", "2"]:
        return None
    where = r.get("stage", "step")
    detail = r.get("crash", {}).get("rva") or r.get("error") or r["status"]
    return f"{where}: {r['status']} ({detail})"


def run_matrix(engines: list[Engine], cases: list[Case], progress=True) -> dict:
    results: dict[str, dict] = {case.id: {} for case in cases}
    with tempfile.TemporaryDirectory(prefix="crosscheck-") as temp:
        workdir = Path(temp)
        for engine in engines:
            engine.unsupported = probe(engine, workdir)
            if progress:
                state = f"UNSUPPORTED - {engine.unsupported}" if engine.unsupported else f"{len(engine.games)} games"
                print(f"  {engine.id} ({state})", file=sys.stderr)
            if engine.unsupported:
                continue
            for case in cases:
                results[case.id][engine.id] = run_case(engine, case, workdir)
    return results


def format_value(value) -> str:
    return "-" if value is None else f"{value[1]} ({value[0]})"


def print_table(engines: list[Engine], cases: list[Case], results: dict) -> None:
    for case in cases:
        print(f"\n{case.id}  [{case.source}]")
        rows: dict[str, list[str]] = {}
        for engine in engines:
            if engine.id not in results[case.id]:
                continue
            r = results[case.id][engine.id]
            cells = []
            for index, step in enumerate(case.steps):
                if index < len(r["steps"]):
                    s = r["steps"][index]
                    state = ", ".join(f"{k}={format_value(v)}" for k, v in s["state"].items())
                    cells.append(f"{step} -> {format_value(s['result'])}" + (f" | {state}" if state else ""))
                else:
                    cells.append(f"{step} -> {r['status'].upper()}" +
                                 (f" @ rva {r['crash']['rva']}" if "crash" in r else ""))
                    break
            if r["status"] not in ("ok", "crash") and not r["steps"]:
                cells.append(f"{r['status'].upper()} ({r.get('stage', '')}: {r.get('error', '')})")
            for message in r.get("messages", []):
                cells.append(f"MSG {message}")
            rows.setdefault("\n".join(cells), []).append(engine.id)
        # Engines that agree are printed once, so version differences stand out.
        for text, ids in rows.items():
            print(f"  {', '.join(ids)}:")
            for line in text.splitlines():
                print(f"      {line}")


def command_engines(args) -> None:
    for engine in discover_engines(args.repo):
        marker = "" if engine.family in DEFAULT_FAMILIES else "  (not bootstrapped by default)"
        print(f"{engine.id:28} {engine.variant:11} {engine.file:15} {', '.join(sorted(engine.games))}{marker}")


def chosen_engines(args) -> list[Engine]:
    engines = discover_engines(args.repo)
    families = args.family or (None if args.engine else list(DEFAULT_FAMILIES))
    if families:
        engines = [e for e in engines if e.family in families]
    return select(engines, args.engine, key=lambda e: e.id)


def grouped(per_engine: dict[str, dict]) -> list[dict]:
    """Engines that recorded exactly the same thing share one entry, so a
    regenerated manifest diffs as "this build changed", not as 20 equal edits."""
    groups: dict[str, dict] = {}
    for engine_id, result in per_engine.items():
        key = json.dumps(result, sort_keys=True)
        groups.setdefault(key, {"engines": [], **result})["engines"].append(engine_id)
    return list(groups.values())


def command_run(args) -> None:
    cases = select(load_cases(), args.case, key=lambda c: c.id)
    engines = chosen_engines(args)
    if not cases or not engines:
        raise SystemExit("nothing to run")
    print(f"running {len(cases)} cases on {len(engines)} engines", file=sys.stderr)
    results = run_matrix(engines, cases)
    if args.table or args.no_write:
        print_table(engines, cases, results)
    if args.no_write:
        return
    if args.case or args.engine or args.family:
        raise SystemExit("refusing to write a partial manifest; add --no-write")
    manifest = {
        "format": FORMAT,
        "generated": datetime.now(timezone.utc).isoformat(timespec="seconds"),
        "generator": "tools/crosscheck/crosscheck.py",
        "engines": {e.id: e.describe() for e in engines},
        "cases": [dict(case.describe(), results=grouped(results[case.id])) for case in cases],
    }
    MANIFEST.write_text(json.dumps(manifest, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"wrote {MANIFEST.relative_to(ROOT)}", file=sys.stderr)


def command_try(args) -> None:
    """Ad-hoc hypothesis check: one CNV file, a few lines, every engine."""
    cnv = Path(args.cnv).read_text(encoding=args.encoding)
    case = Case("try", "", [], cnv, args.steps, args.read, args.cnv)
    engines = chosen_engines(args)
    print_table(engines, [case], run_matrix(engines, [case], progress=False))


def command_verify(args) -> None:
    """Fails when cases changed since the manifest was generated."""
    if not MANIFEST.is_file():
        raise SystemExit(f"{MANIFEST.relative_to(ROOT)} missing - run `crosscheck.py run`")
    recorded = {c["id"]: c["digest"] for c in json.loads(MANIFEST.read_text(encoding="utf-8"))["cases"]}
    current = {c.id: c.digest() for c in load_cases()}
    stale = sorted(i for i in current if recorded.get(i) != current[i])
    removed = sorted(set(recorded) - set(current))
    for case_id in stale:
        print(f"stale or missing: {case_id}")
    for case_id in removed:
        print(f"no longer defined: {case_id}")
    if stale or removed:
        raise SystemExit("engine-results.json is out of date - regenerate it with `crosscheck.py run`")
    print(f"manifest up to date ({len(current)} cases)")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)

    def engine_options(p):
        p.add_argument("--repo", type=Path, default=None, help="DLL repository (default: $CROSSCHECK_DLL_REPOSITORY)")
        p.add_argument("-e", "--engine", action="append", help="engine id pattern (repeatable)")
        p.add_argument("-f", "--family", action="append", help="engine family, e.g. piklib8 (repeatable)")

    p = sub.add_parser("engines", help="list engines found in the repository")
    p.add_argument("--repo", type=Path, default=None)
    p.set_defaults(handler=command_engines)

    p = sub.add_parser("run", help="run cases and write engine-results.json")
    engine_options(p)
    p.add_argument("-k", "--case", action="append", help="case id pattern (repeatable)")
    p.add_argument("--table", action="store_true", help="also print the results")
    p.add_argument("--no-write", action="store_true", help="print only, keep the manifest")
    p.set_defaults(handler=command_run)

    p = sub.add_parser("try", help="run lines against a CNV file on every engine")
    engine_options(p)
    p.add_argument("cnv")
    p.add_argument("steps", nargs="+")
    p.add_argument("-r", "--read", action="append", default=[], help="object to read after each line")
    p.add_argument("--encoding", default=ENGINE_ENCODING)
    p.set_defaults(handler=command_try)

    p = sub.add_parser("verify", help="check that the manifest matches the cases")
    p.set_defaults(handler=command_verify)

    args = parser.parse_args()
    if hasattr(args, "repo") and args.repo is None and args.command != "verify":
        args.repo = default_repository()
    args.handler(args)


if __name__ == "__main__":
    main()
