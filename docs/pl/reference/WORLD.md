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

**Kompatybilność:** `ADDBODY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### ADDFORCE

```
void ADDFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, [DOUBLE forceZ])
void ADDFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, DOUBLE forceZ,
              DOUBLE pointX, DOUBLE pointY, DOUBLE pointZ)
```

Przykłada siłę do obiektu w trzech osiach. Pominięcie `forceZ` jest równoważne podaniu `0.0`. W wariancie siedmioargumentowym siła jest przyłożona w punkcie `(pointX, pointY, pointZ)`, więc może także obrócić ciało.

**Parametry**

- `objectId` — identyfikator obiektu.
- `forceX`, `forceY`, `forceZ` — składowe siły.
- `pointX`, `pointY`, `pointZ` — punkt przyłożenia siły w układzie świata (tylko wariant siedmioargumentowy).

**Przykłady**

```
WORLD^ADDFORCE(100,VARFORCEX,VARFORCEY,0);
WTEST^ADDFORCE(501,0,VARD_TMP1,0);
```

**Kompatybilność:** `ADDFORCE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### ADDGRAVITYEX

```
void ADDGRAVITYEX(INTEGER objectId, INTEGER secondObjectId, BOOL gravityEx)
```

Steruje wyjątkiem od grawitacji centralnej. Gdy `gravityEx` ma wartość `TRUE`, `objectId` **nie jest przyciągany** przez centrum `secondObjectId`; `FALSE` usuwa ten wyjątek i ponownie włącza jego wpływ. Sama metoda nie tworzy pola grawitacyjnego: do tego centrum musi mieć włączone [`SETGRAVITYCENTER`](#setgravitycenter) i niezerowe [`SETG`](#setg).

Nazwa jest myląca, ale w praktyce to bardziej "dodaj wykluczenie" niż "dodaj grawitację".

**Parametry**

- `objectId` — obiekt, którego dotyczy wyjątek.
- `secondObjectId` — identyfikator centrum grawitacji do wykluczenia.
- `gravityEx` — `TRUE` dodaje wykluczenie, `FALSE` je usuwa; pominięty argument zachowuje się jak `TRUE`.

**Przykłady**

```
WTEST^ADDGRAVITYEX(VARI_ID,_I_,TRUE);
```

**Kompatybilność:** `ADDGRAVITYEX` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### ADDOBJECT

```
void ADDOBJECT(INTEGER objectId, DOUBLE x, DOUBLE y, DOUBLE z,
               DOUBLE dim1, DOUBLE dim2, DOUBLE dim3, DOUBLE mass,
               INTEGER rigidFlag, DOUBLE maxSpeed)
```

Starszy skrót do utworzenia dynamicznej sfery i ustawienia jej pozycji. `rigidFlag` jest przyjmowany dla zgodności, ale emulator go nie wykorzystuje. Do nowego kodu lepiej wybrać [`ADDBODY`](#addbody): tam jawnie określasz geometrię i parametry kontaktu.

**Kompatybilność:** `ADDOBJECT` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETFORCE / SETOBJECTFORCE

```
void SETFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, [DOUBLE forceZ])
void SETOBJECTFORCE(INTEGER objectId, DOUBLE forceX, DOUBLE forceY, [DOUBLE forceZ])
```

Obie nazwy są aliasami [`ADDFORCE`](#addforce) w jego zwykłym wariancie. Nie ustawiają trwałej siły: dokładają ją do bieżącego kroku symulacji.

**Kompatybilność:** `SETFORCE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `SETOBJECTFORCE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `FINDPATH` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `FOLLOWPATH` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETANGLE

```
DOUBLE GETANGLE(INTEGER objectId)
```

Zwraca kąt wynikający z wektora prędkości obiektu (w stopniach).

**Parametry**

- `objectId` — identyfikator obiektu.

**Zwraca**: [`DOUBLE`](DOUBLE.md) — kąt w stopniach.

**Kompatybilność:** `GETANGLE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETBKGPOSX

```
INTEGER GETBKGPOSX()
```

Zwraca pozycję X tła powiązanego ze światem fizycznym.

**Kompatybilność:** `GETBKGPOSX` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETBKGPOSY

```
INTEGER GETBKGPOSY()
```

Zwraca pozycję Y tła powiązanego ze światem fizycznym.

**Kompatybilność:** `GETBKGPOSY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETCOLLISION

```
BOOL GETCOLLISION(INTEGER objectId, [INTEGER otherObjectId])
```

Zwraca `TRUE`, gdy obiekt zderzył się w bieżącym kroku symulacji. Po podaniu `otherObjectId` pyta konkretnie o zderzenie z tym obiektem. Wynik dotyczy tylko obiektów, dla których monitoring kolizji w [`SETACTIVE`](#setactive) jest włączony.

**Kompatybilność:** `GETCOLLISION` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETMOVEDISTANCE

```
DOUBLE GETMOVEDISTANCE(INTEGER objectId)
```

Zwraca dystans pokonany przez obiekt od ostatniego resetu pomiaru.

**Parametry**

- `objectId` — identyfikator obiektu.

**Kompatybilność:** `GETMOVEDISTANCE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETPOSITIONX

```
DOUBLE GETPOSITIONX(INTEGER objectId)
```

Zwraca pozycję X obiektu w układzie ekranu (po przesunięciu o `+400` względem początku układu fizyki).

**Kompatybilność:** `GETPOSITIONX` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETPOSITIONY

```
DOUBLE GETPOSITIONY(INTEGER objectId)
```

Zwraca pozycję Y obiektu w układzie ekranu (z odwróconą osią — `300 - Y`).

**Kompatybilność:** `GETPOSITIONY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETPOSITIONZ

```
DOUBLE GETPOSITIONZ(INTEGER objectId)
```

Zwraca pozycję Z obiektu.

**Kompatybilność:** `GETPOSITIONZ` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETROTATIONZ

```
DOUBLE GETROTATIONZ(INTEGER objectId)
```

Zwraca kąt obrotu obiektu względem osi Z (w stopniach).

**Kompatybilność:** `GETROTATIONZ` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETROTATIONX / GETROTATIONY

```
DOUBLE GETROTATIONX(INTEGER objectId)
DOUBLE GETROTATIONY(INTEGER objectId)
```

Zwracają odpowiednio obrót wokół osi X lub Y w stopniach. Tak samo jak `GETROTATIONZ`, zwracają bieżącą orientację ciała fizycznego.

**Kompatybilność:** `GETROTATIONX` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `GETROTATIONY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### GETSPEED

```
DOUBLE GETSPEED(INTEGER objectId)
```

Zwraca prędkość obiektu (długość wektora prędkości liniowej).

**Kompatybilność:** `GETSPEED` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### JOIN

```
void JOIN(INTEGER firstId, INTEGER secondId,
          DOUBLE anchorX, DOUBLE anchorY, DOUBLE anchorZ, DOUBLE limitMotor,
          [DOUBLE lowStop, DOUBLE highStop,
           DOUBLE hingeAxisX, DOUBLE hingeAxisY, DOUBLE hingeAxisZ])
```

Tworzy połączenie zawiasowe (hinge joint) pomiędzy dwoma ciałami. Prawidłowe warianty mają 6, 8 albo 11 argumentów. Bez limitów zakres obrotu wynosi od `-90` do `90` stopni; domyślną osią jest Y, czyli `(0, 1, 0)`.

**Parametry**

- `firstId`, `secondId` — identyfikatory łączonych obiektów.
- `anchorX`, `anchorY`, `anchorZ` — punkt kotwiczenia połączenia.
- `limitMotor` — wartość siły potrzebna do zerwania połączenia.
- `lowStop`, `highStop` — (opcjonalnie) graniczne kąty obrotu w stopniach.
- `hingeAxisX`, `hingeAxisY`, `hingeAxisZ` — (opcjonalnie) oś obrotu.

**Przykłady**

```
WORLD^JOIN(199,200,400,300,0,0,-180,180);
WTEST^JOIN(VARI_ID,VARI_TMP1,VARI_X,VARI_TMP2,0,0,-180,180,0,1,0);
```

**Kompatybilność:** `JOIN` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### JOIN2, JOINTSTEER, JOINTSPEED i BREAK

```
void JOIN2(INTEGER firstId, INTEGER secondId,
           DOUBLE anchorX, DOUBLE anchorY, DOUBLE anchorZ,
           DOUBLE axis1X, DOUBLE axis1Y, DOUBLE axis1Z,
           DOUBLE axis2X, DOUBLE axis2Y, DOUBLE axis2Z)
void JOINTSTEER(INTEGER objectId, DOUBLE angle)
void JOINTSPEED(INTEGER objectId, DOUBLE speed)
void BREAK(INTEGER objectId)
```

`JOIN2` tworzy zawias dwustopniowy (`hinge2`), używany np. przez koła. `JOINTSTEER` ustawia jego skręt, `JOINTSPEED` prędkość napędu, a `BREAK` zrywa połączenie przypisane do obiektu. Dla zwykłego zawiasu `JOIN` sterowanie z `JOINTSTEER` i `JOINTSPEED` nic nie zmienia.

**Kompatybilność:** `JOIN2` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `JOINTSTEER` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `JOINTSPEED` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `BREAK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `LINK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `LOAD` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `MOVEOBJECTS` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### PAUSELINK / RESUMELINK

```
void PAUSELINK(INTEGER objectId)
void RESUMELINK(INTEGER objectId)
```

Wstrzymują lub wznawiają aktualizowanie grafiki powiązanej przez [`LINK`](#link). Fizyka obiektu nadal biegnie; zatrzymuje się tylko przepisywanie jego pozycji do `ANIMO` albo `IMAGE`.

**Kompatybilność:** `PAUSELINK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `RESUMELINK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### REMOVEOBJECT

```
void REMOVEOBJECT(INTEGER objectId)
```

Usuwa obiekt z silnika fizycznego.

**Przykłady**

```
WTEST^REMOVEOBJECT(60);
```

**Kompatybilność:** `REMOVEOBJECT` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETACTIVE

```
void SETACTIVE(INTEGER objectId, BOOL active, [BOOL monitorCollisions])
```

Ustawia stan aktywności obiektu oraz, niezależnie od niego, raportowanie kolizji przez [`GETCOLLISION`](#getcollision). Druga flaga nie wyłącza fizycznych zderzeń; wyłącza ich obserwowanie przez skrypt. Jeśli ją pominąć, przyjmuje wartość `active`.

**Parametry**

- `objectId` — identyfikator obiektu.
- `active` — czy obiekt podlega symulacji.
- `monitorCollisions` — czy kolizje mają być zapamiętywane i raportowane skryptowi.

**Przykłady**

```
WPATH^SETACTIVE(VARI_3DPATHID,$1,$2);
```

**Kompatybilność:** `SETACTIVE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETBKGSIZE

```
void SETBKGSIZE(INTEGER leftX, INTEGER rightX, INTEGER topY, INTEGER bottomY)
```

Ustawia rozmiar prostokąta tła powiązanego ze światem.

**Kompatybilność:** `SETBKGSIZE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `SETG` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETMASS, SETBODYPROPERTIES i SETBODYDYNAMICS

```
void SETMASS(INTEGER objectId, DOUBLE mass)
void SETBODYPROPERTIES(INTEGER objectId, DOUBLE mass,
                       DOUBLE sizeX, DOUBLE sizeY, DOUBLE sizeZ)
void SETBODYDYNAMICS(INTEGER objectId, DOUBLE mu, DOUBLE mu2,
                     DOUBLE bounce, DOUBLE bounceVelocity, DOUBLE maxVelocity)
```

`SETMASS` zmienia samą masę. `SETBODYPROPERTIES` zmienia naraz masę i wymiary geometrii, a `SETBODYDYNAMICS` parametry kontaktu oraz limit prędkości. To wygodny zestaw do zmieniania ciała już po `ADDBODY`, bez tworzenia go od nowa.

**Kompatybilność:** `SETMASS` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `SETBODYPROPERTIES` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `SETBODYDYNAMICS` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETCOLLISIONTYPE

```
void SETCOLLISIONTYPE(INTEGER objectId, INTEGER collisionType)
```

Przypisuje obiektowi numeryczny typ kolizji. Emulator przechowuje tę wartość dla zgodności, ale nie używa jej jeszcze do filtrowania zderzeń.

**Kompatybilność:** `SETCOLLISIONTYPE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `SETGRAVITY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETGRAVITYCENTER

```
void SETGRAVITYCENTER(INTEGER objectId, BOOL gravityCenter)
```

Włącza lub wyłącza traktowanie obiektu jako źródła centralnego pola grawitacyjnego.

**Parametry**

- `objectId` — identyfikator obiektu.
- `gravityCenter` — flaga włączająca.

**Kompatybilność:** `SETGRAVITYCENTER` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `SETLIMIT` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETMAXSPEED

```
void SETMAXSPEED(INTEGER objectId, INTEGER maxSpeed)
```

Ustawia maksymalną prędkość obiektu.

**Przykłady**

```
WORLD^SETMAXSPEED(100,200);
```

**Kompatybilność:** `SETMAXSPEED` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETMOVEFLAGS

```
void SETMOVEFLAGS(DOUBLE moveX, DOUBLE moveY)
```

Steruje śledzeniem kamery w osiach X i Y dla obiektu wybranego przez [`SETREFOBJECT`](#setrefobject). Wartość nieujemna włącza śledzenie osi, ujemna je wyłącza. Nie dotyczy ruchu ciała fizycznego.

**Przykłady**

```
WPATH^SETMOVEFLAGS(VARITEMP0,VARITEMP1);
```

**Kompatybilność:** `SETMOVEFLAGS` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `SETPOSITION` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETPOSITIONCOORD

```
void SETPOSITIONCOORD(INTEGER objectId, INTEGER coordIndex, DOUBLE value)
```

Ustawia pojedynczą składową pozycji: `coordIndex = 0` oznacza X, a `1` oznacza Y. Pozostałe indeksy są ignorowane. Współrzędne X i Y są podawane w układzie ekranu, tak jak w `SETPOSITION`.

**Kompatybilność:** `SETPOSITIONCOORD` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### ROTATE i ZEROALL

```
void ROTATE(INTEGER objectId, [DOUBLE ignored], DOUBLE angle)
void ZEROALL(INTEGER objectId)
```

`ROTATE` ustawia bezwzględny obrót wokół osi Z w stopniach. Przy trzech argumentach środkowy jest ignorowany; przy dwóch drugi jest kątem. `ZEROALL` zeruje prędkości liniową i kątową oraz nagromadzoną siłę i moment.

**Kompatybilność:** `ROTATE` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.
**Kompatybilność:** `ZEROALL` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### SETREFOBJECT

```
void SETREFOBJECT(INTEGER objectId)
```

Ustawia obiekt jako referencyjny — używany przy obliczeniach pozycji względem niego.

**Przykłady**

```
WPATH^SETREFOBJECT(100);
```

**Kompatybilność:** `SETREFOBJECT` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

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

**Kompatybilność:** `SETVELOCITY` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### START

```
void START()
```

Uruchamia symulację (włącza timer silnika fizycznego).

**Przykłady**

```
WORLD^START();
```

**Kompatybilność:** `START` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### STOP

```
void STOP()
```

Zatrzymuje symulację (wyłącza timer silnika fizycznego). Stan obiektów pozostaje zachowany.

**Przykłady**

```
WORLD^STOP();
```

**Kompatybilność:** `STOP` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

### UNLINK

```
void UNLINK(INTEGER objectId)
```

Zrywa powiązanie obiektu z animacją utworzone metodą [`LINK`](#link).

**Przykłady**

```
WTEST^UNLINK(VARI_TMP2);
```

**Kompatybilność:** `UNLINK` - typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`.

## Zgodność, ale bez zachowania

Poniższe wywołania są rozpoznawane, aby skrypty nie wybuchały na nieznanej metodzie, lecz w aktualnym emulatorze nie wykonują działania: `SETANGLE`, `SETSPEED`, `SETDISPERSION`, `SETALWAYSACTIVE`, `ENABLEMESH`, `COLLIDEMESH`, `USEFF` i `ADDMESHWAYPOINT`. `ISMESHENABLED` oraz `ISMESHCOLLIDING` zawsze zwracają `FALSE`.

Metody nawigacyjne `SETOBJECTPROPERTIES`, `FOLLOW`, `MOVETO`, `MOVETOPATH`, `GOTOPATH`, `ADDTOPATH`, `ADDWAYPOINT`, `REMOVEWAYPOINT`, `REMOVEROUTE` i `FOLLOWROUTE` również są przyjmowane, lecz nie są jeszcze zaimplementowane. Do działającego prowadzenia po siatce używaj [`FINDPATH`](#findpath) i [`FOLLOWPATH`](#followpath).

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
