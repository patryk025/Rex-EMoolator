# VECTOR

N-wymiarowy wektor liczb zmiennoprzecinkowych. W praktyce wykorzystywany do reprezentowania współrzędnych dwu- lub trójwymiarowych — w grach z silnika spotykany m.in. w minigrach z fizyką (odbicia, normalizacja kierunku ruchu).

Wartość zmiennej (`value`) typu `VECTOR` to długość euklidesowa wektora, czyli `sqrt(x1² + x2² + … + xN²)`.

## Pola

### SIZE

```
INTEGER SIZE
```

Liczba współrzędnych wektora. Wartości w grach to zwykle `2` lub `3`.

!!! note "SIZE wyznacza zakres obliczeń"
    Metody arytmetyczne — [`ADD`](#add), [`MUL`](#mul), [`NORMALIZE`](#normalize) i [`REFLECT`](#reflect) — działają na **dokładnie `SIZE`** współrzędnych: brakujące traktują jak `0`, a współrzędne ponad `SIZE` (np. dodane przez [`ASSIGN`](#assign)) **pomijają**. Z kolei [`LEN`](#len) oraz wartość zmiennej (długość euklidesowa) liczą się po **wszystkich** faktycznie zapisanych współrzędnych. Te dwa zbiory mogą się rozjechać — patrz uwaga przy [`ASSIGN`](#assign).

### VALUE

```
DOUBLE, DOUBLE, [DOUBLE...] VALUE
```

Lista wartości startowych poszczególnych współrzędnych. Liczba pozycji powinna odpowiadać polu [`SIZE`](#size).

## Metody

### ADD

```
void ADD(STRING|VECTOR vectorName)
```

Dodaje do bieżącego wektora wartości drugiego wektora pozycyjnie. Wynik jest zapisywany w wektorze, na którym wywołano metodę.

**Parametry**

- `vectorName` — wektor dodawany; przekazany jako [`STRING`](STRING.md) (nazwa) lub bezpośrednio jako `VECTOR`.

**Przykłady**

```
VTEMP2^ADD("VTOCENTER");
VTEMP2^ADD(VTOCENTER);
```

### ASSIGN

```
void ASSIGN(DOUBLE x1, DOUBLE x2, [DOUBLE...])
```

Przypisuje wektorowi nowe wartości współrzędnych. Liczba argumentów wyznacza, ile współrzędnych zostanie nadpisanych — pozostałe (jeśli wektor był większy) zachowują swoje wcześniejsze wartości. Jeśli liczba argumentów przekracza bieżącą liczbę zapisanych współrzędnych, lista wartości zostaje rozszerzona.

!!! warning "ASSIGN nie zmienia SIZE"
    `ASSIGN` zapisuje (i w razie potrzeby dokłada) **wartości**, ale **nie aktualizuje pola [`SIZE`](#size)**. Przypisanie większej liczby argumentów niż `SIZE` zostawia nadmiarowe współrzędne poza zakresem obliczeń: będą one widoczne przez [`GET`](#get) i wliczane do [`LEN`](#len)/długości, ale [`ADD`](#add), [`MUL`](#mul), [`NORMALIZE`](#normalize) i [`REFLECT`](#reflect) nadal obejmą tylko pierwsze `SIZE` współrzędnych. Aby je uwzględnić, `SIZE` trzeba ustawić od razu poprawnie podczas definicji obiektu.

**Parametry**

- `x1, x2, …` — kolejne nowe wartości współrzędnych.

**Przykłady**

```
VTEMP1^ASSIGN(0.0,0.0);
VTEMP1^ASSIGN(ARRDIRX^GET(VARPLAYER),ARRDIRY^GET(VARPLAYER));
VNORMAL^ASSIGN([ARRPOSX^GET(VARPLAYER)+ARRHWIDTH^GET(VARPLAYER)],[ARRPOSY^GET(VARPLAYER)+ARRHHEIGHT^GET(VARPLAYER)]);
```

### GET

```
DOUBLE GET(INTEGER index)
```

Zwraca wartość współrzędnej o podanym indeksie (liczonym od zera). Dla indeksów spoza zakresu zwracana jest wartość `0.0`.

**Parametry**

- `index` — indeks współrzędnej (`0`-bazowany).

**Zwraca**: [`DOUBLE`](DOUBLE.md) — wartość współrzędnej lub `0.0`.

**Przykłady**

```
VTEMP1^GET(0);
VTEMP1^GET(1);
```

### LEN

```
DOUBLE LEN()
```

Zwraca długość euklidesową wektora.

**Zwraca**: [`DOUBLE`](DOUBLE.md) — długość wektora.

### MUL

```
void MUL(DOUBLE scalar)
```

Mnoży każdą współrzędną wektora przez skalar.

**Parametry**

- `scalar` — mnożnik.

**Przykłady**

```
VTEMP1^MUL(10.0);
VTEMP1^MUL(ARRSPEED^GET(VARPLAYER));
VTEMP2^MUL(-1);
```

### NORMALIZE

```
void NORMALIZE()
```

Normalizuje wektor do długości `1` (dzieli każdą współrzędną przez aktualną długość). Wywołanie na wektorze zerowym nie zmienia jego wartości.

**Przykłady**

```
VNORMAL^NORMALIZE();
VTEMP1^NORMALIZE();
```

### REFLECT

```
void REFLECT(STRING|VECTOR normalVector, STRING|VECTOR resultVector)
```

Oblicza odbicie bieżącego wektora względem wektora normalnego i zapisuje wynik do wektora docelowego. Bieżący wektor pozostaje niezmieniony.

!!! warning "REFLECT zwraca odbicie z odwróconym znakiem"
    Dla wektora padającego `d` (bieżący) i normalnej `n` silnik liczy `wynik = 2·(d·n)·n − d`, czyli **negację** klasycznego wzoru na odbicie `d − 2·(d·n)·n`. W praktyce zwrócony wektor jest skierowany **przeciwnie** do podręcznikowego odbicia (odwzorowuje to zachowanie oryginalnego silnika). Jeśli potrzebujesz „zwykłego" odbicia, odwróć wynik [`MUL(-1)`](#mul). Normalna powinna być **znormalizowana** ([`NORMALIZE`](#normalize)) — wzór zakłada długość `1`. Obliczenie obejmuje [`SIZE`](#size) współrzędnych bieżącego wektora.

**Parametry**

- `normalVector` — wektor normalny do powierzchni, względem której następuje odbicie.
- `resultVector` — wektor, do którego zostanie zapisany wynik.

**Przykłady**

```
VINCIDENT^REFLECT("VNORMAL","VREFLECT");
VINCIDENT^REFLECT(VNORMAL,VREFLECT);
```

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
