#!/usr/bin/env python3
"""Interactive token inspector for the CNV lexer.

Run it from the repository root:

    python tools/pygments-cnv/lexer_lab.py

The server listens on 127.0.0.1 only and reloads the lexer module on every
request, so saving `lexer.py` is enough to refresh the token stream.
"""

from __future__ import annotations

import argparse
import importlib
import json
import sys
import traceback
import webbrowser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import parse_qs, urlparse

TOOL_DIR = Path(__file__).resolve().parent
REPO_ROOT = TOOL_DIR.parent.parent
SRC_DIR = TOOL_DIR / "src"

# The working copy takes precedence over a package installed in .venv-docs.
sys.path.insert(0, str(SRC_DIR))

from pygments.token import Error, Other, Text, Whitespace  # noqa: E402

SAMPLE_DIRS = ("assets/test-assets/scripts",)
SAMPLE_SUFFIXES = (".cnv", ".def", ".class", ".seq")


def build_lexer():
    """Import the lexer from scratch so edits show up without a restart."""
    import rex_cnv_pygments.lexer as module

    importlib.reload(module)
    return module.CnvLexer()


def short_name(token_type) -> str:
    return str(token_type).removeprefix("Token.") or "Token"


def is_suspicious(token_type, value: str) -> bool:
    """The `.` fallback hides gaps in the lexer, so flag what it swallowed."""
    if token_type in (Error, Other):
        return True
    if token_type is Text and value.strip():
        return True
    return False


def tokenize(source: str, merge: bool) -> dict:
    source = source.replace("\r\n", "\n").replace("\r", "\n")
    lexer = build_lexer()

    raw = list(lexer.get_tokens_unprocessed(source))
    if merge:
        merged: list[tuple[int, object, str]] = []
        for index, token_type, value in raw:
            if merged and merged[-1][1] is token_type:
                prev_index, prev_type, prev_value = merged[-1]
                if prev_index + len(prev_value) == index:
                    merged[-1] = (prev_index, prev_type, prev_value + value)
                    continue
            merged.append((index, token_type, value))
        raw = merged

    # Map offsets to line numbers without rescanning the text per token.
    line_starts = [0]
    for offset, char in enumerate(source):
        if char == "\n":
            line_starts.append(offset + 1)

    def position(offset: int) -> tuple[int, int]:
        low, high = 0, len(line_starts) - 1
        while low < high:
            mid = (low + high + 1) // 2
            if line_starts[mid] <= offset:
                low = mid
            else:
                high = mid - 1
        return low + 1, offset - line_starts[low] + 1

    tokens = []
    stats: dict[str, int] = {}
    covered = 0
    for index, token_type, value in raw:
        line, column = position(index)
        name = short_name(token_type)
        stats[name] = stats.get(name, 0) + 1
        covered += len(value)
        tokens.append(
            {
                "start": index,
                "end": index + len(value),
                "line": line,
                "column": column,
                "type": name,
                "value": value,
                "suspicious": is_suspicious(token_type, value),
                "blank": token_type in (Whitespace, Text) and not value.strip(),
            }
        )

    return {
        "ok": True,
        "tokens": tokens,
        "stats": sorted(stats.items(), key=lambda item: (-item[1], item[0])),
        "length": len(source),
        "covered": covered,
        "suspicious": sum(1 for token in tokens if token["suspicious"]),
    }


def list_samples() -> list[dict]:
    samples = []
    for relative in SAMPLE_DIRS:
        directory = REPO_ROOT / relative
        if not directory.is_dir():
            continue
        for path in sorted(directory.rglob("*")):
            if path.suffix.lower() in SAMPLE_SUFFIXES and path.is_file():
                samples.append(
                    {
                        "path": str(path.relative_to(REPO_ROOT)),
                        "size": path.stat().st_size,
                    }
                )
    return samples


def read_sample(relative: str) -> str:
    path = (REPO_ROOT / relative).resolve()
    if not path.is_file() or REPO_ROOT not in path.parents:
        raise ValueError(f"Outside the repository or missing: {relative}")
    return path.read_text(encoding="utf-8", errors="replace")


