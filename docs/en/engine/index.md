# Engine

**Piklib** (later **BlooMoo**) is a 32-bit graphical engine created by Aidem Media for a series of Polish adventure games released in the 2000s. This documentation describes the engine's internal logic and how it executes the games' scripts.

## Scope

This documentation focuses on the engine's **scripting language** and the **execution model** as seen from script-level code — that is, what a content programmer needs in order to read existing scripts or write new ones.

It is not a documentation of the engine's source code, nor a complete specification of every internal data structure; those areas are being filled in gradually.

## Structure

The engine documentation is divided into five chapters:

- [Scripts](scripts.md) — script syntax, the parser, loading order, and object initialisation.
- [Arithmetic](arithmetic.md) — computational expressions, operators, and conversion rules between primitive types.
- [Events and signals](events.md) — the engine's reactive model, attaching handlers, propagation through the call tree.
- [Global variables](globals.md) — built-in objects (`MOUSE`, `KEYBOARD`, `RAND`, `SYSTEM`), implicit variables (`_I_`, `THIS`, `$N`), and special procedures.
- [Engine quirks](quirks.md) — non-standard behaviours that are easy to miss.

The full list of available data types is in the [Type reference](../reference/index.md).

## Games using the engine

The list is incomplete and will be extended as more titles are identified.

| Game | Engine version |
|---|---|
| Reksio i Skarb Piratów | Piklib 8 |
| Reksio i Ufo | Piklib 7.1, Piklib 8 |
| Reksio i Czarodzieje | Piklib 8 |
| Reksio i Wehikuł Czasu | Piklib 8 |
| Reksio i Kapitan Nemo | BlooMoo |
| Reksio i Kretes w Akcji | BlooMoo |
| Poznaj Mity: Wyprawa po Złote Runo | Piklib 7.1 |
| Poznaj Mity: Wojna Trojańska | Piklib 7.2 |
| Poznaj Mity: Przygody Odyseusza | Piklib 8 |
| Poznaj Mity: Herkules | Piklib 8 |
