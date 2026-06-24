window.BENCHMARK_DATA = {
  "lastUpdate": 1782303635100,
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
      },
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
          "id": "7519686ecf1a0173670d893496cd4e874b48c0eb",
          "message": "perf: fix and optimize CLZW2Compression algorithm\n\nMemory allocation has dropped to nearly the level of the output buffer, and the execution time for operations on the test vectors has decreased by a factor of 3 to 6. An off-by-one error was also fixed in three places. Fixes #39",
          "timestamp": "2026-06-24T14:14:29+02:00",
          "tree_id": "c63db8730133ea3b3988b30f1e583cd8a41467a3",
          "url": "https://github.com/patryk025/Rex-EMoolator/commit/7519686ecf1a0173670d893496cd4e874b48c0eb"
        },
        "date": 1782303634636,
        "tool": "customSmallerIsBetter",
        "benches": [
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=first_literals_20] (time)",
            "value": 0.015877533408617462,
            "range": "± 0",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=first_literals_20] (alloc)",
            "value": 40.00010957672605,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=first_literals_20] (alloc/byte)",
            "value": 2.0000054788363024,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=rle00_1000] (time)",
            "value": 2.758694181979183,
            "range": "± 0.004",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=rle00_1000] (alloc)",
            "value": 1016.0190585769176,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=rle00_1000] (alloc/byte)",
            "value": 1.0160190585769175,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=period_8] (time)",
            "value": 0.501824052750416,
            "range": "± 0.002",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=period_8] (alloc)",
            "value": 1216.003464508778,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=period_8] (alloc/byte)",
            "value": 1.0133362204239817,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=gradient_ramp] (time)",
            "value": 1.5469711405656958,
            "range": "± 0.032",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=gradient_ramp] (alloc)",
            "value": 4112.0106810196185,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=gradient_ramp] (alloc/byte)",
            "value": 1.0039088576708053,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_16x16] (time)",
            "value": 0.37322642982293097,
            "range": "± 0.013",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_16x16] (alloc)",
            "value": 272.0025765565953,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_16x16] (alloc/byte)",
            "value": 1.0625100646742003,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_64x64] (time)",
            "value": 3.6600260215766776,
            "range": "± 0.072",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_64x64] (alloc)",
            "value": 4112.025270481066,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_64x64] (alloc/byte)",
            "value": 1.0039124195510416,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_320x200] (time)",
            "value": 52.07018129793312,
            "range": "± 0.362",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_320x200] (alloc)",
            "value": 64016.36158757044,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_320x200] (alloc/byte)",
            "value": 1.0002556498057882,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_640x480] (time)",
            "value": 487.7401786947891,
            "range": "± 4.463",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_640x480] (alloc)",
            "value": 307219.384690347,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=img_640x480] (alloc/byte)",
            "value": 1.0000631012055567,
            "unit": "B/B"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=random_65536] (time)",
            "value": 4.651822465272593,
            "range": "± 0.459",
            "unit": "us/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=random_65536] (alloc)",
            "value": 65552.03212984207,
            "unit": "B/op"
          },
          {
            "name": "CLZW2DecodeBenchmark.decode [vector=random_65536] (alloc/byte)",
            "value": 1.0002446308874828,
            "unit": "B/B"
          },
          {
            "name": "CNVParserBenchmark.decryptionOnly (time)",
            "value": 0.8974522291572491,
            "range": "± 0.005",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.decryptionOnly (alloc)",
            "value": 895746.0767640534,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseEncryptedLarge (time)",
            "value": 11.799270660502268,
            "range": "± 0.129",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseEncryptedLarge (alloc)",
            "value": 18156563.605531607,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseEncryptedSmall (time)",
            "value": 0.051459223820072085,
            "range": "± 0",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseEncryptedSmall (alloc)",
            "value": 103944.11904726016,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseLargeScript (time)",
            "value": 10.522482707991925,
            "range": "± 0.125",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseLargeScript (alloc)",
            "value": 17999067.894005075,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseMediumScript (time)",
            "value": 2.960397928202804,
            "range": "± 0.082",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseMediumScript (alloc)",
            "value": 3635983.350716906,
            "unit": "B/op"
          },
          {
            "name": "CNVParserBenchmark.parseSmallScript (time)",
            "value": 0.0439945662161878,
            "range": "± 0",
            "unit": "ms/op"
          },
          {
            "name": "CNVParserBenchmark.parseSmallScript (alloc)",
            "value": 101608.10177946305,
            "unit": "B/op"
          }
        ]
      }
    ]
  }
}