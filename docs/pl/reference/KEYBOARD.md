# KEYBOARD

Wbudowany obiekt reprezentujący stan klawiatury. Dostępny pod globalną nazwą `KEYBOARD` z dowolnego kontekstu (zobacz [Obiekty wbudowane](../engine/globals.md#obiekty-wbudowane)). Obsługuje zdarzenia naciśnięcia oraz zwolnienia klawiszy, w tym tryb autorepeat.

## Metody

### DISABLE

```
void DISABLE()
```

Wyłącza obsługę zdarzeń klawiatury — sygnały klawiszy przestają być emitowane.

**Przykłady**

```
KEYBOARD^DISABLE();
```

### ENABLE

```
void ENABLE()
```

Włącza obsługę zdarzeń klawiatury.

**Przykłady**

```
KEYBOARD^ENABLE();
```

### GETLATESTKEY

```
STRING GETLATESTKEY()
```

Zwraca nazwę ostatnio wciśniętego klawisza.

**Zwraca**: nazwa klawisza w postaci akceptowanej przez [`ISKEYDOWN`](#iskeydown) (zobacz [Obsługiwane klawisze](#obslugiwane-klawisze)).

**Przykłady**

```
KEYBOARD^GETLATESTKEY();
```

### ISENABLED

```
BOOL ISENABLED()
```

Sprawdza, czy obsługa klawiatury jest włączona.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli klawiatura reaguje na zdarzenia.

**Przykłady**

```
KEYBOARD^ISENABLED();
```

### ISKEYDOWN

```
BOOL ISKEYDOWN(STRING keyName)
```

Sprawdza, czy podany klawisz jest aktualnie wciśnięty.

**Parametry**

- `keyName` — nazwa klawisza (zobacz [Obsługiwane klawisze](#obslugiwane-klawisze)).

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli klawisz jest wciśnięty. Dla nieznanej nazwy zwracane jest `FALSE`.

**Przykłady**

```
KEYBOARD^ISKEYDOWN("UP");
KEYBOARD^ISKEYDOWN("LEFT");
KEYBOARD^ISKEYDOWN(ARRAYKEYBOARD^GET(0));
```

### SETAUTOREPEAT

```
void SETAUTOREPEAT(BOOL autorepeat)
```

Ustawia, czy sygnał [`ONKEYDOWN`](#onkeydown) ma być emitowany cyklicznie tak długo, jak klawisz pozostaje wciśnięty. Domyślnie wyłączone.

**Parametry**

- `autorepeat` — `TRUE`, aby włączyć powtarzanie.

**Przykłady**

```
KEYBOARD^SETAUTOREPEAT(FALSE);
```

## Sygnały

### ONKEYDOWN

Wywoływany po naciśnięciu klawisza. Sygnał jest [parametryzowany](../engine/events.md#sygnaly-parametryzowane) nazwą klawisza — pozwala podpiąć osobną obsługę pod każdy z nich:

```
KEYBOARD:ONKEYDOWN^UP=BEHGOUP
KEYBOARD:ONKEYDOWN^DOWN=BEHGODOWN
```

Przy włączonym autorepeacie ([`SETAUTOREPEAT(TRUE)`](#setautorepeat)) sygnał jest emitowany w każdej klatce, w której klawisz pozostaje wciśnięty.

### ONKEYUP

Wywoływany po zwolnieniu klawisza. Sygnał jest parametryzowany nazwą klawisza, analogicznie do [`ONKEYDOWN`](#onkeydown).

### ONCHAR

Wywoływany po naciśnięciu klawisza dla każdego wygenerowanego znaku. Sygnał jest parametryzowany nazwą klawisza.

## Obsługiwane klawisze {#obslugiwane-klawisze}

Klawiatura silnika rozpoznaje następujące nazwy klawiszy:

- **Funkcyjne**: `F1`, `F2`, `F3`, `F4`, `F5`, `F6`, `F7`, `F8`, `F9`, `F10`, `F11`, `F12`
- **Strzałki**: `UP`, `DOWN`, `LEFT`, `RIGHT`
- **Modyfikatory**: `LSHIFT`, `RSHIFT`, `LCTRL`, `RCTRL`, `LALT`, `RALT`, `CAPSLOCK`
- **Specjalne**: `ESC`, `ENTER`, `SPACE`, `TAB`, `INSERT`, `PGUP`, `PGDN`, `HOME`
- **Litery**: `Q`, `W`, `E`, `R`, `T`, `U`, `I`, `O`, `P`, `A`, `S`, `D`, `F`, `G`, `H`, `J`, `K`, `L`, `C`, `V`, `B`, `N`, `M`
- **Cyfry**: `0`, `1`, `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`
