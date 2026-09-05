# Rex-EMoolator

**Polski** | [English](README.md)

Otwarto-źródłowy emulator odtwarzający zachowanie silnika Piklib/BlooMoo używanego w grach Aidem Media.

## Dokumentacja

Pełna dokumentacja silnika Piklib/BlooMoo oraz samego emulatora jest publikowana
pod adresem **<https://patryk025.github.io/Rex-EMoolator/>** — język skryptowy,
referencja wszystkich typów skryptowych, formaty plików i wnętrze silnika.
Dostępna jest po polsku i angielsku, a jej źródła leżą w katalogu
[`docs/`](docs/). Renderowane przez Material for MkDocs.

## Wymagania

### Toolchain do budowania
- **JDK**: Wersja 21 lub nowsza
- **Gradle**: Użyj wrappera dołączonego do repozytorium

### Desktop
- **Java**: Wersja 21 lub nowsza
- **System operacyjny**: Windows, Linux lub macOS
- **Pliki gier**: Oryginalne pliki gier (dane z oryginalnej płyty CD-ROM lub instalacji)

### Android
- **Wersja Androida**: 7.0 (API level 24) lub nowsza
- **Android SDK**: Zainstalowany i skonfigurowany przez `local.properties` (`sdk.dir=...`) albo `ANDROID_HOME`
- **Pliki gier**: Oryginalne pliki gier

## Budowanie projektu

Projekt wykorzystuje Gradle jako system budowania.

### Pełny build projektu
```bash
# Windows
gradlew.bat build

# Linux/macOS
./gradlew build
```

### Budowanie wersji Desktop

Moduł desktop produkuje dwa artefakty dystrybucyjne:

- **Single-file shadow JAR** — wygodna dystrybucja w jednym pliku.
- **Distribution ZIP** — thin JAR + każda zależność jako osobny plik w
  katalogu `lib/`, plus skrypty startowe. Rekomendowane do redystrybucji,
  ponieważ poszczególne pliki bibliotek (w tym komponenty na licencji LGPL)
  można podmienić, po prostu nadpisując pliki w `lib/`.

```bash
# Oba artefakty
./gradlew desktop:build

# Tylko shadow JAR
./gradlew desktop:shadowJar

# Tylko distribution ZIP
./gradlew desktop:distZip
```

Shadow JAR powstaje w `desktop/build/libs/`, a distribution ZIP w
`desktop/build/distributions/`.

### Utrzymanie licencji zależności
```bash
# Odtwórz THIRD-PARTY-NOTICES.md z aktualnych zależności runtime
./gradlew updateThirdPartyNotices

# Wywal build, jeśli pojawi się nowa albo niezatwierdzona rodzina licencji
./gradlew checkLicense
```

`desktop:build` odświeża `THIRD-PARTY-NOTICES.md` automatycznie, a `check`
oraz `build` wywalą się, jeśli plik notices albo reguły dozwolonych licencji
nie będą aktualne.

### Budowanie wersji Android Debug
```bash
# Windows
gradlew.bat android:assembleDebug

# Linux/macOS
./gradlew android:assembleDebug
```

Plik debug APK zostanie utworzony w `android/build/outputs/apk/debug/`.

### Budowanie wersji Android Release
```bash
# Windows
gradlew.bat android:assembleRelease

# Linux/macOS
./gradlew android:assembleRelease
```

Plik release APK zostanie utworzony w `android/build/outputs/apk/release/`.

## Uruchamianie emulatora

### Desktop
Po zbudowaniu:
```bash
java -jar desktop/build/libs/Rex_EMoolator-desktop.jar
```

Lub uruchom bezpośrednio przez Gradle:
```bash
# Windows
gradlew.bat desktop:run

# Linux/macOS
./gradlew desktop:run
```

### Android
Zainstaluj wygenerowany APK na urządzeniu Android i uruchom aplikację.

## Wspierane gry

Emulator obsługuje gry oparte na silniku Piklib/BlooMoo.

