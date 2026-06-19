# PATTERN

Plansza złożona z siatki "kafelków" o określonych wymiarach i wielowarstwowej strukturze. Wykorzystywana m.in. w etapach z lataniem na Smokręcie (*Reksio i Wehikuł Czasu*) oraz na miotle (*Reksio i Czarodzieje*) — prawdopodobnie do zapisu mapy przewijającego się poziomu. Analiza tego typu jest w toku.

## Pola

### GRIDX

```
INTEGER GRIDX
```

Szerokość pojedynczego kafelka siatki w pikselach.

### GRIDY

```
INTEGER GRIDY
```

Wysokość pojedynczego kafelka siatki w pikselach.

### HEIGHT

```
INTEGER HEIGHT
```

Wysokość planszy.

### LAYERS

```
INTEGER LAYERS
```

Liczba warstw planszy.

### PRIORITY

```
INTEGER PRIORITY
```

Priorytet rysowania planszy (pozycja w osi Z).

### TOCANVAS

```
BOOL TOCANVAS
```

Określa, czy plansza ma być rysowana na kanwie.

### VISIBLE

```
BOOL VISIBLE
```

Określa, czy plansza jest widoczna.

### WIDTH

```
INTEGER WIDTH
```

Szerokość planszy.

## Metody

### ADD

```
void ADD(STRING tileId, INTEGER posX, INTEGER posY, STRING animoName, [INTEGER layer])
```

Dodaje na planszę obiekt graficzny.

**Parametry**

- `tileId` — identyfikator kafelka.
- `posX`, `posY` — koordynaty kafelka.
- `animoName` — nazwa zmiennej [`ANIMO`](ANIMO.md) wyświetlanej na kafelku.
- `layer` — (opcjonalnie) numer warstwy.

**Przykłady**

```
PATBOARD^ADD(["E"+_I_],VARX,VARY,VARSTRING0,0);
PATTERNFOREST^ADD([VARSTRING0+"_"+_I_],VARINT1,VARINT2,VARSTRING1,VARINT3);
```

### GETGRAPHICSAT

```
STRING GETGRAPHICSAT(INTEGER posX, INTEGER posY, BOOL onlyVisible, BOOL ignoreAlpha, INTEGER minZ, [INTEGER maxZ])
```

Zwraca nazwę kafelka pod punktem `(posX, posY)`. Zachowuje się analogicznie do [`CANVAS_OBSERVER.GETGRAPHICSAT`](CANVAS_OBSERVER.md#getgraphicsat), z tą różnicą, że przeszukuje wyłącznie kafelki tej planszy.

**Parametry**

- `posX`, `posY` — koordynaty sprawdzanego punktu.
- `onlyVisible` — gdy `TRUE`, brane są pod uwagę tylko widoczne kafelki.
- `ignoreAlpha` — gdy `TRUE`, sprawdzany jest tylko prostokąt; gdy `FALSE`, test uwzględnia kanał alfa piksela.
- `minZ` — minimalna warstwa.
- `maxZ` — (opcjonalnie) maksymalna warstwa.

**Zwraca**: [`STRING`](STRING.md) — nazwa kafelka lub `"NULL"`.

**Przykłady**

```
PATTERNMASKKRET^GETGRAPHICSAT([ANNKRET^GETCENTERX()+20],ANNKRET^GETCENTERY(),TRUE,FALSE,0,1);
PATTERNFIRE^GETGRAPHICSAT(ANNCOLLUP^GETCENTERX(),ANNCOLLUP^GETCENTERY(),FALSE,FALSE,0);
```

### MOVE

```
void MOVE(INTEGER offsetX, INTEGER offsetY)
```

Przesuwa planszę o zadane wartości w osiach X i Y.

**Parametry**

- `offsetX`, `offsetY` — wektor przesunięcia.

**Przykłady**

```
PATBOARD^MOVE(0,-3000);
PATTERNBKG^MOVE(VARINT0,0);
```

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
