# TEXT

Tekst wyświetlany na ekranie. Korzysta z czcionki ([`FONT`](FONT.md)) wskazanej w polu [`FONT`](#font), a treść, pozycja i sposób wyrównania są konfigurowane przez pozostałe pola.

## Pola

### FONT

```
STRING FONT
```

Nazwa zmiennej typu [`FONT`](FONT.md), z której pobierane są tekstury znaków.

### HJUSTIFY

```
STRING HJUSTIFY
```

Wyrównanie w poziomie wewnątrz prostokąta `RECT`. Dopuszczalne wartości: `LEFT`, `RIGHT`, `CENTER`.

### PRIORITY

```
INTEGER PRIORITY
```

Priorytet renderowania (`Z`) tekstu względem innych obiektów na scenie.

### RECT

```
INTEGER,INTEGER,INTEGER,INTEGER RECT
```

Prostokąt, w którym tekst jest rysowany — cztery liczby oddzielone przecinkami:
`xLeft, yTop, xRight, yBottom`. W skrypcie pole może też wskazywać na nazwę
zmiennej typu [`ANIMO`](index.md) lub [`IMAGE`](IMAGE.md), z której przejmowane
są wymiary.

### TEXT

```
STRING TEXT
```

Wyświetlany tekst. Modyfikowany metodą [`SETTEXT`](#settext).

Znak `|` oraz CR rozpoczynają nowy wiersz, a sam LF jest pomijany. Tekst jest
zawijany na granicach słów do szerokości [`RECT`](#rect). Metryki i korekty par
pochodzą bezpośrednio z [formatu FNT](../formats/FNT.md).

Piklib pozwala zmieniać kolor odcinka tekstu kodem `<COLORn>`, gdzie `n` jest
dziesiętną wartością koloru RGB555 albo RGB565. Przykładowo `<COLOR63488>`
oznacza czerwień w RGB565. Wartości z przecinkami wewnątrz argumentu
`SETTEXT` są rozdzielane przez parser skryptu, dlatego w takim miejscu należy
używać postaci liczbowej.

### TOCANVAS

```
BOOL TOCANVAS
```

Określa, czy tekst jest renderowany na głównej kanwie sceny. Jeżeli pole jest `FALSE`, tekst nie jest widoczny niezależnie od stanu pola `VISIBLE`.

### VISIBLE

```
BOOL VISIBLE
```

Widoczność tekstu. Modyfikowana metodami [`SHOW`](#show) i [`HIDE`](#hide).

### VJUSTIFY

```
STRING VJUSTIFY
```

Wyrównanie w pionie wewnątrz prostokąta `RECT`. Dopuszczalne wartości: `TOP`, `BOTTOM`, `CENTER`.

## Metody

### HIDE

```
void HIDE()
```

Ukrywa tekst (ustawia [`VISIBLE`](#visible) na `FALSE`).

**Kompatybilność:** `HIDE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETCOLOR

```
void SETCOLOR(INTEGER red, INTEGER green, INTEGER blue)
```

Ustawia bazowy kolor tekstu. Składowe są podawane w zakresie od 0 do 255.

```
NAPIS^SETCOLOR(255, 0, 0);
```

**Kompatybilność:** `PIKLIB8.DLL` ✅.

### SETJUSTIFY

```
void SETJUSTIFY(INTEGER xLeft, INTEGER yTop, INTEGER xRight, INTEGER yBottom, STRING hJustify, STRING vJustify)
```

Ustawia w jednym wywołaniu prostokąt rysowania ([`RECT`](#rect)) oraz wyrównanie poziome ([`HJUSTIFY`](#hjustify)) i pionowe ([`VJUSTIFY`](#vjustify)).

**Parametry**

- `xLeft, yTop, xRight, yBottom` — współrzędne prostokąta.
- `hJustify` — wyrównanie poziome (`LEFT`, `RIGHT`, `CENTER`).
- `vJustify` — wyrównanie pionowe (`TOP`, `BOTTOM`, `CENTER`).

**Kompatybilność:** `SETJUSTIFY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Ustawia priorytet renderowania tekstu.

**Parametry**

- `priority` — nowa wartość pola [`PRIORITY`](#priority).

**Kompatybilność:** `SETPRIORITY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETTEXT

```
void SETTEXT(STRING text)
```

Zmienia wyświetlany tekst.

**Parametry**

- `text` — nowa zawartość pola [`TEXT`](#text).

**Przykłady**

```
TXTDEBUG^SETTEXT(ARRPX^GETSIZE());
TXTDEBUG^SETTEXT("SAVED");
```

**Kompatybilność:** `SETTEXT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SHOW

```
void SHOW()
```

Pokazuje tekst (ustawia [`VISIBLE`](#visible) na `TRUE`).

**Kompatybilność:** `SHOW` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.
