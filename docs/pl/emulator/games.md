# Stan gier

Emulator celuje w gry oparte na silnikach Piklib i BlooMoo. Poniżej stan tych, które są śledzone na bieżąco. Pełną listę znanych tytułów i wersji silnika zawiera rozdział [Silnik](../engine/index.md#gry-wykorzystujace-silnik).

## Legenda

| Status | Znaczenie |
|---|---|
| ![Grywalne](https://img.shields.io/badge/Grywalne-green) | Grę daje się ukończyć od początku do końca, bez błędów, które mogą ją zablokować. |
| ![W grze](https://img.shields.io/badge/W%20grze-yellow) | Gra działa, jednak przez różne błędy i niedoróbki nie jest możliwa do ukończenia. |
| ![W intrze](https://img.shields.io/badge/W%20intrze-orange) | Gra ładuje się, jednak nie przechodzi poza intro. |
| ![Niegrywalne](https://img.shields.io/badge/Niegrywalne-red) | Gra nie inicjalizuje się poprawnie, ładuje się z błędem, wywołuje crash emulatora lub daje czarny obraz. |
| ![Nieznane](https://img.shields.io/badge/Nieznane-lightgrey) | Nie zostało jeszcze sprawdzone. |

## Aktualny stan

Uwagi mogą być nieaktualne: bieżące testy scen prowadzone są w GitHub Projects, a kolumna „Zmiana wpisu” mówi tylko, kiedy ostatnio zmieniono wiersz, a nie kiedy grę sprawdzono.

| Gra | Status | Grywalność | Uwagi | Zmiana wpisu | Tracking |
|---|---|---|---|---|---|
| Reksio i Skarb Piratów | ![Grywalne](https://img.shields.io/badge/Grywalne-green) | 100% | Gra w pełni grywalna z drobnymi bugami. | 2026-04-27 | [Projekt](https://github.com/users/patryk025/projects/3) |
| Reksio i UFO | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | 100% z pewnymi bugami | Gra zdaje się być mniej lub bardziej grywalna od początku do końca, jednak wymagane są jeszcze drobne poprawki oraz przejście gry w sposób manualny. Status zostawiam „W grze” do momentu, aż nie zweryfikuję wszystkiego. | 2026-06-08 | [Projekt](https://github.com/users/patryk025/projects/4) |
| Reksio i Czarodzieje | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | N/D | Blokada jest na etapie budowy teleportków, nie daje się podnieść przedmiotów. | 2026-06-26 | [Projekt](https://github.com/users/patryk025/projects/6) |
| Reksio i Wehikuł Czasu | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | N/D | Wymaga ponownej ewaluacji po refaktoryzacji interpretera. | 2026-04-27 | [Projekt](https://github.com/users/patryk025/projects/8) |
| Reksio i Kapitan Nemo | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | N/D | Intro działa, jednak szuflady zapisów nie działają, jedynie przycisk wyjścia. | 2026-07-25 | [Projekt](https://github.com/users/patryk025/projects/10) |
| Reksio i Kretes w Akcji! | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | N/D | Cutscenki prawie działają (czasami się nie pokazują postaci). Minigierki próbują się włączać. Czasami są to problemy z koordynatami, czasami są problemy z przewijaniem i tłami, a przy „Reksio i UFO” są problemy wynikające z braku implementacji silnika Inertia. | 2026-07-25 | [Projekt](https://github.com/users/patryk025/projects/11) |
| Poznaj Mity: Wyprawa po Złote Runo | ![Grywalne](https://img.shields.io/badge/Grywalne-green) | 100% | Wszystkie minigierki są grywalne. | 2026-09-05 | [Projekt](https://github.com/users/patryk025/projects/12) |
| Poznaj Mity: Wojna Trojańska | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | 93,75% (15/16 scen) | Wyścigi rydwanów nie działają, prawdopodobnie jakieś problemy z obsługą silnika fizycznego (reimplementacja wciąż wymaga pewnych poprawek). | 2026-09-05 | [Projekt](https://github.com/users/patryk025/projects/13) |
| Poznaj Mity: Przygody Odyseusza | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | 58,82% (10/17 scen) | Minigierka ze zbieraniem jedzenia wydaje się działać, ale mam wątpliwości, bo chyba nie resetuje stanu między restartami gry. Quiz się zawiesza i pytania nie są czytane. Rajd statkiem nie działa poprawnie, nie ma przeszkód i wrogów. Cutscenka do rajdu zawiesza się po kliknięciu myszką. Selektor poziomu do rajdu nie działa, dodatkowo crashuje się po wybraniu go ręcznie z selektora scen. Wprowadzenie do minigierki z toporami ma jakąś nieskończoną pętlę, zaś selektor poziomów nie wyświetla poziomów. | 2026-09-05 | [Projekt](https://github.com/users/patryk025/projects/14) |
| Poznaj Mity: Herkules | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | N/D | Minigierki są w mniejszym lub większym stopniu zabugowane. | 2026-06-26 | [Projekt](https://github.com/users/patryk025/projects/15) |
| Tezeusz i Nić Ariadny | ![W grze](https://img.shields.io/badge/W%20grze-yellow) | N/D | Minigierki są w mniejszym lub większym stopniu zabugowane. | 2026-06-26 | [Projekt](https://github.com/users/patryk025/projects/17) |

## Jak liczona jest grywalność

Szacowane poziomy grywalności są wyznaczane na podstawie liczby scen, które odgrywają się poprawnie zgodnie z przepływem narzuconym przez skrypty gry, do pierwszego momentu, w którym błędy emulatora uniemożliwiają przejście dalej. Nie uwzględniam tutaj drobnych błędów ani problemów z animacjami, a jedynie błędy, które powodują, że scena się nie kończy bądź nie zaczyna lub emulator się crashuje.

Wymaga to przechodzenia tytułu przynajmniej od momentu, w którym wszystko działało, a co któryś raz od początku, więc informacja ta nie będzie aktualizowana na bieżąco. Lista scen oraz ocena ich poprawności działania będzie sukcesywnie tworzona w ramach GitHub Projects. Nowsze części gier korzystały jednak z dedykowanych scen obsługujących cutscenki i minigry, które są znacznie trudniejsze do śledzenia w kodzie niż zwykłe przejścia typu `GOTO`, dlatego proces ten nie będzie natychmiastowy.

Jak gry wyglądają w praktyce, pokazują [zrzuty ekranu](screenshots.md).
