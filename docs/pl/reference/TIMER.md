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

### RESET

```
void RESET()
```

Zeruje licznik tyknięć i ustawia punkt odniesienia odmierzania na *teraz*.

**Przykłady**

```
TIMER1^RESET();
```

### SET

```
void SET(INTEGER ticks)
```

Ustawia limit tyknięć w polu [`TICKS`](#ticks). Wartość `0` znosi limit (timer tyka bez końca).

**Parametry**

- `ticks` — nowy limit cykli.

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

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONTICK

Wywoływany po każdym pełnym cyklu o długości [`ELAPSE`](#elapse), o ile timer jest włączony. Argumentem (`$1`) jest aktualna liczba tyknięć (`currentTickCount`).

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
