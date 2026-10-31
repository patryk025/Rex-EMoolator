# Budowanie i rozwój

Projekt budowany jest Gradle'em. Wrapper jest w repozytorium, więc wystarczy JDK 21 lub nowszy; na Windowsie zamiast `./gradlew` używa się `gradlew.bat`.

## Moduły

| Moduł | Rola |
|---|---|
| `core` | cała logika emulatora: interpreter, managery, loadery, VFS |
| `desktop` | launcher Swing i uruchamianie gry przez LWJGL3 |
| `android` | aplikacja na Androida |
| `android-api-check` | moduł wyłącznie dla linta: pokazuje źródła `core` jako bibliotekę Androida |

Budowę kodu opisuje rozdział [Architektura](../internals/architecture.md).

## Desktop

```bash
./gradlew desktop:run        # uruchomienie emulatora
./gradlew desktop:build      # JAR i ZIP dystrybucyjny
./gradlew desktop:shadowJar  # tylko JAR
./gradlew desktop:distZip    # tylko ZIP
./gradlew desktop:jpackage   # instalator dla bieżącego systemu
```

| Artefakt | Ścieżka | Do czego |
|---|---|---|
| JAR | `desktop/build/libs/Rex_EMoolator-desktop-<wersja>.jar` | wygodna dystrybucja w jednym pliku |
| ZIP | `desktop/build/distributions/Rex_EMoolator-desktop-<wersja>.zip` | zalecany do redystrybucji, bo biblioteki (także LGPL) można podmienić, nadpisując pliki w `lib/` |
| instalator | `desktop/build/jpackage/` | `.exe`, `.dmg` albo `.deb`, zależnie od systemu |

Bez zmiennej `RELEASE_VERSION` wersja to `0.1-SNAPSHOT`, więc lokalny JAR uruchamia się poleceniem:

```bash
java -jar desktop/build/libs/Rex_EMoolator-desktop-0.1-SNAPSHOT.jar
```

Zadanie `desktop:jpackage` korzysta z narzędzia `jpackage` z bieżącego JDK albo z `JPACKAGE_JAVA_HOME`. Na Linuksie wymaga pakietu `fakeroot`, a na Windowsie narzędzi WiX.

## Android

Potrzebny jest Android SDK wskazany w `local.properties` (`sdk.dir=...`) albo przez `ANDROID_HOME`.

```bash
./gradlew android:assembleDebug
```

APK trafia do `android/build/outputs/apk/debug/`. Wydania na GitHubie to właśnie wariant `debug` podpisany kluczem z CI.

`android:assembleRelease` buduje wariant zoptymalizowany przez R8, ale projekt nie definiuje dla niego klucza, więc wynikowy APK jest niepodpisany i przed instalacją trzeba go podpisać samodzielnie.

## Testy i kontrola jakości

```bash
./gradlew :core:test                               # testy jednostkowe
./gradlew :android:lintDebug -PandroidApiCheck     # zgodność :android z API 24
./gradlew :android-api-check:lintDebug             # zgodność :core z API 24
./gradlew :core:jmh -PjmhIncludeRegex='.*Benchmark.*'   # benchmarki JMH
```

CI uruchamia testy dla pull requestów oraz dla commitów, których opis zaczyna się od `fix`, `feat`, `refactor`, `perf` albo `test`. Commity `docs:` i `chore:` przechodzą tylko przez kontrolę API. Benchmarki startują przy commitach `perf:`.

## Licencje zależności

```bash
./gradlew updateThirdPartyNotices   # odtwarza THIRD-PARTY-NOTICES.md
./gradlew checkLicense              # zgłasza nową lub niezatwierdzoną rodzinę licencji
```

`desktop:build` odświeża `THIRD-PARTY-NOTICES.md` automatycznie, a `check` i `build` kończą się błędem, jeśli plik albo reguły dozwolonych licencji są nieaktualne.

## Dokumentacja

Serwis powstaje z katalogu `docs/` przy użyciu Material for MkDocs. Strony istnieją po polsku i angielsku, a ich struktury powinny pozostać zgodne.

```bash
python -m venv .venv_docs
.venv_docs/bin/python -m pip install -r docs/requirements.txt   # Windows: .venv_docs\Scripts\python
.venv_docs/bin/python -m mkdocs serve
```

Linki i kotwice sprawdza `tools/check_docs_links.py`. Skrypt uruchamia się Pythonem z tego samego środowiska.

## Śledzenie wykonania

Emulator potrafi nagrać przebieg skryptów. Włączają to właściwości JVM `rex.trace.enabled=true` i opcjonalnie `rex.trace.path=<plik>`. Nagranie otwiera się przeglądarką `python tools/trace-viewer/viewer.py`.

Pozostałe narzędzia opisuje [`tools/README.md`](https://github.com/patryk025/Rex-EMoolator/blob/main/tools/README.md).

## Wydania

Wydanie powstaje po wypchnięciu taga `v*` albo po ręcznym uruchomieniu workflow `Build and Release`. Workflow buduje instalatory dla trzech systemów, JAR, ZIP oraz APK i publikuje je z listą zmian wygenerowaną z historii commitów.
