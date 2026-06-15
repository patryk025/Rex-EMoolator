# Dziwactwa silnika

Silnik Piklib/BlooMoo zawiera szereg niestandardowych zachowań, które mogą zaskoczyć programistę przyzwyczajonego do popularnych języków skryptowych. Niniejszy rozdział zbiera najczęstsze z nich w jednym miejscu.

## Wyrażenia i operatory

### Tylko nawiasy kwadratowe grupują obliczenia

W wyrażeniach arytmetycznych grupowanie wykonuje się wyłącznie za pomocą `[ ]`. Nawiasy okrągłe `( )` są zarezerwowane dla list argumentów wywołań metod i nie pełnią funkcji grupującej (zobacz [Arytmetyka](arithmetic.md#skladnia)).

### `@` zamiast `/` dla dzielenia

Operator dzielenia to znak małpki `@`, nie `/`. Próba użycia `/` w wyrażeniu nie zostanie zinterpretowana jako dzielenie.

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

### `&&` i `||` przyjmują wyłącznie `BOOL`

Operatory logiczne nie wykonują niejawnego rzutowania nawet wtedy, gdy operand byłby konwertowalny do `BOOL`. Próba użycia wartości innego typu jako operandu kończy się błędem silnika.

### Dzielenie przez `0` w wyrażeniach kończy się błędem

W odróżnieniu od metod typów liczbowych (gdzie `DIV` i `MOD` zwracają wartość bez zmian), dzielenie operatorem `@` lub `%` przez `0` w wyrażeniu arytmetycznym powoduje wyjście do pulpitu.

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

### `DOUBLE → INTEGER` zaokrągla, a nie obcina

Przy rzutowaniu [`DOUBLE`](../reference/DOUBLE.md) na [`INTEGER`](../reference/INTEGER.md) liczba jest zaokrąglana do najbliższej liczby całkowitej, a nie obcinana. Dla wartości dodatnich `.5` zaokrąglane jest w górę, dla ujemnych — w dół, więc `-0.5 → -1`, a `0.5 → 1`.

### `STRING → INTEGER` zwraca `0` zamiast błędu

Konwersja tekstu nie będącego liczbą do [`INTEGER`](../reference/INTEGER.md) (lub [`DOUBLE`](../reference/DOUBLE.md)) zwraca `0` (`0.00000`), a nie generuje błędu. Może to maskować błędy w wyrażeniach łączących literały tekstowe z liczbowymi.

## Metody typów

### `DOUBLE.MOD` obcina część ułamkową

Analogicznie do operatora `%`, metoda [`MOD`](../reference/DOUBLE.md#mod) typu `DOUBLE` zwraca część całkowitą reszty z dzielenia.

### `DOUBLE.SGN` zwraca `INTEGER`, nie `DOUBLE`

Metoda [`SGN`](../reference/DOUBLE.md#sgn) jest jedyną metodą typu `DOUBLE`, która nie zmienia wartości zmiennej i jako wartość zwraca [`INTEGER`](../reference/INTEGER.md).

### `INTEGER.RANDOM(min, max)` jest obustronnie włączne

Wariant dwuargumentowy metody [`RANDOM`](../reference/INTEGER.md#random) zwraca wartość z przedziału `[min, max]`, włącznie z oboma końcami. Wariant jednoargumentowy zwraca `[0, bound)`.

### `INTEGER.DIV` i `INTEGER.MOD` z `0` nie modyfikują zmiennej

W odróżnieniu od dzielenia operatorem `@`/`%` w wyrażeniu, metody [`DIV`](../reference/INTEGER.md#div) i [`MOD`](../reference/INTEGER.md#mod) na typie `INTEGER` z dzielnikiem `0` zwracają niezmienioną wartość bieżącą zamiast generować błąd. Analogicznie zachowują się metody na typie `DOUBLE`.

### `STRING.COPYFILE` ignoruje obiekt-odbiorcę

Metoda [`COPYFILE`](../reference/STRING.md#copyfile) na typie [`STRING`](../reference/STRING.md) nie korzysta z wartości zmiennej, na której została wywołana — operuje wyłącznie na dwóch argumentach reprezentujących ścieżki źródłową i docelową.

### `STRING.CHANGEAT` zastępuje dokładnie jeden znak

Niezależnie od długości ciągu w drugim argumencie, [`CHANGEAT`](../reference/STRING.md#changeat) usuwa jeden znak z bieżącej wartości na podanej pozycji i wstawia w jego miejsce cały ciąg argumentu. Długość ciągu po operacji może być inna niż przed.

## `THIS` w obsłudze sygnałów

### `THIS` zwraca `"temp"` dla `GETNAME`

Mimo że `THIS` jest referencją do obiektu, który wyemitował sygnał, próba pobrania jego nazwy metodą `GETNAME` zwraca ciąg `"temp"`, co wskazuje na wewnętrzną reprezentację jako obiekt tymczasowy.

### Nie wszystkie metody typu działają na `THIS`

Bezpiecznie działają `GET`/`SET` (dla typów prymitywnych) oraz `SHOW`/`HIDE`/`PLAY`/`PAUSE`/`STOP`/`RESUME` (dla obiektów graficznych). Wywołanie metod specyficznych dla typu obiektu (np. [`GETCFRAMEINEVENT`](../reference/index.md) na `ANIMO`) zazwyczaj kończy się błędem silnika. Szczegóły opisano w sekcji [Zmienna THIS](scripts.md#zmienna-this).
