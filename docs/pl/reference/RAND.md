# RAND

Wbudowany generator liczb pseudolosowych. Dostępny pod globalną nazwą `RAND` z dowolnego kontekstu, również pod aliasem `RANDOM` (zobacz [Obiekty wbudowane](../engine/globals.md#obiekty-wbudowane)).

## Metody

### GET

```
INTEGER GET(INTEGER range)
INTEGER GET(INTEGER offset, INTEGER range)
```

Zwraca liczbę pseudolosową.

- Wariant z jednym argumentem zwraca liczbę z przedziału `[0, range)`.
- Wariant z dwoma argumentami zwraca liczbę z przedziału `[offset, offset + range)`.

Jeżeli `range` jest mniejsze lub równe `0`, zwracany jest `offset` (lub `0` w wariancie jednoargumentowym).

**Parametry**

- `offset` — początek przedziału (włącznie), domyślnie `0`.
- `range` — rozmiar przedziału (wartość górna jest wyłączona).

**Zwraca**: wylosowaną liczbę.

**Przykłady**

```
RANDOM^GET(100);
RANDOM^GET(VARI_TMP3);
RANDOM^GET(0, 3);
```

**Kompatybilność:** `GET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPLENTY

```
void GETPLENTY(STRING arrayName, INTEGER count, INTEGER offset, INTEGER range, BOOL onlyUnique)
```

Generuje `count` liczb pseudolosowych z przedziału `[offset, offset + range)` i dołącza je do tablicy o nazwie `arrayName`. Tablica nie jest czyszczona przed dodaniem.

Jeżeli `onlyUnique` jest `TRUE`, kolejne wylosowane wartości muszą być różne od siebie; jeżeli wymagana liczba unikalnych wartości przekracza rozmiar przedziału (`count > range`), metoda nie modyfikuje tablicy. Dla `count` mniejszego od `1` metoda również nie ma efektu.

**Parametry**

- `arrayName` — nazwa docelowej zmiennej typu [`ARRAY`](ARRAY.md).
- `count` — liczba elementów do wygenerowania.
- `offset` — początek przedziału (włącznie).
- `range` — rozmiar przedziału (wartość górna jest wyłączona).
- `onlyUnique` — `TRUE`, aby wymusić unikalność wygenerowanych wartości.

**Kompatybilność:** `GETPLENTY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.
