# DOUBLE

Liczba zmiennoprzecinkowa o podwójnej precyzji.

## Pola

### TOINI

```
BOOL TOINI
```

Określa, czy wartość pola jest zapisywana do pliku INI i przywracana po ponownym uruchomieniu.

### VALUE

```
DOUBLE VALUE
```

Aktualna wartość zmiennej. Akceptowane są zapisy w notacji standardowej (np. `1.234`) oraz w notacji wykładniczej z literą `e` lub `d` (np. `1.23e4`, `1.23d4`).

## Metody

### ABS

```
DOUBLE ABS(DOUBLE value)
```

Zapisuje w zmiennej wartość bezwzględną przekazanego argumentu i zwraca ją.

**Parametry**

- `value` — liczba, której wartość bezwzględna zostanie zapisana.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARDTMP2^ABS(VARDTMP2);
DKIERUNEKY^ABS(DKIERUNEKY);
```

### ADD

```
DOUBLE ADD(DOUBLE addend)
```

Dodaje argument do bieżącej wartości zmiennej, zapisuje wynik i zwraca go.

**Parametry**

- `addend` — wartość dodawana.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARDMENUOPACITY^ADD([42.5*VARIMENUVISIBLE]);
VARDTIME^ADD(1.0);
STREX|DPOSX^ADD(STREX|FORCEX);
```

### ARCTAN

```
DOUBLE ARCTAN(DOUBLE value)
```

Zapisuje w zmiennej arcus tangens argumentu wyrażony w stopniach i zwraca tę wartość. Argument traktowany jest jako liczba (tangens kąta), a nie jako kąt.

**Parametry**

- `value` — liczba, dla której wyznaczany jest arcus tangens.

**Zwraca**: nową wartość zmiennej (w stopniach).

**Przykłady**

```
VARDTMP1^ARCTAN(VARDTMP1);
```

### ARCTANEX

```
DOUBLE ARCTANEX(DOUBLE y, DOUBLE x)
```

Zapisuje w zmiennej wartość funkcji `atan2(y, x)` wyrażoną w stopniach i zwraca tę wartość. Jest to kąt wektora `(x, y)` względem dodatniej osi `OX`.

**Parametry**

- `y` — pierwsza składowa wektora.
- `x` — druga składowa wektora.

**Zwraca**: nową wartość zmiennej (w stopniach).

**Przykłady**

```
VARDTEMP1^ARCTANEX(VARIDIRY, VARIDIRX);
VARDTEMP2^ARCTANEX(VREFLECT^GET(1), VREFLECT^GET(0));
```

### CLAMP

```
DOUBLE CLAMP(DOUBLE rangeMin, DOUBLE rangeMax)
```

Sprowadza bieżącą wartość zmiennej do przedziału `[rangeMin, rangeMax]`. Wartości spoza przedziału są przycinane do jego granic.

**Parametry**

- `rangeMin` — dolna granica przedziału (włącznie).
- `rangeMax` — górna granica przedziału (włącznie).

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
D3^CLAMP(0.5, 2.5);
VARDTMP1^CLAMP(-15.0, 15.0);
DKONSPEED^CLAMP(0.0, DKONSPEEDMAX);
```

### CLEAR

```
DOUBLE CLEAR()
```

Ustawia wartość zmiennej na `0.0` i zwraca tę wartość.

**Zwraca**: `0.0`.

### COSINUS

```
DOUBLE COSINUS(DOUBLE angle)
```

Zapisuje w zmiennej cosinus podanego kąta i zwraca tę wartość. Kąt podawany jest w stopniach.

**Parametry**

- `angle` — kąt w stopniach.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARDTEMP0^COSINUS(VARDANGLE);
VARDTEMP1^COSINUS(ARRANGLE^GET(VARPLAYER));
```

### DEC

```
DOUBLE DEC()
```

Zmniejsza wartość zmiennej o `1.0`.

**Zwraca**: nową wartość zmiennej.

### DIV

```
DOUBLE DIV(DOUBLE divisor)
```

Dzieli bieżącą wartość zmiennej przez argument, zapisuje wynik i zwraca go. Dzielenie przez zero nie zmienia wartości zmiennej.

**Parametry**

- `divisor` — dzielnik.

**Zwraca**: nową wartość zmiennej (lub niezmienioną wartość, jeśli `divisor` był równy `0.0`).

**Przykłady**

```
VARDTEMP0^DIV(ARRSPEEDFACTOR^GET(0));
DKONSPEED^DIV(6.0);
VARDTMP2^DIV(15);
```

### GET

```
DOUBLE GET()
```

Zwraca aktualną wartość zmiennej.

**Zwraca**: bieżąca wartość pola `VALUE`.

### INC

```
DOUBLE INC()
```

Zwiększa wartość zmiennej o `1.0`.

**Zwraca**: nową wartość zmiennej.

### LENGTH

```
DOUBLE LENGTH(DOUBLE x, DOUBLE y)
```

Wyznacza długość wektora `(x, y)` jako `sqrt(x² + y²)`, zapisuje wynik i zwraca go.

**Parametry**

- `x` — pierwsza składowa wektora.
- `y` — druga składowa wektora.

**Zwraca**: długość wektora.

**Przykłady**

```
VARDTEMP0^LENGTH(VARIDIRX, VARIDIRY);
```

### LOG

```
DOUBLE LOG(DOUBLE value)
```

Zapisuje w zmiennej logarytm naturalny argumentu i zwraca tę wartość.

