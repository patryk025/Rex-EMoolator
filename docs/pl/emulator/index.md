# Emulator

Rex-EMoolator uruchamia gry oparte na silnikach Piklib i BlooMoo bez oryginalnych bibliotek DLL: sam czyta dane gry i odtwarza zachowanie silnika. Działa na Windowsie, Linuksie, macOS i Androidzie.

!!! note "Pliki gry"
    Emulator nie zawiera żadnych gier. Potrzebna jest własna kopia: płyta, jej obraz albo zainstalowana gra.

## Pobieranie

Gotowe paczki są dołączane do każdego [wydania na GitHubie](https://github.com/patryk025/Rex-EMoolator/releases/latest):

| System | Plik | Uwagi |
|---|---|---|
| Windows | `Rex-EMoolator-<wersja>.exe` | instalator ze środowiskiem Java w zestawie |
| macOS | `Rex-EMoolator-<wersja>.dmg` | obraz ze środowiskiem Java w zestawie |
| Linux | `rex-emoolator_<wersja>_amd64.deb` | pakiet ze środowiskiem Java w zestawie |
| Android 7.0+ | `Rex_Emoolator-android-v<wersja>-debug.apk` | instalacja spoza sklepu |
| dowolny | `Rex_EMoolator-desktop-v<wersja>.jar` | jeden plik, wymaga Javy 21+ |
| dowolny | `Rex_EMoolator-desktop-v<wersja>.zip` | biblioteki jako osobne pliki w `lib/`, wymaga Javy 21+ |

Instalatory nie są podpisane cyfrowo, więc Windows i macOS mogą ostrzec przed uruchomieniem. Wersję z pliku JAR uruchamia się poleceniem `java -jar`, a wersję z ZIP-a skryptem z katalogu `bin/`.

Emulator można też [zbudować ze źródeł](development.md).

## Dodawanie gry

Emulator szuka pliku `dane/Application.def`, który ma każda obsługiwana gra. Może on leżeć w:

- folderze z zainstalowaną grą lub skopiowaną zawartością płyty,
- obrazie płyty ISO 9660 lub UDF,
- archiwum ZIP.

Obraz i archiwum są czytane bezpośrednio, bez rozpakowywania. Format rozpoznawany jest po zawartości, a nie po rozszerzeniu.

1. Kliknij **Dodaj grę**.
2. Wskaż źródło przyciskiem **Wybierz folder** albo **Wybierz obraz/ZIP**.
3. W razie potrzeby wpisz **Podfolder gry**. Bez niego emulator przeszuka źródło do czterech poziomów w głąb, a gdy znajdzie kilka gier (np. na płycie *Herkules + Odyseusz*), pozwoli wybrać, które dodać.
4. Zapisz i uruchom grę przyciskiem **Uruchom**.

Na Androidzie okno wygląda tak samo. Od Androida 11 aplikacja poprosi przy pierwszym uruchomieniu o dostęp do wszystkich plików, bez którego nie odczyta danych gry z pamięci urządzenia.

## Ustawienia gry

Każda gra ma własne ustawienia, dostępne przy dodawaniu i przez **Edytuj grę**:

| Ustawienie | Znaczenie |
|---|---|
| Tryb pracy myszy | **Dotykowo** albo **Fizyczny kursor** |
| Rozdzielczość starego zegara | ziarnistość czasu widziana przez skrypty; zobacz [profile zegara](../internals/loop.md#zegar-silnika) |
| Wirtualny joystick | sterowanie kursorem joystickiem na ekranie |
| Podpowiedź z kodem Pana Policjanta | pokazuje kod w scenach sprawdzających legalność kopii |
| Rozciąganie na cały ekran | ignoruje proporcje obrazu |
| Licznik FPS | wyświetla liczbę klatek na sekundę |
| Rodzina łatek | ręczne wskazanie gry, gdy emulator nie rozpoznaje jej wydania; zobacz [Łatki](patches.md) |

## Skróty klawiszowe

W trakcie gry klawisze funkcyjne włączają narzędzia diagnostyczne:

| Klawisz | Działanie |
|---|---|
| `F1` | podgląd zmiennych |
| `F2` | podgląd grafik |
| `F3` | podgląd przycisków |
| `F4` | obrysy grafik |
| `F5` | podgląd [`MATRIX`](../reference/MATRIX.md) |
| `F6` | statystyki wydajności |
| `F7` | eksport grafik sceny do katalogu `exported_graphics/` |
| `F8` | podgląd [`WORLD`](../reference/WORLD.md) |
| `F9` | wybór sceny |
| `F10` | podgląd kolizji |
| `F11` | pauza |
| `F12` | [jeden puls silnika](../internals/loop.md#pauza-i-krokowanie) |

## Gdzie emulator trzyma dane

Na komputerze wszystko leży w katalogu `.rexemoolator` w folderze domowym użytkownika (na Windowsie `%USERPROFILE%\.rexemoolator`):

| Ścieżka | Zawartość |
|---|---|
| `games.json` | lista gier i ich ustawienia |
| `storage/<id gry>/` | zapisy gry |
| `patches/` | zainstalowane łatki |

Pliki gry nie są modyfikowane. Na Androidzie te same dane leżą w prywatnym katalogu aplikacji.

## Co dalej

- [Stan gier](games.md) — co już działa, a co jeszcze nie.
- [Zrzuty ekranu](screenshots.md) — jak gry wyglądają w emulatorze.
- [Łatki](patches.md) — oficjalne poprawki i modyfikacje fanów.
- [Budowanie i rozwój](development.md) — kompilacja, testy i narzędzia.
