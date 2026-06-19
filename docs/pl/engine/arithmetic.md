# Arytmetyka

Silnik Piklib/BlooMoo wykonuje obliczenia wyłącznie wewnątrz **wyrażeń arytmetycznych** zamkniętych w nawiasach kwadratowych. Nawiasy okrągłe są zarezerwowane dla list argumentów wywołań metod i nie pełnią roli grupującej w wyrażeniach. Sekcja poniżej opisuje obsługiwane operatory, reguły typowania oraz konwersje pomiędzy typami prymitywnymi.

## Składnia {#skladnia}

Wyrażenie arytmetyczne zapisuje się w nawiasach kwadratowych. Może być użyte wszędzie, gdzie spodziewana jest wartość — w tym jako argument metody lub jako wartość przypisywana do pola:

```
NAZWA_ZMIENNEJ^SET([VAL1+VAL2]);
*["ANIMO_"+_I_]^PLAY();
```

Zagnieżdżanie wyrażeń odbywa się przez kolejne pary nawiasów kwadratowych:

```
[[VAL1+VAL2]*VAL3]
```

## Reguła typowania {#regula-typowania}

Wszystkie operacje binarne w wyrażeniach kierują się jedną zasadą:

> **Typ wyniku oraz typ prawego operandu są wyznaczone przez typ lewego operandu.**

Prawy operand jest rzutowany na typ lewego przed wykonaniem operacji, a wynik również ma ten typ. Przykłady:

```
"Wartosc" + 2.5  →  "Wartosc2.50000"   # DOUBLE rzutowany do STRING
2 + "3"          →  5                   # STRING rzutowany do INTEGER
```

Konsekwencja: kolejność operandów ma znaczenie nie tylko dla operatorów nieprzemiennych, ale również dla samego typu wyniku.

## Konwersje typów

W ramach reguły typowania prawy operand jest rzutowany według poniższych zasad.

### Z `STRING`

| Cel | Reguła |
|---|---|
| [`INTEGER`](../reference/INTEGER.md) | Z początkowej części tekstu wyciągana jest liczba całkowita (analogicznie do `parseInt`). Jeżeli tekst nie zaczyna się od liczby, wynikiem jest `0`. |
| [`DOUBLE`](../reference/DOUBLE.md) | Analogicznie do `INTEGER`, ale z zachowaniem części ułamkowej. Separatorem dziesiętnym jest kropka. |
| [`BOOL`](../reference/BOOL.md) | Tekst odpowiadający wartości prawdziwej (`TRUE` lub niezerowa liczba) zwraca `TRUE`; w pozostałych przypadkach `FALSE`. |

```
"5"     →  5
"Test"  →  0
```

### Z `INTEGER`

| Cel | Reguła |
|---|---|
| [`STRING`](../reference/STRING.md) | Zapis dziesiętny liczby. |
| [`DOUBLE`](../reference/DOUBLE.md) | Liczba z zerową częścią ułamkową (pięć zer po przecinku). |
| [`BOOL`](../reference/BOOL.md) | Wartość różna od `0` daje `TRUE`, równa `0` — `FALSE`. |

```
5   →  "5"
3   →  3.00000
-2  →  TRUE
0   →  FALSE
```

### Z `DOUBLE`

| Cel | Reguła |
|---|---|
| [`STRING`](../reference/STRING.md) | Zapis dziesiętny z kropką i pięcioma miejscami po przecinku. Dla wartości równych `0.0` część po przecinku jest pomijana. |
| [`INTEGER`](../reference/INTEGER.md) | Zaokrąglenie do najbliższej liczby całkowitej; przy `.5` w górę dla liczb dodatnich i w dół dla ujemnych. |
| [`BOOL`](../reference/BOOL.md) | Pośrednie: najpierw rzutowanie do `INTEGER` (z powyższym zaokrągleniem), potem do `BOOL`. Wartości z przedziału `(-0.5, 0.5)` dają `FALSE`, pozostałe `TRUE`. |

```
3.5      →  "3.50000",   4,    TRUE
0.0      →  "0",         0,    FALSE
0.45362  →  "0.45362",   0,    FALSE
1.00001  →  "1.00001",   1,    TRUE
-0.5     →  "-0.50000", -1,    TRUE
```

### Z `BOOL`

| Cel | Reguła |
|---|---|
| [`STRING`](../reference/STRING.md) | `TRUE` → `"TRUE"`, `FALSE` → `"FALSE"`. |
| [`INTEGER`](../reference/INTEGER.md) | `TRUE` → `1`, `FALSE` → `0`. |
| [`DOUBLE`](../reference/DOUBLE.md) | `TRUE` → `1.00000`, `FALSE` → `0.00000`. |

## Operatory arytmetyczne

W wyrażeniach dostępne są następujące operatory binarne:

| Operator | Znaczenie |
|---|---|
| `+` | dodawanie / konkatenacja |
| `-` | odejmowanie |
| `*` | mnożenie |
| `@` | dzielenie |
| `%` | reszta z dzielenia |

### Dodawanie (`+`)