**Parametry**

- `value` — liczba, której logarytm jest wyznaczany.

**Zwraca**: nową wartość zmiennej.

### MAXA

```
DOUBLE MAXA(DOUBLE value1, [DOUBLE value2, ..., DOUBLE valueN])
```

Wyznacza maksimum spośród podanych argumentów, zapisuje wynik i zwraca go. Wymaga co najmniej jednego argumentu.

**Parametry**

- `value1, …, valueN` — wartości, spośród których wybierane jest maksimum.

**Zwraca**: największą z podanych wartości.

**Przykłady**

```
VARDPOWER^MAXA(0.0, VARDPOWER);
```

### MINA

```
DOUBLE MINA(DOUBLE value1, [DOUBLE value2, ..., DOUBLE valueN])
```

Wyznacza minimum spośród podanych argumentów, zapisuje wynik i zwraca go. Wymaga co najmniej jednego argumentu.

**Parametry**

- `value1, …, valueN` — wartości, spośród których wybierane jest minimum.

**Zwraca**: najmniejszą z podanych wartości.

**Przykłady**

```
VARDPOWER^MINA(VARDPOWER, 9.0);
```

### MOD

```
DOUBLE MOD(DOUBLE divisor)
```

Wyznacza resztę z dzielenia bieżącej wartości zmiennej przez argument, obcina część ułamkową wyniku do liczby całkowitej, zapisuje i zwraca. Dzielenie przez zero nie zmienia wartości zmiennej.

**Parametry**

- `divisor` — dzielnik.

**Zwraca**: nową wartość zmiennej (lub niezmienioną wartość, jeśli `divisor` był równy `0.0`).

### MUL

```
DOUBLE MUL(DOUBLE multiplier)
```

Mnoży bieżącą wartość zmiennej przez argument, zapisuje wynik i zwraca go.

**Parametry**

- `multiplier` — mnożnik.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
STPLAYER|FORCEX^MUL(0.75);
VARCATFORCEX^MUL(1000000);
STREX|FORCEX^MUL(STREX|DEFIANCE);
```

### RESETINI

```
DOUBLE RESETINI()
```

Przywraca wartość zmiennej do wartości resetu zdefiniowanej w atrybutach obiektu w skrypcie. Silnik szuka wartości w kolejności: `DEFAULT` → `INIT_VALUE` → `VALUE`; używana jest pierwsza znaleziona. Jeśli żadna z nich nie jest ustawiona, wartość ustawiana jest na `0.0`.

**Zwraca**: nową wartość zmiennej.

### SET

```
DOUBLE SET(DOUBLE value)
```

Ustawia wartość zmiennej.

**Parametry**

- `value` — nowa wartość.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARDMAXVEL^SET(300.0);
VARDMAXVELKRET^SET([0.6*VARDMAXVEL]);
VARD_KRETSPEED^SET($1);
```

### SGN

```
INTEGER SGN()
```

Zwraca znak bieżącej wartości zmiennej: `-1` dla wartości ujemnych, `1` dla dodatnich, `0` dla zera. Metoda nie modyfikuje wartości zmiennej i jako jedyna w tym typie zwraca [`INTEGER`](INTEGER.md), nie `DOUBLE`.

**Zwraca**: znak wartości zmiennej (`-1`, `0` lub `1`).

### SINUS

```
DOUBLE SINUS(DOUBLE angle)
```

Zapisuje w zmiennej sinus podanego kąta i zwraca tę wartość. Kąt podawany jest w stopniach.

**Parametry**

- `angle` — kąt w stopniach.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARDTEMP1^SINUS(VARDANGLE);
VARDTEMP2^SINUS(ARRANGLE^GET(VARPLAYER));
```

### SQRT

```
DOUBLE SQRT()
DOUBLE SQRT(DOUBLE value)
```

Zapisuje w zmiennej pierwiastek kwadratowy i zwraca tę wartość.

- W wariancie bez argumentu pierwiastkowana jest bieżąca wartość zmiennej.
- W wariancie z argumentem pierwiastkowany jest argument.

**Parametry**

- `value` — (opcjonalnie) liczba, której pierwiastek jest wyznaczany.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARDODLEGLOSC^SQRT(VARDODLEGLOSC);
```

### SUB

```
DOUBLE SUB(DOUBLE subtrahend)
```

Odejmuje argument od bieżącej wartości zmiennej, zapisuje wynik i zwraca go.

**Parametry**

- `subtrahend` — wartość odejmowana.

**Zwraca**: nową wartość zmiennej.

**Przykłady**

```
VARDANGLE^SUB(VARDTEMP2);
DKONSPEED^SUB([DKONACCELERATION*D3]);
```

### SWITCH

```
DOUBLE SWITCH(DOUBLE valueA, DOUBLE valueB)
```

Jeżeli bieżąca wartość zmiennej jest równa `valueA`, zmiennej zostaje przypisana `valueB`; w przeciwnym razie — `valueA`. Pozwala to naprzemiennie przełączać między dwiema wartościami.

**Parametry**

- `valueA` — pierwsza wartość.
- `valueB` — druga wartość.

**Zwraca**: nową wartość zmiennej.

## Sygnały

### ONCHANGED

Wywoływany, gdy wartość zmiennej zostaje zmieniona na inną niż dotychczasowa.

### ONBRUTALCHANGED

Wywoływany przy każdym wywołaniu metody zmieniającej wartość, niezależnie od tego, czy nowa wartość różni się od poprzedniej.
