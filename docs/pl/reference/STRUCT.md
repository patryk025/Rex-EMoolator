# STRUCT

Struktura danych z nazwanymi, typowanymi polami. W skryptach silnika wykorzystywana wyłącznie razem z [`DATABASE`](DATABASE.md) — opisuje schemat wiersza bazy danych i przechowuje wartości aktualnie wskazywanego rekordu po wywołaniu [`SET`](#set).

## Pola

### FIELDS

```
STRING FIELDS
```

Schemat struktury jako lista pól oddzielonych przecinkiem. Każde pole ma format `NAZWA<TYP>`, gdzie `NAZWA` to nazwa pola, a `TYP` to nazwa typu danych. Dopuszczalne typy: `STRING`, `INTEGER`, `DOUBLE`, `BOOLEAN`. Typy są rozpoznawane bez rozróżniania wielkości liter; nieznana nazwa typu jest traktowana jak `STRING`.

## Metody

### GETFIELD

```
<type> GETFIELD(INTEGER fieldIndex)
```

Zwraca wartość pola o podanym indeksie (liczonym od zera). Typ zwracanej wartości wynika ze schematu — pole zadeklarowane jako `<INTEGER>` zwraca [`INTEGER`](INTEGER.md), jako `<DOUBLE>` — [`DOUBLE`](DOUBLE.md), jako `<BOOLEAN>` — [`BOOL`](BOOL.md), w pozostałych przypadkach [`STRING`](STRING.md). Dla indeksu spoza zakresu zwracana jest wartość pusta. Jeśli struktura nie została wcześniej zsynchronizowana z [`DATABASE`](DATABASE.md), wszystkie pola mają wartość pustą.

**Parametry**

- `fieldIndex` — indeks pola (`0`-bazowany).

**Zwraca**: wartość pola w typie zadeklarowanym w schemacie.

**Przykłady**

```
STLEVEL^GETFIELD(0);
```

**Kompatybilność:** `GETFIELD` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SET

```
void SET(STRING cursorName)
```

Synchronizuje strukturę z bieżącym wierszem wskazywanym przez kursor [`DATABASE`](DATABASE.md). Surowe wartości z kursora są konwertowane do typów zadeklarowanych w schemacie pola [`FIELDS`](#fields).

**Parametry**

- `cursorName` — nazwa zmiennej kursora skojarzonego z bazą danych.

**Przykłady**

```
SOBJECT^SET("DBOBJECTS_CURSOR");
```

**Kompatybilność:** `SET` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
