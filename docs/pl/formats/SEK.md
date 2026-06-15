# Format SEK — fizyka 3D

!!! warning "W opracowaniu"
    Format `.SEK` jest złożony i wciąż analizowany. Poniższy opis odpowiada parserowi `SEKLoader` (wersja `004`) i zawiera pola, których przeznaczenie pozostaje **nieustalone** (oznaczone jako „nieznane"). Traktuj go jako roboczą rekonstrukcję, nie pełną specyfikację.

Plik `.SEK` opisuje świat fizyczny obiektu [`WORLD`](../reference/WORLD.md): obiekty z geometrią kolizyjną oraz punkty i ścieżki do wyznaczania tras. Liczby są **little-endian**.

## Nagłówek

| Pole | Typ | Opis |
|---|---|---|
| magic | `char[16]` | `SEKAI81080701915` |
| wersja | `char[3]` | np. `004` (obsługiwana); `002`/`003` nieobsługiwane |
| liczba encji | `int32` | ile bloków encji następuje |

## Encje

Każda encja zaczyna się od nagłówka:

| Pole | Typ | Opis |
|---|---|---|
| typ encji | `int32` | `1` = obiekt sceny, `4` = punkty trasy; inne pomijane |
| długość | `int32` | rozmiar danych encji (pozwala pominąć nieznane typy) |

### Nagłówek obiektu (wspólny dla typów 1 i 4)

| Pole | Typ | Opis |
|---|---|---|
| id encji | `int32` | identyfikator |
| flagi | `int32` | `3` = obiekt ruchomy, pozycja czytana z pliku; inaczej statyczny w `(0,0,0)` |
| pozycja X/Y/Z | `float × 3` | współrzędne |
| nieznane | `float` | `0` lub `1` |
| rotacja Z | `float` | nietypowe mapowanie kąta |
| nieznane | `float` | zwykle `0` |
| wymiary ciała X/Y/Z | `float × 3` | promień/długość (walec, kula) lub `lx`/`ly`/`lz` (prostopadłościan) |
| liczba własności | `int32` | ile par nazwa–wartość |
| własności | — | po jednej parze: `string` nazwa, `4 B` padding, `string` wartość |

Własność o nazwie `entityDef` zawiera blok tekstowy (linie rozdzielone `\r\n`) z parametrami fizycznymi w składni `parametr(wartości);`: `geomType`, `mass`, `mu`, `friction`, `bounce`, `bounceVel`, `maxVel`, `limit`.

### Typ 1 — obiekt sceny

Po wspólnym nagłówku:

| Pole | Typ | Opis |
|---|---|---|
| typ geometrii | `int32` | |
| liczba trójkątów | `int32` | |
| trójkąty | — | po jednym bloku na trójkąt (patrz niżej) |

Każdy trójkąt: `string` materiał, a następnie **3 wierzchołki** (każdy: pozycja `float × 3`, normalna `float × 3`, współrzędne `u`, `v`), na końcu 3 nieznane `float`.

### Typ 4 — punkty trasy

Po wspólnym nagłówku:

| Pole | Typ | Opis |
|---|---|---|
| liczba punktów | `int32` | |
| liczba ścieżek | `int32` | |
| punkty | — | po jednym: `float × 3` (X/Y/Z) + `4 B` padding |
| ścieżki | — | po jednej: `int32` pierwszy punkt, `int32` drugi punkt, `int32` nieznane (w testach zawsze `3`) |

Z punktów i ścieżek budowany jest graf, na którym działa wyznaczanie tras ([A*](../reference/WORLD.md)).

## Zobacz też

- [`WORLD`](../reference/WORLD.md) — obiekt skryptowy oparty na `.SEK`.
- [Format INE](INE.md) — pokrewny format dla silnika fizyki 2D Inertia.
