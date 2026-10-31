# Dziwactwa silnika

Silnik Piklib/BlooMoo zawiera szereg niestandardowych zachowań, które mogą zaskoczyć programistę przyzwyczajonego do popularnych języków skryptowych. Niniejszy rozdział zbiera najczęstsze z nich w jednym miejscu.

## Wyrażenia i operatory

### Tylko nawiasy kwadratowe grupują obliczenia

W wyrażeniach arytmetycznych grupowanie wykonuje się wyłącznie za pomocą `[ ]`. Nawiasy okrągłe `( )` są zarezerwowane dla list argumentów wywołań metod i nie pełnią funkcji grupującej (zobacz [Arytmetyka](arithmetic.md#skladnia)).

### `@` zamiast `/` dla dzielenia

Operator dzielenia to znak małpki `@`, nie `/`. Próba użycia `/` w wyrażeniu nie zostanie zinterpretowana jako dzielenie.

### Działania liczone są od lewej do prawej

Operatory nie mają priorytetów, więc `[7+3*2]` daje `20`, a nie `13`. Piklib 6.1 i 7.1 są jeszcze bardziej ograniczone: przyjmują w wyrażeniu [tylko jeden operator](arithmetic.md#kolejnosc-dzialan).

### Typ wyniku zależy od typu lewego operandu

Wszystkie operacje binarne rzutują prawy operand na typ lewego. Wynik również ma typ lewego operandu. Skutki kolejności są nietrywialne (zobacz [Reguła typowania](arithmetic.md#regula-typowania)):

```
"Wartosc" + 2.5  →  "Wartosc2.50000"
2 + "3"          →  5
```

### Operatory na `BOOL` mają odwróconą logikę

Operator `+` na [`BOOL`](../reference/BOOL.md) działa jak koniunkcja logiczna (`AND`), a `*` jak alternatywa (`OR`). Operatory `-`, `@` i `%` na `BOOL` nie mają efektu (zobacz [Operatory arytmetyczne](arithmetic.md#operatory-arytmetyczne)).

### `%` na `DOUBLE` traci część ułamkową

Reszta z dzielenia w typie [`DOUBLE`](../reference/DOUBLE.md) jest najpierw obliczana, a następnie obcinana do liczby całkowitej i ponownie rzutowana na `DOUBLE`. W efekcie `1.5 % 2` daje `1.00000`, a nie `1.50000`.

### `&&` i `||` działają tylko w warunkach

Wyrażenia `[ ]` nie mają operatorów logicznych ani porównań. `&&` i `||` występują wyłącznie w [warunku złożonym `@IF`](scripts.md#warunek-zlozony), gdzie `&&` wiąże mocniej niż `||`, a obliczanie jest skracane.

### Dzielenie przez `0` nie zawsze kończy się błędem

Dla `INTEGER` dzielenie przez `0` wywala silnik, tak samo jak `%` na `DOUBLE`. Natomiast `@` na `DOUBLE` daje nieskończoność (`1.#INF0`), a na `STRING` i `BOOL` nie ma efektu.

Podobnie działają metody: [`INTEGER^DIV(0)`](../reference/INTEGER.md#div) i [`INTEGER^MOD(0)`](../reference/INTEGER.md#mod) wywalają silnik, a [`DOUBLE^DIV(0)`](../reference/DOUBLE.md#div) zapisuje `1.#INF0`.

!!! bug "Rozbieżność w emulatorze"
    Crosstesty wykryły różnicę do sprawdzenia: w emulatorze `DIV` i `MOD` zostawiają wartość bez zmian, a operatory dają tekst `NULL`. Zobacz [Rozbieżności z oryginałem](../emulator/divergences.md).

## Składnia skryptów

### Bloki kodu muszą być w jednej linii

Cały blok kodu pomiędzy `{` a `}` musi się zmieścić w jednej linii pliku skryptu. Wieloliniowe bloki nie są obsługiwane.

### Każda instrukcja musi kończyć się średnikiem

Również ostatnia instrukcja w bloku kodu. Pominięcie końcowego średnika może spowodować, że instrukcja nie zostanie wykonana.

### Komentarze: `#` dla linii, `!` dla instrukcji

Linia rozpoczynająca się od `#` jest pomijana w całości. Pojedyncza instrukcja poprzedzona znakiem `!` jest wykomentowana aż do najbliższego średnika.

### Komparator równości zmienia się w warunkach złożonych

W [warunku prostym `@IF`](scripts.md#warunek-prosty) równość zapisuje się jako `_`. W [warunku złożonym `@IF`](scripts.md#warunek-zlozony) ten sam komparator zapisuje się jako apostrof `'`. Pozostałe komparatory (`<`, `>`, `<_`/`<'`, `>_`/`>'`) zachowują się analogicznie.

### Tekst bez cudzysłowów jest najpierw szukany jako zmienna

Wartość zapisana bez cudzysłowów najpierw jest interpretowana jako nazwa zmiennej. Jeśli zmienna o takiej nazwie istnieje, użyta zostanie jej wartość; w przeciwnym razie tekst zostanie potraktowany jako literał typu [`STRING`](../reference/STRING.md). Może to prowadzić do trudnych do wykrycia kolizji, gdy literał przypadkowo zbieżny jest z nazwą zmiennej.

### Wielokrotna deklaracja obiektu scala właściwości

Powtórzenie linii `OBJECT=NAZWA` w tym samym pliku nie nadpisuje wcześniejszej definicji — silnik dołącza nowe właściwości do istniejącej i nadpisuje wpisy o tych samych kluczach.

### `Application.def` powinien kończyć się pustą linią

Brak pustej linii na końcu pliku `Application.def` może spowodować, że ostatnia scena nie zostanie poprawnie zinterpretowana, co objawia się czarnym ekranem.

## Liczby

### `DOUBLE` akceptuje literę `d` jako znak wykładnika

W notacji wykładniczej zarówno `e`, jak i `d` są rozpoznawane jako separator wykładnika: `1.23e4` i `1.23d4` są równoważne.

### `DOUBLE → INTEGER` zależy od wersji silnika {#double-integer-zalezy-od-wersji-silnika}

Rzutowanie [`DOUBLE`](../reference/DOUBLE.md) na [`INTEGER`](../reference/INTEGER.md) działa inaczej w starszych i nowszych silnikach:

| Silnik | Zachowanie | `0.5` | `-0.5` | `2.5` | `7.99` |
|---|---|---|---|---|---|
| Piklib (wszystkie wersje) oraz BlooMoo z „Reksia i Wehikułu Czasu” | obcięcie części ułamkowej | `0` | `0` | `2` | `7` |
| BlooMoo od „Reksia i Kapitana Nemo” | zaokrąglenie; `.5` oddala od zera | `1` | `-1` | `3` | `8` |

Nowsze BlooMoo dostało osobną funkcję zaokrąglającą; wcześniej konwersja była zwykłym rzutowaniem. Dotyczy to każdego miejsca, w którym silnik zamienia `DOUBLE` na `INTEGER`: argumentów metod, prawego operandu w wyrażeniach i [`SET`](../reference/INTEGER.md#set).

Skrypty starszych gier na tym polegają — np. indeks kierunku postaci liczony jako `(kąt + 22.5) / 45` ma sens tylko przy obcinaniu.

### `STRING → INTEGER` zwraca `0` zamiast błędu

Konwersja tekstu nie będącego liczbą do [`INTEGER`](../reference/INTEGER.md) (lub [`DOUBLE`](../reference/DOUBLE.md)) zwraca `0` (`0.00000`), a nie generuje błędu. Może to maskować błędy w wyrażeniach łączących literały tekstowe z liczbowymi.

## Metody typów

### `DOUBLE.MOD` obcina część ułamkową

Analogicznie do operatora `%`, metoda [`MOD`](../reference/DOUBLE.md#mod) typu `DOUBLE` zwraca część całkowitą reszty z dzielenia.

### `DOUBLE.SGN` zwraca `INTEGER`, nie `DOUBLE`

Metoda [`SGN`](../reference/DOUBLE.md#sgn) jest jedyną metodą typu `DOUBLE`, która nie zmienia wartości zmiennej i jako wartość zwraca [`INTEGER`](../reference/INTEGER.md).

### `INTEGER.RANDOM(min, max)` jest obustronnie włączne

Wariant dwuargumentowy metody [`RANDOM`](../reference/INTEGER.md#random) zwraca wartość z przedziału `[min, max]`, włącznie z oboma końcami. Wariant jednoargumentowy zwraca `[0, bound)`.

### Nie każda metoda zwraca nową wartość

Od Pikliba 7.1 `ADD` zwraca wynik, ale `SUB`, `MUL`, `DIV` i `SET` nie zwracają nic. Dokładną listę podają strony [`INTEGER`](../reference/INTEGER.md), [`DOUBLE`](../reference/DOUBLE.md) i [`STRING`](../reference/STRING.md).

### `STRING.COPYFILE` ignoruje obiekt-odbiorcę

Metoda [`COPYFILE`](../reference/STRING.md#copyfile) na typie [`STRING`](../reference/STRING.md) nie korzysta z wartości zmiennej, na której została wywołana — operuje wyłącznie na dwóch argumentach reprezentujących ścieżki źródłową i docelową.

### `STRING.CHANGEAT` zastępuje dokładnie jeden znak

Niezależnie od długości ciągu w drugim argumencie, [`CHANGEAT`](../reference/STRING.md#changeat) usuwa jeden znak z bieżącej wartości na podanej pozycji i wstawia w jego miejsce cały ciąg argumentu. Długość ciągu po operacji może być inna niż przed.

## `THIS` w obsłudze sygnałów

### `THIS` zwraca `"temp"` dla `GETNAME`

Mimo że `THIS` jest referencją do obiektu, który wyemitował sygnał, próba pobrania jego nazwy metodą `GETNAME` zwraca ciąg `"temp"`, co wskazuje na wewnętrzną reprezentację jako obiekt tymczasowy.

### Nie wszystkie metody typu działają na `THIS`

Bezpiecznie działają `GET`/`SET` (dla typów prymitywnych) oraz `SHOW`/`HIDE`/`PLAY`/`PAUSE`/`STOP`/`RESUME` (dla obiektów graficznych). Wywołanie metod specyficznych dla typu obiektu (np. [`GETCFRAMEINEVENT`](../reference/ANIMO.md#getcframeinevent) na `ANIMO`) zazwyczaj kończy się błędem silnika. Szczegóły opisano w sekcji [Zmienna THIS](scripts.md#zmienna-this).
