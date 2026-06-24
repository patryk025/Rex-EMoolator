window.BENCHMARK_DATA = {
  "lastUpdate": 1782291241248,
  "repoUrl": "https://github.com/patryk025/Rex-EMoolator",
  "entries": {
    "JMH Benchmarks": [
      {
        "commit": {
          "author": {
            "email": "43010113+patryk025@users.noreply.github.com",
            "name": "Patryk Gensch",
            "username": "patryk025"
          },
          "committer": {
            "email": "43010113+patryk025@users.noreply.github.com",
            "name": "Patryk Gensch",
            "username": "patryk025"
          },
          "distinct": true,
          "id": "62a1fcd9b8300dc01e87cce68ca5b045f5af6a3d",
          "message": "perf: rename package from jhm to jmh and enhance benchmark scripts",
          "timestamp": "2026-06-24T10:47:41+02:00",
          "tree_id": "ad77ba6325b72263e1b2b23b0426ab02cf881200",
          "url": "https://github.com/patryk025/Rex-EMoolator/commit/62a1fcd9b8300dc01e87cce68ca5b045f5af6a3d"
        },
        "date": 1782291240841,
        "tool": "customSmallerIsBetter",
        "benches": [
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=first_literals_20] (time)",
            "value": 0.07265723390904053,
            "range": "± 0.003",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=first_literals_20] (alloc)",
            "value": 296.00050097793456,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=first_literals_20] (alloc/byte)",
            "value": 14.800025048896728,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=rle00_1000] (time)",
            "value": 2.6274905740852876,
            "range": "± 0.007",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=rle00_1000] (alloc)",
            "value": 1576.0181413355392,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=rle00_1000] (alloc/byte)",
            "value": 1.5760181413355392,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=period_8] (time)",
            "value": 0.9683978421491279,
            "range": "± 0.020",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=period_8] (alloc)",
            "value": 1808.0066832886464,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=period_8] (alloc/byte)",
            "value": 1.506672236073872,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=gradient_ramp] (time)",
            "value": 2.9679171712156003,
            "range": "± 0.046",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=gradient_ramp] (alloc)",
            "value": 5344.020488442164,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=gradient_ramp] (alloc/byte)",
            "value": 1.3046925020610751,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_16x16] (time)",
            "value": 1.6049592344320274,
            "range": "± 0.013",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_16x16] (alloc)",
            "value": 3952.0110901213675,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_16x16] (alloc/byte)",
            "value": 15.437543320786592,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_64x64] (time)",
            "value": 19.805956700747863,
            "range": "± 0.224",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_64x64] (alloc)",
            "value": 46648.13735554596,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_64x64] (alloc/byte)",
            "value": 11.388705409068837,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_320x200] (time)",
            "value": 336.1421947597989,
            "range": "± 2.398",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_320x200] (alloc)",
            "value": 732954.3333397713,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_320x200] (alloc/byte)",
            "value": 11.452411458433925,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_640x480] (time)",
            "value": 1716.056815181865,
            "range": "± 8.035",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_640x480] (alloc)",
            "value": 3721052.0521816774,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_640x480] (alloc/byte)",
            "value": 12.112799649028897,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=random_65536] (time)",
            "value": 12.340029674149168,
            "range": "± 0.322",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=random_65536] (alloc)",
            "value": 138368.08543648143,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=random_65536] (alloc/byte)",
            "value": 2.1113294286572484,
            "unit": "B/B"
          },
          {
            "name": "CNVParserBenchmark.decryptionOnly (time)",
            "value": 0.7397977710074987,
            "range": "± 0.002",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.decryptionOnly (alloc)",
            "value": 895745.7119943786,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseEncryptedLarge (time)",
            "value": 11.341148241321196,
            "range": "± 0.087",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseEncryptedLarge (alloc)",
            "value": 18167992.44910634,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseEncryptedSmall (time)",
            "value": 0.05759812764380536,
            "range": "± 0.000",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseEncryptedSmall (alloc)",
            "value": 104320.13324110265,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseLargeScript (time)",
            "value": 9.875094903512263,
            "range": "± 0.043",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseLargeScript (alloc)",
            "value": 18002423.815918695,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseMediumScript (time)",
            "value": 2.9688943556086493,
            "range": "± 0.025",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseMediumScript (alloc)",
            "value": 3710951.681442438,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseSmallScript (time)",
            "value": 0.050448234439830306,
            "range": "± 0.000",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseSmallScript (alloc)",
            "value": 101360.11671000958,
            "unit": "B/op"
          }
        ]
      }
    ]
  }
}