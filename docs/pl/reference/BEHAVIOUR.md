# BEHAVIOUR

Procedura. Wykonuje kod zapisany w polu `CODE`, opcjonalnie obwarowany warunkiem wskazanym w polu `CONDITION`. Argumenty wywołania dostępne są wewnątrz kodu jako `$1`, `$2`, …, opisane szerzej w [Argumenty procedur](../engine/scripts.md#argumenty-procedur).

## Pola

### CODE

```
STRING CODE
```

Treść procedury — blok kodu w nawiasach klamrowych, zgodny ze składnią opisaną w rozdziale [Skrypty](../engine/scripts.md#bloki-kodu).

Przykład:

```
BEHGOTOTITLE:CODE={BEHSETSCENE^RUN();G_SARCADESCENE^SET("EGIPTLEJ");BEHCSSTART^RUN();}
```

### CONDITION

```
STRING CONDITION
```

Nazwa zmiennej typu [`CONDITION`](CONDITION.md) lub [`COMPLEXCONDITION`](COMPLEXCONDITION.md), używana przez metodę [`RUNC`](#runc) oraz przez [`RUNLOOPED`](#runlooped) jako warunek przerwania pętli. Jeżeli pole nie jest ustawione, metody działają bezwarunkowo.

## Metody

### RUN

```
mixed RUN([mixed param1, ..., mixed paramN])
```

Wykonuje kod procedury. Przekazane argumenty są dostępne w kodzie jako `$1`, `$2`, …. Wartość zwracana to wynik wyrażenia [`@RETURN`](../engine/scripts.md#operatory-skoku) w ciele procedury, lub `NULL`, jeżeli `@RETURN` nie został wywołany.

**Parametry**

- `param1, …, paramN` — argumenty procedury (opcjonalne, dowolnego typu).

**Zwraca**: wartość zwrócona przez procedurę lub `NULL`.

**Przykłady**

```
__LOAD_SETTINGS__^RUN();
BEHSELECTOBJ^RUN(VARITER);
BEHADDITEM^RUN(SOBJECT|SPARAM0, VARITER);
BEHENTERRABBIT^RUN("ANNHILL0", -1);
```

**Kompatybilność:** `RUN` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RUNC

```
mixed RUNC([mixed param1, ..., mixed paramN])
```

Wywołuje procedurę pod warunkiem, że warunek wskazany w polu `CONDITION` jest spełniony. Jeżeli `CONDITION` nie jest ustawione, zachowuje się jak [`RUN`](#run). Argumenty i wartość zwracana — jak w `RUN`.

**Parametry**

- `param1, …, paramN` — argumenty procedury.

**Zwraca**: wartość zwróconą przez procedurę lub `NULL` (również, gdy warunek nie był spełniony).

**Przykłady**

```
BEHREMOVEMENUITEM^RUNC("CHOMIK");
BEHREMOVEMENUITEM^RUNC(VARSTRINGTEMP);
BEH_HERO_FINISHED_0^RUNC();
```

**Kompatybilność:** `RUNC` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RUNLOOPED

```
void RUNLOOPED(INTEGER start, INTEGER length)
void RUNLOOPED(INTEGER start, INTEGER length, INTEGER step, [mixed extraArg1, ..., mixed extraArgN])
```

Wywołuje procedurę w pętli `for` z licznikiem przekazywanym jako `$1`. Dodatkowe argumenty (od czwartego włącznie) trafiają do procedury jako `$2`, `$3`, … . Jeżeli pole `CONDITION` jest ustawione, jego warunek jest sprawdzany przed każdą iteracją — jeżeli warunek nie jest spełniony, pętla się kończy.

Pętla jest równoważna następującemu pseudokodowi:

```
for (int i = start; i < start + length; i += step) {
    // wywołanie procedury z $1 = i, $2..$N = extraArgs
}
```

Jeżeli `step` jest pomijany lub przekazany jako `0`, używana jest wartość `1`. Operator [`@BREAK`](../engine/scripts.md#operatory-skoku) w ciele procedury kończy pętlę `RUNLOOPED` (ale nie procedurę wywołującą).

**Parametry**

- `start` — wartość początkowa licznika.
- `length` — liczba iteracji do wykonania (`startVal < endVal` to `startVal + length`).
- `step` — (opcjonalnie) krok licznika. Domyślnie `1`.
- `extraArg1, …, extraArgN` — (opcjonalnie) dodatkowe argumenty przekazywane do procedury.

**Przykłady**

```
BEHSHOWMENU^RUNLOOPED(0, ARRAYWARSZTATMENUPRZEDMIOTY^GETSIZE());
BEHSHOWPIONEK^RUNLOOPED(1, 9);
BEHINITZASLONAX^RUNLOOPED(0, 7, 1, "[80*$1]");
```

**Kompatybilność:** `RUNLOOPED` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji procedury.

### ONDONE

Wywoływany po zakończeniu wywołania procedury.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
