# CANVAS_OBSERVER

Punkt dostępu do operacji na kanwie — wspólnym obszarze rysowania, na którym silnik wyświetla wszystkie widoczne obiekty graficzne. Udostępnia metody do dodawania i usuwania grafik z ekranu, pobierania nazwy obiektu pod kursorem, ustawiania tła, robienia zrzutów ekranu oraz emitowania sygnałów przy zmianie fokusu okna gry.

Zazwyczaj w scenie istnieje pojedyncza instancja typu `CANVAS_OBSERVER`, do której wszystkie skrypty odwołują się jak do obiektu globalnego.

## Metody

### ADD

```
void ADD(STRING varName)
void ADD(STRING varName, INTEGER priority)
```

Dodaje na kanwę zmienną [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md) — ustawia jej widoczność na `TRUE` i opcjonalnie nadaje priorytet rysowania (domyślnie `1000`).

**Parametry**

- `varName` — nazwa zmiennej graficznej.
- `priority` — (opcjonalnie) priorytet rysowania (pozycja w osi Z).

**Przykłady**

```
CANVASOBSERVER^ADD("ANNMKORBA2");
CANVASOBSERVER^ADD("ANNMPRZEGRYZA");
```

### ENABLENOTIFY

```
void ENABLENOTIFY(BOOL enable)
```

