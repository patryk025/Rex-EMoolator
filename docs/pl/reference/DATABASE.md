# DATABASE

Baza danych w postaci tabeli — zbiór wierszy o jednolitym schemacie. Schemat definiuje powiązana zmienna [`STRUCT`](STRUCT.md): jej pole [`FIELDS`](STRUCT.md#fields) wyznacza kolumny tabeli oraz typy danych w poszczególnych kolumnach.

Dostęp do wierszy realizowany jest sekwencyjnie poprzez kursor. Bieżąca pozycja kursora jest zmieniana metodami [`SELECT`](#select) (bezpośrednio na indeks), [`NEXT`](#next) (kolejny wiersz) i [`FIND`](#find) (po wartości w kolumnie). Dane zapisywane są w plikach `.DTA` w formacie z separatorem `|` — można je ładować ([`LOAD`](#load)) i zapisywać ([`SAVE`](#save)).

## Pola

### MODEL

```
STRING MODEL
```

Nazwa zmiennej typu [`STRUCT`](STRUCT.md) definiującej schemat bazy. Pole obowiązkowe — metoda [`LOAD`](#load) wymaga, by schemat został wcześniej zsynchronizowany ze [`STRUCT`](STRUCT.md).

## Metody

### FIND

```
INTEGER FIND(STRING columnName, mixed columnValue, INTEGER defaultIndex)
```

Wyszukuje pierwszy wiersz, w którym kolumna o nazwie `columnName` ma wartość `columnValue`. Zwraca jego indeks lub `defaultIndex`, jeżeli żaden wiersz nie pasuje.

**Parametry**

- `columnName` — nazwa przeszukiwanej kolumny.
- `columnValue` — szukana wartość.
- `defaultIndex` — indeks zwracany, jeżeli nie znaleziono dopasowania.

**Zwraca**: [`INTEGER`](INTEGER.md) — indeks znalezionego wiersza lub `defaultIndex`.

**Przykłady**

```
DBOBJECTS^FIND("IDNAME",VARSTAKENAME,0);
DBOBJECTS^FIND("TYPE",102,0);
DBDIALOGI^FIND("ID",SDIALOGNAME,IDIALOGINDEKS);
```

**Kompatybilność:** `FIND` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETROWSNO

```
INTEGER GETROWSNO()
```

Zwraca liczbę wierszy w bazie.

**Zwraca**: [`INTEGER`](INTEGER.md) — liczba wierszy.

**Przykłady**

```
DBOBJECTS^GETROWSNO();
```

**Kompatybilność:** `GETROWSNO` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETCURSORPOS

```
INTEGER GETCURSORPOS()
```

Zwraca indeks wiersza, na którym aktualnie stoi kursor bazy. Indeks jest liczony od `0`; pozycję zmieniają między innymi [`SELECT`](#select), [`NEXT`](#next) i [`FIND`](#find).

**Zwraca**: [`INTEGER`](INTEGER.md) — bieżący indeks kursora.

**Kompatybilność:** `GETCURSORPOS` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOAD

```
void LOAD(STRING dtaName)
```

Ładuje zawartość bazy z pliku `.DTA`. Każda linia pliku to jeden wiersz; kolumny w wierszu rozdzielone są znakiem `|`. Wywołanie metody bez wcześniej zdefiniowanego schematu ([`MODEL`](#model)) jest przerywane z komunikatem błędu.

**Parametry**

- `dtaName` — ścieżka do pliku `.DTA`.

**Przykłady**

```
DBOBJECTS^LOAD(VARSCURRARCADE);
DBITEMS^LOAD("$COMMON\ITEMS0.DTA");
```

**Kompatybilność:** `LOAD` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### NEXT

```
void NEXT()
```

Przesuwa kursor do kolejnego wiersza.

**Przykłady**

```
DBSCENE^NEXT();
```

**Kompatybilność:** `NEXT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEALL

```
void REMOVEALL()
```

Usuwa wszystkie wiersze z bazy. Schemat ([`MODEL`](#model)) pozostaje bez zmian.

**Przykłady**

```
DBITEMS^REMOVEALL();
```

**Kompatybilność:** `REMOVEALL` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SAVE

```
void SAVE(STRING dtaName)
```

Zapisuje aktualną zawartość bazy do pliku `.DTA` w tym samym formacie, którego oczekuje [`LOAD`](#load) (wiersze rozdzielone znakiem nowej linii, kolumny — znakiem `|`).

**Parametry**

- `dtaName` — ścieżka docelowego pliku `.DTA`.

**Przykłady**

```
DBOBJECTS^SAVE(VARSCURRARCADE);
DBLEVEL^SAVE(["$COMMON\SAVE_BD\BD_CLEV"+VARIACTIVESLOT+".FLD"]);
```

**Kompatybilność:** `SAVE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SELECT

```
void SELECT(INTEGER rowIndex)
```

Ustawia kursor na wiersz o podanym indeksie (liczonym od zera).

**Parametry**

- `rowIndex` — indeks docelowego wiersza.

**Przykłady**

```
DBOBJECTS^SELECT(0);
DBOBJECTS^SELECT(VARITER);
```

**Kompatybilność:** `SELECT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
