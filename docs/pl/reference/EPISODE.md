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

Wraca do sceny odtwarzanej bezpośrednio przed bieżącą. Tak jak [`GOTO`](#goto), tylko zgłasza przejście, a scena zmienia się po zakończeniu bieżącej klatki.

Epizod prowadzi historię scen: przy każdym wejściu do sceny dopisuje ją na koniec. `BACK` wybiera przedostatni wpis i usuwa ostatni, a gdy historia ma tylko jeden wpis, nic nie robi. Scena, do której się wraca, zostaje dopisana ponownie, więc drugie `BACK` z rzędu nie cofa dalej, tylko ponownie wchodzi do tej samej sceny. Przykład: po historii `A, B, C` pierwsze `BACK` prowadzi do `B` (historia `A, B, B`), a drugie znowu do `B`.

!!! note "Weryfikacja"
    Historia i zachowanie `BACK` sprawdzone w `BlooMooDLL.dll` (Reksio i Wehikuł Czasu), w `CMC_Episode::back`.

**Przykłady**

```
PRZYGODA^BACK();
```

**Kompatybilność:** `BACK` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

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

**Kompatybilność:** `GETCURRENTSCENE` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETLATESTSCENE

```
STRING GETLATESTSCENE()
```

Zwraca nazwę sceny, którą epizod zwolnił jako ostatnią, czyli zwykle sceny odtwarzanej przed bieżącą. Wartość zmienia się dopiero przy faktycznym przejściu, więc wywołanie tuż po [`GOTO`](#goto) w tej samej klatce zwraca jeszcze starą wartość. Nie zawsze jest to scena, do której prowadzi [`BACK`](#back): po `BACK` z `C` do `B` zwraca `C`.

**Zwraca**: nazwa ostatnio zwolnionej sceny albo pusty napis, jeśli żadnej nie było.

**Kompatybilność:** `GETLATESTSCENE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GOTO

```
void GOTO(STRING sceneName)
```

Przełącza grę do podanej sceny.

Przejście jest **odroczone**. `GOTO` tylko zapamiętuje scenę docelową, a przełączenie wykonuje pętla główna, gdy w danej klatce input, timery, animacje, dźwięki i kolizje już się wykonały. Reszta skryptu, który wywołał `GOTO`, oraz wszystkie handlery wokół niego, kończą się więc w **starej** scenie, na jej obiektach. `@BREAK()` po `GOTO` przerywa tylko bieżący handler, a nie te, które go wywołały.

- Kilka wywołań `GOTO` w tej samej klatce nadpisuje się nawzajem: wygrywa ostatnie. `GOTO` z nieznaną nazwą kasuje wcześniejsze zgłoszenie.
- `GOTO` wywołane w trakcie ładowania sceny, do której właśnie się przechodzi (z jej `__INIT__` albo `ONINIT`), przepada, bo pętla czyści zgłoszenie dopiero po zakończeniu przejścia.
- Wyjątkiem jest scena startowa epizodu. Wchodzi się do niej bezpośrednio, przy starcie gry albo zmianie epizodu, więc `GOTO` z jej `__INIT__` zostaje i wykona się zaraz potem. Na tym opierają się sceny przekierowujące, na przykład `START` w Reksiu i Kapitanie Nemo.

!!! note "Weryfikacja"
    Odroczenie sprawdzone w `PIKLIB61.DLL`, `PIKLIB71.DLL`, `PIKLIB8.DLL` i `BlooMooDLL.dll` (`CMC_Scene::_goto` i `CMC_Scene::domodal`). Kolejność w klatce oraz utrata `GOTO` z inicjalizacji sprawdzone w `BlooMooDLL.dll`.

**Parametry**

- `sceneName` — nazwa sceny docelowej.

**Przykłady**

```
PRZYGODA^GOTO("CREDITS");
PRZYGODA^GOTO("MAGIC");
PRZYGODA^GOTO(G_SARCADEOBJECTS);
PRZYGODA^GOTO(UFO^RUN(["VARLEVEL"+VARNR], "GET"));
```

**Kompatybilność:** `GOTO` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.
