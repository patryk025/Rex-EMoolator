# Emulator method map

Each square is one script method within a type. Its color tells whether the emulator registers it: teal is a registration, amber a stub, gray no match. Hover a square, reach it with the keyboard or tap it to see the method name. A type name leads to its page in the reference.

<div id="method-map" class="method-map"></div>
<noscript>This map requires JavaScript.</noscript>

!!! note "Reading this map"

    - Colors describe emulator code, not correctness against the original engine. A stub may affect only one branch of a method, and a missing match is not a confirmed missing implementation.
    - The original methods come from analysing the PIKLIB 6.1–8 and BlooMoo engine DLLs and the scene modules (`SCENE:DLLS=`: WORLD, KOLOROWANKA, MATRIX, TEST, INERTIA, BOARD). Type → runner comes from the engine runner factory or from the module object.
    - Base methods (`CLONE`, `SEND`, …) appear once, in the "Common methods" card, which is not a script type. "@ statements" are `@IF`, `@LOOP` and the other built-in statements.
    - A card's ratio is registrations without a stub flag / identified methods, computed over the whole group even when filtered. It is not functional coverage. A "No data" card does not mean 0%.
    - The emulator helper type `INSTANCE` is excluded.

The map is refreshed manually by an external DLL analyzer that is not part of this repository. The date and emulator revision are shown above the legend.
