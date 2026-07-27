# FONT

A bitmap font definition. The object exposes no script-callable methods or signals — it is used by the [`TEXT`](TEXT.md) type as a source of character textures.

## Fields

### DEF

```
STRING DEF_<name>_<style>_<size>
```

A field that declares an [`.FNT`](../formats/FNT.md) font file. The field name
encodes the variant's family, style, and size. This metadata is not stored
inside the FNT file.

Script syntax:

```
FONT:DEF_<name>_<style>_<size>=<file>.FNT
```

**Example**

```
FONT:DEF_ARIAL_STANDARD_14=ARIAL14.FNT
```

A single `FONT` object may contain multiple `DEF_*` fields. Piklib builds a
variant collection from them and treats the first variant as the base font.
Inline formatting codes can select styles such as `STANDARD`, `ITALIC`,
`BOLD`, `LSTRIKE`, `USTRIKE`, and `UNDERLINE` when a matching variant exists.
