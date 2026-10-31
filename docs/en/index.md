# Rex-EMoolator

Unofficial documentation of the **Piklib** and **BlooMoo** scripting engines, which Aidem Media used for series such as *Reksio's Adventures* and *Poznaj Mity*, and of the **Rex-EMoolator** emulator.

## Documentation map

<div class="grid cards" markdown>

-   :material-gamepad-variant-outline:{ .lg .middle } __Emulator__

    ---

    Downloading and running games, their current status, patches, and building from source.

    [:octicons-arrow-right-24: Emulator](emulator/index.md)

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

-   :material-auto-fix:{ .lg .middle } __Minigames__

    ---

    Specific scenes taken apart, starting with the spell duel from "Reksio i Czarodzieje".

    [:octicons-arrow-right-24: Minigames](minigames/index.md)

-   :material-format-list-bulleted-type:{ .lg .middle } __Type reference__

    ---

    A list of the types available in scripts, with their fields, methods, and signals, plus an interactive method map.

    [:octicons-arrow-right-24: Type reference](reference/index.md)

</div>

## What you'll find here

- how to run games in the emulator and how well they work,
- how the engine's scripting language is built and how it runs game logic,
- the data types and objects available to scripts, and the methods, fields, and signals they expose,
- how rendering, animation, and time work in the engine — and how they differed from the original,
- how game assets are encoded on disk (file formats).

## Status

This documentation is a work in progress. Much of it is derived from analysis of the original scripts and engine reverse-engineering; behaviour that has not yet been confirmed is annotated accordingly. Technical descriptions are, where possible, reconciled with the emulator's actual implementation.
