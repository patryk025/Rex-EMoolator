# ANIMO

Animacja wczytywana z pliku `.ANN`. Najbardziej rozbudowany typ wizualny w silniku — obsługuje wiele zdarzeń (sekwencji klatek), zmianę FPS-u, kotwicę punktu zaczepienia, przezroczystość, monitorowanie kolizji oraz tryb przycisku.

Animacja składa się z **zdarzeń** (`event`), z których każde jest sekwencją **klatek** (`frame`). Numer klatki w zdarzeniu liczony jest od `0`, a indeks globalny klatki w całej animacji liczony jest od `0` osobno.

## Pola

### ASBUTTON

```
BOOL ASBUTTON
```

Traktuje animację jako klikalny przycisk. Modyfikowane przez metodę [`SETASBUTTON`](#setasbutton).

### FILENAME

```
STRING FILENAME
```

Nazwa pliku `.ANN` z animacją. Pole odczytywane podczas inicjalizacji zmiennej; może być nadpisane metodą [`LOAD`](#load).

### FPS

```
INTEGER FPS
```

Liczba klatek animacji na sekundę. Modyfikowane przez metodę [`SETFPS`](#setfps).

### MONITORCOLLISION

```
BOOL MONITORCOLLISION
```

Określa, czy animacja uczestniczy w detekcji kolizji. Modyfikowane przez metody [`MONITORCOLLISION`](#monitorcollision-1) i [`REMOVEMONITORCOLLISION`](#removemonitorcollision).

### MONITORCOLLISIONALPHA

```
BOOL MONITORCOLLISIONALPHA
```

Określa, czy w detekcji kolizji uwzględniany jest kanał przezroczystości.

### PRELOAD

```
BOOL PRELOAD
```

Określa, czy dane animacji mają być załadowane od razu przy inicjalizacji.

### PRIORITY

```
INTEGER PRIORITY
```

Priorytet renderowania (`Z`) względem innych obiektów na scenie.

### TOCANVAS

```
BOOL TOCANVAS
```

Określa, czy bazowy obiekt animacji zostaje zarejestrowany w `CRefreshScreen`, czyli na liście obiektów rysowanych. Ustawienie `FALSE` nie zatrzymuje odtwarzania ani emisji zdarzeń; sam obiekt bazowy nie jest jednak rysowany.

Klonowanie jest szczególnym przypadkiem odziedziczonym z oryginalnego silnika: `CLONE` zawsze rejestruje klon `ANIMO` na kanwie. Jeżeli wzorzec miał `TOCANVAS = FALSE`, klon zaczyna z priorytetem `0`. Jego skopiowane `VISIBLE` nadal decyduje, czy faktycznie jest widoczny. `TOCANVAS` nie oznacza osobnej warstwy nad kanwą.

### VISIBLE

```
BOOL VISIBLE
```

Widoczność animacji. Modyfikowana metodami [`SHOW`](#show) i [`HIDE`](#hide).

## Metody

### GETANCHOR

```
STRING GETANCHOR()
```

Zwraca aktualnie ustawioną kotwicę animacji w postaci, w jakiej została przekazana do [`SETANCHOR`](#setanchor).

**Zwraca**: nazwa kotwicy lub jej współrzędne.

**Kompatybilność:** `GETANCHOR` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (5/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCENTERX

```
INTEGER GETCENTERX()
```

Zwraca współrzędną X środka bounding boxa aktualnej klatki animacji.

**Zwraca**: środek X.

**Przykłady**

```
ANNREX^GETCENTERX();
```

**Kompatybilność:** `GETCENTERX` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCENTERY

```
INTEGER GETCENTERY()
```

Zwraca współrzędną Y środka bounding boxa aktualnej klatki animacji.

**Zwraca**: środek Y.

**Kompatybilność:** `GETCENTERY` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCFRAMEINEVENT

```
INTEGER GETCFRAMEINEVENT()
```

Zwraca numer bieżącej klatki wewnątrz aktualnie odtwarzanego zdarzenia (licząc od `0`).

**Zwraca**: indeks klatki w zdarzeniu.

**Przykłady**

```
ANNREX^GETCFRAMEINEVENT();
```

**Kompatybilność:** `GETCFRAMEINEVENT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCURRFRAMEPOSX

```
INTEGER GETCURRFRAMEPOSX()
```

Zwraca przesunięcie w osi X dla aktualnie wyświetlanego obrazka (per-klatkowe, definiowane w pliku animacji).

**Zwraca**: przesunięcie X obrazka.

**Kompatybilność:** `GETCURRFRAMEPOSX` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCURRFRAMEPOSY

```
INTEGER GETCURRFRAMEPOSY()
```

Zwraca przesunięcie w osi Y dla aktualnie wyświetlanego obrazka.

**Zwraca**: przesunięcie Y obrazka.

**Kompatybilność:** `GETCURRFRAMEPOSY` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETENDX

```
INTEGER GETENDX()
```

Zwraca prawą krawędź bounding boxa aktualnej klatki.

**Zwraca**: prawa krawędź X.

**Kompatybilność:** `GETENDX` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (4/5).

### GETENDY

```
INTEGER GETENDY()
```

Zwraca dolną krawędź bounding boxa aktualnej klatki.

**Zwraca**: dolna krawędź Y.

**Kompatybilność:** `GETENDY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (4/5).

### GETEVENTNAME

```
STRING GETEVENTNAME()
```

Zwraca nazwę aktualnie odtwarzanego zdarzenia.

**Zwraca**: nazwa zdarzenia.

**Przykłady**

```
ANNREX^GETEVENTNAME();
```

**Kompatybilność:** `GETEVENTNAME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETFRAME

```
INTEGER GETFRAME()
```

Zwraca globalny indeks aktualnie odtwarzanej klatki (niezależny od podziału na zdarzenia).

**Zwraca**: globalny indeks klatki.

**Kompatybilność:** `GETFRAME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETFRAMENAME

```
STRING GETFRAMENAME()
```

Zwraca nazwę aktualnie odtwarzanej klatki (nazwa pliku obrazka).

**Zwraca**: nazwa klatki.

**Kompatybilność:** `GETFRAMENAME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETHEIGHT

```
INTEGER GETHEIGHT()
```

Zwraca wysokość aktualnej klatki animacji.

**Zwraca**: wysokość w pikselach.

**Kompatybilność:** `GETHEIGHT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETMAXHEIGHT

```
INTEGER GETMAXHEIGHT()
```

Zwraca maksymalną wysokość spośród wszystkich klatek animacji.

**Zwraca**: największa wysokość w pikselach.

**Kompatybilność:** `GETMAXHEIGHT` - `PIKLIB61.DLL` ⚠️ (1/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETMAXWIDTH

```
INTEGER GETMAXWIDTH()
```

Zwraca maksymalną szerokość spośród wszystkich klatek animacji.

**Zwraca**: największa szerokość w pikselach.

**Przykłady**

```
ANN_STATEK^GETMAXWIDTH();
```

**Kompatybilność:** `GETMAXWIDTH` - `PIKLIB61.DLL` ⚠️ (1/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETNAME

```
STRING GETNAME()
```

Zwraca nazwę zmiennej animacji.

**Zwraca**: nazwa zmiennej.

**Kompatybilność:** `GETNAME` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETNOE

```
INTEGER GETNOE()
```

Zwraca liczbę zdarzeń w animacji (skrót od *Number Of Events*).

**Zwraca**: liczba zdarzeń.

**Przykłady**

```
ANNLISCIESLOTY^GETNOE();
ANNBTN^GETNOE();
```

**Kompatybilność:** `GETNOE` - `PIKLIB61.DLL` ⚠️ (1/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETNOF

```
INTEGER GETNOF()
```

Zwraca łączną liczbę klatek w animacji (skrót od *Number Of Frames*).

**Zwraca**: liczba klatek.

**Kompatybilność:** `GETNOF` - `PIKLIB61.DLL` ⚠️ (1/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETNOFINEVENT

```
INTEGER GETNOFINEVENT(INTEGER eventId)
INTEGER GETNOFINEVENT(STRING eventName)
```

Zwraca liczbę klatek w podanym zdarzeniu. Zdarzenie można wskazać numerem (od `0`) lub nazwą (wielkość liter bez znaczenia). Dla nieistniejącego zdarzenia zwracane jest `0`.

**Parametry**

- `eventId` / `eventName` — identyfikator zdarzenia.

**Zwraca**: liczba klatek w zdarzeniu.

**Przykłady**

```
ANNREX^GETNOFINEVENT(VARSTEMP0);
ANNUKLAD^GETNOFINEVENT(0);
ANNPLANNAK^GETNOFINEVENT("IDLE");
```

**Kompatybilność:** `GETNOFINEVENT` - `PIKLIB61.DLL` ⚠️ (1/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSITIONX

```
INTEGER GETPOSITIONX([BOOL absolute])
```

Zwraca współrzędną X lewego górnego rogu aktualnej klatki na kanwie. Wariant z `BOOL` zwraca pozycję bezwzględną, bez uwzględniania per-klatkowych przesunięć z pliku animacji.

**Parametry**

- `absolute` — (opcjonalnie) `TRUE`, aby pominąć przesunięcia per-klatkowe.

**Zwraca**: pozycja X.

**Kompatybilność:** `GETPOSITIONX` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSITIONY

```
INTEGER GETPOSITIONY([BOOL absolute])
```

Zwraca współrzędną Y lewego górnego rogu aktualnej klatki na kanwie. Wariant z `BOOL` zwraca pozycję bezwzględną.

**Parametry**

- `absolute` — (opcjonalnie) `TRUE`, aby pominąć przesunięcia per-klatkowe.

**Zwraca**: pozycja Y.

**Kompatybilność:** `GETPOSITIONY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPRIORITY

```
INTEGER GETPRIORITY()
```

Zwraca priorytet renderowania (`Z`) animacji.

**Zwraca**: wartość pola [`PRIORITY`](#priority).

**Kompatybilność:** `GETPRIORITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETWIDTH

```
INTEGER GETWIDTH()
```

Zwraca szerokość aktualnej klatki animacji.

**Zwraca**: szerokość w pikselach.

**Kompatybilność:** `GETWIDTH` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### HIDE

```
void HIDE()
```

Ukrywa animację wizualnie, nie przerywając jej odtwarzania. Po wywołaniu [`PLAY`](#play) widoczność zostanie automatycznie przywrócona.

**Kompatybilność:** `HIDE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ISAT

```
BOOL ISAT(INTEGER posX, INTEGER posY)
```

Sprawdza, czy punkt o podanych współrzędnych znajduje się wewnątrz bounding boxa aktualnej klatki.

**Parametry**

- `posX` — współrzędna X punktu.
- `posY` — współrzędna Y punktu.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli punkt jest wewnątrz bounding boxa.

**Kompatybilność:** `ISAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (8/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ISNEAR

```
BOOL ISNEAR(STRING animoName, INTEGER iouThresholdPercent)
```

Sprawdza, czy bieżąca animacja jest w pobliżu drugiej animacji. Wewnętrznie wyznaczany jest indeks Jaccarda (*Intersection over Union*, IoU) bounding boxów dwóch animacji; jeżeli IoU przekracza podany próg (wyrażony w procentach), zwracane jest `TRUE`.

**Parametry**

- `animoName` — nazwa drugiej animacji.
- `iouThresholdPercent` — próg IoU w procentach.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli IoU przekracza próg.

**Przykłady**

```
ENEMY^ISNEAR("HERO", 1);
ANNORKA^ISNEAR("ANNLODKA", 12);
```

**Kompatybilność:** `ISNEAR` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ISPLAYING

```
BOOL ISPLAYING()
BOOL ISPLAYING(STRING eventName)
```

Sprawdza, czy animacja jest odtwarzana. Wariant bez argumentu sprawdza, czy jakiekolwiek zdarzenie jest aktualnie odtwarzane; wariant z nazwą zdarzenia sprawdza, czy odtwarzane jest konkretnie to zdarzenie.

**Parametry**

- `eventName` — (opcjonalnie) nazwa zdarzenia do sprawdzenia.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli animacja (lub konkretne zdarzenie) jest odtwarzana.

**Przykłady**

```
ANNREX^ISPLAYING();
ANNREXGLOWA^ISPLAYING("SPI");
```

**Kompatybilność:** `ISPLAYING` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ISVISIBLE

```
BOOL ISVISIBLE()
```

Zwraca stan widoczności animacji (`VISIBLE`). Nie sprawdza, czy obiekt jest zarejestrowany na kanwie przez [`TOCANVAS`](#tocanvas).

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli animacja jest widoczna.

**Kompatybilność:** `ISVISIBLE` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOAD

```
void LOAD(STRING path)
```

Wczytuje animację z pliku `.ANN`, zastępując dotychczasową zawartość.

**Parametry**

- `path` — ścieżka pliku `.ANN` w VFS gry.

**Przykłady**

```
ANNBKG^LOAD(SOBJECT|NAME);
ANNCHARACTER^LOAD("PIXEL.ANN");
ANNMINIMAPA^LOAD([""+ILEVEL+"_MINIMAPA.ANN"]);
```

**Kompatybilność:** `LOAD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MONITORCOLLISION {#monitorcollision-1}

```
void MONITORCOLLISION()
```

Włącza monitorowanie kolizji animacji z innymi obiektami.

**Kompatybilność:** `MONITORCOLLISION` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MONITORCOLLISIONALPHA {#monitorcollisionalpha-1}

```
void MONITORCOLLISIONALPHA()
```

Włącza uwzględnianie kanału alfa przy detekcji kolizji.

**Kompatybilność:** `MONITORCOLLISIONALPHA` - brak w przeanalizowanych bibliotekach z `compat.json`.

### MOVE

```
void MOVE(INTEGER offsetX, INTEGER offsetY)
```

Przesuwa animację o zadane wartości względem aktualnej pozycji.

**Parametry**

- `offsetX` — przesunięcie w osi X.
- `offsetY` — przesunięcie w osi Y.

**Przykłady**

```
ANNELEMENT^MOVE(-200, 0);
ANNPLAYER^MOVE(VARDX, VARDY);
ANNITEMDRAGGING^MOVE([IMOUSEX-IMOUSELASTX], [IMOUSEY-IMOUSELASTY]);
```

**Kompatybilność:** `MOVE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### NEXTFRAME

```
void NEXTFRAME()
```

Przeskakuje do następnej klatki bieżącego zdarzenia.

**Kompatybilność:** `NEXTFRAME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### NPLAY

```
void NPLAY(INTEGER eventId)
```

Rozpoczyna odtwarzanie zdarzenia o podanym indeksie (numerowanym od `0`).

**Parametry**

- `eventId` — indeks zdarzenia.

**Przykłady**

```
ANNDARK0^NPLAY(VARITEMP2);
CZAS^NPLAY(0);
```

**Kompatybilność:** `NPLAY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (9/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PAUSE

```
void PAUSE()
```

Wstrzymuje odtwarzanie animacji w aktualnej klatce.

**Kompatybilność:** `PAUSE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PLAY

```
void PLAY([STRING eventName])
```

Rozpoczyna odtwarzanie zdarzenia. Wariant bez argumentu wznawia ostatnio odtwarzane zdarzenie od początku.

**Parametry**

- `eventName` — (opcjonalnie) nazwa zdarzenia do odtworzenia (wielkość liter bez znaczenia).

**Przykłady**

```
G_STLPAGE^PLAY("ELAPSE");
ANNREX^PLAY(VARITEMP0);
ANNKRET^PLAY(["IDLE_"+ANNKRET^GETEVENTNAME()]);
ANIMOREKSIO^PLAY($1);
```

**Kompatybilność:** `PLAY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PREVFRAME

```
void PREVFRAME()
```

Przechodzi do poprzedniej klatki bieżącego zdarzenia.

**Kompatybilność:** `PREVFRAME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEMONITORCOLLISION

```
void REMOVEMONITORCOLLISION()
```

Wyłącza monitorowanie kolizji, włączone wcześniej przez [`MONITORCOLLISION`](#monitorcollision-1).

**Kompatybilność:** `REMOVEMONITORCOLLISION` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEMONITORCOLLISIONALPHA

```
void REMOVEMONITORCOLLISIONALPHA()
```

Wyłącza uwzględnianie kanału alfa przy detekcji kolizji, włączone wcześniej przez [`MONITORCOLLISIONALPHA`](#monitorcollisionalpha-1).

**Kompatybilność:** `REMOVEMONITORCOLLISIONALPHA` - brak w przeanalizowanych bibliotekach z `compat.json`.

### RESUME

```
void RESUME()
```

Wznawia odtwarzanie wstrzymane przez [`PAUSE`](#pause).

**Kompatybilność:** `RESUME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETANCHOR

```
void SETANCHOR(STRING anchor)
void SETANCHOR(INTEGER offsetX, INTEGER offsetY)
```

Ustawia kotwicę animacji — punkt zaczepienia, który jest odejmowany od współrzędnych przekazywanych do [`SETPOSITION`](#setposition).

Wariant z `STRING` przyjmuje nazwę pozycji wyliczonej z bounding boxa: `CENTER`, `LEFTUPPER`, `RIGHTUPPER`, `LEFTLOWER`, `RIGHTLOWER`, `LEFT`, `RIGHT`, `TOP`, `BOTTOM`.

Wariant z dwoma `INTEGER`-ami przyjmuje współrzędne kotwicy bezpośrednio.

**Parametry**

- `anchor` — nazwa pozycji w bounding boxie.
- `offsetX, offsetY` — współrzędne kotwicy.

**Przykłady**

```
ANNSELECT^SETANCHOR("CENTER");
ANNREX^SETANCHOR("LEFTLOWER");
ANNREX^SETANCHOR(0, -100);
```

**Kompatybilność:** `SETANCHOR` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETASBUTTON

```
void SETASBUTTON(BOOL enabled, BOOL changeCursor)
```

Ustawia animację jako klikalny przycisk. Niezależnie od wartości argumentów wywołanie sprawia, że animacja staje się widoczna.

**Parametry**

- `enabled` — `TRUE`, aby aktywować obsługę kliknięć.
- `changeCursor` — `TRUE`, aby kursor zmieniał wygląd po najechaniu na animację.

**Przykłady**

```
ANNEXIT^SETASBUTTON(TRUE, TRUE);
ANIMOPOWROT^SETASBUTTON(FALSE, FALSE);
```

**Kompatybilność:** `SETASBUTTON` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETBACKWARD

```
void SETBACKWARD()
```

Ustawia kierunek odtwarzania animacji na wsteczny.

**Kompatybilność:** `SETBACKWARD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETFORWARD

```
void SETFORWARD()
```

Ustawia kierunek odtwarzania animacji na zgodny z naturalnym (do przodu).

**Kompatybilność:** `SETFORWARD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETFPS

```
void SETFPS(INTEGER fps)
```

Zmienia tempo odtwarzania animacji.

**Parametry**

- `fps` — liczba klatek na sekundę.

**Przykłady**

```
STLMAGIC^SETFPS(5);
ANNMUCHA1^SETFPS(30);
ANNKON^SETFPS([IKONFPS*8]);
```

**Kompatybilność:** `SETFPS` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETFRAME

```
void SETFRAME(INTEGER frameNumber)
void SETFRAME(INTEGER eventId, INTEGER frameNumber)
void SETFRAME(STRING eventName, INTEGER frameNumber)
void SETFRAME(STRING eventName, STRING frameName)
```

Ustawia animację na konkretną klatkę. Wariant z jednym argumentem ustawia klatkę po jej globalnym indeksie. Wariant dwuargumentowy wybiera zdarzenie przez indeks lub nazwę, a następnie pozycję w nim przez numer lub nazwę klatki.

Oryginalny silnik ma istotny quirk: jeżeli tekstowa nazwa zdarzenia nie istnieje, `CAnimo::getFrameNo` zwraca `0`, więc `SETFRAME` używa pierwszego zdarzenia.

**Parametry**

- `eventId` — indeks zdarzenia od `0`.
- `eventName` — nazwa zdarzenia.
- `frameNumber` — indeks klatki w zdarzeniu (od `0`) lub globalny indeks klatki.
- `frameName` — nazwa konkretnej klatki w zdarzeniu.

**Przykłady**

```
ANNREX^SETFRAME(VARSTEMP0, [VARITEMP2-1]);
ANNSCIAGA^SETFRAME("PLAY", VARIREPEATSPELL);
OFERTA^SETFRAME(3);
ANN_H_PIECYK^SETFRAME("ROT", "PIECYK4");
```

**Kompatybilność:** `SETFRAME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETFRAMENAME

```
void SETFRAMENAME(INTEGER eventId, INTEGER frameNumber, STRING name)
```

Zmienia nazwę konkretnej klatki w podanym zdarzeniu.

**Parametry**

- `eventId` — indeks zdarzenia (od `0`).
- `frameNumber` — indeks klatki w zdarzeniu (od `0`).
- `name` — nowa nazwa klatki.

**Przykłady**

```
ANNKALAREPA^SETFRAMENAME(0, 0, "200");
ANNKALAREPA^SETFRAMENAME(1, 0, "300");
```

**Kompatybilność:** `SETFRAMENAME` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETOPACITY

```
void SETOPACITY(INTEGER opacity)
```

Ustawia przezroczystość animacji w skali `0–255` (`0` — pełna przezroczystość, `255` — pełna nieprzezroczystość).

**Parametry**

- `opacity` — wartość kanału alfa.

**Przykłady**

```
ANNPLAYER0^SETOPACITY(255);
ANNPLAYER^SETOPACITY(100);
```

**Kompatybilność:** `SETOPACITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPOSITION

```
void SETPOSITION(INTEGER posX, INTEGER posY)
```

Ustawia bezwzględną pozycję animacji. Jeżeli wcześniej ustawiono kotwicę metodą [`SETANCHOR`](#setanchor), jej współrzędne są odejmowane od podanych argumentów.

**Parametry**

- `posX` — współrzędna X.
- `posY` — współrzędna Y.

**Przykłady**

```
ANNREX^SETPOSITION(400, 300);
ANNEXIT^SETPOSITION(-700, -450);
ANNBKG^SETPOSITION([VARIBKGOFFSETX-VARDTEMP0], [VARIBKGOFFSETY-VARDTEMP1]);
```

**Kompatybilność:** `SETPOSITION` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Ustawia priorytet renderowania.

**Parametry**

- `priority` — nowa wartość pola [`PRIORITY`](#priority).

**Przykłady**

```
ANNREX^SETPRIORITY(VARIPRIORITY);
ANNHEAD1^SETPRIORITY(15);
```

**Kompatybilność:** `SETPRIORITY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SHOW

```
void SHOW()
```

Pokazuje animację (ustawia [`VISIBLE`](#visible) na `TRUE`).

**Kompatybilność:** `SHOW` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### STOP

```
void STOP([BOOL emitSignal])
```

Zatrzymuje odtwarzanie animacji.

**Parametry**

- `emitSignal` — (opcjonalnie) jeżeli `FALSE`, sygnał [`ONFINISHED`](#onfinished) nie zostanie wyemitowany. Domyślnie sygnał jest emitowany.

**Przykłady**

```
G_STLPAGE^STOP(FALSE);
ANNREX^STOP(FALSE);
ANNBLANK^STOP();
```

**Kompatybilność:** `STOP` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Zgodność

### TOP

```
void TOP()
```

Metoda jest rozpoznawana dla zgodności ze skryptami, ale aktualnie nie zmienia kolejności renderowania ani stanu animacji.

## Sygnały

### ONCLICK

Wywoływany po kliknięciu w animację, jeżeli jest ustawiona jako przycisk ([`SETASBUTTON`](#setasbutton)).

### ONCOLLISION

Wywoływany po wykryciu rozpoczęcia kolizji.

### ONCOLLISIONFINISHED

Wywoływany po zakończeniu kolizji.

### ONDONE

Wywoływany po zakończeniu wszystkich zdarzeń animacji.

### ONFINISHED

Wywoływany po zakończeniu odtwarzania zdarzenia. Sygnał jest [parametryzowany](../engine/events.md#sygnaly-parametryzowane) nazwą zdarzenia, więc można podpiąć handler dla konkretnego zdarzenia:

```
ANIMACJA:ONFINISHED^IDLE=BEHAFTERIDLE
```

### ONFIRSTFRAME

Wywoływany po odtworzeniu pierwszej klatki zdarzenia.

### ONFOCUSON

Wywoływany po najechaniu kursorem na animację, jeżeli jest ustawiona jako przycisk.

### ONFOCUSOFF

Wywoływany po zjechaniu kursorem z animacji, jeżeli jest ustawiona jako przycisk.

### ONFRAMECHANGED

Wywoływany po zmianie klatki animacji.

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONPAUSED

Wywoływany po wstrzymaniu animacji metodą [`PAUSE`](#pause).

### ONRELEASE

Wywoływany po zwolnieniu przycisku myszy nad animacją ustawioną jako przycisk.

### ONRESUMED

Wywoływany po wznowieniu animacji metodą [`RESUME`](#resume).

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).

### ONSTARTED

Wywoływany po rozpoczęciu odtwarzania zdarzenia. Sygnał emitowany jest po [`ONFRAMECHANGED`](#onframechanged) dla pierwszej klatki zdarzenia.
