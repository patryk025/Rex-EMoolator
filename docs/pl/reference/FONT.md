# FONT

Definicja czcionki bitmapowej. Obiekt nie udostępnia metod skryptowych ani sygnałów — jest używany przez typ [`TEXT`](TEXT.md) jako źródło tekstur znaków.

## Pola

### DEF

```
STRING DEF_<nazwa>_<styl>_<rozmiar>
```

Pole definiujące plik czcionki w formacie [`.FNT`](../formats/FNT.md). Nazwa
pola koduje metadane konkretnego wariantu czcionki: rodzinę, styl i rozmiar.
Te informacje nie są zapisane wewnątrz pliku FNT.

Format zapisu w skrypcie:

```
FONT:DEF_<nazwa>_<styl>_<rozmiar>=<plik>.FNT
```

**Przykład**

```
FONT:DEF_ARIAL_STANDARD_14=ARIAL14.FNT
```

Jeden obiekt `FONT` może zawierać wiele pól `DEF_*`. Piklib tworzy z nich
kolekcję wariantów, a pierwszy wariant jest fontem bazowym. Kody formatowania
w tekście mogą wybierać m.in. style `STANDARD`, `ITALIC`, `BOLD`, `LSTRIKE`,
`USTRIKE` i `UNDERLINE`, jeżeli odpowiedni wariant istnieje w kolekcji.
