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

Określa, czy animacja jest rysowana na głównej kanwie sceny. Ustawienie `FALSE` ukrywa animację wizualnie, ale silnik nadal ją odtwarza i emituje powiązane zdarzenia.

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

### GETCENTERY

```
INTEGER GETCENTERY()
```

Zwraca współrzędną Y środka bounding boxa aktualnej klatki animacji.

**Zwraca**: środek Y.

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

### GETCURRFRAMEPOSX

```
INTEGER GETCURRFRAMEPOSX()
```

Zwraca przesunięcie w osi X dla aktualnie wyświetlanego obrazka (per-klatkowe, definiowane w pliku animacji).

**Zwraca**: przesunięcie X obrazka.

### GETCURRFRAMEPOSY

```
INTEGER GETCURRFRAMEPOSY()
```

Zwraca przesunięcie w osi Y dla aktualnie wyświetlanego obrazka.

**Zwraca**: przesunięcie Y obrazka.

### GETENDX

```
INTEGER GETENDX()
```

Zwraca prawą krawędź bounding boxa aktualnej klatki.

**Zwraca**: prawa krawędź X.

### GETENDY

```
INTEGER GETENDY()
```

Zwraca dolną krawędź bounding boxa aktualnej klatki.

**Zwraca**: dolna krawędź Y.

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

### GETFRAME

```
INTEGER GETFRAME()
```

Zwraca globalny indeks aktualnie odtwarzanej klatki (niezależny od podziału na zdarzenia).

**Zwraca**: globalny indeks klatki.

### GETFRAMENAME

```
STRING GETFRAMENAME()
```

Zwraca nazwę aktualnie odtwarzanej klatki (nazwa pliku obrazka).

**Zwraca**: nazwa klatki.

### GETHEIGHT

```
INTEGER GETHEIGHT()
```

Zwraca wysokość aktualnej klatki animacji.

**Zwraca**: wysokość w pikselach.

### GETMAXHEIGHT

```
INTEGER GETMAXHEIGHT()
```

Zwraca maksymalną wysokość spośród wszystkich klatek animacji.

**Zwraca**: największa wysokość w pikselach.

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

### GETNAME

```
STRING GETNAME()
```

Zwraca nazwę zmiennej animacji.

**Zwraca**: nazwa zmiennej.

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

### GETNOF

```
INTEGER GETNOF()
```

Zwraca łączną liczbę klatek w animacji (skrót od *Number Of Frames*).

**Zwraca**: liczba klatek.

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

### GETPOSITIONX

```
INTEGER GETPOSITIONX([BOOL absolute])
```

Zwraca współrzędną X lewego górnego rogu aktualnej klatki na kanwie. Wariant z `BOOL` zwraca pozycję bezwzględną, bez uwzględniania per-klatkowych przesunięć z pliku animacji.

**Parametry**

- `absolute` — (opcjonalnie) `TRUE`, aby pominąć przesunięcia per-klatkowe.

**Zwraca**: pozycja X.

### GETPOSITIONY

```
INTEGER GETPOSITIONY([BOOL absolute])
```

Zwraca współrzędną Y lewego górnego rogu aktualnej klatki na kanwie. Wariant z `BOOL` zwraca pozycję bezwzględną.

**Parametry**

- `absolute` — (opcjonalnie) `TRUE`, aby pominąć przesunięcia per-klatkowe.

**Zwraca**: pozycja Y.

### GETPRIORITY

```
INTEGER GETPRIORITY()
```

Zwraca priorytet renderowania (`Z`) animacji.

**Zwraca**: wartość pola [`PRIORITY`](#priority).

### GETWIDTH

```
INTEGER GETWIDTH()
```

Zwraca szerokość aktualnej klatki animacji.

**Zwraca**: szerokość w pikselach.

### HIDE

```
void HIDE()
```

Ukrywa animację wizualnie, nie przerywając jej odtwarzania. Po wywołaniu [`PLAY`](#play) widoczność zostanie automatycznie przywrócona.

### ISAT

```
BOOL ISAT(INTEGER posX, INTEGER posY)
```

Sprawdza, czy punkt o podanych współrzędnych znajduje się wewnątrz bounding boxa aktualnej klatki.

**Parametry**

- `posX` — współrzędna X punktu.
- `posY` — współrzędna Y punktu.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli punkt jest wewnątrz bounding boxa.

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

### ISVISIBLE

```
BOOL ISVISIBLE()
```

Sprawdza, czy animacja jest widoczna ([`VISIBLE`](#visible) = `TRUE` i [`TOCANVAS`](#tocanvas) = `TRUE`).

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli animacja jest widoczna.

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

### MONITORCOLLISION {#monitorcollision-1}

```
void MONITORCOLLISION()
```

Włącza monitorowanie kolizji animacji z innymi obiektami.

### MONITORCOLLISIONALPHA {#monitorcollisionalpha-1}

```
void MONITORCOLLISIONALPHA()
```

Włącza uwzględnianie kanału alfa przy detekcji kolizji.

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

### NEXTFRAME

```
void NEXTFRAME()
```

Przeskakuje do następnej klatki bieżącego zdarzenia.

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

### PAUSE

```
void PAUSE()
```

Wstrzymuje odtwarzanie animacji w aktualnej klatce.

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

### PREVFRAME

```
void PREVFRAME()
```

Przechodzi do poprzedniej klatki bieżącego zdarzenia.

### REMOVEMONITORCOLLISION

```
void REMOVEMONITORCOLLISION()
```

Wyłącza monitorowanie kolizji, włączone wcześniej przez [`MONITORCOLLISION`](#monitorcollision-1).

### REMOVEMONITORCOLLISIONALPHA

```
void REMOVEMONITORCOLLISIONALPHA()
```

Wyłącza uwzględnianie kanału alfa przy detekcji kolizji, włączone wcześniej przez [`MONITORCOLLISIONALPHA`](#monitorcollisionalpha-1).

### RESUME

```
void RESUME()
```

Wznawia odtwarzanie wstrzymane przez [`PAUSE`](#pause).

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

### SETBACKWARD

```
void SETBACKWARD()
```

Ustawia kierunek odtwarzania animacji na wsteczny.

### SETFORWARD

```
void SETFORWARD()
```

Ustawia kierunek odtwarzania animacji na zgodny z naturalnym (do przodu).

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

### SETFRAME

```
void SETFRAME(INTEGER frameNumber)
void SETFRAME(STRING eventName, INTEGER frameNumber)
void SETFRAME(STRING eventName, STRING frameName)
```

Ustawia animację na konkretną klatkę. Wariant z jednym argumentem ustawia klatkę po jej globalnym indeksie. Wariant dwuargumentowy wybiera zdarzenie, a następnie pozycję w nim (przez numer lub nazwę klatki).

**Parametry**

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

### SHOW

```
void SHOW()
```

Pokazuje animację (ustawia [`VISIBLE`](#visible) na `TRUE`).

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
