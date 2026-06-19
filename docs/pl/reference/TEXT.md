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

Prostokąt, w którym tekst jest rysowany — cztery liczby oddzielone przecinkami: `xLeft, yBottom, xRight, yTop`. W skrypcie pole może też wskazywać na nazwę zmiennej typu [`ANIMO`](index.md) lub [`IMAGE`](IMAGE.md), z której przejmowane są wymiary.

### TEXT

```
STRING TEXT
```

Wyświetlany tekst. Modyfikowany metodą [`SETTEXT`](#settext).

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

### SETJUSTIFY

```
void SETJUSTIFY(INTEGER xLeft, INTEGER yBottom, INTEGER xRight, INTEGER yTop, STRING hJustify, STRING vJustify)
```

Ustawia w jednym wywołaniu prostokąt rysowania ([`RECT`](#rect)) oraz wyrównanie poziome ([`HJUSTIFY`](#hjustify)) i pionowe ([`VJUSTIFY`](#vjustify)).

**Parametry**

- `xLeft, yBottom, xRight, yTop` — współrzędne prostokąta.
- `hJustify` — wyrównanie poziome (`LEFT`, `RIGHT`, `CENTER`).
- `vJustify` — wyrównanie pionowe (`TOP`, `BOTTOM`, `CENTER`).

### SETPRIORITY

```
void SETPRIORITY(INTEGER priority)
```

Ustawia priorytet renderowania tekstu.

**Parametry**

- `priority` — nowa wartość pola [`PRIORITY`](#priority).

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

### SHOW

```
void SHOW()
```

Pokazuje tekst (ustawia [`VISIBLE`](#visible) na `TRUE`).

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.
