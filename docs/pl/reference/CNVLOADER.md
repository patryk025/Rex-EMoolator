# CNVLOADER

Dynamiczny ładowacz plików `.CNV` w trakcie działania silnika. W przeciwieństwie do [`CLASS`](CLASS.md), który definiuje izolowany kontekst per-instancja, `CNVLOADER` doładowuje zmienne ze wskazanego pliku bezpośrednio do bieżącego kontekstu — zachowują się tak, jakby były tam zdefiniowane od początku.

Jeden obiekt `CNVLOADER` może mieć jednocześnie załadowanych wiele plików `.CNV`. Każde wywołanie [`RELEASE`](#release) zwalnia jeden konkretny plik.

## Metody

### LOAD

```
void LOAD(STRING cnvFile)
```

Ładuje wskazany plik `.CNV`. Zmienne zdefiniowane w pliku zostają dodane do bieżącego kontekstu. Próba ponownego załadowania pliku już raz załadowanego jest ignorowana.

**Parametry**

- `cnvFile` — ścieżka do pliku `.CNV` (rozwiązywana przez VFS silnika).

**Przykłady**

```
CNVLOADER^LOAD(VARSTEMP0);
CNVLOADER^LOAD([G_SCUTSCENE+".CNV"]);
```

### RELEASE

```
void RELEASE(STRING cnvFile)
```

Zwalnia wcześniej załadowany plik — usuwa z bieżącego kontekstu wszystkie zmienne, które do niego należały. Wywołanie z plikiem, który nie został wcześniej załadowany, nie ma efektu.

**Parametry**

- `cnvFile` — ścieżka do uprzednio załadowanego pliku.

**Przykłady**

```
CNVLOADER^RELEASE([G_SCUTSCENE+".CNV"]);
CNVLOADER^RELEASE("WYNURZENIE.CNV");
```

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
