# CONDITION

Obiekt opisujący warunek porównania dwóch operandów. Konfigurowany trzema polami w skrypcie i wywoływany metodą [`CHECK`](#check) lub jedną z metod sterujących przepływem ([`BREAK`](#break), [`ONE_BREAK`](#one_break)).

## Pola

### OPERAND1

```
STRING OPERAND1
```

Lewy operand porównania. Pole zawiera tekstowy zapis operandu, który zostanie zinterpretowany przy każdej ewaluacji warunku. Dopuszczalne formy:

- literał tekstowy w cudzysłowach (`"..."` lub `'...'`),
- literał logiczny (`TRUE`, `FALSE`),
- literał liczbowy (`5`, `-3.14`),
- nazwa zmiennej (zostanie pobrana jej wartość; jeżeli zmienna jest typu [`EXPRESSION`](index.md), `CONDITION` lub [`COMPLEXCONDITION`](COMPLEXCONDITION.md), zostanie ewaluowana),
- wyrażenie skryptowe — fragment kodu rozpoczynający się od `[`, `*` lub zawierający operatory `^` albo `|`.

### OPERAND2

```
STRING OPERAND2
```

Prawy operand porównania. Reguły interpretacji są identyczne jak dla `OPERAND1`.

### OPERATOR

```
STRING OPERATOR
```

Operator porównania. Domyślnie `EQUAL`. Dopuszczalne wartości:

| Wartość | Znaczenie |
|---|---|
| `EQUAL` | równa się |
| `NOTEQUAL` | różne niż |
| `LESS` | mniejsze niż |
| `GREATER` | większe niż |
| `LESSEQUAL` | mniejsze lub równe |
| `GREATEREQUAL` | większe lub równe |

## Metody

### BREAK

```
void BREAK([BOOL emitSignals])
```

Ewaluuje warunek. Jeżeli wynik jest `TRUE`, przerywa całe bieżące drzewo wywołań (efekt analogiczny do [`@BREAK`](../engine/scripts.md#operatory-skoku)). Jeżeli wynik jest `FALSE`, metoda nie ma efektu.

**Parametry**

- `emitSignals` — (opcjonalnie) jeżeli `TRUE`, dodatkowo emitowany jest sygnał [`ONRUNTIMESUCCESS`](#onruntimesuccess) lub [`ONRUNTIMEFAILED`](#onruntimefailed) w zależności od wyniku. Domyślnie `FALSE`.

**Przykłady**

```
COND1^BREAK(TRUE);
CONDKONTROLA^BREAK(TRUE);
```

### CHECK

```
BOOL CHECK([BOOL emitSignals])
```

Ewaluuje warunek i zwraca wynik porównania.

**Parametry**

- `emitSignals` — (opcjonalnie) jeżeli `TRUE`, dodatkowo emitowany jest sygnał [`ONRUNTIMESUCCESS`](#onruntimesuccess) lub [`ONRUNTIMEFAILED`](#onruntimefailed) w zależności od wyniku. Domyślnie `FALSE`.

**Zwraca**: [`BOOL`](BOOL.md) — wynik porównania.

**Przykłady**

```
CONPR1^CHECK(TRUE);
CONPR2^CHECK(TRUE);
```

### ONE_BREAK

```
void ONE_BREAK([BOOL emitSignals])
```

Ewaluuje warunek. Jeżeli wynik jest `TRUE`, przerywa wyłącznie bieżącą procedurę (efekt analogiczny do [`@ONEBREAK`](../engine/scripts.md#operatory-skoku)). Jeżeli wynik jest `FALSE`, metoda nie ma efektu.

**Parametry**

- `emitSignals` — (opcjonalnie) jak w [`BREAK`](#break).

**Przykłady**

```
COND1^ONE_BREAK(TRUE);
CONDREMOVEMENUITEM^ONE_BREAK(TRUE);
```

## Sygnały

### ONRUNTIMESUCCESS

Wywoływany, gdy ewaluacja warunku zwróciła `TRUE` i `emitSignals` było ustawione na `TRUE`.

### ONRUNTIMEFAILED

Wywoływany, gdy ewaluacja warunku zwróciła `FALSE` i `emitSignals` było ustawione na `TRUE`.
