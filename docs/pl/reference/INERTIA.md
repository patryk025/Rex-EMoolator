# INERTIA

Interfejs do wbudowanego silnika fizycznego 2D o tej samej nazwie (Inertia). Zarządza ciałami sztywnymi: tworzeniem obiektów, ich wiązaniem z animacjami, polem grawitacji, prędkościami, tłumieniem i przykładaniem sił. Wykorzystany w *Reksio i Kretes w Akcji*.

Każdy obiekt fizyczny ma identyfikator (`objectId`) — wartość całkowitą używaną przez większość metod do wskazania ciała. Plik definiujący świat fizyczny ma rozszerzenie [`.INE`](../engine/index.md) i jest ładowany jednorazowo metodą [`LOAD`](#load).

## Metody

### ADDFORCE

```
void ADDFORCE(INTEGER objectId, INTEGER forceX, INTEGER forceY)
```

Przykłada siłę do obiektu w osiach X i Y.

**Parametry**

- `objectId` — identyfikator obiektu.
- `forceX`, `forceY` — składowe siły.

**Przykłady**

```
EXTWORLD^ADDFORCE(1,-500,0);
EXTWORLD^ADDFORCE(1,0,-50);
```

**Kompatybilność:** `ADDFORCE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### CREATESPHERE

```
void CREATESPHERE(INTEGER objectId, INTEGER posX, INTEGER posY, INTEGER radius)
```

Tworzy w silniku fizycznym sferę o podanej pozycji środka i promieniu, przypisując jej wskazany identyfikator.

**Parametry**

- `objectId` — identyfikator nowego obiektu.
- `posX`, `posY` — pozycja środka sfery.
- `radius` — promień sfery.

**Przykłady**

```
EXTWORLD^CREATESPHERE(5,10,10,10);
```

**Kompatybilność:** `CREATESPHERE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### DELETEBODY

```
void DELETEBODY(INTEGER objectId)
```

Usuwa obiekt z silnika fizycznego.

**Parametry**

- `objectId` — identyfikator usuwanego obiektu.

**Przykłady**

```
EXTWORLD^DELETEBODY(IHANDLEDEL);
EXTWORLD^DELETEBODY(IRAKIETAOBJ);
```

**Kompatybilność:** `DELETEBODY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETPOSITIONX

```
INTEGER GETPOSITIONX(INTEGER objectId)
```

Zwraca aktualną pozycję X obiektu o podanym identyfikatorze.

**Parametry**

- `objectId` — identyfikator obiektu.

**Zwraca**: [`INTEGER`](INTEGER.md) — koordynata X.

**Kompatybilność:** `GETPOSITIONX` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETPOSITIONY

```
INTEGER GETPOSITIONY(INTEGER objectId)
```

Zwraca aktualną pozycję Y obiektu o podanym identyfikatorze.

**Parametry**

- `objectId` — identyfikator obiektu.

**Zwraca**: [`INTEGER`](INTEGER.md) — koordynata Y.

**Kompatybilność:** `GETPOSITIONY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETSPEED

```
DOUBLE GETSPEED(INTEGER objectId)
```

Zwraca prędkość obiektu o podanym identyfikatorze (długość wektora prędkości liniowej).

**Parametry**

- `objectId` — identyfikator obiektu.

**Zwraca**: [`DOUBLE`](DOUBLE.md) — wartość prędkości.

**Kompatybilność:** `GETSPEED` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### LINK

```
void LINK(INTEGER objectId, STRING animoName, BOOL flag1, BOOL flag2)
```

Wiąże obiekt fizyczny z animacją [`ANIMO`](ANIMO.md) — pozycja animacji jest aktualizowana na podstawie symulacji fizyki. Znaczenie obu flag boolowskich nie zostało jeszcze ustalone (w grach zawsze podawane są jako `TRUE`).

**Parametry**

- `objectId` — identyfikator obiektu fizycznego.
- `animoName` — nazwa zmiennej [`ANIMO`](ANIMO.md).
- `flag1`, `flag2` — flagi konfiguracyjne (znaczenie nieustalone).

**Przykłady**

```
EXTWORLD^LINK(1,"ANNSZCZUREK",TRUE,TRUE);
EXTWORLD^LINK(IOBIEKT,["ANNSTRZAL_"+ISTRZAL],TRUE,TRUE);
```

**Kompatybilność:** `LINK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### LOAD

```
void LOAD(STRING path)
```

Ładuje plik `.INE` z definicją świata fizycznego.

**Parametry**

- `path` — ścieżka do pliku `.INE`.

**Przykłady**

```
EXTWORLD^LOAD("WORLD.INE");
```

**Kompatybilność:** `LOAD` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### RESETTIMER

```
void RESETTIMER()
```

Resetuje wewnętrzny zegar symulacji.

**Przykłady**

```
EXTWORLD^RESETTIMER();
```

**Kompatybilność:** `RESETTIMER` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETGRAVITY

```
void SETGRAVITY(DOUBLE gravityX, DOUBLE gravityY)
```

Ustawia globalny wektor grawitacji. Wartość `(0, 0)` wyłącza grawitację.

**Parametry**

- `gravityX`, `gravityY` — składowe grawitacji.

**Przykłady**

```
EXTWORLD^SETGRAVITY(0,0);
```

**Kompatybilność:** `SETGRAVITY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETLINEARDAMPING

```
void SETLINEARDAMPING(INTEGER objectId, INTEGER linearDamping)
```

Ustawia tłumienie liniowe (stopniowe spowalnianie prędkości liniowej) dla obiektu.

**Parametry**

- `objectId` — identyfikator obiektu.
- `linearDamping` — wartość tłumienia.

**Przykłady**

```
EXTWORLD^SETLINEARDAMPING(1,300);
```

**Kompatybilność:** `SETLINEARDAMPING` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETMATERIAL

```
void SETMATERIAL(INTEGER objectId, STRING material)
```

Ustawia materiał obiektu. Materiały kontrolują, w jaki sposób obiekty reagują na kontakt (sztywność, sprężystość, tarcie). W skryptach gier spotykana jest m.in. nazwa `"TRIGGER"`, dla której silnik wywołuje na powiązanej animacji sygnał `ONSIGNAL^TRIGGER`.

**Parametry**

- `objectId` — identyfikator obiektu.
- `material` — nazwa materiału.

**Przykłady**

```
EXTWORLD^SETMATERIAL(IOBIEKT,"TRIGGER");
```

**Kompatybilność:** `SETMATERIAL` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETPOSITION

```
void SETPOSITION(INTEGER objectId, INTEGER posX, INTEGER posY)
```

Ustawia bezwzględną pozycję obiektu w świecie fizycznym.

**Parametry**

- `objectId` — identyfikator obiektu.
- `posX`, `posY` — nowa pozycja.

**Przykłady**

```
EXTWORLD^SETPOSITION(IOBIEKT,[ANNSZCZUREK^GETCENTERX()+70],[ANNSZCZUREK^GETCENTERY()-1]);
EXTWORLD^SETPOSITION(IRAKIETAOBJ,ANNDODATKI_7^GETPOSITIONX(),ANNDODATKI_7^GETPOSITIONY());
```

**Kompatybilność:** `SETPOSITION` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETVELOCITY

```
void SETVELOCITY(INTEGER objectId, INTEGER speedX, INTEGER speedY)
```

Ustawia prędkość obiektu w osiach X i Y.

**Parametry**

- `objectId` — identyfikator obiektu.
- `speedX`, `speedY` — składowe prędkości.

**Przykłady**

```
EXTWORLD^SETVELOCITY(1,0,0);
EXTWORLD^SETVELOCITY(IOBIEKT,8,0);
```

**Kompatybilność:** `SETVELOCITY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### TICK

```
void TICK()
```

Wykonuje pojedynczy krok symulacji. Bez wywołania `TICK` świat fizyczny pozostaje zamrożony — typowo wywoływane z sygnału [`ONTICK`](TIMER.md#ontick) [`TIMER`](TIMER.md).

**Przykłady**

```
EXTWORLD^TICK();
```

**Kompatybilność:** `TICK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### UNLINK

```
void UNLINK(INTEGER objectId)
```

Zrywa powiązanie obiektu z animacją utworzone metodą [`LINK`](#link).

**Parametry**

- `objectId` — identyfikator obiektu.

**Przykłady**

```
EXTWORLD^UNLINK(IID);
EXTWORLD^UNLINK(1);
```

**Kompatybilność:** `UNLINK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
