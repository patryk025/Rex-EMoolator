# BUTTON

Interaktywny przycisk z trzema stanami wizualnymi (normalny, najechany, wciśnięty) oraz osobnymi grafikami i dźwiękami dla każdego z nich. Przycisk można dodatkowo wyłączyć — albo całkowicie (`DISABLE`), albo z zachowaniem widoczności (`DISABLEBUTVISIBLE`).

Wewnętrznie przycisk jest maszyną stanów. Każde przejście stanu automatycznie pokazuje lub ukrywa odpowiednie grafiki, odtwarza powiązany dźwięk oraz może wyemitować sygnał.

## Stany przycisku

| Stan | Znaczenie |
| --- | --- |
| `STANDARD` | spoczynek — pokazywana jest grafika z `GFXSTANDARD`. |
| `HOVERED` | kursor jest nad obszarem przycisku — pokazywana jest grafika z `GFXONMOVE` (jeśli ustawiona, w przeciwnym razie nadal `GFXSTANDARD`). |
| `PRESSED` | przycisk jest aktualnie wciśnięty — pokazywana jest grafika z `GFXONCLICK` (jeśli ustawiona, w przeciwnym razie nadal `GFXSTANDARD`). |
| `DISABLED` | przycisk wyłączony i ukryty. |
| `DISABLED_BUT_VISIBLE` | przycisk wyłączony, ale pokazywana jest grafika z `GFXSTANDARD`. |

## Pola

### DRAG

```
STRING DRAG
```

