# Engine

**Piklib** (later **BlooMoo**) is a 32-bit graphical engine created by Aidem Media for a series of Polish games released in the 2000s. This documentation describes the engine's internal logic and how it executes the games' scripts.

## Scope

This documentation focuses on the engine's **scripting language** and the **execution model** as seen from script-level code — that is, what a content programmer needs in order to read existing scripts or write new ones.

It is not a documentation of the engine's source code, nor a complete specification of every internal data structure; those areas are being filled in gradually.

## Original and emulator

The engine sections describe the **original**, which is the source of truth. The emulator's state appears only in marked boxes: **Emulator** for missing implementation and **Difference in the emulator** for differences detected by crosstests. The [Differences from the original](../emulator/divergences.md) page has the combined list.

## Structure

The engine documentation is divided into five chapters:

- [Scripts](scripts.md) — script syntax, the parser, loading order, and object initialisation.
- [Arithmetic](arithmetic.md) — computational expressions, operators, and conversion rules between primitive types.
- [Events and signals](events.md) — the engine's reactive model, attaching handlers, propagation through the call tree.
- [Global variables](globals.md) — built-in objects (`MOUSE`, `KEYBOARD`, `RAND`, `SYSTEM`), implicit variables (`_I_`, `THIS`, `$N`), and special procedures.
- [Engine quirks](quirks.md) — non-standard behaviours that are easy to miss.

The full list of available data types is in the [Type reference](../reference/index.md).

## Games using the engine

The list is incomplete and will be extended as more titles are identified. Engine versions were checked against the DLLs of the available editions; *Wojna Trojańska*, *Król Maciuś Pierwszy*, and *Piękna i Bestia* have not been confirmed this way yet.

| Game                                                     | Engine version                          |
|----------------------------------------------------------|-----------------------------------------|
| **Przygody Reksia**                                      |                                         |
| Reksio i Skarb Piratów                                   | Piklib 6.1, Piklib 7.1, Piklib 8        |
| Reksio i UFO                                             | Piklib 7.1, Piklib 8                    |
| Reksio i Czarodzieje                                     | Piklib 8                                |
| Reksio i Wehikuł Czasu                                   | BlooMoo (first build)                   |
| Reksio i Kapitan Nemo                                    | BlooMoo                                 |
| Reksio i Kretes w Akcji                                  | BlooMoo                                 |
| **Poznaj Mity**                                          |                                         |
| Wyprawa po Złote Runo                                    | Piklib 8                                |
| Wojna Trojańska                                          | Piklib 7.2                              |
| Przygody Odyseusza                                       | Piklib 8                                |
| Herkules                                                 | Piklib 8                                |
| Tezeusz i nić Ariadny                                    | Piklib 8                                |
| **Bolek i Lolek**                                        |                                         |
| Bolek i Lolek na tropie zaginionej księgi ortografii     | Piklib 3.0                              |
| Bolek i Lolek. Alfabet i nauka czytania                  | BlooMoo                                 |
| Bolek i Lolek: Język angielski dla dzieci                | Piklib 8                                |
| Bolek i Lolek: Język niemiecki dla najmłodszych          | BlooMooWeb                              |
| Bolek i Lolek: Moje pierwsze literki                     | BlooMoo                                 |
| Bolek i Lolek: Moje pierwsze studio plastyczne           | BlooMoo                                 |
| Bolek i Lolek: Olimpiada Letnia                          | Piklib 8                                |
| Bolek i Lolek: Zwariowana Olimpiada                      | Piklib 8                                |
| Wesołe przedszkole Bolka i Lolka                         | Piklib 8                                |
| **Król Maciuś Pierwszy**                                 |                                         |
| Król Maciuś Pierwszy. Przedszkole, zabawy z przyjaciółmi | BlooMoo                                 |
| Król Maciuś Pierwszy. Wesołe miasteczko                  | BlooMooWeb                              |
| Król Maciuś Pierwszy. Wyspa Togo-Pogo                    | BlooMoo                                 |
| **Komputerowy świat bajek** / **Gry-Bajki**              |                                         |
| Aladyn                                                   | Piklib 6.1                              |
| Pinokio                                                  | Piklib 6.1                              |
| Piotruś Pan                                              | Piklib 6.1                              |
| Ali Baba i czterdziestu rozbójników                      | Piklib 8 (but config is in BlooMoo.ini) |
| Robinson Crusoe                                          | Piklib 8 (6.1 to be checked)            |
| Sindbad                                                  | Piklib 8 (6.1 to be checked)            |
| Robin Hood                                               | Piklib 8 (6.1 to be checked)            |
| Księga Dżungli                                           | Piklib 8 (6.1 to be checked)            |
| Doktor Dolittle                                          | Piklib 8 (6.1 to be checked)            |
| **Gry dla dziewczynek**                                  |                                         |
| Piękna i Bestia                                          | BlooMoo                                 |
| Roszpunka                                                | BlooMoo                                 |
| **Koziołek Matołek**                                     |                                         |
| Szkoła Koziołka Matołka                                  | Piklib 7.2                              |
| Wesołe przedszkole Koziołka Matołka                      | Piklib 8                                |
| **Edukacja XXI wieku**                                   |                                         |
| Matematyka: Dodawanie i odejmowanie                      | Piklib 4.1                              |
| Alfabet. Nauka czytania i pisania                        | Piklib 4.1                              |
| **Inne gry z Reksiem**                                   |                                         |
| ABC z Reksiem                                            | Piklib 4.1, RPiklib (?)                 |
| Liczę z Reksiem                                          | Piklib 6.0                              |
| Reksio i Ortografia                                      | Piklib 7.2                              |
| Wesołe Przedszkole Reksia                                | Piklib 8                                |
| **Inne tytuły**                                          |                                         |
| Wielki Teleturniej Smoka Wawelskiego                     | Piklib 5.0                              |
