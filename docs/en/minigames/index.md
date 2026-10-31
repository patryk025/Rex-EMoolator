# Minigames

Aidem Media games break up ordinary scenes with **minigames** — self-contained interludes with their own logic, controls and win conditions, written entirely in engine scripts. This section takes them apart: how they collect player input, how they recognize intent, and how they trigger effects.

Unlike the [Engine](../engine/index.md) and [Engine internals](../internals/index.md) sections, which describe general mechanisms, here we look at **specific scenes** as case studies — with references to the actual `.cnv` files and on-disk data.

!!! note "Section under construction"
    For now one minigame is documented (the spell duel). The structure is deliberately open — further minigames are added as separate pages following the same schema: *overview → input → recognition → effects*.

## Chapters

<div class="grid cards" markdown>

-   :material-auto-fix:{ .lg .middle } __Spell duel__

    ---

    Spells "drawn" in the air with the cursor: how a gesture is turned into a sequence of angles and matched against patterns and the spell catalog.

    [:octicons-arrow-right-24: Read](magic.md)

</div>

## See also

- [ARR format](../formats/ARR.md) — binary array storage, including point-list gesture "trails".
- [Coordinates & anchors](../internals/coordinates.md) — the 800×600 canvas and the downward Y axis, key to interpreting gesture angles.
- [Type reference](../reference/index.md) — objects (`ARRAY`, `MOUSE`, `BEHAVIOUR`) used by minigame logic.
