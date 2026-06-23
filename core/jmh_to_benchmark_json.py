"""Convert a JMH results.json into the `customSmallerIsBetter` format consumed by
benchmark-action/github-action-benchmark.

The action's native `tool: 'jmh'` parser only reads `primaryMetric` (time) and ignores
`secondaryMetrics`, so allocation would never be tracked. This converter emits two entries per
benchmark - one for average time and one for `gc.alloc.rate.norm` (B/op) - giving the action a
single, diffable dataset where both time and allocation are charted and alert on regression.

Usage: jmh_to_benchmark_json.py <results.json>   # prints the JSON array to stdout
"""
import json
import sys
from pathlib import Path

PREFIX = "pl.genschu.bloomooemulator.jhm."


def bench_label(bench):
    name = bench.get("benchmark", "?")
    if name.startswith(PREFIX):
        name = name[len(PREFIX):]
    params = bench.get("params") or {}
    if params:
        name += " [" + ", ".join(f"{k}={v}" for k, v in params.items()) + "]"
    return name


def main(path):
    data = json.loads(Path(path).read_text(encoding="utf-8"))

    entries = []
    for bench in data:
        label = bench_label(bench)

        metrics = bench.get("primaryMetric") or {}
        score = metrics.get("score")
        if score is not None:
            entry = {
                "name": f"{label} (time)",
                "unit": metrics.get("scoreUnit", "?"),
                "value": score,
            }
            error = metrics.get("scoreError")
            if isinstance(error, (int, float)) and error == error:  # not NaN
                entry["range"] = f"± {error:.3f}"
            entries.append(entry)

        secondary = bench.get("secondaryMetrics") or {}
        alloc = (secondary.get("gc.alloc.rate.norm") or {}).get("score")
        if alloc is not None:
            entries.append({
                "name": f"{label} (alloc)",
                "unit": "B/op",
                "value": alloc,
            })

    json.dump(entries, sys.stdout, indent=2)
    sys.stdout.write("\n")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: jmh_to_benchmark_json.py <results.json>", file=sys.stderr)
        sys.exit(1)
    main(sys.argv[1])
