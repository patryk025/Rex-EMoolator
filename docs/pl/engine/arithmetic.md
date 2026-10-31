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

## Kolejność działań {#kolejnosc-dzialan}

Operatory nie mają priorytetów: wyrażenie liczone jest ściśle od lewej do prawej, a zmienić to można wyłącznie nawiasami kwadratowymi.

```
[7+3*2]    →  20   # (7+3)*2
[7+[3*2]]  →  13
```

!!! warning "Piklib 6.1 i 7.1"
    Te silniki obsługują w wyrażeniu tylko jeden operator: tekst dzielony jest na pierwszym znaku operatora, a prawa strona traktowana jest jako jeden operand. Dlatego `[7+3*2]` daje `10`, a `[1+2+3]` daje `3`. Nawias po prawej stronie działa (`[7+[3*2]]` to `13`), a nawias po lewej (`[[7+3]*2]`) wywala silnik.

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
| [`STRING`](../reference/STRING.md) | Zapis dziesiętny z kropką i pięcioma miejscami po przecinku; szczegóły zależą od silnika (tabela niżej). |
| [`INTEGER`](../reference/INTEGER.md) | BlooMoo od „Reksia i Kapitana Nemo”: zaokrąglenie do najbliższej liczby całkowitej; przy `.5` w górę dla liczb dodatnich i w dół dla ujemnych. Piklib oraz BlooMoo z „Reksia i Wehikułu Czasu”: obcięcie części ułamkowej ([szczegóły](quirks.md#double-integer-zalezy-od-wersji-silnika)). |
| [`BOOL`](../reference/BOOL.md) | Pośrednie: najpierw rzutowanie do `INTEGER` (z powyższym zaokrągleniem), potem do `BOOL`. Wartości z przedziału `(-0.5, 0.5)` dają `FALSE`, pozostałe `TRUE`. |

```
3.5      →  "3.50000",   4,    TRUE
0.0      →  "0",         0,    FALSE
0.45362  →  "0.45362",   0,    FALSE
1.00001  →  "1.00001",   1,    TRUE
-0.5     →  "-0.50000", -1,    TRUE
```

Przykłady dotyczą BlooMoo od „Reksia i Kapitana Nemo”. Zapis tekstowy wygląda inaczej w starszych silnikach:

| Silnik | `3.25` | `0.0` | `-0.5` |
|---|---|---|---|
| BlooMoo od „Reksia i Kapitana Nemo” | `3.25000` | `0` | `-0.50000` |
| Piklib 7.2 i 8, BlooMoo z „Reksia i Wehikułu Czasu” | `3.25000` | `00000` | `0.-50000` |
| Piklib 7.1 | `3.25` | `00` | `-50` |

W Piklibie 6.1 każda wartość `DOUBLE` odczytana z tekstu to `0`, a zapis liczby to surowe cyfry bez kropki (`2` → `200`).

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

Dzielenie przez `0` zależy od typu lewego operandu: `INTEGER` wywala silnik, `DOUBLE` daje nieskończoność (`1.#INF0`), a `STRING` i `BOOL` zostają bez zmian. Zerem może być też dzielnik po rzutowaniu, np. tekst niebędący liczbą.

### Reszta z dzielenia (`%`)

| Typ lewego operandu | Zachowanie |
|---|---|
| [`STRING`](../reference/STRING.md) | Brak efektu; wynikiem jest lewy operand. |
| [`INTEGER`](../reference/INTEGER.md) | Reszta z dzielenia. |
| [`DOUBLE`](../reference/DOUBLE.md) | Reszta z dzielenia obcięta do liczby całkowitej, a następnie rzutowana z powrotem do `DOUBLE`. Skutkuje to utratą części ułamkowej, np. `1.5 % 2` daje `1.00000`, nie `1.50000`. |
| [`BOOL`](../reference/BOOL.md) | Brak efektu; wynikiem jest lewy operand. |

Reszta z dzielenia przez `0` wywala silnik dla `INTEGER` i `DOUBLE`, a `STRING` i `BOOL` zostają bez zmian.

!!! bug "Rozbieżność w emulatorze"
    Crosstesty wykryły różnicę do sprawdzenia: w emulatorze `@` i `%` przez `0` zawsze dają tekst `NULL`. Zobacz [Rozbieżności z oryginałem](../emulator/divergences.md).

## Operatory porównań

Wyrażenia w `[ ]` znają tylko pięć operatorów arytmetycznych. Porównania są częścią warunków: [`@IF`](scripts.md#instrukcja-warunkowa), [`@WHILE`](scripts.md#while) oraz obiektów [`CONDITION`](../reference/CONDITION.md) i [`COMPLEXCONDITION`](../reference/COMPLEXCONDITION.md), a ich zapis opisuje rozdział [Skrypty](scripts.md#instrukcja-warunkowa).

Prawy operand jest najpierw rzutowany na typ lewego (zgodnie z [regułą typowania](#regula-typowania)), a następnie porównywany.

### Równość i nierówność

Równość zachodzi, jeżeli oba operandy (po rzutowaniu) są równe, a nierówność jest jej przeciwieństwem. Dla typu [`STRING`](../reference/STRING.md) porównanie odbywa się znak po znaku, dla typów liczbowych — wartościami.

### Porównania mniejsze / większe

| Typ lewego operandu | `<` zwraca `TRUE`, jeżeli |
|---|---|
| [`STRING`](../reference/STRING.md) | Leksykograficznie lewy operand poprzedza prawy (porównanie znak po znaku w kodowaniu CP1250). |
| [`INTEGER`](../reference/INTEGER.md) | Wartość liczbowa lewego operandu jest mniejsza. |
| [`DOUBLE`](../reference/DOUBLE.md) | Wartość liczbowa lewego operandu jest mniejsza. |
| [`BOOL`](../reference/BOOL.md) | Lewy operand to `FALSE`, a prawy `TRUE` (`FALSE < TRUE`). |

`>` zwraca `TRUE` w sytuacjach przeciwnych. „Mniejsze lub równe” oraz „większe lub równe” są równoważne odpowiednio `<` lub równości oraz `>` lub równości.

## Operatory logiczne

Operatory `&&` (koniunkcja) i `||` (alternatywa) nie należą do wyrażeń arytmetycznych. Występują tylko w [warunku złożonym instrukcji `@IF`](scripts.md#warunek-zlozony), jako część ciągu warunku.

| Operator | Wynik |
|---|---|
| `&&` | `TRUE` tylko jeżeli oba człony są prawdziwe. |
| `||` | `TRUE` jeżeli przynajmniej jeden z członów jest prawdziwy. |
