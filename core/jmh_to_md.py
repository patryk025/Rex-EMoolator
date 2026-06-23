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
        params        = bench.get("params") or {}
        if params:
            name += " [" + ", ".join(f"{k}={v}" for k, v in params.items()) + "]"
        mode          = bench.get("mode", "?")
        iterations    = bench.get("measurementIterations", "?")
        metrics       = bench.get("primaryMetric") or {}
        score         = metrics.get("score", float("nan"))
        error         = metrics.get("scoreError", float("nan"))
        units         = metrics.get("scoreUnit", "?")
        secondary     = bench.get("secondaryMetrics") or {}
        alloc_metric  = secondary.get("gc.alloc.rate.norm") or {}
        alloc         = alloc_metric.get("score")
        groups[group] = groups.get(group, [])
        groups[group].append({"name": name, "mode": mode, "iterations": iterations, "score": score, "error": error, "units": units, "alloc": alloc})

    for group in groups:
        print(f"## {group}")
        # Nagłówek tabeli
        print("| Benchmark | Mode | Iterations | Score | Error | Units | Alloc (B/op) |")
        print("|-----------|------|-------|-------|-------|-------|-------|")
        for bench in groups[group]:
            alloc = f"{bench['alloc']:.1f}" if bench['alloc'] is not None else "n/a"
            print(f"| {bench['name']} | {bench['mode']} | {bench['iterations']} | {bench['score']:.3f} | {bench['error']:.3f} | {bench['units']} | {alloc} |")
        print()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: jmh_to_md.py <results.json>", file=sys.stderr)
        sys.exit(1)
    main(sys.argv[1])
