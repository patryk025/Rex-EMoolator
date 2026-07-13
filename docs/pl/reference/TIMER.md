# TIMER

Cykliczny licznik czasu emitujący sygnał [`ONTICK`](#ontick) co `ELAPSE` milisekund. Pozwala uruchamiać kod skryptu w regularnych odstępach — bez timera nie da się w silniku zrealizować nic, co wymaga upływu czasu (animowanie wartości, opóźnienia, regeneracja).

Wartość zmiennej (`value`) typu `TIMER` to liczba dotychczas wykonanych tyknięć.

## Pola

### ELAPSE

```
INTEGER ELAPSE
```

Długość cyklu timera w milisekundach. Dla wartości `0` lub mniejszej timer nie tyka, nawet jeżeli jest włączony.

### ENABLED

```
BOOL ENABLED
```

Określa, czy timer ma działać po inicjalizacji. Domyślnie `TRUE`.

### TICKS

```
INTEGER TICKS
```

Limit cykli — po wyemitowaniu tylu tyknięć timer wyłącza się automatycznie (`ENABLED = FALSE`). Wartość `0` oznacza brak limitu (timer tyka bez końca).

## Metody

### DISABLE

```
void DISABLE()
```

Wyłącza timer. Sygnał [`ONTICK`](#ontick) przestaje być emitowany; bieżąca liczba tyknięć nie zostaje wyzerowana.

**Przykłady**

```
TIMER1^DISABLE();
```

**Kompatybilność:** `DISABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLE

```
void ENABLE()
```

Włącza timer i ustawia punkt odniesienia odmierzania na *teraz*. Pierwszy tick po wywołaniu zostanie wyemitowany po upływie pełnego `ELAPSE`, niezależnie od tego, ile czasu minęło od ostatniego wyłączenia.

**Przykłady**

```
TIMCNV^ENABLE();
TIMER2^ENABLE();
```

**Kompatybilność:** `ENABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETTICKS

```
INTEGER GETTICKS()
```

Zwraca liczbę dotychczas wykonanych tyknięć (zmierzoną od ostatniego wywołania [`RESET`](#reset) lub od inicjalizacji).

**Zwraca**: [`INTEGER`](INTEGER.md) — liczba tyknięć.

**Przykłady**

```
TIMER1^GETTICKS();
```

**Kompatybilność:** `GETTICKS` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESET

```
void RESET()
```

Zeruje licznik tyknięć i ustawia punkt odniesienia odmierzania na *teraz*.

**Przykłady**

```
TIMER1^RESET();
```

**Kompatybilność:** `RESET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
void SET(INTEGER ticks)
```

Ustawia limit tyknięć w polu [`TICKS`](#ticks). Wartość `0` znosi limit (timer tyka bez końca).

**Parametry**

- `ticks` — nowy limit cykli.

**Kompatybilność:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETELAPSE

```
void SETELAPSE(INTEGER timeMs)
```

Ustawia długość cyklu timera w milisekundach i ustawia punkt odniesienia odmierzania na *teraz*.

**Parametry**

- `timeMs` — nowa długość cyklu (w milisekundach).

**Przykłady**

```
TIMERSEQ^SETELAPSE(RANDOM^GET(30000,10000));
TIMERPIECHUR^SETELAPSE(ARRAYPIECHURZYPARAM^GET(0));
TIMER1^SETELAPSE(VARTIMEUFO);
```

**Kompatybilność:** `SETELAPSE` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONTICK

Wywoływany po każdym pełnym cyklu o długości [`ELAPSE`](#elapse), o ile timer jest włączony. Argumentem (`$1`) jest aktualna liczba tyknięć (`currentTickCount`).

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