class Handler(BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def log_message(self, fmt, *args):  # keep the console quiet
        pass

    def _send(self, payload: bytes, content_type: str, status: int = 200) -> None:
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(payload)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(payload)

    def _send_json(self, data: dict, status: int = 200) -> None:
        self._send(
            json.dumps(data).encode("utf-8"), "application/json; charset=utf-8", status
        )

    def do_GET(self) -> None:
        route = urlparse(self.path)
        if route.path == "/":
            self._send(PAGE.encode("utf-8"), "text/html; charset=utf-8")
        elif route.path == "/samples":
            self._send_json({"samples": list_samples()})
        elif route.path == "/sample":
            wanted = parse_qs(route.query).get("path", [""])[0]
            try:
                self._send_json({"ok": True, "source": read_sample(wanted)})
            except Exception as exc:
                self._send_json({"ok": False, "error": str(exc)}, 400)
        else:
            self._send(b"not found", "text/plain; charset=utf-8", 404)

    def do_POST(self) -> None:
        if urlparse(self.path).path != "/tokenize":
            self._send(b"not found", "text/plain; charset=utf-8", 404)
            return
        length = int(self.headers.get("Content-Length", "0"))
        request = json.loads(self.rfile.read(length) or b"{}")
        try:
            result = tokenize(request.get("source", ""), request.get("merge", True))
        except Exception:
            result = {"ok": False, "error": traceback.format_exc()}
        self._send_json(result)


PAGE = r"""
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Lexer lab &middot; CNV</title>
<style>
  :root {
    color-scheme: dark;
    --bg: #14161a; --panel: #1b1e24; --line: #2b3038;
    --fg: #dfe3ea; --muted: #8b93a1; --accent: #7aa2f7; --warn: #f7768e;
  }
  * { box-sizing: border-box; }
  body { margin: 0; height: 100vh; display: flex; flex-direction: column;
         background: var(--bg); color: var(--fg);
         font: 13px/1.5 ui-sans-serif, system-ui, sans-serif; }
  header { display: flex; gap: 10px; align-items: center; flex-wrap: wrap;
           padding: 8px 12px; border-bottom: 1px solid var(--line); }
  header strong { color: var(--accent); }
  select, button, label { font: inherit; color: var(--fg); }
  select, button { background: var(--panel); border: 1px solid var(--line);
                   border-radius: 6px; padding: 4px 8px; }
  button { cursor: pointer; }
  label { color: var(--muted); display: flex; align-items: center; gap: 4px; }
  main { flex: 1; display: grid; grid-template-columns: 1fr 1fr; min-height: 0; }
  .pane { display: flex; flex-direction: column; min-width: 0; min-height: 0; }
  .pane + .pane { border-left: 1px solid var(--line); }
  .pane h2 { margin: 0; padding: 6px 12px; font-size: 11px; font-weight: 600;
             letter-spacing: .08em; text-transform: uppercase; color: var(--muted);
             border-bottom: 1px solid var(--line); display: flex;
             justify-content: space-between; gap: 8px; }
  textarea { flex: 1; resize: none; border: 0; outline: none; padding: 10px 12px;
             background: transparent; color: var(--fg); tab-size: 4;
             font: 12px/1.55 ui-monospace, "SF Mono", Menlo, monospace; }
  .scroll { flex: 1; overflow: auto; }
  table { width: 100%; border-collapse: collapse;
          font: 12px/1.45 ui-monospace, "SF Mono", Menlo, monospace; }
  td { padding: 2px 8px; border-bottom: 1px solid #21252c; vertical-align: top;
       white-space: pre; cursor: pointer; }
  tr:hover td { background: #232830; }
  tr.active td { background: #2a3550; }
  tr.suspicious .type { color: var(--warn); }
  tr.blank { opacity: .45; }
  .pos { color: var(--muted); text-align: right; width: 1%; }
  .type { color: var(--accent); width: 1%; }
  .val { color: #c3e88d; }
  footer { border-top: 1px solid var(--line); padding: 6px 12px; color: var(--muted);
           display: flex; gap: 14px; flex-wrap: wrap;
           font: 12px ui-monospace, Menlo, monospace; }
  footer b { color: var(--fg); font-weight: 600; }
  footer .warn { color: var(--warn); }
  .chip { cursor: pointer; }
  .chip.on { color: var(--accent); text-decoration: underline; }
  pre.error { margin: 0; padding: 12px; color: var(--warn); white-space: pre-wrap;
              font: 12px ui-monospace, Menlo, monospace; }
</style>
</head>
<body>
<header>
  <strong>Lexer lab</strong>
  <select id="samples"><option value="">-- load a file --</option></select>
  <label><input type="checkbox" id="merge" checked> merge adjacent tokens</label>
  <label><input type="checkbox" id="onlySuspicious"> suspicious only</label>
  <label><input type="checkbox" id="hideBlank"> hide whitespace</label>
  <button id="retokenize">Reload lexer (Ctrl+Enter)</button>
</header>
<main>
  <section class="pane">
    <h2><span>Source</span><span id="cursor">1:1</span></h2>
    <textarea id="source" spellcheck="false" placeholder="Paste a CNV script..."></textarea>
  </section>
  <section class="pane">
    <h2><span>Tokens</span><span id="shown"></span></h2>
    <div class="scroll" id="output"></div>
  </section>
</main>
<footer id="stats"></footer>
<script>
const $ = (id) => document.getElementById(id);
const source = $("source"), output = $("output"), stats = $("stats");
let tokens = [], typeFilter = null, activeRow = null;

const DEFAULT = `OBJECT=FLAG\nFLAG:TYPE=BOOL\nFLAG:VALUE=TRUE\n\nOBJECT=__INIT__\n__INIT__:TYPE=BEHAVIOUR\n__INIT__:CODE={FLAG^SET(TRUE);@IF("FLAG^GET()'TRUE","{STRING1^SET("hello $1");}","");}\n`;

async function tokenize() {
  const response = await fetch("/tokenize", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ source: source.value, merge: $("merge").checked }),
  });
  const data = await response.json();
  if (!data.ok) {
    tokens = [];
    output.innerHTML = "";
    output.appendChild(Object.assign(document.createElement("pre"),
      { className: "error", textContent: data.error }));
    stats.textContent = "the lexer failed to load";
    return;
  }
  tokens = data.tokens;
  render();
  renderStats(data);
}

function render() {
  const onlySuspicious = $("onlySuspicious").checked;
  const hideBlank = $("hideBlank").checked;
  const table = document.createElement("table");
  const body = document.createElement("tbody");
  let shown = 0;
  tokens.forEach((token, index) => {
    if (onlySuspicious && !token.suspicious) return;
    if (hideBlank && token.blank) return;
    if (typeFilter && token.type !== typeFilter) return;
    shown++;
    const row = document.createElement("tr");
    row.className = (token.suspicious ? "suspicious " : "") + (token.blank ? "blank" : "");
    row.dataset.index = index;
    row.innerHTML =
      `<td class="pos">${token.line}:${token.column}</td>` +
      `<td class="type"></td><td class="val"></td>`;
    row.children[1].textContent = token.type;
    row.children[2].textContent = JSON.stringify(token.value).slice(1, -1);
    row.onclick = () => select(index);
    body.appendChild(row);
  });
  table.appendChild(body);
  output.innerHTML = "";
  output.appendChild(table);
  $("shown").textContent = `${shown} / ${tokens.length}`;
}

function renderStats(data) {
  stats.innerHTML = "";
  const add = (html, onClick, className) => {
    const span = document.createElement("span");
    span.innerHTML = html;
    if (className) span.className = className;
    if (onClick) span.onclick = onClick;
    stats.appendChild(span);
  };
  add(`chars <b>${data.covered}</b>/${data.length}`);
  add(`tokens <b>${data.tokens.length}</b>`);
  add(`suspicious <b>${data.suspicious}</b>`, null, data.suspicious ? "warn" : "");
  data.stats.forEach(([name, count]) => {
    add(`${name} <b>${count}</b>`, () => {
      typeFilter = typeFilter === name ? null : name;
      render();
      renderStats(data);
    }, "chip" + (typeFilter === name ? " on" : ""));
  });
}

function select(index) {
  const token = tokens[index];
  if (!token) return;
  source.focus();
  source.setSelectionRange(token.start, token.end);
  markActive(index);
}

function markActive(index) {
  if (activeRow) activeRow.classList.remove("active");
  activeRow = output.querySelector(`tr[data-index="${index}"]`);
  if (activeRow) {
    activeRow.classList.add("active");
    activeRow.scrollIntoView({ block: "nearest" });
  }
}

function syncFromCursor() {
  const at = source.selectionStart;
  const before = source.value.slice(0, at);
  const line = before.split("\n").length;
  $("cursor").textContent = `${line}:${at - before.lastIndexOf("\n")}`;
  const index = tokens.findIndex((token) => token.start <= at && at < token.end);
  if (index >= 0) markActive(index);
}

let timer = null;
const schedule = () => { clearTimeout(timer); timer = setTimeout(tokenize, 150); };

source.addEventListener("input", schedule);
source.addEventListener("keyup", syncFromCursor);
source.addEventListener("click", syncFromCursor);
["merge", "onlySuspicious", "hideBlank"].forEach((id) =>
  $(id).addEventListener("change", () => (id === "merge" ? tokenize() : render())));
$("retokenize").onclick = tokenize;
document.addEventListener("keydown", (event) => {
  if (event.key === "Enter" && (event.ctrlKey || event.metaKey)) {
    event.preventDefault();
    tokenize();
  }
});

$("samples").addEventListener("change", async (event) => {
  const path = event.target.value;
  if (!path) return;
  const data = await (await fetch("/sample?path=" + encodeURIComponent(path))).json();
  if (data.ok) { source.value = data.source; tokenize(); }
});

(async () => {
  const data = await (await fetch("/samples")).json();
  data.samples.forEach((sample) => {
    const option = document.createElement("option");
    option.value = sample.path;
    option.textContent = `${sample.path} (${(sample.size / 1024).toFixed(1)} kB)`;
    $("samples").appendChild(option);
  });
})();

source.value = window.localStorage.getItem("cnv-lab") || DEFAULT;
source.addEventListener("input", () => window.localStorage.setItem("cnv-lab", source.value));
tokenize();
</script>
</body>
</html>
"""


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--port", type=int, default=8765)
    parser.add_argument("--no-browser", action="store_true")
    args = parser.parse_args()

    build_lexer()  # failing early beats serving a blank page
    server = ThreadingHTTPServer(("127.0.0.1", args.port), Handler)
    url = f"http://127.0.0.1:{args.port}/"
    print(f"Lexer lab: {url}  (Ctrl+C to stop)")
    if not args.no_browser:
        webbrowser.open(url)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print()


if __name__ == "__main__":
    main()
