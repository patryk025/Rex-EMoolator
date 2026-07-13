# MOUSE

Wbudowany obiekt reprezentujący stan myszy. Dostępny pod globalną nazwą `MOUSE` z dowolnego kontekstu (zobacz [Obiekty wbudowane](../engine/globals.md#obiekty-wbudowane)). Obsługuje pozycję kursora, stan przycisków oraz reaktywne zdarzenia kliknięcia, ruchu i podwójnego kliknięcia.

## Pola

### RAW

```
BOOL RAW
```

Określa, czy obiekt odczytuje surowe dane myszy (z pominięciem przyspieszenia i kalibracji systemowej).

## Metody

### DISABLE

```
void DISABLE()
```

Wyłącza odbieranie zdarzeń myszy — kursor przestaje aktualizować pozycję, a sygnały nie są emitowane.

**Przykłady**

```
MOUSE^DISABLE();
```

**Kompatybilność:** `DISABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DISABLESIGNAL

```
void DISABLESIGNAL()
```

Wyłącza emisję sygnałów myszy. W przeciwieństwie do [`DISABLE`](#disable) pozycja kursora nadal jest śledzona, ale obsługa sygnałów ([`ONMOVE`](#onmove), [`ONCLICK`](#onclick) itd.) nie jest wywoływana.

**Przykłady**

```
MOUSE^DISABLESIGNAL();
```

**Kompatybilność:** `DISABLESIGNAL` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLE

```
void ENABLE()
```

Włącza odbieranie zdarzeń myszy.

**Przykłady**

```
MOUSE^ENABLE();
```

**Kompatybilność:** `ENABLE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ENABLESIGNAL

```
void ENABLESIGNAL()
```

Włącza emisję sygnałów myszy.

**Przykłady**

```
MOUSE^ENABLESIGNAL();
```

**Kompatybilność:** `ENABLESIGNAL` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSX

```
INTEGER GETPOSX()
```

Zwraca aktualną pozycję kursora w osi X.

**Zwraca**: współrzędna X kursora.

**Przykłady**

```
MOUSE^GETPOSX();
```

**Kompatybilność:** `GETPOSX` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPOSY

```
INTEGER GETPOSY()
```

Zwraca aktualną pozycję kursora w osi Y.

**Zwraca**: współrzędna Y kursora.

**Przykłady**

```
MOUSE^GETPOSY();
```

**Kompatybilność:** `GETPOSY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### HIDE

```
void HIDE()
```

Ukrywa kursor myszy.

**Przykłady**

```
MOUSE^HIDE();
```

**Kompatybilność:** `HIDE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ISLBUTTONDOWN

```
BOOL ISLBUTTONDOWN()
```

Sprawdza, czy lewy przycisk myszy jest aktualnie wciśnięty.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli przycisk jest wciśnięty.

**Przykłady**

```
MOUSE^ISLBUTTONDOWN();
```

**Kompatybilność:** `ISLBUTTONDOWN` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (4/5).

### SET

```
void SET(STRING cursorStyle)
```

Ustawia styl kursora.

**Parametry**

- `cursorStyle` — nazwa stylu kursora (np. `"ACTIVE"`, `"ARROW"`).

**Przykłady**

```
MOUSE^SET("ACTIVE");
MOUSE^SET("ARROW");
```

**Kompatybilność:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPOSITION

```
void SETPOSITION(INTEGER posX, INTEGER posY)
```

Ustawia pozycję kursora myszy na ekranie. Jeżeli pozycja faktycznie się zmieniła, dodatkowo emitowany jest sygnał [`ONMOVE`](#onmove).

**Parametry**

- `posX` — nowa współrzędna X kursora.
- `posY` — nowa współrzędna Y kursora.

**Przykłady**

```
MOUSE^SETPOSITION(400, 300);
MOUSE^SETPOSITION(MOUSE^GETPOSX(), VARINT0);
MOUSE^SETPOSITION(ANNMUCHA0^GETCENTERX(), ANNMUCHA0^GETCENTERY());
```

**Kompatybilność:** `SETPOSITION` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SHOW

```
void SHOW()
```

Wyświetla kursor myszy.

**Kompatybilność:** `SHOW` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONCLICK

Wywoływany po kliknięciu przycisku myszy. Sygnał jest [parametryzowany](../engine/events.md#sygnaly-parametryzowane) nazwą wciśniętego przycisku (`LEFT`, `RIGHT`), co pozwala podpiąć osobną obsługę dla każdego z nich:

```
NAZWA_OBIEKTU:ONCLICK^LEFT=BEHLEFTCLICK
NAZWA_OBIEKTU:ONCLICK^RIGHT=BEHRIGHTCLICK
```

### ONDBLCLICK

Wywoływany po dwukrotnym kliknięciu przycisku myszy.

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONMOVE

Wywoływany po wykryciu ruchu myszy (zmiany pozycji kursora).

### ONRELEASE

Wywoływany po zwolnieniu przycisku myszy.
