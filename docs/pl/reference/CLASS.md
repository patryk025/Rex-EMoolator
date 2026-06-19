# CLASS

Definicja klasy obiektów. Plik definicji ma rozszerzenie `.class` i wewnętrznie składnię analogiczną do plików `.CNV`. Z definicji klasy tworzone są instancje (`INSTANCE`) metodą [`NEW`](#new); usuwane są metodą [`DELETE`](#delete).

## Pola

### DEF

```
STRING DEF
```

Ścieżka do pliku z definicją klasy. Jeżeli ścieżka nie zaczyna się od `$`, jest poprzedzana prefiksem `$COMMON/classes/`.

### BASE

```
STRING BASE
```

Klasa bazowa do dziedziczenia. W aktualnej implementacji emulatora pole jest odczytywane, ale nie używane.

## Metody

### NEW

```
mixed NEW(STRING varName, [mixed param1, ..., mixed paramN])
```

Tworzy nową instancję klasy o nazwie `varName`. Nowa zmienna jest rejestrowana w kontekście, w którym zadeklarowana jest klasa (a nie w kontekście wywołującym) — instancja przeżywa zatem zmianę sceny, jeżeli klasa jest zadeklarowana na poziomie aplikacji.

Po utworzeniu instancji, jeżeli plik definicji klasy zawiera procedurę o nazwie `CONSTRUCTOR`, jest ona wywoływana z argumentami przekazanymi do `NEW` (włącznie z `varName` jako `$1`).

**Parametry**

- `varName` — nazwa nowej zmiennej-instancji.
- `param1, …, paramN` — (opcjonalnie) argumenty przekazywane do procedury `CONSTRUCTOR`.

**Zwraca**: wartość zwróconą przez procedurę `CONSTRUCTOR` lub `NULL`.

**Przykłady**

```
MM^NEW("G_MENU");
CLSLOGOBJ^NEW("LOG", FALSE);
CLSEIFELENEMYOBJ^NEW("ENEMY0", "1_ENEMY0.ANN", 2, 5, 16, 4, 0, 2, 18);
CLSBDENEMYOBJ^NEW(["BDENEMY"+I2], _I_, I1, I2, IBDKRAINA);
```

### DELETE

```
mixed DELETE(STRING varName, [mixed param1, ..., mixed paramN])
```

Usuwa instancję klasy o nazwie `varName`. Jeżeli definicja klasy zawiera procedurę o nazwie `DESTRUCTOR`, jest ona wywoływana z argumentami przekazanymi do `DELETE` (włącznie z `varName` jako `$1`) zanim zmienna zostanie wyrejestrowana z kontekstu.

**Parametry**

- `varName` — nazwa usuwanej zmiennej-instancji.
- `param1, …, paramN` — (opcjonalnie) argumenty przekazywane do procedury `DESTRUCTOR`.

**Zwraca**: wartość zwróconą przez procedurę `DESTRUCTOR` lub `NULL`.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji zmiennej klasy.
