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

### NEXT

```
mixed NEXT()
```

Przesuwa marker o jedno w prawo (do następnego elementu, ograniczony wartością ostatniego indeksu) i zwraca referencję do elementu wskazywanego po przesunięciu.

**Zwraca**: referencja do elementu pod nowym markerem.

**Przykłady**

```
GENEMIES^NEXT();
GBAZUK^NEXT();
```

### PREV

```
mixed PREV()
```

Przesuwa marker o jedno w lewo (do poprzedniego elementu, ograniczony zerem) i zwraca referencję do elementu pod nowym markerem.

**Zwraca**: referencja do elementu pod nowym markerem.

### REMOVE

```
void REMOVE(STRING varName)
```

Usuwa z grupy element o podanej nazwie. Jeżeli marker wskazywał na element poza nowym zakresem, zostaje przesunięty na ostatni dostępny element (lub `-1`, jeśli grupa stała się pusta).

**Parametry**

- `varName` — nazwa zmiennej do usunięcia.

**Przykłady**

```
GOBJ^REMOVE(S1);
GOBJ^REMOVE("ANNTNTR");
```

### REMOVEALL

```
void REMOVEALL()
```

Czyści grupę z wszystkich elementów i resetuje marker.

**Przykłady**

```
GRPHIDE^REMOVEALL();
```

### RESETMARKER

```
void RESETMARKER()
```

Ustawia marker na pierwszy element grupy (indeks `0`). Dla pustej grupy marker przyjmuje wartość `-1`.

**Przykłady**

```
GENEMIES^RESETMARKER();
```

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
