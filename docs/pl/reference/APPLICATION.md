# APPLICATION

Obiekt aplikacji — najwyższy poziom hierarchii skryptów gry. Deklarowany w pliku [`Application.def`](../engine/scripts.md#punkt-startowy) jako pierwszy obiekt; zawiera listę epizodów i wskazuje, który z nich uruchomić jako pierwszy.

## Pola

### EPISODES

```
STRING EPISODES
```

Lista nazw epizodów ([`EPISODE`](EPISODE.md)) tworzących aplikację, oddzielonych przecinkami. W analizowanych grach z reguły zawiera jedną pozycję.

### PATH

```
STRING PATH
```

Ścieżka relatywna do katalogu `dane`, w którym znajdują się pliki aplikacji. Używana przez silnik przy lokalizowaniu pliku `.CNV` powiązanego z aplikacją (zobacz [Ładowanie kolejnych plików](../engine/scripts.md#ladowanie-kolejnych-plikow)).

### STARTWITH

```
STRING STARTWITH
```

Nazwa epizodu, od którego silnik rozpocznie grę.

### Metadane

Następujące pola są zapisywane w pliku skryptu jako metadane i nie wpływają bezpośrednio na działanie silnika:

- `AUTHOR` — autor pliku.
- `BLOOMOO_VERSION` — wersja silnika BlooMoo.
- `CREATIONTIME` — data utworzenia pliku.
- `DESCRIPTION` — opis aplikacji.
- `LASTMODIFYTIME` — data ostatniej modyfikacji pliku.
- `VERSION` — wersja aplikacji.

## Metody

### EXIT

```
void EXIT()
```

Kończy działanie aplikacji.

**Przykłady**

```
GAME^EXIT();
```

**Kompatybilność:** `EXIT` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETLANGUAGE

```
STRING GETLANGUAGE()
```

Zwraca aktualnie ustawiony język aplikacji. Wartością domyślną jest `"POL"`.

**Zwraca**: kod języka.

**Przykłady**

```
UFO^GETLANGUAGE();
```

**Kompatybilność:** `GETLANGUAGE` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RUN

```
mixed RUN(STRING varName, STRING methodName, [mixed param1, ..., mixed paramN])
```

Wywołuje metodę `methodName` na zmiennej `varName`, przekazując pozostałe argumenty jako jej parametry. Wartość zwracana to wynik wywołanej metody. Mechanizm pełni rolę dynamicznego wywołania — pozwala wybrać zarówno docelową zmienną, jak i metodę w runtime.

**Parametry**

- `varName` — nazwa docelowej zmiennej.
- `methodName` — nazwa wywoływanej metody.
- `param1, …, paramN` — (opcjonalnie) argumenty przekazywane do wywołania.

**Zwraca**: wartość zwróconą przez wywołaną metodę.

**Przykłady**

```
UFO^RUN(VARSTRINGTEMP, "SETASBUTTON", FALSE, FALSE);
UFO^RUN(VARSTRINGTEMP, "HIDE");
UFO^RUN(["ANIMO"+$1], "HIDE");
UFO^RUN($1, "PLAY", $2);
UFO^RUN(ARRCARS^GET(VARPLAYER), "SETPRIORITY", ARRPRIORITY^GET(VARPLAYER));
```

**Kompatybilność:** `RUN` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RUNENV

```
mixed RUNENV(STRING sceneName, STRING behaviourName)
```

Wywołuje procedurę `behaviourName`, ale tylko wtedy, gdy aktualnie aktywna scena ma nazwę `sceneName`. W przeciwnym razie metoda nie ma efektu. Mechanizm służy do warunkowego uruchamiania procedur, które mają sens wyłącznie w konkretnym kontekście sceny.

**Parametry**

- `sceneName` — nazwa sceny, w której procedura ma być wykonana.
- `behaviourName` — nazwa wywoływanej procedury.

**Zwraca**: wartość zwróconą przez procedurę lub `NULL`, jeżeli warunek sceny nie został spełniony.

**Przykłady**

```
GAME^RUNENV(SCENENAME, "__HELPSTART__");
GAME^RUNENV(SCENENAME, "B_PAUSE_START");
GAME^RUNENV(SCENENAME, "__CUTINIT__");
```

**Kompatybilność:** `RUNENV` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETLANGUAGE

```
void SETLANGUAGE(STRING languageCode)
```

Ustawia kod języka aplikacji. Silnik mapuje przekazany kod LCID Windows na wewnętrzny identyfikator języka według poniższej tabeli:

| Kod LCID | Język     | Wewnętrzny ID | Podkatalog  |
|----------|-----------|---------------|-------------|
| `0415`   | polski    | `1`           | `POL`       |
| `0405`   | czeski    | `2`           | `CZE`       |
| `0402`   | bułgarski | `3`           | `BUL`       |
| `0418`   | rumuński  | `4`           | `ROM`       |
| `0419`   | rosyjski  | `5`           | `RUS`       |
| `040E`   | węgierski | `6`           | `HUN`       |
| `041B`   | słowacki  | `7`           | `SLO`       |
| `0422`   | ukraiński | `8`           | `UKR`       |
| `0407`   | niemiecki | `9`           | `NIEM`      |
| `0c07`   | niemiecki | `10`          | `NIEM`      |
| `0807`   | niemiecki | `11`          | `NIEM`      |

Wybrany identyfikator decyduje o podkatalogu z lokalizowanymi zasobami, z którego silnik wczytuje pliki gry (zobacz [Ładowanie kolejnych plików](../engine/scripts.md#ladowanie-kolejnych-plikow)). Identyfikatory `9`, `10` i `11` (w toku trwających analiz widziałem je jedynie w *Reksio i Kretes w Akcji*) odpowiadają podkatalogowi `NIEM` — wersji niemieckojęzycznej. Identyfikator spoza powyższego zakresu skutkuje pustym podkatalogiem. Po ustawieniu języka silnik ponownie inicjalizuje również układ klawiatury.

**Parametry**

- `languageCode` — kod LCID w postaci czterocyfrowej liczby szesnastkowej.

**Przykłady**

```
UFO^SETLANGUAGE("0415");
UFO^SETLANGUAGE("040E");
UFO^SETLANGUAGE("0419");
```

**Kompatybilność:** `SETLANGUAGE` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.
