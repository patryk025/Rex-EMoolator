# SCENE

Pojedyncza scena — jedna plansza, ekran lub minigra. Należy do [`EPISODE`](EPISODE.md). Definiuje tło, muzykę, zakres priorytetów hotspotów oraz udostępnia metody sterowania zawartością sceny.

## Pola

### BACKGROUND

```
STRING BACKGROUND
```

Ścieżka do pliku `.IMG` z obrazem tła sceny.

### DLLS

```
STRING DLLS
```

Lista bibliotek DLL podpinanych do sceny (rozszerzeń biblioteki BlooMoo, np. `INERTIA`).

### MUSIC

```
STRING MUSIC
```

Ścieżka do pliku z muzyką tła sceny.

### MUSICVOLUME

```
INTEGER MUSICVOLUME
```

Głośność muzyki sceny. Wartość `1000` odpowiada 100%. Modyfikowane metodą [`SETMUSICVOLUME`](#setmusicvolume).

### MINHSPRIORITY

```
INTEGER MINHSPRIORITY
```

Minimalny priorytet (`Z`) hotspotów aktywnych na scenie. Modyfikowane metodą [`SETMINHSPRIORITY`](#setminhspriority).

### MAXHSPRIORITY

```
INTEGER MAXHSPRIORITY
```

Maksymalny priorytet (`Z`) hotspotów aktywnych na scenie. Modyfikowane metodą [`SETMAXHSPRIORITY`](#setmaxhspriority).

### PATH

```
STRING PATH
```

Ścieżka relatywna do katalogu `dane` zawierająca pliki sceny.

### Metadane

Następujące pola są zapisywane jako metadane i nie wpływają bezpośrednio na działanie silnika:

- `AUTHOR` — autor pliku.
- `CREATIONTIME` — data utworzenia pliku.
- `LASTMODIFYTIME` — data ostatniej modyfikacji pliku.
- `VERSION` — wersja sceny.

## Metody

### GETDRAGGEDNAME

```
STRING GETDRAGGEDNAME()
```

Zwraca nazwę obiektu graficznego aktualnie przeciąganego przez [`BUTTON`](BUTTON.md). Poza operacją przeciągania zwraca pusty ciąg.

**Kompatybilność:** `GETDRAGGEDNAME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETMAXHSPRIORITY

```
INTEGER GETMAXHSPRIORITY()
```

Zwraca maksymalny priorytet hotspotów aktywnych na scenie.

**Zwraca**: bieżąca wartość pola [`MAXHSPRIORITY`](#maxhspriority).

**Kompatybilność:** `GETMAXHSPRIORITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETMINHSPRIORITY

```
INTEGER GETMINHSPRIORITY()
```

Zwraca minimalny priorytet hotspotów aktywnych na scenie.

**Zwraca**: bieżąca wartość pola [`MINHSPRIORITY`](#minhspriority).

**Kompatybilność:** `GETMINHSPRIORITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPLAYINGANIMO

```
void GETPLAYINGANIMO(STRING groupName)
```

Wypełnia zmienną typu [`GROUP`](index.md) o podanej nazwie listą nazw wszystkich obiektów [`ANIMO`](index.md) aktualnie odtwarzanych na scenie. Istniejąca zawartość grupy jest nadpisywana.

**Parametry**

- `groupName` — nazwa zmiennej `GROUP`, która ma zostać wypełniona.

**Kompatybilność:** `GETPLAYINGANIMO` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PAUSE

```
void PAUSE()
```

Pauzuje muzykę sceny oraz wszystkie odtwarzane animacje [`ANIMO`](index.md).

**Przykłady**

```
BARANDALF^PAUSE();
```

**Kompatybilność:** `PAUSE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVECLONES

```
void REMOVECLONES(STRING varName, INTEGER firstId, INTEGER lastId)
```

Usuwa klony zmiennej `varName` o numerach z zakresu `[firstId, lastId]`. Wartość `-1` w `lastId` oznacza „do ostatniego klonu". Klony są nazwane wg wzorca `varName_N`, gdzie `N` rośnie od `1`.

**Parametry**

- `varName` — nazwa bazowa klonowanej zmiennej.
- `firstId` — numer pierwszego klonu do usunięcia (najmniej `1`).
- `lastId` — numer ostatniego klonu do usunięcia, lub `-1` dla wszystkich klonów do końca.

**Przykłady**

```
ARCADE^REMOVECLONES(VARSCURRENTITEMOBJECT, -1, -1);
CUTSCENKI^REMOVECLONES(SANN, -1, -1);
```

**Kompatybilność:** `REMOVECLONES` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESUME

```
void RESUME()
```

Wznawia muzykę sceny (z głośnością z pola [`MUSICVOLUME`](#musicvolume)) oraz wszystkie wstrzymane animacje [`ANIMO`](index.md).

**Przykłady**

```
BARANDALF^RESUME();
```

**Kompatybilność:** `RESUME` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESUMEONLY

```
void RESUMEONLY(STRING groupName)
```

Wznawia tylko te wstrzymane animacje, których nazwy znajdują się w zmiennej typu [`GROUP`](index.md) o podanej nazwie.

**Parametry**

- `groupName` — nazwa zmiennej `GROUP` z listą animacji do wznowienia.

**Kompatybilność:** `RESUMEONLY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RUN

```
mixed RUN(STRING varName, STRING methodName, [mixed param1, ..., mixed paramN])
```

Dynamicznie wywołuje metodę `methodName` na zmiennej `varName`. Zachowanie identyczne jak [`APPLICATION.RUN`](APPLICATION.md#run); wariant na poziomie sceny istnieje dla skryptów, które mają referencję do sceny zamiast do aplikacji.

**Parametry**

- `varName` — nazwa docelowej zmiennej.
- `methodName` — nazwa wywoływanej metody.
- `param1, …, paramN` — (opcjonalnie) argumenty.

**Zwraca**: wartość zwróconą przez wywołaną metodę.

**Przykłady**

```
S16_SPACEINVADERS^RUN(VARNAME, "SETPOSITION", 0, 0);
S16_SPACEINVADERS^RUN(VARNAME, "PLAY", VARTEMPSTRING);
S16_SPACEINVADERS^RUN(VARUFOHIT, "PLAY", ["TRAFIONY"+RANDOM^GET(0,2)]);
```

**Kompatybilność:** `RUN` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RUNCLONES

```
void RUNCLONES(STRING varName, INTEGER firstId, INTEGER lastId, STRING behaviourName)
```

Wywołuje procedurę `behaviourName` dla każdego klonu zmiennej `varName` w zakresie `[firstId, lastId]`. Wartość `-1` w `lastId` oznacza „do ostatniego klonu". Procedura otrzymuje jako pierwszy argument (`$1`) nazwę klonu.

**Parametry**

- `varName` — nazwa bazowa klonowanej zmiennej.
- `firstId` — numer pierwszego klonu (najmniej `1`).
- `lastId` — numer ostatniego klonu lub `-1`.
- `behaviourName` — nazwa wywoływanej procedury.

**Przykłady**

```
S16_SPACEINVADERS^RUNCLONES("ANIMOUFO", -1, -1, "BEHINITUFO");
S71_DROGA^RUNCLONES("ANNKURA", -1, -1, "BEHINITCLONES");
```

**Kompatybilność:** `RUNCLONES` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETMAXHSPRIORITY

```
void SETMAXHSPRIORITY(INTEGER maxHSPriority)
```

Ustawia maksymalny priorytet hotspotów aktywnych na scenie.

**Parametry**

- `maxHSPriority` — nowa wartość maksymalnego priorytetu.

**Kompatybilność:** `SETMAXHSPRIORITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETMINHSPRIORITY

```
void SETMINHSPRIORITY(INTEGER minHSPriority)
```

Ustawia minimalny priorytet hotspotów aktywnych na scenie.

**Parametry**

- `minHSPriority` — nowa wartość minimalnego priorytetu.

**Przykłady**

```
MENUGLOWNE^SETMINHSPRIORITY(999);
MENUGLOWNE^SETMINHSPRIORITY(0);
```

**Kompatybilność:** `SETMINHSPRIORITY` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETMUSICVOLUME

```
void SETMUSICVOLUME(INTEGER volume)
```

Ustawia głośność muzyki sceny. Wartość `1000` odpowiada 100%. Zmiana jest stosowana natychmiast, jeżeli muzyka jest aktualnie odtwarzana.

**Parametry**

- `volume` — nowa głośność.

**Przykłady**

```
ARCADE^SETMUSICVOLUME(G_ARRSETTINGS^GET(1));
INTRO_2^SETMUSICVOLUME(500);
DIALOGS^SETMUSICVOLUME([0.8*G_ARRSETTINGS^GET(1)]);
```

**Kompatybilność:** `SETMUSICVOLUME` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### STARTMUSIC

```
void STARTMUSIC(STRING filename)
```

Zapisuje w polu [`MUSIC`](#music) ścieżkę do pliku muzycznego, który będzie odtwarzany jako muzyka tła sceny.

**Parametry**

- `filename` — ścieżka do pliku muzycznego.

**Przykłady**

```
ARCADE^STARTMUSIC(VARSMUSIC);
MAGIC^STARTMUSIC("POJEDYNEK.WAV");
DIALOGS^STARTMUSIC("GABINETY.WAV");
```

**Kompatybilność:** `STARTMUSIC` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.
