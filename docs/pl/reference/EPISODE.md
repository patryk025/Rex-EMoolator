# EPISODE

Logiczny segment gry — kontener scen ([`SCENE`](SCENE.md)) wewnątrz [`APPLICATION`](APPLICATION.md). W praktyce gry AidemMedia używały jednego epizodu na całą grę.

## Pola

### SCENES

```
STRING SCENES
```

Lista nazw scen tworzących epizod, oddzielonych przecinkami.

### PATH

```
STRING PATH
```

Ścieżka relatywna do katalogu `dane` zawierająca pliki epizodu. Używana przez silnik przy lokalizowaniu plików `.CNV` scen.

### STARTWITH

```
STRING STARTWITH
```

Nazwa sceny, od której rozpoczyna się epizod.

### Metadane

Następujące pola są zapisywane jako metadane i nie wpływają bezpośrednio na działanie silnika:

- `AUTHOR` — autor pliku.
- `CREATIONTIME` — data utworzenia pliku.
- `DESCRIPTION` — opis epizodu.
- `LASTMODIFYTIME` — data ostatniej modyfikacji pliku.
- `VERSION` — wersja epizodu.

## Metody

### BACK

```
void BACK()
```

Wraca do sceny odtwarzanej bezpośrednio przed bieżącą.

**Przykłady**

```
PRZYGODA^BACK();
```

### GETCURRENTSCENE

```
STRING GETCURRENTSCENE()
```

Zwraca nazwę aktualnie aktywnej sceny.

**Zwraca**: nazwa sceny.

**Przykłady**

```
PRZYGODA^GETCURRENTSCENE();
```

### GETLATESTSCENE

```
STRING GETLATESTSCENE()
```

Zwraca nazwę sceny odtwarzanej przed bieżącą — tej, do której wróciłoby wywołanie [`BACK`](#back).

**Zwraca**: nazwa poprzedniej sceny.

### GOTO

```
void GOTO(STRING sceneName)
```

Przełącza grę do podanej sceny.

**Parametry**

- `sceneName` — nazwa sceny docelowej.

**Przykłady**

```
PRZYGODA^GOTO("CREDITS");
PRZYGODA^GOTO("MAGIC");
PRZYGODA^GOTO(G_SARCADEOBJECTS);
PRZYGODA^GOTO(UFO^RUN(["VARLEVEL"+VARNR], "GET"));
```
