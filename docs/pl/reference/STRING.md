# STRING

Ciąg znaków.

## Pola

### TOINI

```
BOOL TOINI
```

Określa, czy wartość pola jest zapisywana do pliku INI i przywracana po ponownym uruchomieniu.

### VALUE

```
STRING VALUE
```

Aktualna wartość zmiennej.

## Metody

### ADD

```
STRING ADD(STRING text)
```

Dokleja argument do bieżącej wartości zmiennej (konkatenacja), zapisuje wynik i zwraca go.

**Parametry**

- `text` — tekst do dołączenia.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
G_SLASTOBJECTS^ADD(VARSTEMP0);
VARSMUSIC^ADD(".WAV");
S_0^ADD($1);
```

**Kompatybilność:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CHANGEAT

```
STRING CHANGEAT(INTEGER index, STRING replacement)
```

Zastępuje pojedynczy znak na pozycji `index` ciągiem `replacement`. Jeżeli `replacement` ma długość różną od `1`, długość ciągu po operacji zmienia się odpowiednio. Wywołanie z `index` poza zakresem nie zmienia wartości zmiennej.

**Parametry**

- `index` — pozycja znaku do zastąpienia (numerowana od `0`).
- `replacement` — ciąg, którym zostanie zastąpiony znak.

**Zwraca**: nową wartość zmiennej (lub niezmienioną wartość, jeśli `index` był poza zakresem).

**Przykłady**

```
*VARARRAYNAME^CHANGEAT([VARCLONE-1], "NULL");
*VARENEMYSTATE^CHANGEAT([VARINT1-1], VARSTRING1);
```

**Kompatybilność:** `CHANGEAT` - brak w przeanalizowanych bibliotekach z `compat.json`.

### COPYFILE

```
BOOL COPYFILE(STRING source, STRING destination)
```

Kopiuje plik w wirtualnym systemie plików gry (VFS) z lokalizacji `source` do `destination`. Metoda nie korzysta z wartości zmiennej, na której jest wywoływana — operuje wyłącznie na argumentach.

**Parametry**

- `source` — ścieżka pliku źródłowego w VFS.
- `destination` — ścieżka pliku docelowego w VFS.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli kopiowanie się powiodło; `FALSE` w przeciwnym razie (np. gdy plik źródłowy nie istnieje lub wystąpił błąd I/O).

**Przykłady**

```
VARSTEMP0^COPYFILE("$COMMON\ITEMS_DEF.DTA", "$COMMON\ITEMS0.DTA");
S1^COPYFILE(S1, S2);
```

**Kompatybilność:** `COPYFILE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (6/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CUT

```
STRING CUT(INTEGER index, INTEGER length)
```

Zastępuje wartość zmiennej fragmentem bieżącego ciągu, zaczynającym się od pozycji `index` i o długości `length`. Jeżeli długość przekracza dostępną liczbę znaków, fragment kończy się na końcu ciągu. Jeżeli `index` jest poza zakresem, zmienna przyjmuje wartość pustego ciągu.

**Parametry**

- `index` — początkowa pozycja fragmentu (numerowana od `0`).
- `length` — długość fragmentu.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARSTRING0^CUT(0, VARSTRING0^FIND("_"));
```

**Kompatybilność:** `CUT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### FIND

```
INTEGER FIND(STRING needle)
INTEGER FIND(STRING needle, INTEGER offset)
```

Wyszukuje w bieżącej wartości zmiennej pozycję pierwszego wystąpienia podanego ciągu. Metoda nie modyfikuje wartości zmiennej.

**Parametry**

- `needle` — szukany ciąg.
- `offset` — (opcjonalnie) pozycja, od której rozpoczyna się wyszukiwanie. Domyślnie `0`.

**Zwraca**: pozycję pierwszego wystąpienia (numerowaną od `0`) lub `-1`, jeżeli ciąg nie został znaleziony.

**Przykłady**

```
VARSTEMP0^FIND("IDLE");
SWYRAZ^FIND(S1);
```

**Kompatybilność:** `FIND` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GET

```
STRING GET()
STRING GET(INTEGER index)
STRING GET(INTEGER index, INTEGER length)
```

Zwraca fragment bieżącej wartości zmiennej. Metoda nie modyfikuje wartości zmiennej.

- W wariancie bez argumentów zwracana jest cała wartość pola `VALUE`.
- W wariantach z argumentami zwracany jest fragment zaczynający się od pozycji `index` i o długości `length` (domyślnie `1`).

Jeżeli `index` jest poza zakresem, zwracany jest pusty ciąg. Jeżeli długość przekracza liczbę dostępnych znaków, fragment kończy się na końcu ciągu.

**Parametry**

- `index` — początkowa pozycja fragmentu (numerowana od `0`).
- `length` — (opcjonalnie) długość fragmentu. Domyślnie `1`.

**Zwraca**: wycięty fragment ciągu (lub całą wartość w wariancie bezargumentowym).

**Przykłady**

```
VARSTEMP3^GET(7);
VARENEMYNAME^GET(0, VARENEMYNAME^FIND("_"));
SOBJNAME^GET(0, 1);
```

**Kompatybilność:** `GET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LENGTH

```
INTEGER LENGTH()
```

Zwraca długość bieżącej wartości zmiennej. Metoda nie modyfikuje wartości zmiennej.

**Zwraca**: [`INTEGER`](INTEGER.md) — liczbę znaków w ciągu.

**Przykłady**

```
VARSTEMP0^LENGTH();
```

**Kompatybilność:** `LENGTH` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOWER

```
STRING LOWER()
```

Zamienia wszystkie litery w bieżącej wartości zmiennej na małe.

**Zwraca**: nową wartość zmiennej.

**Kompatybilność:** `LOWER` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ❌, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ⚠️ (3/5).

### REPLACEAT

```
STRING REPLACEAT(INTEGER index, INTEGER length, STRING replacement)
```

Zastępuje fragment bieżącego ciągu o długości `length`, zaczynający się od pozycji `index`, ciągiem `replacement`. Jeżeli długość fragmentu przekracza dostępne znaki, podmieniana jest reszta ciągu. Wywołanie z `index` poza zakresem nie zmienia wartości zmiennej.

**Parametry**

- `index` — początkowa pozycja podmienianego fragmentu (numerowana od `0`).
- `length` — długość podmienianego fragmentu.
- `replacement` — ciąg, który zostanie wstawiony w miejsce fragmentu.

**Zwraca**: nową wartość zmiennej (lub niezmienioną wartość, jeśli `index` był poza zakresem).

**Przykłady**

```
S3^REPLACEAT(0, 1, S1);
VARSTMP2^REPLACEAT(8, 2, ["00"+VARIKRAINANO]);
```

**Kompatybilność:** `REPLACEAT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESETINI

```
STRING RESETINI()
```

Przywraca wartość zmiennej do wartości resetu zdefiniowanej w atrybutach obiektu w skrypcie. Silnik szuka wartości w kolejności: `DEFAULT` → `INIT_VALUE` → `VALUE`; używana jest pierwsza znaleziona. Jeśli żadna z nich nie jest ustawiona, wartość ustawiana jest na pusty ciąg.

**Zwraca**: nową wartość zmiennej.

**Kompatybilność:** `RESETINI` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
STRING SET(STRING value)
```

Ustawia wartość zmiennej.

**Parametry**

- `value` — nowa wartość.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
SCENENAME^SET(PRZYGODA^GETCURRENTSCENE());
VARSTEMP0^SET(["BEHCLICK_"+SOBJECT|IDNAME]);
VARSTEMP0^SET($1);
```

**Kompatybilność:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SUB

```
STRING SUB(INTEGER index, INTEGER length)
```

Usuwa z bieżącej wartości zmiennej fragment o długości `length`, zaczynający się od pozycji `index`. Pozostałe fragmenty (przed i za usuwanym) są scalane. Wywołanie z `index` poza zakresem nie zmienia wartości zmiennej.

**Parametry**

- `index` — początkowa pozycja usuwanego fragmentu (numerowana od `0`).
- `length` — długość usuwanego fragmentu.

**Zwraca**: nową wartość zmiennej (lub niezmienioną wartość, jeśli `index` był poza zakresem).

**Przykłady**

```
VARSTEMP0^SUB(0, 5);
VARSTEMP0^SUB(VARITEMP0, [VARSTEMP0^LENGTH()-VARITEMP0]);
```

**Kompatybilność:** `SUB` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### UPPER

```
STRING UPPER()
```

Zamienia wszystkie litery w bieżącej wartości zmiennej na wielkie.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
SDIALOGWAVENAME^UPPER();
SDIALOGPERSON^UPPER();
```

**Kompatybilność:** `UPPER` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONCHANGED

Wywoływany, gdy wartość zmiennej zostaje zmieniona na inną niż dotychczasowa.

### ONBRUTALCHANGED

Wywoływany przy każdym wywołaniu metody zmieniającej wartość, niezależnie od tego, czy nowa wartość różni się od poprzedniej.

### ONINIT

Wywoływany w momencie inicjalizacji zmiennej.
