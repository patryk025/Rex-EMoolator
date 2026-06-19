# ARRAY

Tablica indeksowana od `0`, przechowująca wartości dowolnego typu. Pełna obsługa zapisu i operacji arytmetycznych dotyczy czterech typów prymitywnych: [`INTEGER`](INTEGER.md), [`DOUBLE`](DOUBLE.md), [`STRING`](STRING.md) i [`BOOL`](BOOL.md). Mieszane typy w pojedynczej tablicy są dozwolone, ale niektóre metody mogą interpretować elementy niezgodnie z intuicją (patrz uwagi przy [`FIND`](#find), [`CONTAINS`](#contains) i [`GETSUMVALUE`](#getsumvalue)).

## Pola

### TOINI

```
BOOL TOINI
```

Określa, czy zawartość tablicy jest serializowana do pliku INI i przywracana po ponownym uruchomieniu.

## Metody

### ADD

```
void ADD(mixed value1, [mixed value2, ..., mixed valueN])
```

Dodaje przekazane wartości na koniec tablicy. Typy argumentów nie muszą być jednakowe.

**Parametry**

- `value1, …, valueN` — wartości do dodania.

**Przykłady**

```
G_ARRSETTINGS^ADD(0, 600);
G_ARRDATAS^ADD("PODWIECZOREK1", "PODWIECZOREK2", "PODWIECZOREK3");
ARRFLAMESDIR^ADD("R", "R", "R", "L", "L");
ARR_JOINTS^ADD(FALSE, 6, "B", 10, "A", 4);
```

### ADDAT

```
void ADDAT(INTEGER index, mixed value)
```

Dodaje argument do elementu na pozycji `index`. Operacja konwertuje element i argument do `DOUBLE`, dodaje, i zapisuje wynik jako [`DOUBLE`](DOUBLE.md) — typ elementu na tej pozycji po wywołaniu zawsze jest `DOUBLE`. Wywołanie z `index` poza zakresem nie zmienia tablicy.

**Parametry**

- `index` — pozycja elementu (numerowana od `0`).
- `value` — wartość do dodania.

**Przykłady**

```
ARRIDLETIME^ADDAT(0, 1);
ARRENEMYY^ADDAT(VARITMPINDEX, VARITMP1);
ARRSPEED^ADDAT(0, 2.0);
```

### CHANGEAT

```
void CHANGEAT(INTEGER index, mixed value)
```

Zastępuje element na pozycji `index` przekazaną wartością. Typ nowego elementu jest zachowywany dokładnie taki, jaki został przekazany. Wywołanie z `index` poza zakresem nie zmienia tablicy.

**Parametry**

- `index` — pozycja elementu (numerowana od `0`).
- `value` — nowa wartość.

**Przykłady**

```
ARRIDLETIME^CHANGEAT(0, 0);
G_ARRREXSPELLS^CHANGEAT(VARIREPEATSPELL, 1);
ARRAYPLAYERSSTATE^CHANGEAT([VARCLONE-1], "NULL");
```

### CLAMPAT

```
void CLAMPAT(INTEGER index, mixed rangeMin, mixed rangeMax)
```

Sprowadza wartość na pozycji `index` do przedziału `[rangeMin, rangeMax]`. Typ elementu jest zachowywany — wartości `INTEGER` pozostają `INTEGER`, wartości `DOUBLE` pozostają `DOUBLE`. Dla elementów innych typów (oraz dla `index` poza zakresem) wywołanie nie zmienia tablicy.

**Parametry**

- `index` — pozycja elementu (numerowana od `0`).
- `rangeMin` — dolna granica (włącznie).
- `rangeMax` — górna granica (włącznie).

**Przykłady**

```
ARRSPEED^CLAMPAT(VARPLAYER, 0.0, 100.0);
ARRSPEED^CLAMPAT(0, 0.0, 17.0);
```

### CONTAINS

```
BOOL CONTAINS(mixed needle)
```

Sprawdza, czy tablica zawiera element o podanej wartości. Porównanie wykonywane jest na poziomie tekstowej reprezentacji elementów — wartość `needle` jest rzutowana do [`STRING`](STRING.md) i porównywana z wynikiem `toDisplayString` każdego elementu. To różni się od `FIND`, które porównuje wartości zgodnie z [regułami arytmetyki silnika](../engine/arithmetic.md#regula-typowania).

**Parametry**

- `needle` — szukana wartość.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli element został znaleziony.

**Przykłady**

```
ART0^CONTAINS(ICIK);
ARRAYWARSZTATPRZEDMIOTY^CONTAINS(ARRAYTEMP^GET($1));
```

### COPYTO

```
void COPYTO(STRING arrayVarName)
```

Dokleja zawartość bieżącej tablicy na koniec tablicy o nazwie podanej w argumencie. Tablica docelowa musi już istnieć i być typu `ARRAY`. Tablica docelowa **nie** jest czyszczona przed kopiowaniem.

**Parametry**

- `arrayVarName` — nazwa docelowej zmiennej tablicowej.

**Przykłady**

```
ARAG^COPYTO("ARTMP");
```

### FIND

```
INTEGER FIND(mixed needle)
```

Wyszukuje w tablicy pierwszy element równy podanej wartości. Porównanie wykonywane jest zgodnie z [regułami arytmetyki silnika](../engine/arithmetic.md#regula-typowania) — typ elementu w tablicy wyznacza, do jakiego typu rzutowana jest wartość `needle` w danej iteracji. Skutkuje to nieintuicyjnymi wynikami w tablicach mieszanych typów, np. wyszukiwanie `240` w tablicy zawierającej `TRUE` zwróci indeks elementu `TRUE`, ponieważ `240` zostaje rzutowane do `BOOL` (wartość różna od zera, czyli `TRUE`).

**Parametry**

- `needle` — szukana wartość.

**Zwraca**: indeks pierwszego dopasowanego elementu, lub `-1`, jeżeli element nie został znaleziony.

**Przykłady**

```
G_ARRCUTSCENES^FIND(G_SCUTSCENE);
ARRSTARTNAME0^FIND("NULL");
ARRCLONES^FIND(-1);
```

### GET

```
mixed GET(INTEGER index)
```

Zwraca element na pozycji `index`. Dla `index` poza zakresem zwracana jest wartość `NULL`.

**Parametry**

- `index` — pozycja elementu (numerowana od `0`).

**Zwraca**: wartość elementu lub `NULL`.

**Przykłady**

```
ARRIDLETIME^GET(0);
ARRACTIVESPELLS^GET(_I_);
ARRAYPLAYERSSTATE^GET([VARCLONE-1]);
```

### GETSIZE

```
INTEGER GETSIZE()
```

Zwraca liczbę elementów w tablicy.

**Zwraca**: rozmiar tablicy.

**Przykłady**

```
G_ARRSETTINGS^GETSIZE();
ARRENEMYROUTEX^GETSIZE();
```

### GETSUMVALUE

```
DOUBLE GETSUMVALUE()
```

Zwraca sumę wartości wszystkich elementów tablicy. Każdy element jest rzutowany do `DOUBLE` zgodnie z [regułami konwersji](../engine/arithmetic.md#konwersje-typow); elementy nienumeryczne mogą wnosić niespodziewane wartości (`BOOL` → `1.0` lub `0.0`, [`STRING`](STRING.md) nie będący liczbą → `0.0`).

**Zwraca**: suma wartości jako [`DOUBLE`](DOUBLE.md).

**Przykłady**

```
ARCONTAINER^GETSUMVALUE();
```

### INSERTAT

```
void INSERTAT(INTEGER index, mixed value)
```

Wstawia wartość na pozycji `index`, przesuwając istniejące elementy w prawo. Dopuszczalne wartości `index` to `[0, rozmiar]` — wstawienie na pozycji równej rozmiarowi tablicy dokleja element na koniec. Wywołanie z `index` poza tym zakresem nie zmienia tablicy.

**Parametry**

- `index` — pozycja wstawienia.
- `value` — wstawiana wartość.

**Przykłady**

```
ARRTURNIEJ^INSERTAT(I3, I1);
ARRTURNIEJ^INSERTAT(4, I1);
```

### LOAD

```
void LOAD(STRING path)
```

Zastępuje zawartość tablicy danymi wczytanymi z binarnego pliku `.ARR`. Plik zapisany jest w little-endian: 4-bajtowa liczba elementów, a następnie dla każdego elementu 4-bajtowy znacznik typu (`1`=`INTEGER`, `2`=`STRING`, `3`=`BOOL`, `4`=`DOUBLE`) i odpowiadająca mu reprezentacja wartości.

**Parametry**

- `path` — ścieżka pliku `.ARR` w VFS gry.

**Przykłady**

```
G_ARRSETTINGS^LOAD("$COMMON\SETTINGS.ARR");
ARRPATH^LOAD(["MAPA"+ILEVEL+".ARR"]);
```

### LOADINI

```
void LOADINI()
```

Zastępuje zawartość tablicy danymi zserializowanymi w pliku INI gry pod kluczem o nazwie tej zmiennej. Format zapisu w INI to lista wartości oddzielonych przecinkami:

```
NAZWA_TABLICY=wartość1,wartość2,wartość3,...
```

Każdy element jest interpretowany kolejno jako [`INTEGER`](INTEGER.md), [`DOUBLE`](DOUBLE.md), [`BOOL`](BOOL.md) lub [`STRING`](STRING.md) — pierwszy pasujący typ zostaje przyjęty.

**Przykłady**

```
ARRAYWARSZTATMENUPRZEDMIOTY^LOADINI();
```

### MODAT

```
void MODAT(INTEGER index, mixed divisor)
```

Zapisuje resztę z dzielenia elementu na pozycji `index` przez argument. Operacja konwertuje element i argument do `DOUBLE`, wykonuje modulo, i zapisuje wynik jako [`DOUBLE`](DOUBLE.md). Dzielenie przez zero oraz wywołanie z `index` poza zakresem nie zmieniają tablicy.

**Parametry**

- `index` — pozycja elementu.
- `divisor` — dzielnik.

**Przykłady**

```
ARRANGLE^MODAT(VARPLAYER, 360);
```

### MULAT

```
void MULAT(INTEGER index, mixed multiplier)
```

Mnoży element na pozycji `index` przez argument. Operacja konwertuje element i argument do `DOUBLE`, mnoży, i zapisuje wynik jako [`DOUBLE`](DOUBLE.md). Wywołanie z `index` poza zakresem nie zmienia tablicy.

**Parametry**

- `index` — pozycja elementu.
- `multiplier` — mnożnik.

**Przykłady**

```
ARRDIRY^MULAT(VARPLAYER, -1.0);
ARRDIRX^MULAT(VARPLAYER, -1);
```

### REMOVEALL

```
void REMOVEALL()
```

Usuwa wszystkie elementy z tablicy.

**Przykłady**

```
G_ARRSETTINGS^REMOVEALL();
ARRTEMP^REMOVEALL();
```

### REMOVEAT

```
void REMOVEAT(INTEGER index)
```

Usuwa element na pozycji `index`, przesuwając pozostałe w lewo. Wywołanie z `index` poza zakresem nie zmienia tablicy.

**Parametry**

- `index` — pozycja usuwanego elementu.

**Przykłady**

```
ARRTEMP^REMOVEAT(VARITEMP2);
ARRENEMYROUTEX^REMOVEAT(0);
```

### REVERSEFIND

```
INTEGER REVERSEFIND(mixed needle)
```

Działa jak [`FIND`](#find), ale przeszukuje tablicę od końca. Stosują się te same reguły porównań zależnych od typu elementu.

**Parametry**

- `needle` — szukana wartość.

**Zwraca**: indeks ostatniego dopasowanego elementu, lub `-1`, jeżeli element nie został znaleziony.

**Przykłady**

```
ARRAYKURNIKFREESLOTS^REVERSEFIND(0);
```

### SAVE

```
void SAVE(STRING path)
```

Zapisuje zawartość tablicy do binarnego pliku `.ARR` w formacie opisanym przy [`LOAD`](#load).

**Parametry**

- `path` — ścieżka docelowego pliku `.ARR` w VFS gry.

**Przykłady**

```
G_ARRSETTINGS^SAVE("$COMMON\SETTINGS.ARR");
ARRPATH^SAVE(["MAPA"+ILEVEL+".ARR"]);
```

### SAVEINI

```
void SAVEINI()
```

Serializuje zawartość tablicy do pliku INI gry pod kluczem o nazwie tej zmiennej, w formacie listy wartości rozdzielonych przecinkami (opisany przy [`LOADINI`](#loadini)).

**Przykłady**

```
ARRAYPLATFORMOWKAPRZEDMIOTY^SAVEINI();
```

### SUB

```
void SUB(mixed value)
```

Odejmuje argument od każdego elementu tablicy. Operacja konwertuje każdy element do `DOUBLE` przed odjęciem; wszystkie elementy po wywołaniu mają typ `DOUBLE`.

**Parametry**

- `value` — wartość odejmowana.

**Przykłady**

```
ARRAYBKGA^SUB([0-VARINT2]);
```

### SUBAT

```
void SUBAT(INTEGER index, mixed value)
```

Odejmuje argument od elementu na pozycji `index`. Operacja konwertuje element i argument do `DOUBLE`, odejmuje, i zapisuje wynik jako [`DOUBLE`](DOUBLE.md). Wywołanie z `index` poza zakresem nie zmienia tablicy.

**Parametry**

- `index` — pozycja elementu.
- `value` — wartość odejmowana.

**Przykłady**

```
ARRBUTTONPRESSED^SUBAT(IBUTTONNR, 1);
ARRSPEED^SUBAT(VARPLAYER, 0.15);
```

### SUM

```
void SUM(mixed value)
```

Dodaje argument do każdego elementu tablicy. Operacja konwertuje każdy element do `DOUBLE` przed dodaniem; wszystkie elementy po wywołaniu mają typ `DOUBLE`.

**Parametry**

- `value` — wartość dodawana.

**Przykłady**

```
ARRCHICKENX^SUM(-60);
ARRCHICKENY^SUM(-110);
```

## Sygnały

### ONCHANGE

Wywoływany po dokonaniu zmiany w tablicy.

### ONINIT

Wywoływany w momencie inicjalizacji zmiennej.

### ONDONE

Wywoływany w momencie wychodzenia ze sceny, do której należy zmienna.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (przez wywołanie metody `SEND` — zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
