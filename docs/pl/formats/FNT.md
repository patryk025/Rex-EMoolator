# Format FNT — czcionki

Plik `.FNT` przechowuje bitmapową czcionkę: tabelę znaków, metryki,
macierz korekt par oraz jeden wspólny atlas koloru i alfy. To format używany
przez obiekty [`FONT`](../reference/FONT.md) i [`TEXT`](../reference/TEXT.md).
Wszystkie liczby wielobajtowe są zapisane jako little-endian.

## Układ pliku
|       Nazwa sekcji       |  Długość sekcji  |
|:------------------------:|:----------------:|
|         nagłówek         |       20 B       |
|  identyfikatory znaków   |       N B        |
|    macierz korekt par    |      N*N B       |
|     lewe przycięcia      |       N B        |
|     prawe przycięcia     |       N B        |
|   atlas RGB555/RGB565    |     W*H*2 B      |
|        atlas alfa        |      W*H B       |

Całkowity rozmiar pliku wynosi:

```text
20 + N*N + 3*N + 3*W*H
```

## Nagłówek

|  Offset | Pole          | Typ       | Znaczenie                           |
|--------:|---------------|-----------|-------------------------------------|
|  `0x00` | `magic`       | `char[4]` | `46 4E 54 00`, czyli `FNT\0`        |
|  `0x04` | `atlasWidth`  | `uint32`  | szerokość całego atlasu `W`         |
|  `0x08` | `atlasHeight` | `uint32`  | wysokość atlasu i wiersza glifu `H` |
|  `0x0C` | `pixelFormat` | `uint32`  | `15` = RGB555, `16` = RGB565        |
|  `0x10` | `glyphCount`  | `uint32`  | liczba glifów `N`                   |

Szerokość jednakowej komórki atlasu jest wyliczana:

```text
cellWidth = atlasWidth / glyphCount
```

Szerokość atlasu musi być podzielna przez liczbę glifów.

## Znaki i kodowanie

Każdy glif ma jednobajtowy identyfikator. FNT nie zapisuje nazwy kodowania;
musi ono odpowiadać kodowaniu tekstów gry. `arial14.fnt` używa Windows-1250.

Silnik ma kilka przypadków specjalnych:

- spacja nie jest pobierana z atlasu i ma szerokość małego `l`;
- `~` nie jest rysowane i ma szerokość 1 px;
- NUL kończy tekst;
- nieznany znak nie jest rysowany, lecz pętla tekstu nadal dodaje odstęp 2 px.

## Przycięcia i region glifu

Lewe i prawe przycięcia są dwoma osobnymi blokami po `N` bajtów:

```text
cellStart(i) = i * cellWidth
sourceX(i)   = cellStart(i) + leftTrim[i]
inkWidth(i)  = cellWidth - leftTrim[i] - rightTrim[i]
```

Renderer wycina z atlasu tylko obszar od `sourceX` o szerokości `inkWidth`.

## Macierz korekt par

Macierz ma `N*N` elementów typu **signed int8** i jest zapisana wierszami:

```text
K(previous, current) = matrix[previousIndex * N + currentIndex]
```

Dla bieżącego glifu:

```text
drawX   = penX - K(previous, current)
advance = inkWidth(current) - K(previous, current) + 2
```

Dodatnia korekta przesuwa glif w lewo i skraca przesunięcie pióra, a ujemna
przesuwa go w prawo i zwiększa przesunięcie. Dla pierwszego znaku albo
nieznanego poprzednika używane jest `K=0`.

Generator z `Piklib8.dll` ma błąd i praktycznie generuje samo `+1`;
`arial14.fnt` ma `118*118` takich wartości. Nowszy `BlooMooDLL.dll` naprawia
obliczanie zależne od obu glifów, bez zmiany formatu pliku.

## Kolor i alfa

Atlas koloru zawiera jeden `uint16` na piksel:

- `pixelFormat=15`: RGB555;
- `pixelFormat=16`: RGB565.

Po nim znajduje się ośmiobitowy atlas alfa o tych samych wymiarach. Wartość
`0` jest przezroczysta, `255` nieprzezroczysta, a wartości pośrednie są
blendowane. Oba obrazy są zapisane wierszami dla całego atlasu.

RGB zapisane w pliku nie musi być kolorem widocznym na ekranie.
`CSimpleFont6` i `CText6` domyślnie używają `0xFFFF` (bieli), a
`CSimpleFont6::setColor` zastępuje każdą 16-bitową wartość w atlasie koloru,
nie zmieniając alfy. Przykładowo `arial14.fnt` przechowuje czarne RGB glifów,
ale jest standardowo renderowany jako biała maska alfa. Rex-EMoolator stosuje
takie samo domyślne kolorowanie na biało podczas ładowania FNT.

## Przykład `arial14.fnt`

| Pole              |        Wartość |
|-------------------|---------------:|
| atlas             | `2124 × 22 px` |
| format            |  `16` (RGB565) |
| glify             |          `118` |
| szerokość komórki |        `18 px` |
| rozmiar pliku     |     `154482 B` |

## Zobacz też

- [`FONT`](../reference/FONT.md) — kolekcja wariantów `.FNT`;
- [`TEXT`](../reference/TEXT.md) — układ i wyświetlanie tekstu.