### Legenda statusów grywalności
- ![Grywalne](https://img.shields.io/badge/Grywalne-green) Grę daje się ukończyć od początku do końca, bez błędów, które mogą ją zablokować
- ![W grze](https://img.shields.io/badge/W%20grze-yellow) Gra działa, jednak przez różne błędy i niedoróbki nie jest możliwa do ukończenia
- ![W intrze](https://img.shields.io/badge/W%20intrze-orange) Gra ładuje się, jednak nie przechodzi poza intro
- ![Niegrywalne](https://img.shields.io/badge/Niegrywalne-red) Gra nie inicjalizuje się poprawnie, ładuje się z błędem, wywołuje crash emulatora lub daje czarny obraz
- ![Nieznane](https://img.shields.io/badge/Nieznane-lightgrey) Nie zostało jeszcze sprawdzone

Szacowane poziomy grywalności są wyznaczane na podstawie ilości scen, które się odgrywają poprawnie zgodnie z przepływem narzuconym przez skrypty gry do pierwszego momentu, w którym błędy emulatora uniemożliwiają przejście dalej. Nie uwzględniam tutaj drobnych błędów, problemów z animacjami, a jedynie błędy, które powodują, że scena się nie kończy bądź nie zaczyna lub emulator się crashuje. Z racji, iż wymaga to przechodzenia tytułu przynajmniej od momentu, gdzie wszystko działało, a co któryś raz od początku, informacja ta nie będzie aktualizowana na bieżąco. Lista scen oraz ocena ich poprawności działania będzie sukcesywnie tworzona w ramach GitHub Projects. Należy jednak pamiętać, że nowsze części gier korzystały z dedykowanych scen obsługujących cutscenki i minigry, które są znacznie trudniejsze do śledzenia w kodzie niż zwykłe przejścia typu `GOTO`, dlatego proces ten nie będzie natychmiastowy.

### Aktualne statusy gier opartych na silniku Piklib/BlooMoo
<table>
<thead>
<tr>
<th>Nazwa gry</th>
<th>Status</th>
<th>Uwagi</th>
<th>Szacowany poziom grywalności</th>
<th>Link do projektu z trackingiem</th>
</tr>
</thead>
<tbody>
<tr>
<td>Reksio i Skarb Piratów</td>
<td><img alt="Grywalne" src="https://img.shields.io/badge/Grywalne-green"/></td>
<td>Gra w pełni grywalna z drobnymi bugami.</td>
<td>100%</td>
<td><a href="https://github.com/users/patryk025/projects/3">Link</a></td>
</tr>
<tr>
<td>Reksio i Ufo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Gra zdaje się być mniej lub bardziej grywalna od początku do końca, jednak wymagane są jeszcze drobne poprawki oraz przejście gry w sposób manualny. Status zostawiam "W grze" do momentu, aż nie zweryfikuję wszystkiego.</td>
<td>100% z pewnymi bugami</td>
<td><a href="https://github.com/users/patryk025/projects/4">Link</a></td>
</tr>
<tr>
<td>Reksio i Czarodzieje</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Blokada jest na etapie budowy teleportków, nie daje się podnieść przedmiotów.</td>
<td>N/D</td>
<td><a href="https://github.com/users/patryk025/projects/6">Link</a></td>
</tr>
<tr>
<td>Reksio i Wehikuł Czasu</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Wymaga ponownej ewaluacji po refaktoryzacji interpretera</td>
<td>N/D</td>
<td><a href="https://github.com/users/patryk025/projects/8">Link</a></td>
</tr>
<tr>
<td>Reksio i Kapitan Nemo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Intro działa, jednak szuflady zapisów nie działają, jedynie przycisk wyjścia.</td>
<td>N/D</td>
<td><a href="https://github.com/users/patryk025/projects/10">Link</a></td>
</tr>
<tr>
<td>Reksio i Kretes w Akcji!</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Cutscenki prawie działają (czasami się nie pokazują postaci). Minigierki próbują się włączać. Czasami są to problemy z koordynatami, czasami są problemy z przewijaniem i tłami, a przy "Reksio i Ufo" są problemy wynikające z braku implementacji silnika Inertia.</td>
<td>N/D</td>
<td><a href="https://github.com/users/patryk025/projects/11">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Wyprawa po Złote Runo</td>
<td><img alt="Grywalne" src="https://img.shields.io/badge/Grywalne-green"/></td>
<td>Wszystkie minigierki są grywalne.</td>
<td>100%</td>
<td><a href="https://github.com/users/patryk025/projects/12">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Wojna Trojańska</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Wyścigi rydwanów nie działają, prawdopodobnie jakieś problemy z obsługą silnika fizycznego (reimplementacja wciąż wymaga pewnych poprawek)</td>
<td>93,75% (15/16 scen grywalne)</td>
<td><a href="https://github.com/users/patryk025/projects/13">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Przygody Odyseusza</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Minigierka ze zbieraniem jedzenia wydaje się działać, ale mam wątpliwości, bo chyba nie resetuje stanu między restartami gry. Quiz się zawiesza i pytania nie są czytane. Rajd statkiem nie działa poprawnie, nie ma przeszkód i wrogów. Cutscenka do rajdu zawiesza się po kliknięciu myszką. Selektor poziomu do rajdu nie działa, dodatkowo crashuje się po wybraniu go ręcznie z selektora scen. Wprowadzenie do minigierki z toporami ma jakąś nieskończoną pętlę, zaś selektor poziomów nie wyświetla poziomów.</td>
<td>58,82% (10/17 scen grywalne)</td>
<td><a href="https://github.com/users/patryk025/projects/14">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Herkules</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Minigierki są w mniejszym lub większym stopniu zabugowane.</td>
<td>N/D</td>
<td><a href="https://github.com/users/patryk025/projects/15">Link</a></td>
</tr>
<tr>
<td>Tezeusz i Nić Ariadny</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W%20grze-yellow"/></td>
<td>Minigierki są w mniejszym lub większym stopniu zabugowane.</td>
<td>N/D</td>
<td><a href="https://github.com/users/patryk025/projects/17">Link</a></td>
</tr>
</tbody>
</table>

## Garść screenshotów

Zrzuty są zwinięte per gra — rozwiń tę, która Cię interesuje.

<details>
<summary><b>Reksio i Skarb Piratów</b></summary>

<img src="images/risp.jpg" alt="Screen z próby uruchomienia Reksio i Skarb Piratów"/>
<br/><em>Screen z próby uruchomienia Reksio i Skarb Piratów</em>

</details>

<details>
<summary><b>Reksio i Ufo</b></summary>

<img src="images/riu.jpg" alt="Screen z próby uruchomienia Reksio i Ufo"/>
<br/><em>Screen z próby uruchomienia Reksio i Ufo</em>

</details>

<details>
<summary><b>Reksio i Czarodzieje</b></summary>

<img src="images/ric.jpg" alt="Screen z próby uruchomienia Reksio i Czarodzieje"/>
<br/><em>Screen z próby uruchomienia Reksio i Czarodzieje</em>

</details>

<details>
<summary><b>Reksio i Wehikuł Czasu</b></summary>

<img src="images/riwc.jpg" alt="Screen z próby uruchomienia Reksio i Wehikuł Czasu"/>
<br/><em>Screen z próby uruchomienia Reksio i Wehikuł Czasu</em>

</details>

<details>
<summary><b>Reksio i Kapitan Nemo</b></summary>

<table>
<tr>
<td width="50%" valign="top"><img src="images/rikn_menu.jpg" alt="Menu gry Reksio i Kapitan Nemo" width="100%"/><br/><em>Menu gry Reksio i Kapitan Nemo</em></td>
<td width="50%" valign="top"><img src="images/rikn_gra_intro.jpg" alt="Intro po kliknięciu szuflady" width="100%"/><br/><em>Intro po kliknięciu szuflady</em></td>
</tr>
</table>

</details>

<details>
<summary><b>Reksio i Kretes w Akcji</b></summary>

<table>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_intro.jpg" alt="Intro gry Reksio i Kretes w Akcji" width="100%"/><br/><em>Intro gry Reksio i Kretes w Akcji</em></td>
<td width="50%" valign="top"><img src="images/rikwa_menu.jpg" alt="Menu gry Reksio i Kretes w Akcji" width="100%"/><br/><em>Menu gry Reksio i Kretes w Akcji</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_risp_intro.jpg" alt="Intro gry Reksio i Skarb Piratów" width="100%"/><br/><em>Intro gry Reksio i Skarb Piratów</em></td>
<td width="50%" valign="top"><img src="images/rikwa_risp_gra.jpg" alt="Gra Reksio i Skarb Piratów" width="100%"/><br/><em>Gra Reksio i Skarb Piratów</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_riu_intro.jpg" alt="Intro gry Reksio i Ufo" width="100%"/><br/><em>Intro gry Reksio i Ufo</em></td>
<td width="50%" valign="top"><img src="images/rikwa_riu_gra.jpg" alt="Gra Reksio i Ufo" width="100%"/><br/><em>Gra Reksio i Ufo</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_ric_intro1.jpg" alt="Intro gry Reksio i Czarodzieje" width="100%"/><br/><em>Intro gry Reksio i Czarodzieje</em></td>
<td width="50%" valign="top"><img src="images/rikwa_ric_intro2.jpg" alt="Intro gry Reksio i Czarodzieje" width="100%"/><br/><em>Intro gry Reksio i Czarodzieje</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_ric_gra.jpg" alt="Gra Reksio i Czarodzieje" width="100%"/><br/><em>Gra Reksio i Czarodzieje</em></td>
<td width="50%" valign="top"><img src="images/rikwa_riwc_menu.jpg" alt="Menu gry Reksio i Wehikuł Czasu" width="100%"/><br/><em>Menu gry Reksio i Wehikuł Czasu</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_riwc_gra.png" alt="Gra Reksio i Wehikuł Czasu na moment przed wywaleniem się" width="100%"/><br/><em>Gra Reksio i Wehikuł Czasu na moment przed wywaleniem się</em></td>
<td width="50%" valign="top"><img src="images/rikwa_shikn_intro.jpg" alt="Intro gry Super Heros i Kapitan Nemo" width="100%"/><br/><em>Intro gry Super Heros i Kapitan Nemo</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_shikn_gra.jpg" alt="Gra Super Heros i Kapitan Nemo" width="100%"/><br/><em>Gra Super Heros i Kapitan Nemo</em></td>
<td width="50%"></td>
</tr>
</table>

</details>

<details>
<summary><b>Poznaj Mity: Wyprawa po Złote Runo</b></summary>

<img src="images/wpzr.jpg" alt="Screen z próby uruchomienia Wyprawy po Złote Runo"/>
<br/><em>Screen z próby uruchomienia Wyprawy po Złote Runo</em>

</details>

<details>
<summary><b>Poznaj Mity: Wojna Trojańska</b></summary>

<img src="images/wt.jpg" alt="Screen z próby uruchomienia Wojny Trojańskiej"/>
<br/><em>Screen z próby uruchomienia Wojny Trojańskiej</em>

</details>

<details>
<summary><b>Poznaj Mity: Przygody Odyseusza</b></summary>

<img src="images/po.jpg" alt="Screen z próby uruchomienia Przygód Odyseusza"/>
<br/><em>Screen z próby uruchomienia Przygód Odyseusza</em>

</details>

<details>
<summary><b>Poznaj Mity: Herkules</b></summary>

<img src="images/herc.jpg" alt="Screen z próby uruchomienia Herkulesa"/>
<br/><em>Screen z próby uruchomienia Herkulesa</em>

</details>

## Licencja

Sam Rex-EMoolator jest projektem otwarto-źródłowym udostępnianym na
**licencji MIT**. Pełną treść licencji znajdziesz w pliku [`LICENSE`](LICENSE).

Projekt korzysta z różnych bibliotek (m.in. libGDX, LWJGL czy ODE4J), z których każda
posiada własną licencję. Pełną listę tych bibliotek wraz z ich licencjami
znajdziesz w pliku [`THIRD-PARTY-NOTICES.md`](THIRD-PARTY-NOTICES.md).

Jeśli interesuje Cię, jak dbamy o zgodność z licencjami LGPL (np. w przypadku
bibliotek JLayer czy JOrbis), wszystkie szczegóły techniczne również opisaliśmy
w tym pliku.

Licencja MIT dotyczy kodu źródłowego emulatora znajdującego się w tym
repozytorium. Dane gier, mody, fanowskie tłumaczenia, patche i inne treści
ładowane przez emulator są odrębnymi utworami i podlegają własnym licencjom,
zgodom oraz ograniczeniom prawnoautorskim.

### Kilka słów o materiałach z gier

Ten emulator powstał w celach edukacyjnych, archiwizacyjnych i związanych z
interoperacyjnością. Możesz używać go z oryginalnymi plikami gier, własnymi
kopiami, fanowskimi modami, tłumaczeniami, patchami i innymi kompatybilnymi
zasobami, o ile masz prawo legalnie z nich korzystać.

Nie rozpowszechniamy grafik, muzyki ani innych danych chronionych prawem
autorskim z oryginalnych gier. Katalog `assets/` zawiera jedynie minimalne,
techniczne fragmenty używane do testów automatycznych, by mieć pewność, że
silnik działa poprawnie.

Rex-EMoolator jest niezależnym projektem i nie jest w żaden sposób powiązany z oryginalnymi twórcami czy właścicielami praw do
gier. Emulator jest udostępniany w dobrej wierze, „tak jak jest”, bez jakichkolwiek gwarancji, jako narzędzie badawcze i dokumentacyjne.
