# EXPRESSION

Wyrażenie arytmetyczne dwuargumentowe. Zmienna pełni rolę nazwanej formuły — odczyt jej wartości każdorazowo oblicza `OPERAND1 OPERATOR OPERAND2` w bieżącym kontekście, więc wynik aktualizuje się automatycznie, gdy zmieniają się zmienne wejściowe.

Operandy mogą być literałami liczbowymi, nazwami zmiennych lub wyrażeniami w nawiasach kwadratowych (zobacz [Arytmetyka](../engine/arithmetic.md)). Sam typ `EXPRESSION` nie udostępnia żadnych metod skryptowych.

## Pola

### OPERAND1

```
STRING OPERAND1
```

Lewy operand wyrażenia.

### OPERAND2

```
STRING OPERAND2
```

Prawy operand wyrażenia.

### OPERATOR

```
STRING OPERATOR
```

Operator binarny stosowany do operandów. Akceptowane wartości:

| Wartość | Operacja |
| --- | --- |
| `ADD` | dodawanie |
| `SUB` | odejmowanie |
| `MUL` | mnożenie |
| `DIV` | dzielenie |
| `MOD` | reszta z dzielenia |

Reguły typu wyniku (liczba całkowita vs zmiennoprzecinkowa) są takie same jak dla zwykłej arytmetyki w skryptach — zobacz [Arytmetyka — reguła typowania](../engine/arithmetic.md#regula-typowania).

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
