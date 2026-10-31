# INTEGER

Liczba całkowita ze znakiem.

## Pola

### TOINI

```
BOOL TOINI
```

Określa, czy wartość pola jest zapisywana do pliku INI i przywracana po ponownym uruchomieniu.

### VALUE

```
INTEGER VALUE
```

Aktualna wartość zmiennej.

## Metody

!!! bug "Rozbieżność w emulatorze"
    Opisy dotyczą silników od Pikliba 7.1. Crosstesty wykryły różnicę do sprawdzenia: metody oznaczone tu jako `void` w emulatorze zwracają nową wartość. Zobacz [Rozbieżności z oryginałem](../emulator/divergences.md).

### ABS

```
INTEGER ABS(INTEGER value)
```

Zapisuje w zmiennej wartość bezwzględną przekazanego argumentu i zwraca ją.

**Parametry**

- `value` — liczba, której wartość bezwzględna zostanie zapisana.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARINT0^ABS(VARINT0);
I_7^ABS(ARRFLDCLONES^GET(I_FIELD_INDEX));
```

**Kompatybilność:** `ABS` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ADD

```
INTEGER ADD(INTEGER addend)
```

Dodaje argument do bieżącej wartości zmiennej, zapisuje wynik i zwraca go.

**Parametry**

- `addend` — wartość dodawana.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARIRADIUS^ADD([VARIMENUVISIBLE*16]);
VARIKRETSTARTX^ADD(50);
VARITEMP0^ADD(VARIRADIUS);
```

**Kompatybilność:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### AND

```
INTEGER AND(INTEGER value)
```

Wykonuje bitową koniunkcję bieżącej wartości zmiennej z argumentem, zapisuje wynik i zwraca go.

**Parametry**

- `value` — wartość, z którą wykonywana jest operacja.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARITEMP2^AND(1);
VARITEMP1^AND(ARRMASK^GET(ARRENEMYMASK^GET(VARENEMY)));
```

**Kompatybilność:** `AND` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ARCTANEX

```
INTEGER ARCTANEX(DOUBLE y, DOUBLE x, [DOUBLE offset])
```

Liczy kąt wektora `(x, y)` w stopniach, znormalizowany do przedziału `0`–`359`. W odróżnieniu od zwykłego arcus tangens bierze pod uwagę ćwiartkę układu, więc rozróżnia kierunki przeciwne. Wynik zostaje zapisany w zmiennej i jednocześnie zwrócony.

Opcjonalny `offset` jest dodawany do wyniku przed ponownym sprowadzeniem go do zakresu `0`–`359` — przydaje się, gdy zero kąta ma leżeć gdzie indziej niż na osi X.

**Parametry**

- `y` — składowa pionowa wektora.
- `x` — składowa pozioma wektora.
- `offset` — opcjonalne przesunięcie kąta w stopniach.

**Kompatybilność:** `ARCTANEX` - brak w przeanalizowanych bibliotekach z `compat.json`.

### CLAMP

```
INTEGER CLAMP(INTEGER rangeMin, INTEGER rangeMax)
```

Sprowadza bieżącą wartość zmiennej do przedziału `[rangeMin, rangeMax]`. Wartości spoza przedziału są przycinane do jego granic.

**Parametry**

- `rangeMin` — dolna granica przedziału (włącznie).
- `rangeMax` — górna granica przedziału (włącznie).

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARIMENUX^CLAMP(92, 708);
I1^CLAMP(0, IMIECZMAX);
IFRAMER^CLAMP(IFRAMECENTER, IFRAMEMAX);
```

**Kompatybilność:** `CLAMP` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CLEAR

```
void CLEAR()
```

Ustawia wartość zmiennej na `0`.

**Kompatybilność:** `CLEAR` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DEC

```
void DEC()
```

Zmniejsza wartość zmiennej o `1`.

**Przykłady**

```
VARITIMETOEXIT^DEC();
VARIWAIT^DEC();
```

**Kompatybilność:** `DEC` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### DIV

```
void DIV(INTEGER divisor)
```

Dzieli bieżącą wartość zmiennej (dzielenie całkowite) przez argument i zapisuje wynik. Dzielenie przez zero wywala silnik.

**Parametry**

- `divisor` — dzielnik.

**Przykłady**

```
VARITEMP0^DIV(2);
VARMOUSEDX^DIV(VARMOUSESPEED);
```

**Kompatybilność:** `DIV` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GET

```
INTEGER GET()
```

Zwraca aktualną wartość zmiennej.

**Zwraca**: bieżąca wartość pola `VALUE`.

