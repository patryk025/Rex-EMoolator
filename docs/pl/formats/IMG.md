# Format IMG — obrazy

Plik `.IMG` przechowuje pojedynczą bitmapę hi-color z opcjonalnym kanałem alfa. To format obrazów [`IMAGE`](../reference/IMAGE.md) oraz teł scen. Wszystkie liczby są **little-endian**. Układ odpowiada parserowi `ImageLoader`.

## Struktura pliku

```mermaid
flowchart LR
    A["Sygnatura PIK\0"] --> B[Nagłówek 36 B]
    B --> C["Dane koloru<br/>(rozmiar danych obrazka)"]
    C --> D["Dane alfa<br/>(rozmiar danych alfa, opcjonalne)"]
```

## Nagłówek

Sygnatura `PIK\0` (4 bajty), a po niej blok 36 bajtów:

| Offset | Pole | Typ | Opis |
|---:|---|---|---|
| 0 | magic | `char[4]` | `50 49 4B 00` (`PIK\0`) |
| 4 | szerokość | `uint32` | w pikselach |
| 8 | wysokość | `uint32` | w pikselach |
| 12 | głębia kolorów | `uint32` | zwykle `16` (RGB565) |
| 16 | rozmiar danych obrazka | `uint32` | długość bloku koloru |
| 20 | — | 4 B | nieużywane / padding |
| 24 | typ kompresji | `uint32` | patrz tabela |
| 28 | rozmiar danych alfa | `uint32` | `0` = brak kanału alfa |
| 32 | offset X | `uint32` | pozycja startowa obrazu |
| 36 | offset Y | `uint32` | pozycja startowa obrazu |

Po nagłówku: blok danych koloru (`rozmiar danych obrazka` bajtów), a jeśli `rozmiar danych alfa > 0` — blok danych alfa.

!!! note "Offset to pozycja startowa"
    Inaczej niż w [animacji](ANN.md), gdzie offsety działają per-klatka, w `IMG` offset z nagłówka staje się **bezwzględną pozycją startową** obrazu na kanwie (`ImageLoader` ustawia z niego `posX`/`posY`). Patrz [współrzędne](../internals/rendering.md#uklad-wspolrzednych-i-odbicie-osi-y).

## Typy kompresji

| Wartość | Kompresja |
|---:|---|
| `0` | brak |
| `2` | CLZW2 |
| `4` | brak (traktowane jak `0`) |
| `5` | JPEG / CLZW2 |

!!! tip "Kwirk: `4` znaczy `0`"
    `ImageLoader` normalizuje typ kompresji `4` do `0` — czyli oba oznaczają dane nieskompresowane. To utrwalona w kodzie zgodność z plikami oryginału.

## Zobacz też

- [`IMAGE`](../reference/IMAGE.md) — obiekt skryptowy oparty na `.IMG`.
- [Format ANN](ANN.md) — animacje używają tego samego kodowania pikseli.
- [Renderowanie](../internals/rendering.md) — co dzieje się z bitmapą po dekodowaniu.