Nazwa zmiennej [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md), która ma być przesuwana podczas przeciągania. Pole jest opcjonalne. Jeżeli nie wskazuje prawidłowej grafiki, silnik używa kolejno [`GFXSTANDARD`](#gfxstandard), a następnie [`GFXONMOVE`](#gfxonmove).

### DRAGGABLE

```
BOOL DRAGGABLE
```

Określa, czy przycisk można przeciągać.

### ENABLE

```
BOOL ENABLE
```

Określa, czy przycisk ma być włączony po inicjalizacji. Wartość `FALSE` umieszcza przycisk w stanie [`DISABLED`](#stany-przycisku).

### GFXONCLICK

```
STRING GFXONCLICK
```

Nazwa zmiennej [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md) pokazywanej w stanie [`PRESSED`](#stany-przycisku). Pole opcjonalne — jeżeli nie zostanie ustawione, w stanie `PRESSED` nadal wyświetlana jest grafika z [`GFXSTANDARD`](#gfxstandard).

### GFXONMOVE

```
STRING GFXONMOVE
```

Nazwa zmiennej [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md) pokazywanej w stanie [`HOVERED`](#stany-przycisku). Pole opcjonalne.

### GFXSTANDARD

```
STRING GFXSTANDARD
```

Nazwa zmiennej [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md) wyświetlanej w stanie spoczynku. Jeżeli pole [`ENABLE`](#enable) zostało ustawione na `FALSE`, grafika jest automatycznie ukrywana po inicjalizacji — z jednym wyjątkiem: jeżeli powiązana animacja jest właśnie w trakcie odtwarzania, pozostaje widoczna (przykład: pochodnia w `16BLAWA` w *Reksio i Skarb Piratów*). W takim przypadku ustawienie [`TOCANVAS`](ANIMO.md#tocanvas) na powiązanej grafice nie ma znaczenia.

### RECT

```
INTEGER, INTEGER, INTEGER, INTEGER RECT
STRING RECT
```

Obszar reagujący na kursor. Pole może być podane na dwa sposoby:

- Cztery liczby całkowite `xLeft,yBottom,xRight,yTop` definiujące prostokąt na ekranie.
- Nazwa zmiennej typu [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md), z której pobierany jest bieżący prostokąt graficzny.

Jeżeli pole nie zostanie ustawione, używany jest prostokąt zmiennej wskazanej przez [`GFXSTANDARD`](#gfxstandard).

### SNDONCLICK

```
STRING SNDONCLICK
```

Nazwa zmiennej [`SOUND`](SOUND.md) odtwarzanej przy przejściu w stan [`PRESSED`](#stany-przycisku).

### SNDONMOVE

```
STRING SNDONMOVE
```

Nazwa zmiennej [`SOUND`](SOUND.md) odtwarzanej przy przejściu w stan [`HOVERED`](#stany-przycisku).

### SNDSTANDARD

```
STRING SNDSTANDARD
```

Nazwa zmiennej [`SOUND`](SOUND.md) odtwarzanej przy powrocie do stanu [`STANDARD`](#stany-przycisku).

### VISIBLE

```
BOOL VISIBLE
```

Pole zapisywane w danych skryptowych, ale nieobserwowane przez silnik — z testów wynika, że jego wartość nie wpływa na żadne zachowanie przycisku.

## Metody

### DISABLE

```
void DISABLE()
```

Wyłącza przycisk i ukrywa powiązane z nim grafiki ustawione przez [`GFXSTANDARD`](#gfxstandard), [`GFXONMOVE`](#gfxonmove) i [`GFXONCLICK`](#gfxonclick). Grafiki podpięte przez [`RECT`](#rect) pozostają bez zmian.

**Przykłady**

```
B_GLOBAL_PAUSE^DISABLE();
```

**Kompatybilność:** `DISABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DISABLEBUTVISIBLE

```
void DISABLEBUTVISIBLE()
```

Wyłącza przycisk, ale pozostawia widoczną grafikę z [`GFXSTANDARD`](#gfxstandard) (analogicznie do `DISABLE`, ale stan końcowy to [`DISABLED_BUT_VISIBLE`](#stany-przycisku)).

**Przykłady**

```
BTNBONE^DISABLEBUTVISIBLE();
BTNFORGOT^DISABLEBUTVISIBLE();
```

**Kompatybilność:** `DISABLEBUTVISIBLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLE

```
void ENABLE()
```

Włącza przycisk i przywraca widoczność grafice z [`GFXSTANDARD`](#gfxstandard). Wywołanie na już włączonym przycisku nie ma efektu.

**Przykłady**

```
B_GLOBAL_PAUSE^ENABLE();
BTNEXIT^ENABLE();
```

**Kompatybilność:** `ENABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLEDRAGGING

```
void ENABLEDRAGGING()
```

Włącza możliwość przeciągania przycisku, analogicznie do ustawienia [`DRAGGABLE`](#draggable) na `TRUE`.

**Kompatybilność:** `ENABLEDRAGGING` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DISABLEDRAGGING

```
void DISABLEDRAGGING()
```

Wyłącza rozpoczynanie nowych operacji przeciągania.

**Kompatybilność:** `DISABLEDRAGGING` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPRIORITY

```
void SETPRIORITY(INTEGER posZ)
```

Ustawia priorytet rysowania (pozycję w osi Z) dla wszystkich trzech grafik powiązanych z przyciskiem ([`GFXSTANDARD`](#gfxstandard), [`GFXONMOVE`](#gfxonmove), [`GFXONCLICK`](#gfxonclick)). Wyższa wartość oznacza rysowanie później (z wierzchu).

**Parametry**

- `posZ` — nowa wartość priorytetu.

**Przykłady**

```
B_GLOBAL_PAUSE^SETPRIORITY(5001);
BTNNULLFADE^SETPRIORITY(40000);
```

**Kompatybilność:** `SETPRIORITY` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETRECT

```
void SETRECT(STRING varName)
void SETRECT(INTEGER xLeft, INTEGER yBottom, INTEGER xRight, INTEGER yTop)
```

Ustawia obszar reagujący na kursor. Metoda ma dwie formy: pierwsza pobiera prostokąt z zadanej zmiennej [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md), druga definiuje go bezpośrednio przez koordynaty.

**Parametry**

- `varName` — nazwa zmiennej graficznej **(forma 1)**.
- `xLeft`, `yBottom`, `xRight`, `yTop` — koordynaty prostokąta **(forma 2)**.

**Przykłady**

```
EXITPROGAM^SETRECT("ANNEXIT");
*STMP0^SETRECT([ITMPX+[ITMPDX*_I_]],[ITMPY+[ITMPDY*_I_]],[ITMPX+15+[ITMPDX*_I_]],[ITMPY+15+[ITMPDY*_I_]]);
```

**Kompatybilność:** `SETRECT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETSTD

```
void SETSTD(STRING varName)
void SETSTD(STRING varName, BOOL flag)
```

Ustawia grafikę standardową przycisku (pole [`GFXSTANDARD`](#gfxstandard)) i zeruje priorytet nowej grafiki. W skryptach gier występuje również forma dwuargumentowa z dodatkową flagą boolowską — zawsze wywoływana z wartością `FALSE`, znaczenie tego argumentu nie zostało ustalone.

**Parametry**

- `varName` — nazwa nowej zmiennej standardowej (pusty ciąg `""` czyści powiązanie).
- `flag` — flaga konfiguracyjna **(forma 2, znaczenie nieustalone)**.

**Przykłady**

```
BTNEXIT^SETSTD("ANNOBJECT2");
B_GLOBAL_PAUSE^SETSTD("",FALSE);
BTNBAD^SETSTD("ZLY",FALSE);
```

**Kompatybilność:** `SETSTD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONFOCUSON

Wywoływany przy przejściu ze stanu [`STANDARD`](#stany-przycisku) do [`HOVERED`](#stany-przycisku) — czyli gdy kursor wjedzie na przycisk.

### ONFOCUSOFF

Wywoływany przy przejściu z [`HOVERED`](#stany-przycisku) do [`STANDARD`](#stany-przycisku) — gdy kursor opuści przycisk.

### ONCLICKED

Wywoływany przy przejściu w stan [`PRESSED`](#stany-przycisku) (wciśnięcie przycisku myszy).

### ONRELEASED

Wywoływany przy przejściu z [`PRESSED`](#stany-przycisku) do [`HOVERED`](#stany-przycisku) — gdy przycisk myszy zostanie zwolniony nad obszarem przycisku.

### ONACTION

Wywoływany razem z [`ONRELEASED`](#onreleased) — sygnał potwierdzający, że nastąpiło kliknięcie (wciśnięcie i zwolnienie nad obszarem przycisku).

### ONSTARTDRAGGING

Wywoływany po rozpoczęciu przeciągania przycisku (dostępne tylko dla przycisków z polem [`DRAGGABLE`](#draggable) ustawionym na `TRUE`). W tym momencie [`SCENE^GETDRAGGEDNAME()`](SCENE.md#getdraggedname) zwraca nazwę przesuwanej grafiki.

### ONDRAGGING

Wywoływany podczas przesuwania grafiki, gdy pozycja kursora ulegnie zmianie.

### ONENDDRAGGING

Wywoływany po zakończeniu przeciągania przycisku.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
