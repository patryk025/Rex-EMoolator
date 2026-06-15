# WORLD

Interfejs do silnika fizycznego 3D zbudowanego na bibliotece Sekai — cienkiego wrappera Open Dynamics Engine (ODE). Świat zarządza ciałami sztywnymi, ich połączeniami i ścieżkami, oraz emituje siły, grawitację i tłumienia. Wersja 3D używana w grach Piklib. Pojęciowo odpowiednik [`INERTIA`](INERTIA.md), ale z dostępem do trzeciej osi i własnym, znacznie szerszym API.

Każdy obiekt w świecie identyfikowany jest liczbą całkowitą (`objectId`). Świat ładowany jest z pliku `.SEK`, który zawiera definicje obiektów, ich parametrów fizycznych, siatek kolizyjnych, listy punktów na scenie oraz ich połączeń.

## Typy geometrii

Wartości akceptowane przez parametr `geomType` metody [`ADDBODY`](#addbody):

| Wartość | Geometria |
| --- | --- |
| `0` | prostopadłościan (box) |
| `1` | cylinder |
| `2` | sfera |
| `3` | trimesh (siatka trójkątów; używana wyłącznie podczas ładowania z pliku `.SEK`) |
| `4` | samochodzik (cztery koła + prostopadłościan) |

## Pola

### FILENAME

```
STRING FILENAME
```

Ścieżka do pliku `.SEK` z definicją świata fizycznego.

## Metody

### ADDBODY

```
void ADDBODY(INTEGER objectId, DOUBLE mass, DOUBLE mu, DOUBLE mu2,
             DOUBLE bounce, DOUBLE bounceVelocity, DOUBLE maxVelocity,
             INTEGER bodyType, INTEGER geomType,
             DOUBLE dim1, DOUBLE dim2, DOUBLE dim3)
```

Tworzy w świecie nowe ciało fizyczne. Parametry `mass`, `mu`, `mu2`, `bounce` i `bounceVelocity` są mapowane bezpośrednio na odpowiednie parametry ODE: masa, tarcie, tarcie w drugim kierunku, wartość odbicia oraz minimalna prędkość wymagana do odbicia. `maxVelocity` ogranicza prędkość obiektu.

Wymiary `dim1`, `dim2`, `dim3` zależą od `geomType`:

- **box** — długości w osiach X, Y, Z.
- **cylinder** — `dim1` to promień, `dim2` wysokość; `dim3` ignorowane.
- **sfera** — `dim1` to promień; `dim2` i `dim3` ignorowane.

**Parametry**

- `objectId` — identyfikator nowego ciała.
- `mass` — masa obiektu.
- `mu`, `mu2` — współczynniki tarcia.
- `bounce` — wartość odbicia.
- `bounceVelocity` — minimalna prędkość wymagana do odbicia.
- `maxVelocity` — limit prędkości obiektu.
- `bodyType` — typ ciała (znaczenie zarezerwowane przez ODE).
- `geomType` — typ geometrii (zobacz [Typy geometrii](#typy-geometrii)).
- `dim1`, `dim2`, `dim3` — wymiary obiektu zgodnie z geometrią.

**Przykłady**

```
WORLD^ADDBODY(100,10,0.0,10000.0,0.0,0.0,40000,1,2,30,16,16);
WORLD^ADDBODY(VARINT0,0.1,0.5,0.5,0.0,0.0,100000,1,2,16,16,16);
```

### ADDFORCE

```
void ADDFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, [DOUBLE forceZ])
```

Przykłada siłę do obiektu w trzech osiach. Pominięcie `forceZ` jest równoważne podaniu `0.0`.

**Parametry**

- `objectId` — identyfikator obiektu.
- `forceX`, `forceY`, `forceZ` — składowe siły.

**Przykłady**

```
WORLD^ADDFORCE(100,VARFORCEX,VARFORCEY,0);
WTEST^ADDFORCE(501,0,VARD_TMP1,0);
```

### ADDGRAVITYEX

```
void ADDGRAVITYEX(INTEGER objectId, INTEGER secondObjectId, BOOL gravityEx)
```

Dodaje rozszerzoną grawitację pomiędzy dwoma obiektami. Pełne znaczenie nie zostało ustalone.

**Przykłady**

```
WTEST^ADDGRAVITYEX(VARI_ID,_I_,TRUE);
```

### FINDPATH

```
void FINDPATH(INTEGER objectId, INTEGER pointObjectId,
              INTEGER targetX, INTEGER targetY, INTEGER targetZ,
              BOOL saveIntermediates, [BOOL flag])
```

Wyznacza ścieżkę dla obiektu między aktualną pozycją a punktem docelowym, korzystając z grafu nawigacyjnego załadowanego z pliku `.SEK`. Wynik jest zapamiętywany przez silnik fizyczny i wykorzystywany w kolejnych wywołaniach [`FOLLOWPATH`](#followpath).

**Parametry**

- `objectId` — identyfikator obiektu, dla którego liczona jest ścieżka.
- `pointObjectId` — identyfikator punktu nawigacyjnego (zaczepu).
- `targetX`, `targetY`, `targetZ` — koordynaty punktu docelowego.
- `saveIntermediates` — gdy `TRUE`, zapamiętywane są punkty pośrednie ścieżki.
- `flag` — (opcjonalnie) flaga konfiguracyjna (znaczenie nieustalone).

**Przykłady**

```
WPATH^FINDPATH(100,VARIPATHID,$3,$4,0,TRUE,FALSE);
WPATH^FINDPATH(101,VARIPATHID,VARIKRETGOX,VARIKRETGOY,0,FALSE);
```

### FOLLOWPATH

```
DOUBLE FOLLOWPATH(INTEGER objectId, INTEGER arrivalRadius, DOUBLE turnClamp, DOUBLE speed)
```

Przemieszcza obiekt wzdłuż ścieżki wyznaczonej wcześniej przez [`FINDPATH`](#findpath). Zwraca pozostały dystans do celu.

**Parametry**

- `objectId` — identyfikator obiektu.
- `arrivalRadius` — promień, w którym obiekt uważany jest za zatrzymany przy celu.
- `turnClamp` — ograniczenie skrętu na jednym kroku.
- `speed` — prędkość ruchu.

**Zwraca**: [`DOUBLE`](DOUBLE.md) — pozostały dystans.

**Przykłady**

```
WPATH^FOLLOWPATH(100,20,0.5,VARDMAXVEL);
WPATH^FOLLOWPATH(101,20,0.5,VARD_KRETSPEED);
```

### GETANGLE

```
DOUBLE GETANGLE(INTEGER objectId)
```

Zwraca kąt wynikający z wektora prędkości obiektu (w stopniach).

**Parametry**

- `objectId` — identyfikator obiektu.

**Zwraca**: [`DOUBLE`](DOUBLE.md) — kąt w stopniach.

### GETBKGPOSX

```
INTEGER GETBKGPOSX()
```

Zwraca pozycję X tła powiązanego ze światem fizycznym.

### GETBKGPOSY

```
INTEGER GETBKGPOSY()
```

Zwraca pozycję Y tła powiązanego ze światem fizycznym.

### GETMOVEDISTANCE

```
DOUBLE GETMOVEDISTANCE(INTEGER objectId)
```

Zwraca dystans pokonany przez obiekt od ostatniego resetu pomiaru.

**Parametry**

- `objectId` — identyfikator obiektu.

### GETPOSITIONX

```
INTEGER GETPOSITIONX(INTEGER objectId)
```

Zwraca pozycję X obiektu w układzie ekranu (po przesunięciu o `+400` względem początku układu fizyki).

### GETPOSITIONY

```
INTEGER GETPOSITIONY(INTEGER objectId)
```

Zwraca pozycję Y obiektu w układzie ekranu (z odwróconą osią — `300 - Y`).

### GETPOSITIONZ

```
INTEGER GETPOSITIONZ(INTEGER objectId)
```

Zwraca pozycję Z obiektu.

### GETROTATIONZ

```
DOUBLE GETROTATIONZ(INTEGER objectId)
```

Zwraca kąt obrotu obiektu względem osi Z (w stopniach).

### GETSPEED

```
DOUBLE GETSPEED(INTEGER objectId)
```

Zwraca prędkość obiektu (długość wektora prędkości liniowej).

### JOIN

```
void JOIN(INTEGER firstId, INTEGER secondId,
          DOUBLE anchorX, DOUBLE anchorY, DOUBLE anchorZ,
          DOUBLE limitMotor, DOUBLE lowStop, DOUBLE highStop,
          [DOUBLE hingeAxisX, DOUBLE hingeAxisY, DOUBLE hingeAxisZ])
```

Tworzy połączenie zawiasowe (hinge joint) pomiędzy dwoma ciałami. Opcjonalne argumenty wyznaczają oś obrotu — domyślnie `(0, 0, 1)`.

**Parametry**

- `firstId`, `secondId` — identyfikatory łączonych obiektów.
- `anchorX`, `anchorY`, `anchorZ` — punkt kotwiczenia połączenia.
- `limitMotor` — wartość siły potrzebna do zerwania połączenia.
- `lowStop`, `highStop` — graniczne kąty obrotu.
- `hingeAxisX`, `hingeAxisY`, `hingeAxisZ` — (opcjonalnie) oś obrotu.

**Przykłady**

```
WORLD^JOIN(199,200,400,300,0,0,-180,180);
WTEST^JOIN(VARI_ID,VARI_TMP1,VARI_X,VARI_TMP2,0,0,-180,180,0,1,0);
```

### LINK

```
void LINK(INTEGER objectId, STRING objectName)
```

Wiąże ciało fizyczne ze zmienną [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md) — pozycja grafiki jest aktualizowana na podstawie pozycji ciała.

**Parametry**

- `objectId` — identyfikator ciała.
- `objectName` — nazwa zmiennej graficznej.

**Przykłady**

```
WPATH^LINK(100,"ANNREX");
WORLD^LINK(VARINT0,VARSTRING0);
```

### LOAD

```
void LOAD(STRING filename)
```

Resetuje silnik fizyczny i ładuje świat z pliku `.SEK`.

**Parametry**

- `filename` — ścieżka do pliku `.SEK`.

**Przykłady**

```
WPATH^LOAD(SOBJECT|NAME);
```

### MOVEOBJECTS

```
DOUBLE MOVEOBJECTS()
```

Wykonuje jeden krok symulacji i przesuwa wszystkie obiekty zgodnie z prawami fizyki. Zwraca czas, który upłynął w symulacji w tym kroku.

**Zwraca**: [`DOUBLE`](DOUBLE.md) — czas symulacji.

**Przykłady**

```
WORLD^MOVEOBJECTS();
```

### REMOVEOBJECT

```
void REMOVEOBJECT(INTEGER objectId)
```

Usuwa obiekt z silnika fizycznego.

**Przykłady**

```
WTEST^REMOVEOBJECT(60);
```

### SETACTIVE

```
void SETACTIVE(INTEGER objectId, BOOL active, BOOL collidable)
```

Ustawia stan aktywności obiektu.

**Parametry**

- `objectId` — identyfikator obiektu.
- `active` — czy obiekt podlega symulacji.
- `collidable` — czy obiekt bierze udział w wykrywaniu kolizji.

**Przykłady**

```
WPATH^SETACTIVE(VARI_3DPATHID,$1,$2);
```

### SETBKGSIZE

```
void SETBKGSIZE(INTEGER leftX, INTEGER rightX, INTEGER topY, INTEGER bottomY)
```

Ustawia rozmiar prostokąta tła powiązanego ze światem.

### SETG

```
void SETG(INTEGER objectId, DOUBLE g)
```

Ustawia indywidualną stałą grawitacyjną dla obiektu (modeluje magnesy). Domyślna wartość `0` oznacza brak przyciągania.

**Parametry**

- `objectId` — identyfikator obiektu.
- `g` — stała grawitacyjna.

**Przykłady**

```
WTEST^SETG(VARI_ID,VARD_MAGNESREACT);
WTEST^SETG(501,-7000000);
```

### SETGRAVITY

```
void SETGRAVITY(DOUBLE gravityX, DOUBLE gravityY, DOUBLE gravityZ)
```

Ustawia wektor grawitacji globalnej dla całego świata.

**Parametry**

- `gravityX`, `gravityY`, `gravityZ` — składowe grawitacji.

**Przykłady**

```
WORLD^SETGRAVITY(0,0,-1000);
WORLD^SETGRAVITY(0,-15,0);
```

### SETGRAVITYCENTER

```
void SETGRAVITYCENTER(INTEGER objectId, BOOL gravityCenter)
```

Włącza lub wyłącza traktowanie obiektu jako źródła centralnego pola grawitacyjnego.

**Parametry**

- `objectId` — identyfikator obiektu.
- `gravityCenter` — flaga włączająca.

### SETLIMIT

```
void SETLIMIT(INTEGER objectId, INTEGER minX, INTEGER minY, INTEGER minZ,
              INTEGER maxX, INTEGER maxY, INTEGER maxZ)
```

Ustawia ograniczenie pozycji obiektu (prostopadłościenny obszar dozwolonego ruchu).

**Parametry**

- `objectId` — identyfikator obiektu.
- `minX`, `minY`, `minZ` — dolne granice w trzech osiach.
- `maxX`, `maxY`, `maxZ` — górne granice w trzech osiach.

**Przykłady**

```
WORLD^SETLIMIT(100,0,0,0,800,600,999999);
```

### SETMAXSPEED

```
void SETMAXSPEED(INTEGER objectId, INTEGER maxSpeed)
```

Ustawia maksymalną prędkość obiektu.

**Przykłady**

```
WORLD^SETMAXSPEED(100,200);
```

### SETMOVEFLAGS

```
void SETMOVEFLAGS(BOOL moveX, BOOL moveY)
```

Włącza lub wyłącza ruch obiektu w osiach X i Y. Pełne znaczenie i kontekst zastosowania nie zostały ustalone.

**Przykłady**

```
WPATH^SETMOVEFLAGS(VARITEMP0,VARITEMP1);
```

### SETPOSITION

```
void SETPOSITION(INTEGER objectId, DOUBLE x, DOUBLE y, DOUBLE z)
```

Ustawia bezwzględną pozycję obiektu (koordynaty w układzie ekranu — silnik przelicza je na układ fizyki).

**Przykłady**

```
WORLD^SETPOSITION(100,150,400,0);
WTEST^SETPOSITION(VARI_ID,VARI_X,VARI_Y,0);
```

### SETREFOBJECT

```
void SETREFOBJECT(INTEGER objectId)
```

Ustawia obiekt jako referencyjny — używany przy obliczeniach pozycji względem niego.

**Przykłady**

```
WPATH^SETREFOBJECT(100);
```

### SETVELOCITY

```
void SETVELOCITY(INTEGER objectId, DOUBLE speedX, DOUBLE speedY, DOUBLE speedZ)
```

Ustawia prędkość obiektu w trzech osiach.

**Przykłady**

```
WORLD^SETVELOCITY(207,5000000,0,0);
WORLD^SETVELOCITY(VARPLAYERID,0,0,0);
```

### START

```
void START()
```

Uruchamia symulację (włącza timer silnika fizycznego).

**Przykłady**

```
WORLD^START();
```

### STOP

```
void STOP()
```

Zatrzymuje symulację (wyłącza timer silnika fizycznego). Stan obiektów pozostaje zachowany.

**Przykłady**

```
WORLD^STOP();
```

### UNLINK

```
void UNLINK(INTEGER objectId)
```

Zrywa powiązanie obiektu z animacją utworzone metodą [`LINK`](#link).

**Przykłady**

```
WTEST^UNLINK(VARI_TMP2);
```

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
