# FONT

A bitmap font definition. The object exposes no script-callable methods or signals — it is used by the [`TEXT`](TEXT.md) type as a source of character textures.

## Fields

### DEF

```
STRING DEF_<name>_<style>_<size>
```

A field that declares a `.FNT` font file. The field's name encodes the font variant's metadata: its name, style, and size.

Script syntax:

```
FONT:DEF_<name>_<style>_<size>=<file>.FNT
```

**Example**

```
FONT:DEF_ARIAL_STANDARD_14=ARIAL14.FNT
```