Włącza lub wyłącza emitowanie sygnałów o zmianie fokusu okna gry ([`ONWINDOWFOCUSON`](#onwindowfocuson), [`ONWINDOWFOCUSOFF`](#onwindowfocusoff)).

**Parametry**

- `enable` — `TRUE` włącza notyfikacje, `FALSE` je wyłącza.

**Przykłady**

```
CANVASOBSERVER^ENABLENOTIFY(TRUE);
```

### GETBPP

```
INTEGER GETBPP()
```

Zwraca głębię koloru kanwy w bitach na piksel. Oryginalny silnik BlooMoo działa w trybie 16 bpp (RGB565) — metoda zawsze zwraca `16`.

**Zwraca**: [`INTEGER`](INTEGER.md) — głębia koloru w bitach (`16`).

### GETGRAPHICSAT

```
STRING GETGRAPHICSAT(INTEGER posX, INTEGER posY, BOOL onlyVisible, INTEGER minZ, INTEGER maxZ)
STRING GETGRAPHICSAT(INTEGER posX, INTEGER posY, BOOL onlyVisible, INTEGER minZ, INTEGER maxZ, BOOL ignoreAlpha)
```

Zwraca nazwę zmiennej graficznej znajdującej się pod punktem `(posX, posY)`. Przeszukuje wyłącznie bieżącą scenę. Wyszukiwanie zaczyna od grafik o najwyższym priorytecie. Jeśli żaden obiekt nie spełnia warunków, zwracane jest `"NULL"`.

**Parametry**

- `posX`, `posY` — koordynaty sprawdzanego punktu.
- `onlyVisible` — gdy `TRUE`, brane są pod uwagę tylko widoczne grafiki.
- `minZ`, `maxZ` — zakres priorytetu (osi Z) ograniczający wyszukiwanie.
- `ignoreAlpha` — (opcjonalnie) gdy `TRUE`, sprawdzany jest tylko prostokąt grafiki; gdy `FALSE` (lub pominięte), test uwzględnia kanał alfa piksela.

**Zwraca**: [`STRING`](STRING.md) — nazwa znalezionego obiektu lub `"NULL"`.

**Przykłady**

```
CANVASOBSERVER^GETGRAPHICSAT(MOUSE^GETPOSX(),MOUSE^GETPOSY(),TRUE,2998,2998,FALSE);
CANVASOBSERVER^GETGRAPHICSAT(VARICURSORX,VARICURSORY,TRUE,40,40,TRUE);
```

### GETGRAPHICSAT2

```
STRING GETGRAPHICSAT2(INTEGER posX, INTEGER posY, BOOL onlyVisible, INTEGER minZ, INTEGER maxZ)
STRING GETGRAPHICSAT2(INTEGER posX, INTEGER posY, BOOL onlyVisible, INTEGER minZ, INTEGER maxZ, BOOL ignoreAlpha)
```

Wariant [`GETGRAPHICSAT`](#getgraphicsat) przeszukujący nie tylko bieżącą scenę, ale również nadrzędne kontenery (epizod, root).

### MOVEBKG

```
void MOVEBKG(INTEGER deltaX, INTEGER deltaY)
```

Przesuwa tło o zadane wartości w osiach X i Y (względem aktualnej pozycji).

**Parametry**

- `deltaX`, `deltaY` — wektor przesunięcia w pikselach.

**Przykłady**

```
CANVASOBSERVER^MOVEBKG(0,ARRAYDY^GET(0));
CANVASOBSERVER^MOVEBKG(ISCROLLMOVEX,ISCROLLMOVEY);
```

### PASTE

```
void PASTE(STRING varName, INTEGER posX, INTEGER posY)
```

Wkleja na kanwę kopię bieżącej zawartości grafiki [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md) w postaci statycznej, niemodyfikowalnej tekstury w punkcie `(posX, posY)`. Operacja nie wpływa na samą zmienną źródłową.

**Parametry**

- `varName` — nazwa zmiennej graficznej.
- `posX`, `posY` — pozycja wklejenia.

**Przykłady**

```
CANVASOBSERVER^PASTE("ANNBUM",[I1-IPLANPOSX],[I2-IPLANPOSY]);
CANVASOBSERVER^PASTE("IMG1",0,0);
```

### REDRAW

```
void REDRAW()
```

W oryginalnym silniku oznacza kanwę jako wymagającą ponownego rysowania. W praktyce silnik i tak rysuje całość każdą klatkę, więc metoda zachowuje się jak no-op.

### REFRESH

```
void REFRESH()
```

Wymusza ponowne narysowanie wszystkich obiektów [`IMAGE`](IMAGE.md) w bieżącej scenie — wewnętrznie wywołuje na nich metodę [`INVALIDATE`](IMAGE.md#invalidate).

### REMOVE

```
void REMOVE(STRING varName1, [STRING varName2, ...])
```

Ukrywa wymienione obiekty graficzne na kanwie (ustawia ich widoczność na `FALSE`). Metoda przyjmuje dowolną liczbę argumentów.

**Parametry**

- `varName1, varName2, …` — kolejne nazwy zmiennych graficznych do ukrycia.

**Przykłady**

```
CANVASOBSERVER^REMOVE("ZLY");
CANVASOBSERVER^REMOVE("ANNAUTOR","ANNAUTOL","ANNAUTORMASK","ANNAUTOLMASK");
```

### SAVE

```
void SAVE(STRING imgFileName, DOUBLE xScaleFactor, DOUBLE yScaleFactor)
void SAVE(STRING imgFileName, DOUBLE xScaleFactor, DOUBLE yScaleFactor, INTEGER xLeft, INTEGER yTop, INTEGER xRight, INTEGER yBottom)
```

Zapisuje aktualny widok kanwy do pliku `.IMG`. Forma siedmioargumentowa pozwala wyciąć prostokąt kanwy przed skalowaniem.

**Parametry**

- `imgFileName` — ścieżka docelowego pliku `.IMG`.
- `xScaleFactor`, `yScaleFactor` — współczynniki skalowania w osiach X i Y.
- `xLeft`, `yTop`, `xRight`, `yBottom` — (opcjonalnie) prostokąt do wycięcia przed skalowaniem.

**Przykłady**

```
CANVASOBSERVER^SAVE("$COMMON\PAGE.IMG",1,1);
CANVASOBSERVER^SAVE("$COMMON\ZOOM.IMG",2,2,$1,$2,$3,$4);
CANVASOBSERVER^SAVE(["$COMMON\SAVE_BD\BD_SCR"+VARISLOTNO+".IMG"],0.5,0.5);
```

### SETBACKGROUND

```
void SETBACKGROUND(STRING imageName)
```

Ustawia obraz tła kanwy. Argument może być nazwą istniejącej zmiennej [`IMAGE`](IMAGE.md) albo ścieżką do pliku `.IMG` — w drugim przypadku silnik utworzy ukryty obiekt [`IMAGE`](IMAGE.md) z plikiem.

**Parametry**

- `imageName` — nazwa zmiennej [`IMAGE`](IMAGE.md) lub ścieżka do pliku `.IMG`.

**Przykłady**

```
CANVASOBSERVER^SETBACKGROUND(SOBJECT|NAME);
CANVASOBSERVER^SETBACKGROUND("LOGO.IMG");
```

### SETBKGPOS

```
void SETBKGPOS(INTEGER posX, INTEGER posY)
```

Ustawia bezwzględną pozycję tła w osiach X i Y.

**Parametry**

- `posX`, `posY` — nowa pozycja tła.

**Przykłady**

```
CANVASOBSERVER^SETBKGPOS([VARI_BKGX-VARI_BKGXOFFSET],[VARI_BKGY-VARI_BKGYOFFSET]);
CANVASOBSERVER^SETBKGPOS(VARI_TMPX,0);
```

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONWINDOWFOCUSON

Wywoływany po uzyskaniu fokusu przez okno gry (np. powrót z minimalizacji). Emisja działa tylko, gdy notyfikacje są włączone metodą [`ENABLENOTIFY`](#enablenotify).

### ONWINDOWFOCUSOFF

Wywoływany po utracie fokusu przez okno gry (np. przełączenie na inną aplikację). Emisja działa tylko, gdy notyfikacje są włączone.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
