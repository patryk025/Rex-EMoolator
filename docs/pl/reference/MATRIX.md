# MATRIX

Plansza złożona z prostokątnej siatki pól z prostym systemem fizyki kamieni i prymitywnym wyznaczaniem ruchu przeciwników. Zaprojektowany pod jeden, konkretny przypadek użycia — minigrę z podkopem (sekcje Kretesa w *Reksio i Ufo*: mur na Indorze oraz podkop do więzienia na Kuranie).

Dane planszy są przechowywane jako jednowymiarowa tablica liczb całkowitych — adres pola wyznacza się ze wzoru `index = y * width + x`. Stąd obecność metod konwertujących indeks na pozycję 2D i odwrotnie.

## Kody pól {#kody-pol}

Wartości używane przez metody [`GET`](#get), [`GETCELLSNO`](#getcellsno), [`SET`](#set), [`SETROW`](#setrow) i wewnętrzny system fizyki:

| Kod | Znaczenie |
| --- | --- |
| `0` | puste pole |
| `1` | grunt |
| `2` | kamień |
| `3` | laska dynamitu |
| `4` | wyburzalna ściana |
| `5` | przeciwnik |
| `6` | nierozbijalna ściana |
| `7` | odpalona laska dynamitu |
| `8` | pole w zasięgu eksplozji |
| `9` | wyjście |
| `99` | pozycja Kretesa |

## Kody kierunku ruchu {#kody-kierunku-ruchu}

Wartości używane przez [`TICK`](#tick), [`NEXT`](#next) i [`CALCENEMYMOVEDIR`](#calcenemymovedir):

| Kod | Kierunek |
| --- | --- |
| `0` | w lewo |
| `1` | w górę |
| `2` | w prawo |
| `3` | w dół |

Wewnętrzny system fizyki używa dodatkowych kodów zwracanych przez [`TICK`](#tick) i odczytywanych przez [`NEXT`](#next):

| Kod | Operacja |
| --- | --- |
| `1` | kamień spada w dół |
| `2` | kamień spada w lewo po skosie |
| `3` | kamień spada w prawo po skosie |
| `4` | kamień z przeciwnikiem eksplodują |

## Pola

### BASEPOS

```
INTEGER, INTEGER BASEPOS
```

Pozycja lewego górnego rogu planszy na ekranie w pikselach (`X,Y`).

### CELLHEIGHT

```
INTEGER CELLHEIGHT
```

Wysokość pojedynczego pola w pikselach.

### CELLWIDTH

```
INTEGER CELLWIDTH
```

Szerokość pojedynczego pola w pikselach.

### SIZE

```
INTEGER, INTEGER SIZE
```

Wymiary planszy w polach (`szerokość,wysokość`). Wartość wyznacza również rozmiar wewnętrznej tablicy z kodami pól.

## Metody

### CALCENEMYMOVEDEST

```
INTEGER CALCENEMYMOVEDEST(INTEGER oldCell, INTEGER direction)
```

Wylicza indeks pola, na które przeciwnik przejdzie z pola `oldCell` przy ruchu w kierunku `direction`. Jeśli pole docelowe znajduje się poza planszą lub nie jest puste, zwracany jest niezmieniony `oldCell`.

**Parametry**

- `oldCell` — bieżąca pozycja przeciwnika (indeks pola).
- `direction` — kierunek ruchu (`0`–`3`, zobacz [Kody kierunku ruchu](#kody-kierunku-ruchu)).

**Zwraca**: [`INTEGER`](INTEGER.md) — indeks pola docelowego.

**Przykłady**

```
MAT^CALCENEMYMOVEDEST(IENOLDCELL,IENNEWDIR);
```

**Kompatybilność:** `CALCENEMYMOVEDEST` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### CALCENEMYMOVEDIR

```
INTEGER CALCENEMYMOVEDIR(INTEGER oldCell, INTEGER oldDir)
```

Wylicza kierunek ruchu przeciwnika. Algorytm preferuje skręt w lewo: w pierwszej kolejności sprawdzany jest kierunek `(oldDir+3) MOD 4`, następnie `oldDir`, dalej w prawo i wreszcie do tyłu. Zwracany jest pierwszy kierunek, dla którego pole docelowe jest puste; jeżeli żaden nie jest dostępny — preferowany skręt w lewo.

**Parametry**

- `oldCell` — bieżąca pozycja przeciwnika.
- `oldDir` — kierunek ruchu z poprzedniego kroku.

**Zwraca**: [`INTEGER`](INTEGER.md) — nowy kierunek ruchu.

**Przykłady**

```
MAT^CALCENEMYMOVEDIR(IENOLDCELL,IENOLDDIR);
```

**Kompatybilność:** `CALCENEMYMOVEDIR` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### CANHEROGOTO

```
BOOL CANHEROGOTO(INTEGER cellNo)
```

Sprawdza, czy główny bohater może wejść na podane pole. Pole jest przejezdne, jeżeli nie zawiera słabej ściany, mocnej ściany, przeciwnika ani kamienia, a także nie należy do obszaru bramy ustawionej metodą [`SETGATE`](#setgate).

**Parametry**

- `cellNo` — indeks sprawdzanego pola.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli pole jest przejezdne.

**Przykłady**

```
MAT^CANHEROGOTO(I_0);
```

**Kompatybilność:** `CANHEROGOTO` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GET

```
INTEGER GET(INTEGER cellNo)
```

Zwraca kod pola o podanym indeksie. Dla indeksów spoza zakresu zwraca `0`.

**Parametry**

- `cellNo` — indeks pola.

**Zwraca**: [`INTEGER`](INTEGER.md) — kod pola (zobacz [Kody pól](#kody-pol)).

**Przykłady**

```
MAT^GET(I_ITERATOR);
MAT^GET(I_MOLE_FIELD_CURR);
```

**Kompatybilność:** `GET` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETCELLOFFSET

```
INTEGER GETCELLOFFSET(INTEGER x, INTEGER y)
```

Zwraca indeks pola na podstawie współrzędnych `(x, y)`. Dla współrzędnych spoza planszy zwracane jest `-1`.

**Parametry**

- `x`, `y` — koordynaty pola.

**Zwraca**: [`INTEGER`](INTEGER.md) — indeks pola lub `-1`.

**Przykłady**

```
MAT^GETCELLOFFSET(ISSRCX,ISSRCY);
MAT^GETCELLOFFSET(ISTRGX,ISTRGY);
```

**Kompatybilność:** `GETCELLOFFSET` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETCELLPOSX

```
INTEGER GETCELLPOSX(INTEGER cellNo)
```

Zwraca pozycję X pola w pikselach (`BASEPOS.X + col * CELLWIDTH`).

**Parametry**

- `cellNo` — indeks pola.

**Zwraca**: [`INTEGER`](INTEGER.md) — koordynata X w pikselach.

**Kompatybilność:** `GETCELLPOSX` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETCELLPOSY

```
INTEGER GETCELLPOSY(INTEGER cellNo)
```

Zwraca pozycję Y pola w pikselach (`BASEPOS.Y + row * CELLHEIGHT`).

**Parametry**

- `cellNo` — indeks pola.

**Zwraca**: [`INTEGER`](INTEGER.md) — koordynata Y w pikselach.

**Kompatybilność:** `GETCELLPOSY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETCELLSNO

```
INTEGER GETCELLSNO(INTEGER cellCode)
```

Zwraca liczbę pól z podanym kodem.

**Parametry**

- `cellCode` — kod pola, którego wystąpienia mają zostać policzone.

**Zwraca**: [`INTEGER`](INTEGER.md) — liczba pól o podanym kodzie.

**Przykłady**

```
MAT^GETCELLSNO(IC_FIELD_CODE_EXIT);
MAT^GETCELLSNO(IC_FIELD_CODE_ENEMY);
```

**Kompatybilność:** `GETCELLSNO` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETFIELDPOSX

```
INTEGER GETFIELDPOSX(INTEGER cellNo)
```

Alias [`GETCELLPOSX`](#getcellposx) używany w *Reksio i Ufo* z silnikiem Piklib 7.1. W Piklib 8 metoda nie jest już udostępniana — próba jej wywołania w nowszej wersji powoduje awarię silnika.

**Parametry**

- `cellNo` — indeks pola.

**Zwraca**: [`INTEGER`](INTEGER.md) — koordynata X w pikselach.

**Kompatybilność:** `GETFIELDPOSX` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETFIELDPOSY

```
INTEGER GETFIELDPOSY(INTEGER cellNo)
```

Alias [`GETCELLPOSY`](#getcellposy) używany w *Reksio i Ufo* z silnikiem Piklib 7.1. W Piklib 8 metoda nie jest już udostępniana — próba jej wywołania w nowszej wersji powoduje awarię silnika.

**Parametry**

- `cellNo` — indeks pola.

**Zwraca**: [`INTEGER`](INTEGER.md) — koordynata Y w pikselach.

**Kompatybilność:** `GETFIELDPOSY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETOFFSET

```
INTEGER GETOFFSET(INTEGER x, INTEGER y)
```

Alias [`GETCELLOFFSET`](#getcelloffset) używany w *Reksio i Ufo* z silnikiem Piklib 7.1. W Piklib 8 metoda została zastąpiona przez `GETCELLOFFSET`.

**Parametry**

- `x`, `y` — koordynaty pola.

**Zwraca**: [`INTEGER`](INTEGER.md) — indeks pola lub `-1`.

**Kompatybilność:** `GETOFFSET` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### ISGATEEMPTY

```
BOOL ISGATEEMPTY()
```

Sprawdza, czy wszystkie pola w obszarze bramy ustawionej metodą [`SETGATE`](#setgate) mają kod `0` (puste). Jeśli brama nie została ustawiona, zwracane jest `FALSE`.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli wszystkie pola bramy są puste.

**Przykłady**

```
MAT^ISGATEEMPTY();
```

**Kompatybilność:** `ISGATEEMPTY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### ISINGATE

```
BOOL ISINGATE(INTEGER cellNo)
```

Sprawdza, czy pole o podanym indeksie należy do obszaru bramy ustawionej metodą [`SETGATE`](#setgate).

**Parametry**

- `cellNo` — indeks pola.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli pole znajduje się w obszarze bramy.

**Kompatybilność:** `ISINGATE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### MOVE

```
void MOVE(INTEGER oldCell, INTEGER newCell)
```

Przesuwa zawartość pola źródłowego na pole docelowe; pole źródłowe pozostaje puste (kod `0`).

**Parametry**

- `oldCell` — indeks pola źródłowego.
- `newCell` — indeks pola docelowego.

**Przykłady**

```
MAT^MOVE(I_0,I_1);
```

**Kompatybilność:** `MOVE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### NEXT

```
INTEGER NEXT()
```

Wykonuje kolejny zaplanowany ruch z kolejki wygenerowanej przez [`TICK`](#tick). Pole źródłowe jest czyszczone, a pole docelowe otrzymuje kod kamienia. Po wykonaniu ruchu emitowany jest sygnał [`ONNEXT`](#onnext) (dla wszystkich ruchów oprócz ostatniego) lub [`ONLATEST`](#onlatest) (dla ostatniego ruchu z kolejki).

**Zwraca**: [`INTEGER`](INTEGER.md) — `0`, jeżeli kolejka jest pusta; `1` przy zwykłym ruchu kamienia; `2`, jeśli kamień wylądował dwa pola nad Kretesem (kolizja).

**Przykłady**

```
MAT^NEXT();
```

**Kompatybilność:** `NEXT` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SET

```
void SET(INTEGER cellNo, INTEGER cellCode)
void SET(INTEGER x, INTEGER y, INTEGER cellCode)
```

Ustawia kod pola. Metoda ma dwie formy: pierwsza przyjmuje gotowy indeks pola, druga — współrzędne `(x, y)`.

**Parametry**

- `cellNo` — indeks pola **(forma 1)**.
- `x`, `y` — koordynaty pola **(forma 2)**.
- `cellCode` — nowa wartość pola (zobacz [Kody pól](#kody-pol)).

**Przykłady**

```
MAT^SET(I_MOLE_FIELD_CURR,IC_FIELD_CODE_EMPTY);
MAT^SET([I_FIELD_INDEX%I_LEVEL_WIDTH],[I_FIELD_INDEX@I_LEVEL_WIDTH],IC_FIELD_CODE_EMPTY);
```

**Kompatybilność:** `SET` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETGATE

```
void SETGATE(INTEGER startX, INTEGER startY, INTEGER endX, INTEGER endY)
```

Wyznacza prostokątny obszar bramy określony przez współrzędne początkowego i końcowego pola (włącznie). Brama wpływa na działanie metod [`CANHEROGOTO`](#canherogoto), [`ISGATEEMPTY`](#isgateempty) oraz [`ISINGATE`](#isingate).

**Parametry**

- `startX`, `startY` — koordynaty lewego górnego rogu obszaru bramy.
- `endX`, `endY` — koordynaty prawego dolnego rogu obszaru bramy.

**Przykłady**

```
MAT^SETGATE(3,1,16,4);
```

**Kompatybilność:** `SETGATE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETROW

```
void SETROW(INTEGER row, INTEGER cellCode1, [INTEGER cellCode2, ...])
```

Ustawia kody pól w podanym wierszu. Kolejne argumenty są wpisywane do pól od kolumny `0` w prawo; nadmiarowe argumenty (więcej niż szerokość planszy) są ignorowane.

**Parametry**

- `row` — indeks wiersza.
- `cellCode1`, `cellCode2`, … — kolejne kody pól w wierszu.

**Przykłady**

```
MAT^SETROW(0,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6);
MAT^SETROW(1,6,6,6,2,2,2,2,2,2,2,2,2,2,2,2,2,2,6,6,6);
```

**Kompatybilność:** `SETROW` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### TICK

```
void TICK()
```

Wykonuje jedno tyknięcie wewnętrznego systemu fizyki. Plansza jest skanowana od dołu do góry, od lewej do prawej. Dla każdego kamienia sprawdzane są kolejno:

1. Czy pod kamieniem jest puste pole — zaplanowanie ruchu w dół.
2. Czy pod kamieniem znajduje się przeciwnik — zaplanowanie eksplozji.
3. Czy obok kamienia leży inny kamień, a pola na ukos w lewo lub w prawo są puste — zaplanowanie zsuwu po skosie.

Ruchy są zapisywane do wewnętrznej kolejki i wykonywane kolejno przez wywołania [`NEXT`](#next). Każdy wpis zawiera koordynatę X i Y pola źródłowego oraz kod operacji.

**Przykłady**

```
MAT^TICK();
```

**Kompatybilność:** `TICK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONNEXT

Wywoływany przez [`NEXT`](#next) po wykonaniu ruchu, który nie był ostatnim w bieżącej kolejce. Argumentami sygnału są: koordynata X (`$1`), koordynata Y (`$2`) i kod operacji (`$3`) pola źródłowego.

### ONLATEST

Wywoływany przez [`NEXT`](#next) po wykonaniu ostatniego ruchu z bieżącej kolejki. Argumenty sygnału są identyczne jak w [`ONNEXT`](#onnext).

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
