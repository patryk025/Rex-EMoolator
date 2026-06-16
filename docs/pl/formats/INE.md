# Format INE — fizyka Inertia

!!! note "Rozpracowany, ale bez loadera"
    Poniższa struktura to kompletna rekonstrukcja formatu `.INE` — rozpoznana pełniej niż [`.SEK`](SEK.md). W odróżnieniu od pozostałych stron formatów **nie** jest jednak uzgodniona z kodem parsera: emulator na ten moment nie zawiera loadera `INE`. Liczby są **little-endian**.

Plik `.INE` przechowuje dane dla wbudowanego silnika fizyki 2D **Inertia**, użytego w grze *Reksio i Kretes w Akcji* ([`INERTIA`](../reference/INERTIA.md)). To płaska lista **ciał** fizycznych — każde to kształt kolizyjny z materiałem i umiejscowieniem na scenie.

## Nagłówek

| Pole | Typ | Opis |
|---|---|---|
| magic | `char[4]` | `INE1` |
| count | `uint32` | liczba bloków ciał, które następują |

## Ciała

Każde ciało zaczyna się od wspólnego nagłówka:

| Pole | Typ | Opis |
|---|---|---|
| id obiektu | `int32` | powiązany obiekt sceny; `-1` = przydzielany automatycznie |
| typ ciała | `int32` | rodzaj kształtu (patrz [Typy ciał](#typy-cial)) |
| nieużywane | `uint32` | czytane, ale ignorowane |
| grupa / materiał | `int32` | materiał kolizyjny (patrz [Materiały](#materialy)) |
| X | `float` | pozycja w pikselach; w świecie `X = (X − 400) × 0.01` (100 px na metr) |
| Y | `float` | pozycja w pikselach; w świecie `Y = (300 − Y) × 0.01` (odwrócona oś Y, 100 px na metr) |
| kąt | `float` | obrót (dokładne mapowanie niepotwierdzone) |

To, co następuje po nagłówku, zależy od typu ciała: bryły pełne niosą masę i wymiary, a ciała typu hull/level — tablicę wierzchołków.

### Typy ciał {#typy-cial}

| Wartość | Nazwa | Kształt |
|---|---|---|
| 1 | `CBodyBox` | prostopadłościan |
| 2 | `CBodySphere` | kula |
| 3 | `CBodyCone` | stożek |
| 4 | `CBodyCCylinder` | walec zakończony czaszami (kapsuła) |
| 5 | `CBodyCylinder` | walec |
| 6 | `CBodyHull` | otoczka wypukła wyciągnięta z łamanej |
| 7 | `CBodyLevel` | statyczna geometria poziomu wyciągnięta z łamanej |

### Materiały {#materialy}

| Wartość | Nazwa | Uwagi |
|---|---|---|
| 0 | `DEFAULT` | |
| 1 | `BOUNCY` | |
| 2 | `RIGID` | |
| 5 | `TRIGGER` | dedykowane callbacki |
| 6 | `GHOST` | |
| 7 | `REACTIVE` | mieszane callbacki |

### Parametry kształtu

Po nagłówku bryły pełne zapisują masę i wymiary. To, które pola występują, zależy od typu ciała:

| Typ ciała | `mass` | `dim1` | `dim2` | `dim3` |
|---|:--:|:--:|:--:|:--:|
| `CBodyBox` (1) | ✓ | ✓ | ✓ | ✓ |
| `CBodySphere` (2) | ✓ | ✓ | ✓ | ✓ |
| `CBodyCone` (3) | ✓ | ✓ | ✓ | |
| `CBodyCCylinder` (4) | ✓ | ✓ | ✓ | |
| `CBodyCylinder` (5) | ✓ | ✓ | ✓ | |
| `CBodyHull` (6) | ✓ | | | |
| `CBodyLevel` (7) | | | | |

Każde obecne pole to `float`. W skrócie: `CBodyLevel` nie ma ani masy, ani wymiarów, `CBodyHull` ma tylko masę, rodzina stożek/walec dokłada dwa wymiary, a prostopadłościan/kula trzeci.

### Tablica wierzchołków (hull i level)

Dla `CBodyHull` (6) i `CBodyLevel` (7) po nagłówku — zamiast wymiarów — następuje łamana:

| Pole | Typ | Opis |
|---|---|---|
| liczba wierzchołków | `uint32` | liczba punktów |
| wierzchołki | `Point2D[]` | każdy: `float` x, `float` y (w pikselach) |

Łamana jest **wyciągana** (extrude) w ściany kolizyjne — na głębokość **10 m** dla `CBodyHull` i **2 m** dla `CBodyLevel`. Wierzchołki są później podnoszone do `vec4(x, y, 0, 1)`.

## Zobacz też

- [`INERTIA`](../reference/INERTIA.md) — obiekt skryptowy oparty na `.INE`.
- [Format SEK](SEK.md) — analogiczny format dla fizyki 3D ([`WORLD`](../reference/WORLD.md)).
