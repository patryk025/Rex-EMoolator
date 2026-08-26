# GROUP

Grupa zmiennych, do której można wysyłać zbiorowe wywołania metod. Każda metoda wywołana na obiekcie typu `GROUP` — która nie należy do własnego API grupy — jest delegowana do każdego elementu po kolei. Jeżeli dany element nie implementuje wywołanej metody, jest pomijany cicho (bez błędu).

Grupa utrzymuje wewnętrzny **marker** wskazujący jeden z elementów. Pozycja markera jest modyfikowana metodami [`NEXT`](#next), [`PREV`](#prev) i [`RESETMARKER`](#resetmarker). Markerem można posłużyć się do sekwencyjnego przechodzenia po elementach grupy.

Wartość zmiennej (`value`) typu `GROUP` to liczba elementów w grupie.

## Metody

### \[nazwa metody\]

```
void <methodName>(mixed param1, ..., mixed paramN)
```

Każda metoda spoza własnego API grupy jest delegowana do wszystkich elementów grupy z tymi samymi argumentami. Elementy, które nie implementują takiej metody, są pomijane.

**Przykłady**

```
GRPHIDE^HIDE();
GRPMOVE^SETPOSITION(VARX,VARY);
```



### ADD

```
void ADD(STRING varName1, [STRING varName2, ...])
```

Dodaje do grupy jeden lub więcej elementów po nazwie zmiennej. Próba ponownego dodania elementu już obecnego w grupie jest ignorowana.

**Parametry**

- `varName1, varName2, …` — kolejne nazwy zmiennych do dodania.

**Przykłady**

```
GRPHIDE^ADD("ANNREX");
GRPMOVE^ADD("ANNBODY1","ANNWAND1","ANNHEAD1");
GALL^ADD(["ANNPOLA_"+ICLONENO]);
```

**Kompatybilność:** `ADD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ADDCLONES

```
void ADDCLONES(STRING varName, INTEGER firstCloneIndex, INTEGER lastCloneIndex)
```

Dodaje do grupy zakres klonów zmiennej — od `firstCloneIndex` do `lastCloneIndex` włącznie. Klony są referencjami po nazwie wygenerowanej według wzorca silnika (sufiks indeksu).

**Parametry**

- `varName` — nazwa zmiennej bazowej.
- `firstCloneIndex` — indeks pierwszego klona.
- `lastCloneIndex` — indeks ostatniego klona.

**Przykłady**

```
GBKG^ADDCLONES("ANNPLANNAK",0,[I1-1]);
GTRASA^ADDCLONES("ANNSKRZYNIA",1,ITMPCLONENO);
GRPLANS^ADDCLONES("IMGPLAN1",1,10);
```

**Kompatybilność:** `ADDCLONES` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETSIZE

```
INTEGER GETSIZE()
```

Zwraca liczbę elementów w grupie.

**Zwraca**: [`INTEGER`](INTEGER.md) — rozmiar grupy.

**Przykłady**

```
GRPHIDE^GETSIZE();
```

**Kompatybilność:** `GETSIZE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETNAMEATMARKER

```
STRING GETNAMEATMARKER()
```

Zwraca nazwę elementu wskazywanego przez bieżący marker. Wynik można zapisać do zmiennej `STRING`, a następnie wywoływać metody pośrednio przez `*nazwa`. Dla nieprawidłowej pozycji emulator bezpiecznie zwraca `"NULL"`.

### GETMARKERPOS

```
INTEGER GETMARKERPOS()
```

Zwraca bieżącą pozycję markera. Wartość `-1` oznacza pozycję przed pierwszym elementem.

### NEXT

```
STRING NEXT([INTEGER step])
```

Przesuwa marker w prawo o jeden element lub o podany krok. Po przekroczeniu końca grupy pozycja zawija się na początek.

**Zwraca**: nazwa elementu pod nowym markerem.

**Przykłady**

```
GENEMIES^NEXT();
GBAZUK^NEXT();
```

**Kompatybilność:** `NEXT` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PREV

```
STRING PREV([INTEGER step])
```

Przesuwa marker w lewo o jeden element lub o podany krok. Po przekroczeniu początku grupy pozycja zawija się na koniec.

**Zwraca**: nazwa elementu pod nowym markerem.

**Kompatybilność:** `PREV` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVE

```
void REMOVE(STRING varName)
```

Usuwa z grupy element o podanej nazwie. Pozycja markera nie jest automatycznie zmieniana.

**Parametry**

- `varName` — nazwa zmiennej do usunięcia.

**Przykłady**

```
GOBJ^REMOVE(S1);
GOBJ^REMOVE("ANNTNTR");
```

**Kompatybilność:** `REMOVE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### REMOVEALL

```
void REMOVEALL()
```

Czyści grupę z wszystkich elementów. Pozycja markera nie jest automatycznie zmieniana; do jej wyzerowania służy `RESETMARKER()`.

**Przykłady**

```
GRPHIDE^REMOVEALL();
```

**Kompatybilność:** `REMOVEALL` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESETMARKER

```
void RESETMARKER()
```

Ustawia marker na `-1`, czyli przed pierwszym elementem. Następne wywołanie `NEXT()` wybierze element o indeksie `0`.

**Przykłady**

```
GENEMIES^RESETMARKER();
```

**Kompatybilność:** `RESETMARKER` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETMARKERPOS

```
void SETMARKERPOS(INTEGER position)
```

Ustawia marker na podanej pozycji. Wartość jest ograniczana do zakresu od `-1` do indeksu ostatniego elementu; dla pustej grupy marker przyjmuje `-1`.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
