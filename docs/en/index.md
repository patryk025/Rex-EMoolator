# Rex-EMoolator

Unofficial documentation of the **Piklib** and **BlooMoo** scripting engines used by the *Reksio's Adventures* game series, and of the **Rex-EMoolator** emulator.

## Documentation map

<div class="grid cards" markdown>

-   :material-code-braces:{ .lg .middle } __Engine__

    ---

    The scripting language as seen by the content author: syntax, arithmetic, events and signals, global variables, and non-standard behaviours.

    [:octicons-arrow-right-24: Engine](engine/index.md)

-   :material-engine-outline:{ .lg .middle } __Engine internals__

    ---

    How the engine works under the hood: the loop and clock, rendering (today and in the original), the animation system, and time and timers.

    [:octicons-arrow-right-24: Engine internals](internals/index.md)

-   :material-file-tree:{ .lg .middle } __File formats__

    ---

    The byte-level structure of files: animations (`ANN`), images (`IMG`), fonts (`FNT`), arrays (`ARR`), data (`DTA`), and more.

    [:octicons-arrow-right-24: File formats](formats/index.md)

-   :material-format-list-bulleted-type:{ .lg .middle } __Type reference__

    ---

    An alphabetical list of the ~45 types available in scripts, with the fields, methods, and signals of each.

    [:octicons-arrow-right-24: Type reference](reference/index.md)

</div>

## What you'll find here

- how the engine's scripting language is built and how it runs game logic,
- the data types and objects available to scripts, and the methods, fields, and signals they expose,
- how rendering, animation, and time work in the engine — and how they differed from the original,
- how game assets are encoded on disk (file formats).

## Status

This documentation is a work in progress. Much of it is derived from analysis of the original scripts and engine reverse-engineering; behaviour that has not yet been confirmed is annotated accordingly. Technical descriptions are, where possible, reconciled with the emulator's actual implementation.