**Kompatybilność:** `GET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### INC

```
void INC()
```

Zwiększa wartość zmiennej o `1`.

**Przykłady**

```
VARINUMITEMS^INC();
VARITUTCOUNT^INC();
```

**Kompatybilność:** `INC` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LENGTH

```
INTEGER LENGTH(INTEGER x, INTEGER y)
```

Wyznacza długość wektora `(x, y)` jako `sqrt(x² + y²)`, obcina wynik do liczby całkowitej i zwraca go. Stan zmiennej po wywołaniu zależy od silnika (zobacz [Rozbieżności z oryginałem](../emulator/divergences.md)).

**Parametry**

- `x` — pierwsza składowa wektora.
- `y` — druga składowa wektora.

**Zwraca**: długość wektora obciętą do liczby całkowitej.

**Przykłady**

```
VARI_TMP1^LENGTH([VARI_TMPX-VARI_CARX], [VARI_TMPY-VARI_CARY]);
I3^LENGTH(I3, 600);
```

**Kompatybilność:** `LENGTH` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MOD

```
void MOD(INTEGER divisor)
```

Zapisuje w zmiennej resztę z dzielenia jej bieżącej wartości przez argument. Dzielenie przez zero wywala silnik.

**Parametry**

- `divisor` — dzielnik.

**Przykłady**

```
VARITEMP4^MOD(8);
IGC^MOD(ARLEVG^GETSIZE());
```

**Kompatybilność:** `MOD` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### MUL

```
void MUL(INTEGER multiplier)
```

Mnoży bieżącą wartość zmiennej przez argument i zapisuje wynik.

**Parametry**

- `multiplier` — mnożnik.

**Przykłady**

```
VARITEMP0^MUL(34);
I1^MUL(IGRID);
```

**Kompatybilność:** `MUL` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### NOT

```
INTEGER NOT()
```

Wykonuje bitową negację (dopełnienie) bieżącej wartości zmiennej, zapisuje wynik i zwraca go.

**Zwraca**: nową wartość zmiennej.

**Kompatybilność:** `NOT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### OR

```
INTEGER OR(INTEGER value)
```

Wykonuje bitową alternatywę bieżącej wartości zmiennej z argumentem, zapisuje wynik i zwraca go.

**Parametry**

- `value` — wartość, z którą wykonywana jest operacja.

**Zwraca**: nową wartość zmiennej.

**Kompatybilność:** `OR` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### POWER

```
INTEGER POWER(INTEGER exponent)
```

Podnosi bieżącą wartość zmiennej do podanej potęgi, zaokrągla wynik do liczby całkowitej, zapisuje i zwraca.

**Parametry**

- `exponent` — wykładnik potęgi.

**Zwraca**: nową wartość zmiennej.

**Kompatybilność:** `POWER` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RANDOM

```
INTEGER RANDOM(INTEGER bound)
INTEGER RANDOM(INTEGER min, INTEGER max)
```

Zapisuje w zmiennej liczbę pseudolosową i zwraca ją.

- W wariancie z jednym argumentem zwracana jest liczba z przedziału `[0, bound)`.
- W wariancie z dwoma argumentami zwracana jest liczba z przedziału `[min, max]` (oba końce włącznie).

**Parametry**

- `bound` — wartość graniczna (wyłącznie).
- `min`, `max` — granice przedziału (włącznie).

**Zwraca**: wylosowaną wartość.

**Przykłady**

```
VARITEMP0^RANDOM(0, 100);
VARI_TMP3^RANDOM(VARI_TMP3);
```

**Kompatybilność:** `RANDOM` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (6/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESETINI

```
INTEGER RESETINI()
```

Przywraca wartość zmiennej do wartości resetu zdefiniowanej w atrybutach obiektu w skrypcie. Silnik szuka wartości w kolejności: `DEFAULT` → `INIT_VALUE` → `VALUE`; używana jest pierwsza znaleziona. Jeśli żadna z nich nie jest ustawiona, wartość ustawiana jest na `0`.

**Zwraca**: nową wartość zmiennej.

**Kompatybilność:** `RESETINI` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
void SET(INTEGER value)
```

Ustawia wartość zmiennej.

**Parametry**

- `value` — nowa wartość.

**Przykłady**

```
G_IPAGE^SET(800);
VARITEMP1^SET($2);
ITEMP^SET(STCBAZA|SRODEK);
```

**Kompatybilność:** `SET` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SUB

```
void SUB(INTEGER subtrahend)
```

Odejmuje argument od bieżącej wartości zmiennej i zapisuje wynik.

**Parametry**

- `subtrahend` — wartość odejmowana.

**Przykłady**

```
G_IPAGE^SUB(100);
VARIPRIORITY^SUB(VARIBKGOFFSETY);
```

**Kompatybilność:** `SUB` - `PIKLIB61.DLL` ⚠️ (3/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SWITCH

```
void SWITCH(INTEGER valueA, INTEGER valueB)
```

Jeżeli bieżąca wartość zmiennej jest równa `valueA`, zmiennej zostaje przypisana `valueB`; w przeciwnym razie — `valueA`. Pozwala to naprzemiennie przełączać między dwiema wartościami.

**Parametry**

- `valueA` — pierwsza wartość.
- `valueB` — druga wartość.

**Kompatybilność:** `SWITCH` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### XOR

```
INTEGER XOR(INTEGER value)
```

Wykonuje bitową różnicę symetryczną bieżącej wartości zmiennej z argumentem, zapisuje wynik i zwraca go.

**Parametry**

- `value` — wartość, z którą wykonywana jest operacja.

**Zwraca**: nową wartość zmiennej.

**Kompatybilność:** `XOR` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONCHANGED

Wywoływany, gdy wartość zmiennej zostaje zmieniona na inną niż dotychczasowa.

### ONBRUTALCHANGED

Wywoływany przy każdym wywołaniu metody zmieniającej wartość, niezależnie od tego, czy nowa wartość różni się od poprzedniej.

### ONINIT

Wywoływany w momencie inicjalizacji zmiennej.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału.
