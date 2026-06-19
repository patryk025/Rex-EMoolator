# FONT

Definicja czcionki bitmapowej. Obiekt nie udostępnia metod skryptowych ani sygnałów — jest używany przez typ [`TEXT`](TEXT.md) jako źródło tekstur znaków.

## Pola

### DEF

```
STRING DEF_<nazwa>_<styl>_<rozmiar>
```

Pole definiujące plik czcionki w formacie `.FNT`. Nazwa pola koduje metadane konkretnego wariantu czcionki: jej nazwę, styl i rozmiar.

Format zapisu w skrypcie:

```
FONT:DEF_<nazwa>_<styl>_<rozmiar>=<plik>.FNT
```

**Przykład**

```
FONT:DEF_ARIAL_STANDARD_14=ARIAL14.FNT
```
