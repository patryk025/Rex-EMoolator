# MULTIARRAY

Tablica wielowymiarowa, indeksowana od `0`. Domyślnie tworzona jako tablica dwuwymiarowa o wymiarach `16 × 16`; liczba wymiarów może być zmieniona w skrypcie polem `DIMENSIONS`. Każdy wymiar rozszerza się automatycznie (podwajając rozmiar) przy próbie zapisu do pozycji wykraczającej poza bieżący zakres.

## Pola

### DIMENSIONS

```
INTEGER DIMENSIONS
```

Liczba wymiarów tablicy. Pole odczytywane podczas inicjalizacji zmiennej; domyślnie `2`. Każdy wymiar jest tworzony z początkowym rozmiarem `16`.

## Metody

### GET

```
mixed GET(INTEGER index1, [INTEGER index2, ..., INTEGER indexN])
```

Zwraca wartość z komórki o podanych współrzędnych. Liczba argumentów musi być równa liczbie wymiarów zadeklarowanej polem `DIMENSIONS`. Dla komórki, do której nie zapisano wartości, lub gdy współrzędne są poza zakresem, zwracany jest `NULL`.

**Parametry**

- `index1, …, indexN` — współrzędne komórki (numerowane od `0`), po jednej na każdy wymiar.

**Zwraca**: wartość komórki lub `NULL`.

**Przykłady**

```
ARRMAPA^GET(IKRETMOVEONMAPAX, IKRETPOSMAPAY);
ARRMAPA^GET([IPLAYERPOSX-1], IPLAYERPOSY);
ARRMAPA^GET(_I_, I1);
```

### SET

```
void SET(INTEGER index1, [INTEGER index2, ..., INTEGER indexN], mixed value)
```

Zapisuje wartość w komórce o podanych współrzędnych. Liczba argumentów musi być równa liczbie wymiarów + 1; ostatni argument to zapisywana wartość. Jeżeli którakolwiek współrzędna wykracza poza bieżący zakres wymiaru, tablica jest automatycznie powiększana (rozmiar wymiaru jest podwajany aż do objęcia współrzędnej).

**Parametry**

- `index1, …, indexN` — współrzędne komórki.
- `value` — zapisywana wartość.

**Przykłady**

```
ARRMAPA^SET(ITMPX, ITMPY, 0);
ARRMAPA^SET(IX, IY, SPOLE);
ARRMAPA^SET(IPLAYERGOONX, IPLAYERGOONY, "PLAYER");
ARRMAPA^SET([IPLAYERPOSX+ILASTDIRX], [IPLAYERPOSY+ILASTDIRY], IPLAYER);
```

### GETSIZE

```
INTEGER GETSIZE(INTEGER dimension)
```

Zwraca rozmiar podanego wymiaru tablicy.

**Parametry**

- `dimension` — indeks wymiaru (numerowany od `0`).

**Zwraca**: rozmiar wymiaru lub `0` dla nieprawidłowego indeksu.

### LOAD

```
void LOAD(STRING path)
```

Zastępuje zawartość tablicy danymi wczytanymi z pliku binarnego. Format obejmuje wymiary tablicy oraz wartości komórek.

**Parametry**

- `path` — ścieżka pliku w VFS gry.

### SAVE

```
void SAVE(STRING path)
```

Zapisuje zawartość tablicy do pliku binarnego.

**Parametry**

- `path` — ścieżka docelowego pliku w VFS gry.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji zmiennej; w tym momencie odczytywana jest wartość pola `DIMENSIONS` i alokowane są wymiary tablicy.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
