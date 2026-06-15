# IMAGE

Statyczny obraz wyświetlany na scenie. Obsługuje pozycję, przezroczystość, przycinanie obszaru rysowania, dynamiczne ładowanie pliku oraz monitorowanie kolizji z innymi obiektami.

## Pola

### FILENAME

```
STRING FILENAME
```

Nazwa pliku `.IMG` z obrazem. Pole odczytywane przy inicjalizacji zmiennej; może być również nadpisane przez metodę [`LOAD`](#load).

### MONITORCOLLISION

```
BOOL MONITORCOLLISION
```

Określa, czy obraz uczestniczy w detekcji kolizji z innymi obiektami. Modyfikowane przez metody [`MONITORCOLLISION`](#monitorcollision-1) i [`REMOVEMONITORCOLLISION`](#removemonitorcollision).

### MONITORCOLLISIONALPHA

```
BOOL MONITORCOLLISIONALPHA
```

Określa, czy w detekcji kolizji uwzględniany jest kanał przezroczystości obrazu — kolizja nie zachodzi w pikselach całkowicie przezroczystych.

### PRIORITY

```
INTEGER PRIORITY
```

Priorytet renderowania (`Z`) względem innych obiektów na scenie.

### VISIBLE

```
BOOL VISIBLE
```

Widoczność obrazu. Modyfikowana metodami [`SHOW`](#show) i [`HIDE`](#hide).

## Metody

### GETALPHA

```
INTEGER GETALPHA(INTEGER posX, INTEGER posY)
```

Zwraca wartość kanału alfa piksela o podanych współrzędnych (w skali `0–255`, gdzie `255` to pełna nieprzezroczystość).

**Parametry**

- `posX` — współrzędna X piksela.
- `posY` — współrzędna Y piksela.

**Zwraca**: wartość alfa piksela.

**Przykłady**

```
IMGLEVEL^GETALPHA(VARX, VARY);
IMGALPHA^GETALPHA(EXPMASKX, EXPMASKY);
```

### GETCENTERX

```
INTEGER GETCENTERX()
```

Zwraca współrzędną X środka prostokąta zajmowanego przez obraz.

**Zwraca**: środek X.

### GETCENTERY

```
INTEGER GETCENTERY()
```

Zwraca współrzędną Y środka prostokąta zajmowanego przez obraz.

**Zwraca**: środek Y.

### GETHEIGHT

```
INTEGER GETHEIGHT()
```

Zwraca wysokość obrazu w pikselach.

**Zwraca**: wysokość obrazu.

**Przykłady**

```
IMGLINA^GETHEIGHT();
```

### GETPIXEL

```
INTEGER GETPIXEL(INTEGER posX, INTEGER posY)
```

Zwraca wartość piksela na podanych współrzędnych jako liczbę całkowitą zakodowaną zgodnie z głębią koloru obrazu: dla 16-bitowych obrazów — RGB565, dla 15-bitowych — RGB555.

**Parametry**

- `posX` — współrzędna X piksela.
- `posY` — współrzędna Y piksela.

**Zwraca**: zakodowana wartość koloru piksela.

**Przykłady**

```
IMGMASK^GETPIXEL(IKONNEWX, IKONNEWY);
```

### GETPOSITIONX

```
INTEGER GETPOSITIONX()
```

Zwraca współrzędną X lewego górnego rogu obrazu.

**Zwraca**: pozycja X.

### GETPOSITIONY

```
INTEGER GETPOSITIONY()
```

Zwraca współrzędną Y lewego górnego rogu obrazu.

**Zwraca**: pozycja Y.

### GETWIDTH

```
INTEGER GETWIDTH()
```

Zwraca szerokość obrazu w pikselach.

**Zwraca**: szerokość obrazu.

**Przykłady**

```
IMGPASEK^GETWIDTH();
```

### HIDE

```
void HIDE()
```

Ukrywa obraz (ustawia [`VISIBLE`](#visible) na `FALSE`).

**Przykłady**

```
G_IMGPAGE^HIDE();
```

### INVALIDATE

```
void INVALIDATE()
```

Stosuje oczekującą wartość przezroczystości ustawioną metodą [`SETOPACITY`](#setopacity). Bez wywołania `INVALIDATE` zmiana przezroczystości nie staje się widoczna.

**Przykłady**

```
G_IMGPAGE^INVALIDATE();
```

### ISAT

```
BOOL ISAT(INTEGER posX, INTEGER posY)
```

Sprawdza, czy punkt o podanych współrzędnych znajduje się wewnątrz prostokąta zajmowanego przez obraz.

**Parametry**

- `posX` — współrzędna X punktu.
- `posY` — współrzędna Y punktu.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli punkt jest wewnątrz prostokąta obrazu.

### LOAD

```
void LOAD(STRING path)
```

Wczytuje obraz z pliku, zastępując dotychczasową zawartość. Obraz po wczytaniu jest automatycznie pokazywany ([`VISIBLE`](#visible) ustawiane na `TRUE`).

**Parametry**

- `path` — ścieżka pliku `.IMG` w VFS gry.

**Przykłady**

```
G_IMGPAGE^LOAD("$COMMON\PAGE.IMG");
IMGTLO^LOAD(VARSTEMP0);
IMGSCR^LOAD(["$COMMON\SAVE_BD\BD_SCR"+VARIACTIVESLOT+".IMG"]);
```

### MERGEALPHA

```
void MERGEALPHA(INTEGER posX, INTEGER posY, STRING maskName)
```

Wiąże z obrazem maskę alfa pochodzącą z innego obrazu. Maska jest pozycjonowana w punkcie `(posX, posY)` i modyfikuje wynikową przezroczystość bieżącego obrazu w obszarze pokrycia.

**Parametry**

- `posX` — współrzędna X pozycji maski.
- `posY` — współrzędna Y pozycji maski.
- `maskName` — nazwa zmiennej typu `IMAGE`, której kanał alfa zostanie użyty jako maska.

**Przykłady**

```
IMGDARK^MERGEALPHA([ANNPLAYER0^GETCENTERX()-75], [ANNPLAYER0^GETCENTERY()-75], "IMGKOLKO");
IMG_WODA^MERGEALPHA(800, VARI_Y, "IMG_LIGHT");
```

### MONITORCOLLISION {#monitorcollision-1}

```
void MONITORCOLLISION()
```

Włącza monitorowanie kolizji obrazu z innymi obiektami. Po wywołaniu pole [`MONITORCOLLISION`](#monitorcollision) ma wartość `TRUE`, a obraz jest rejestrowany w mechanizmie detekcji kolizji.

### MONITORCOLLISIONALPHA {#monitorcollisionalpha-1}

```
void MONITORCOLLISIONALPHA()
```

Włącza uwzględnianie kanału alfa przy detekcji kolizji. Po wywołaniu pole [`MONITORCOLLISIONALPHA`](#monitorcollisionalpha) ma wartość `TRUE`.

### MOVE

```
void MOVE(INTEGER offsetX, INTEGER offsetY)
```

Przesuwa obraz o zadane wartości względem aktualnej pozycji.

**Parametry**

- `offsetX` — przesunięcie w osi X.
- `offsetY` — przesunięcie w osi Y.

**Przykłady**

```
IMGBKGA^MOVE(0, 800);
IMGLINA^MOVE(0, IMOVDY);
IMRECT^MOVE(IGSX, 0);
```

### REMOVEMONITORCOLLISION

```
void REMOVEMONITORCOLLISION()
```

Wyłącza monitorowanie kolizji włączone wcześniej przez [`MONITORCOLLISION`](#monitorcollision-1).

### REMOVEMONITORCOLLISIONALPHA

```
void REMOVEMONITORCOLLISIONALPHA()
```

Wyłącza uwzględnianie kanału alfa przy detekcji kolizji, włączone wcześniej przez [`MONITORCOLLISIONALPHA`](#monitorcollisionalpha-1).

### SETCLIPPING

```
void SETCLIPPING(INTEGER xLeft, INTEGER yBottom, INTEGER xRight, INTEGER yTop)
```

Ogranicza obszar rysowania obrazu do prostokąta o podanych krawędziach. Piksele poza prostokątem nie są rysowane.

**Parametry**

- `xLeft, yBottom, xRight, yTop` — współrzędne prostokąta przycinania.

**Przykłady**

```
G_IMGPAGE^SETCLIPPING(0, 0, G_IPAGE, 600);
```

### SETOPACITY

```
void SETOPACITY(INTEGER opacity)
```

Ustawia oczekującą wartość przezroczystości obrazu w skali `0–255` (`0` — pełna przezroczystość, `255` — pełna nieprzezroczystość). Wartości spoza zakresu są przycinane do jego granic. **Zmiana nie staje się widoczna, dopóki nie zostanie wywołana metoda [`INVALIDATE`](#invalidate).**

**Parametry**

- `opacity` — wartość kanału alfa (`0–255`).

**Przykłady**

```
AIDEMMEDIA^SETOPACITY(VARNR);
IMGBRIDGE^SETOPACITY(200);
```

### SETPOSITION

```
void SETPOSITION(INTEGER posX, INTEGER posY)
```

Ustawia bezwzględną pozycję lewego górnego rogu obrazu.

**Parametry**

- `posX` — współrzędna X.
- `posY` — współrzędna Y.

**Przykłady**

```
IMGENERGIA^SETPOSITION([795-VARENERGIA], 78);
IMGKOLKO^SETPOSITION(-500, -500);
IMGNAKLADKA^SETPOSITION(VARIPOSX, 0);
```

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Ustawia priorytet renderowania obrazu.

**Parametry**

- `priority` — nowa wartość pola [`PRIORITY`](#priority).

**Przykłady**

```
G_IMGPAGE^SETPRIORITY(3000);
AIDEMMEDIA^SETPRIORITY(3);
```

### SHOW

```
void SHOW()
```

Pokazuje obraz (ustawia [`VISIBLE`](#visible) na `TRUE`).

**Przykłady**

```
G_IMGPAGE^SHOW();
REX^SHOW();
```

## Sygnały

### ONCLICK

Wywoływany po kliknięciu w obraz.

### ONFOCUSON

Wywoływany po najechaniu kursorem na obraz.

### ONFOCUSOFF

Wywoływany po zjechaniu kursorem z obrazu.

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.
