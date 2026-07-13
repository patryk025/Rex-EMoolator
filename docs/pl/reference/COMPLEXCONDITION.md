# COMPLEXCONDITION

Obiekt łączący dwa warunki ([`CONDITION`](CONDITION.md) lub zagnieżdżone `COMPLEXCONDITION`) operatorem logicznym `AND` lub `OR`. Konfigurowany trzema polami w skrypcie i wywoływany analogicznie jak `CONDITION`.

## Pola

### CONDITION1

```
STRING CONDITION1
```

Nazwa zmiennej z lewym warunkiem składowym. Wartością powinna być zmienna typu [`CONDITION`](CONDITION.md) albo `COMPLEXCONDITION` — w obu przypadkach warunek zostanie zewaluowany rekurencyjnie.

### CONDITION2

```
STRING CONDITION2
```

Nazwa zmiennej z prawym warunkiem składowym; reguły identyczne jak dla `CONDITION1`.

### OPERATOR

```
STRING OPERATOR
```

Operator logiczny łączący warunki. Domyślnie `AND`. Dopuszczalne wartości:

| Wartość | Znaczenie |
|---|---|
| `AND` | koniunkcja — całość prawdziwa, gdy oba warunki są prawdziwe |
| `OR` | alternatywa — całość prawdziwa, gdy przynajmniej jeden warunek jest prawdziwy |

## Metody

### BREAK

```
void BREAK([BOOL emitSignals])
```

Ewaluuje warunek złożony. Jeżeli wynik jest `TRUE`, przerywa całe bieżące drzewo wywołań (efekt analogiczny do [`@BREAK`](../engine/scripts.md#operatory-skoku)).

**Parametry**

- `emitSignals` — (opcjonalnie) jeżeli `TRUE`, sygnały [`ONRUNTIMESUCCESS`](#onruntimesuccess)/[`ONRUNTIMEFAILED`](#onruntimefailed) są emitowane zarówno przez ten obiekt, jak i przez każdy warunek składowy. Domyślnie `FALSE`.

**Przykłady**

```
COC_END^BREAK(TRUE);
CCONDISATPOS^BREAK(TRUE);
```

**Kompatybilność:** `BREAK` - `PIKLIB61.DLL` ⚠️ (2/4), `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### CHECK

```
BOOL CHECK([BOOL emitSignals])
```

Ewaluuje warunek złożony i zwraca wynik.

**Parametry**

- `emitSignals` — (opcjonalnie) jak w [`BREAK`](#break).

**Zwraca**: [`BOOL`](BOOL.md) — wynik kombinacji warunków.

**Przykłady**

```
CCONDTESTEND^CHECK(TRUE);
```

**Kompatybilność:** `CHECK` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ONE_BREAK

```
void ONE_BREAK([BOOL emitSignals])
```

Ewaluuje warunek złożony. Jeżeli wynik jest `TRUE`, przerywa wyłącznie bieżącą procedurę (efekt analogiczny do [`@ONEBREAK`](../engine/scripts.md#operatory-skoku)).

**Parametry**

- `emitSignals` — (opcjonalnie) jak w [`BREAK`](#break).

**Przykłady**

```
COC_END^ONE_BREAK(TRUE);
CCONDISATPOS^ONE_BREAK(TRUE);
```

**Kompatybilność:** `ONE_BREAK` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONRUNTIMESUCCESS

Wywoływany, gdy warunek złożony zwrócił `TRUE` i `emitSignals` było ustawione na `TRUE`.

### ONRUNTIMEFAILED

Wywoływany, gdy warunek złożony zwrócił `FALSE` i `emitSignals` było ustawione na `TRUE`.
