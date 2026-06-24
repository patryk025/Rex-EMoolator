import json
import sys
from pathlib import Path

from jmh_common import human_bytes, vector_name, vector_sizes

def main(path):
    data = json.loads(Path(path).read_text(encoding="utf-8"))

    groups = {}

    for bench in data:
        tmp           = bench.get("benchmark", "?").replace("pl.genschu.bloomooemulator.jmh.", "").split(".")
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

        # Test-vector context: input (encoded) size, decoded (output) size and the allocation
        # overhead = bytes allocated per byte of decoded output. n/a for non-vector benchmarks.
        sizes         = vector_sizes(vector_name(bench))
        input_size    = sizes[0] if sizes else None
        decoded_size  = sizes[1] if sizes else None
        overhead      = (alloc / decoded_size) if (alloc is not None and decoded_size) else None

        groups[group] = groups.get(group, [])
        groups[group].append({
            "name": name, "mode": mode, "iterations": iterations,
            "score": score, "error": error, "units": units, "alloc": alloc,
            "input": input_size, "decoded": decoded_size, "overhead": overhead,
        })

    for group in groups:
        print(f"## {group}")
        # Nagłówek tabeli
        print("| Benchmark | Mode | Iterations | Score | Error | Units | Alloc/op | Input | Decoded | Overhead |")
        print("|-----------|------|------------|-------|-------|-------|----------|-------|---------|----------|")
        for bench in groups[group]:
            alloc    = human_bytes(bench["alloc"]) if bench["alloc"] is not None else "n/a"
            inp      = human_bytes(bench["input"]) if bench["input"] is not None else "—"
            decoded  = human_bytes(bench["decoded"]) if bench["decoded"] is not None else "—"
            overhead = f"×{bench['overhead']:.2f}" if bench["overhead"] is not None else "—"
            print(f"| {bench['name']} | {bench['mode']} | {bench['iterations']} | {bench['score']:.3f} | {bench['error']:.3f} | {bench['units']} | {alloc} | {inp} | {decoded} | {overhead} |")
        print()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: jmh_to_md.py <results.json>", file=sys.stderr)
        sys.exit(1)
    main(sys.argv[1])
