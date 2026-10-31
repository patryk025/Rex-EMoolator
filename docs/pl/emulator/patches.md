# Łatki

Łatka to nakładka na pliki gry: oficjalna poprawka, zmiana poziomu trudności albo fanowska modyfikacja. Emulator nie zmienia przy tym oryginalnych danych, tylko montuje pliki łatki ponad nimi. Przy odczycie pierwszeństwo mają zapisy gry, potem łatki, a na końcu oryginalne pliki.

## Zarządzanie łatkami

Okno otwiera przycisk **Łatki** przy grze. Lista łączy zainstalowane łatki z katalogiem wbudowanym w emulator oraz z katalogiem pobieranym z sieci, ale pokazuje tylko to, co pasuje do danej gry.

| Działanie | Znaczenie |
|---|---|
| **Zainstaluj** | pobiera archiwum łatki (ZIP lub RAR) i rozpakowuje je do katalogu łatek |
| **Włącz/Wyłącz** | decyduje, czy łatka zostanie zamontowana przy kolejnym uruchomieniu gry |
| **W górę** / **W dół** | zmienia kolejność montowania, gdy kilka łatek dotyka tych samych plików |
| **Odinstaluj** | usuwa pliki łatki |
| **Importuj lokalne archiwum** | instaluje archiwum z dysku; musi ono zawierać `patch.json` |
| **Podłącz folder roboczy** | montuje folder bez kopiowania, więc zmiany są widoczne przy kolejnym uruchomieniu gry |

Instalator pomija pliki `.dll`, `.exe` i `install.ini` z archiwum, bo emulator ich nie używa.

## Zgodność

Emulator rozpoznaje wydanie gry po skrócie SHA-1 biblioteki silnika i na tej podstawie ocenia każdą łatkę:

| Zgodność | Znaczenie |
|---|---|
| **Dokładna** | autor łatki przygotował ją dla tego wydania |
| **Rodzina (wymaga wymuszenia)** | łatka dotyczy tej gry, ale innego wydania; włączenie jej to świadomy wybór użytkownika |
| **Brak** | łatka nie pasuje do gry |

Jeśli biblioteka silnika została zmodyfikowana (np. przez łatkę no-CD), jej skrót nie będzie znany. Emulator próbuje wtedy rozpoznać grę po nazwie, a gdy to nie wystarczy, w ustawieniach gry można wpisać **Rodzinę łatek** ręcznie, np. `reksio-czarodzieje`.

## Tworzenie łatki

Łatka to katalog z plikiem `patch.json` i drzewem plików, które odwzorowuje układ gry:

```
moja-latka/
├── patch.json
└── files/
    └── DANE/
        └── scene12/music.cnv
```

Przykładowy manifest:

```json
{
  "id": "reksio-ufo-s65-zamek",
  "name": "Reksio i UFO — naprawa przyciemniania ekranu",
  "description": "Naprawia zbugowane przyciemnianie ekranu w scenie S65_ZAMEK",
  "author": "Autor",
  "version": "1.0.0",
  "targetHashes": ["5F6CBB33576D4F5493F141DDEE70CD108EA0F9D5"],
  "targetFamily": "reksio-ufo",
  "filesRoot": "files",
  "source": { "type": "LOCAL" }
}
```

| Pole | Znaczenie |
|---|---|
| `id` | unikalny identyfikator, a zarazem nazwa katalogu łatki |
| `targetHashes` | skróty SHA-1 bibliotek silnika, na których łatka była testowana |
| `targetFamily` | rodzina gry, która pozwala włączyć łatkę w innych wydaniach |
| `filesRoot` | podkatalog z plikami nakładki, domyślnie `files` |
| `archiveRoot` | katalog wewnątrz archiwum, który jest korzeniem nakładki; przydaje się, gdy archiwum ma folder nadrzędny |
| `source` | pochodzenie łatki: `LOCAL`, `URL` albo `GDRIVE` (link z dostępem dla każdego) |
| `reference` | opcjonalny link do opisu łatki |
| `conflicts`, `requires`, `supersedes` | identyfikatory łatek, z którymi łatka się wyklucza, których wymaga lub które zastępuje |

Podczas pracy nad łatką wygodnie jest użyć **Podłącz folder roboczy**. Folder nie musi mieć `patch.json`: bez niego sam jest traktowany jako korzeń nakładki.

Nazwy rodzin znajdują się w klasie [`GameFamilies`](https://github.com/patryk025/Rex-EMoolator/blob/main/core/src/pl/genschu/bloomooemulator/logic/GameFamilies.java), a wbudowany katalog łatek w pliku [`assets/patch_catalog.json`](https://github.com/patryk025/Rex-EMoolator/blob/main/assets/patch_catalog.json). Katalog zdalny może dodawać nowe wpisy i nadpisywać istniejące o tym samym `id`.
