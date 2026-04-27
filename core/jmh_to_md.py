import json
import sys
from pathlib import Path

def main(path):
    data = json.loads(Path(path).read_text(encoding="utf-8"))

    groups = {}

    for bench in data:
        tmp           = bench.get("benchmark", "?").replace("pl.genschu.bloomooemulator.jhm.", "").split(".")
        group         = tmp[0]
        name          = tmp[1]
        mode          = bench.get("mode", "?")
        iterations    = bench.get("measurementIterations", "?")
        metrics       = bench.get("primaryMetric") or {}
        score         = metrics.get("score", float("nan"))
        error         = metrics.get("scoreError", float("nan"))
        units         = metrics.get("scoreUnit", "?")
        groups[group] = groups.get(group, [])
        groups[group].append({"name": name, "mode": mode, "iterations": iterations, "score": score, "error": error, "units": units})

    for group in groups:
        print(f"## {group}")
        # Nagłówek tabeli
        print("| Benchmark | Mode | Iterations | Score | Error | Units |")
        print("|-----------|------|-------|-------|-------|-------|")
        for bench in groups[group]:
            print(f"| {bench['name']} | {bench['mode']} | {bench['iterations']} | {bench['score']:.3f} | {bench['error']:.3f} | {bench['units']} |")
        print()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: jmh_to_md.py <results.json>", file=sys.stderr)
        sys.exit(1)
    main(sys.argv[1])
