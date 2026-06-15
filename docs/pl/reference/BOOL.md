# BOOL

Typ logiczny. Przechowuje jedną z dwóch wartości: `TRUE` lub `FALSE`.

## Pola

### TOINI

```
BOOL TOINI
```

Określa, czy wartość pola jest zapisywana do pliku INI i przywracana po ponownym uruchomieniu.

### VALUE

```
BOOL VALUE
```

Aktualna wartość zmiennej.

## Metody

### GET

```
BOOL GET()
```

Zwraca aktualną wartość zmiennej.

**Zwraca**

- `BOOL` — bieżąca wartość pola `VALUE`.

### RESETINI

```
void RESETINI()
```

Przywraca wartość zmiennej do wartości resetu zdefiniowanej w atrybutach obiektu w skrypcie. Silnik szuka wartości w kolejności: `DEFAULT` → `INIT_VALUE` → `VALUE`; używana jest pierwsza znaleziona.

### SET

```
void SET(BOOL value)
```

Ustawia wartość zmiennej.

**Parametry**

- `value` — nowa wartość typu `BOOL`.

**Przykłady**

```
VARBLOCKSCENE^SET(FALSE);
__KEYB__^SET(KEYBOARD^ISENABLED());
VARBTEMP1^SET($2);
```

### SWITCH

```
void SWITCH(BOOL value1, BOOL value2)
```

Przełącza wartość zmiennej między wartościami podanymi w argumentach. Metoda przyjmuje dwa parametry ze względu na zgodność sygnatury z metodą `SWITCH` typów [`INTEGER`](INTEGER.md) oraz [`DOUBLE`](DOUBLE.md), choć w przypadku typu `BOOL` pełna informacja zawarta byłaby już w jednym argumencie.

**Parametry**

- `value1` — pierwsza wartość.
- `value2` — druga wartość.

**Przykłady**

```
B_0^SWITCH(TRUE, FALSE);
```

## Sygnały

### ONCHANGED

Wywoływany, gdy wartość zmiennej zostaje zmieniona na inną niż dotychczasowa.

### ONBRUTALCHANGED

Wywoływany przy każdym wywołaniu metody zmieniającej wartość, niezależnie od tego, czy nowa wartość różni się od poprzedniej.