| Typ lewego operandu | Zachowanie |
|---|---|
| [`STRING`](../reference/STRING.md) | Konkatenacja prawego operandu (po rzutowaniu) do lewego. |
| [`INTEGER`](../reference/INTEGER.md) | Suma liczbowa. |
| [`DOUBLE`](../reference/DOUBLE.md) | Suma liczbowa. |
| [`BOOL`](../reference/BOOL.md) | Koniunkcja logiczna (`AND`). `TRUE + FALSE` daje `FALSE`. |

### Odejmowanie (`-`)

| Typ lewego operandu | Zachowanie |
|---|---|
| [`STRING`](../reference/STRING.md) | Brak efektu; wynikiem jest lewy operand. |
| [`INTEGER`](../reference/INTEGER.md) | Różnica liczbowa. |
| [`DOUBLE`](../reference/DOUBLE.md) | Różnica liczbowa. |
| [`BOOL`](../reference/BOOL.md) | Brak efektu; wynikiem jest lewy operand. |

### Mnożenie (`*`)

| Typ lewego operandu | Zachowanie |
|---|---|
| [`STRING`](../reference/STRING.md) | Brak efektu; wynikiem jest lewy operand. |
| [`INTEGER`](../reference/INTEGER.md) | Iloczyn liczbowy. |
| [`DOUBLE`](../reference/DOUBLE.md) | Iloczyn liczbowy. |
| [`BOOL`](../reference/BOOL.md) | Alternatywa logiczna (`OR`). `FALSE * TRUE` daje `TRUE`. |

### Dzielenie (`@`)

| Typ lewego operandu | Zachowanie |
|---|---|
| [`STRING`](../reference/STRING.md) | Brak efektu; wynikiem jest lewy operand. |
| [`INTEGER`](../reference/INTEGER.md) | Iloraz całkowity. |
| [`DOUBLE`](../reference/DOUBLE.md) | Iloraz zmiennoprzecinkowy. |
| [`BOOL`](../reference/BOOL.md) | Brak efektu; wynikiem jest lewy operand. |

Dzielenie przez `0` w typach liczbowych powoduje błąd silnika.

### Reszta z dzielenia (`%`)

| Typ lewego operandu | Zachowanie |
|---|---|
| [`STRING`](../reference/STRING.md) | Brak efektu; wynikiem jest lewy operand. |
| [`INTEGER`](../reference/INTEGER.md) | Reszta z dzielenia. |
| [`DOUBLE`](../reference/DOUBLE.md) | Reszta z dzielenia obcięta do liczby całkowitej, a następnie rzutowana z powrotem do `DOUBLE`. Skutkuje to utratą części ułamkowej, np. `1.5 % 2` daje `1.00000`, nie `1.50000`. |
| [`BOOL`](../reference/BOOL.md) | Brak efektu; wynikiem jest lewy operand. |

Modulo z drugim operandem równym `0` powoduje błąd silnika.

## Operatory porównań

W wyrażeniach dostępne są standardowe operatory porównań: `==`, `!=`, `<`, `<=`, `>`, `>=`. Prawy operand jest najpierw rzutowany na typ lewego (zgodnie z [regułą typowania](#regula-typowania)), a następnie porównywany.

### Równość i nierówność

`==` zwraca `TRUE`, jeżeli oba operandy (po rzutowaniu) są równe; `!=` zwraca przeciwieństwo. Dla typu [`STRING`](../reference/STRING.md) porównanie odbywa się znak po znaku, dla typów liczbowych — wartościami.

### Porównania mniejsze / większe

| Typ lewego operandu | `<` zwraca `TRUE`, jeżeli |
|---|---|
| [`STRING`](../reference/STRING.md) | Leksykograficznie lewy operand poprzedza prawy (porównanie znak po znaku w kodowaniu CP1250). |
| [`INTEGER`](../reference/INTEGER.md) | Wartość liczbowa lewego operandu jest mniejsza. |
| [`DOUBLE`](../reference/DOUBLE.md) | Wartość liczbowa lewego operandu jest mniejsza. |
| [`BOOL`](../reference/BOOL.md) | Lewy operand to `FALSE`, a prawy `TRUE` (`FALSE < TRUE`). |

`>` zwraca `TRUE` w sytuacjach przeciwnych. Operatory `<=` oraz `>=` są równoważne odpowiednio `<` lub `==` oraz `>` lub `==`.

## Operatory logiczne

Operatory `&&` (koniunkcja) i `||` (alternatywa) przyjmują wyłącznie operandy typu [`BOOL`](../reference/BOOL.md). Podanie operandu innego typu (nawet jeżeli mógłby zostać rzutowany) powoduje błąd silnika.

| Operator | Wynik |
|---|---|
| `&&` | `TRUE` tylko jeżeli oba operandy są `TRUE`. |
| `||` | `TRUE` jeżeli przynajmniej jeden z operandów jest `TRUE`. |

W [warunku złożonym instrukcji `@IF`](scripts.md#warunek-zlozony) operatory `&&` i `||` zachowują się tak samo, ale zapisywane są jako część ciągu warunku, nie jako operatory wyrażenia arytmetycznego.
