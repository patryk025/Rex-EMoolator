# Formaty plików

Silnik Piklib/BlooMoo trzyma zasoby gry w kilkunastu formatach plików — część tekstowych (skrypty, dane), część binarnych (grafika, dźwięk, dane fizyki). Ten dział opisuje ich budowę bajt po bajcie, na podstawie analizy plików **uzgodnionej z parserami Rex-EMoolatora** (`AnimoLoader`, `ImageLoader`, `FontLoader` i pokrewne).

!!! note "Skąd ta dokumentacja"
    Opisy łączą wcześniejszy reverse-engineering formatów z tym, jak faktycznie czyta je emulator. Tam, gdzie kod rozumie format dokładniej niż dawne notatki (np. pola zdarzeń w [`.ANN`](ANN.md)), pierwszeństwo ma implementacja. Pola wciąż niepewne są wyraźnie oznaczone.

## Formaty binarne

<div class="grid cards" markdown>

-   :material-animation-play:{ .lg .middle } __[ANN](ANN.md)__ — animacje

    ---

    Zdarzenia, klatki i wspólna pula obrazków. Magic: `NVP\0`.

-   :material-image:{ .lg .middle } __[IMG](IMG.md)__ — obrazy

    ---

    Pojedyncza bitmapa hi-color z opcjonalnym kanałem alfa. Magic: `PIK\0`.

-   :material-format-font:{ .lg .middle } __[FNT](FNT.md)__ — czcionki

    ---

    Bitmapowa czcionka ze znakami w jednej długiej bitmapie. Magic: `FNT\0`.

-   :material-table:{ .lg .middle } __[ARR](ARR.md)__ — tablice

    ---

    Zrzut tablicy [`ARRAY`](../reference/ARRAY.md): typ + wartość na element.

-   :material-cube-outline:{ .lg .middle } __[SEK](SEK.md)__ — fizyka 3D :material-progress-wrench:

    ---

    Waypointy, obiekty fizyczne i punkty trasy dla [`WORLD`](../reference/WORLD.md). Magic: `SEKAI…`. *W opracowaniu.*

-   :material-cog-outline:{ .lg .middle } __[INE](INE.md)__ — fizyka Inertia :material-progress-wrench:

    ---

    Dane silnika [`INERTIA`](../reference/INERTIA.md). *W opracowaniu.*

</div>

## Formaty tekstowe

| Format | Rola | Opis |
|---|---|---|
| `CNV` | skrypty obiektów sceny | główny format definicji obiektów — patrz [Skrypty](../engine/scripts.md) |
| `DEF` | punkt startowy (`Application.def`) | definicje `APPLICATION`/`EPISODE`/`SCENE` — patrz [kolejność wczytywania](../engine/scripts.md#kolejnosc-wczytywania-skryptow) |
| `CLASS` | definicje klas | szablony obiektów typu [`CLASS`](../reference/CLASS.md) |
| `SEQ` | sekwencje | scenariusze animacji z synchronizacją dźwięku ([`SEQUENCE`](../reference/SEQUENCE.md)) |
| [`DTA`](DTA.md) | baza danych | namiastka bazy dla [`DATABASE`](../reference/DATABASE.md) |
| `INI` | konfiguracja | ustawienia gry |

Formaty skryptowe (`CNV`/`DEF`/`CLASS`/`SEQ`) mają wspólną składnię tekstową opisaną w rozdziale [Skrypty](../engine/scripts.md).

## Magic bytes

Większość formatów binarnych zaczyna się stałą sygnaturą — to pierwsza rzecz, jaką sprawdza parser:

| Format | Bajty | ASCII |
|---|---|---|
| ANN | `4E 56 50 00` | `NVP\0` |
| IMG | `50 49 4B 00` | `PIK\0` |
| FNT | `46 4E 54 00` | `FNT\0` |
| SEK | `53 45 4B 41 49 …` | `SEKAI81080701915004` |

Pliki [`ARR`](ARR.md) i [`DTA`](DTA.md) nie mają sygnatury — `ARR` zaczyna się od liczby elementów, `DTA` jest zwykłym tekstem.

## Kodowanie pikseli

Grafika silnika to **hi-color** bez palety:

- **RGB565** (16 bitów: 5R-6G-5B) — najczęstsze,
- **RGB555** (15 bitów: 5R-5G-5B).

Kanał alfa, jeśli istnieje, zapisywany jest **osobno** od danych koloru (jeden bajt na piksel) i łączony przy dekodowaniu. Dane obrazu bywają dodatkowo skompresowane.

!!! abstract "Kompresja i szyfrowanie — wkrótce"
    Algorytmy kompresji (CRLE, CLZW2) oraz szyfr skryptów to temat na osobny rozdział, który powstanie w następnej turze rozbudowy. Na razie poszczególne formaty wskazują jedynie **typ** użytej kompresji.

## Zobacz też

- [System animacji](../internals/animation.md) — jak silnik interpretuje dane z [`.ANN`](ANN.md).
- [Renderowanie](../internals/rendering.md) — co dzieje się z bitmapą po dekodowaniu.
- [Referencja typów](../reference/index.md) — obiekty skryptowe korzystające z tych plików.
