# Silnik

**Piklib** (później **BlooMoo**) to 32-bitowy silnik graficzny stworzony przez firmę Aidem Media na potrzeby polskich gier z lat 2000. Niniejsza dokumentacja opisuje wewnętrzną logikę silnika i sposób, w jaki wykonuje on skrypty gry.

## Czego dotyczy ta dokumentacja

Dokumentacja koncentruje się na **języku skryptowym** silnika i **modelu wykonania** widzianym z poziomu skryptów — czyli na tym, co programista treści gry musi wiedzieć, żeby zrozumieć działanie istniejących skryptów lub pisać własne.

Nie jest to dokumentacja kodu źródłowego silnika ani pełna specyfikacja wszystkich struktur danych; są to obszary, które będą uzupełniane stopniowo.

## Struktura

Dokumentacja silnika podzielona jest na pięć rozdziałów:

- [Skrypty](scripts.md) — składnia skryptów, parser, kolejność wczytywania i inicjalizacji obiektów.
- [Arytmetyka](arithmetic.md) — wyrażenia obliczeniowe, operatory i reguły konwersji między typami prymitywnymi.
- [Zdarzenia i sygnały](events.md) — model reaktywny silnika, podłączanie obsługi, propagacja przez drzewo wywołań.
- [Zmienne globalne](globals.md) — wbudowane obiekty (`MOUSE`, `KEYBOARD`, `RAND`, `SYSTEM`), zmienne niejawne (`_I_`, `THIS`, `$N`) i specjalne procedury.
- [Dziwactwa silnika](quirks.md) — niestandardowe zachowania, które łatwo przeoczyć.

Pełną listę dostępnych typów danych zawiera [Referencja typów](../reference/index.md).

## Gry wykorzystujące silnik

Lista jest niekompletna i będzie uzupełniana w miarę identyfikowania kolejnych tytułów.

| Gra                                                      | Wersja silnika                       |
|----------------------------------------------------------|--------------------------------------|
| **Przygody Reksia**                                      |                                      |
| Reksio i Skarb Piratów                                   | Piklib 6.1, Piklib 7, Piklib 8       |
| Reksio i Ufo                                             | Piklib 7.1, Piklib 8                 |
| Reksio i Czarodzieje                                     | Piklib 8                             |
| Reksio i Wehikuł Czasu                                   | Piklib 8                             |
| Reksio i Kapitan Nemo                                    | BlooMoo                              |
| Reksio i Kretes w Akcji                                  | BlooMoo                              |
| **Poznaj Mity**                                          |                                      |
| Wyprawa po Złote Runo                                    | Piklib 7.1                           |
| Wojna Trojańska                                          | Piklib 7.2                           |
| Przygody Odyseusza                                       | Piklib 8                             |
| Herkules                                                 | Piklib 8                             |
| Tezeusz i nić Ariadny                                    | Piklib 8                             |
| **Bolek i Lolek**                                        |                                      |
| Bolek i Lolek na tropie zaginionej księgi ortografii     | Piklib 3.0                           |
| Bolek i Lolek. Alfabet i nauka czytania                  | BlooMoo                              |
| Bolek i Lolek: Język angielski dla dzieci                | Piklib 8                             |
| Bolek i Lolek: Język niemiecki dla najmłodszych          | BlooMoo                              |
| Bolek i Lolek: Moje pierwsze literki                     | BlooMoo                              |
| Bolek i Lolek: Moje pierwsze studio plastyczne           | BlooMoo                              |
| Bolek i Lolek: Olimpiada Letnia                          | Piklib 8                             |
| Bolek i Lolek: Zwariowana Olimpiada                      | Piklib 8                             |
| Wesołe przedszkole Bolka i Lolka                         | Piklib 8                             |
| **Król Maciuś Pierwszy**                                 |                                      |
| Król Maciuś Pierwszy. Przedszkole, zabawy z przyjaciółmi | BlooMoo                              |
| Król Maciuś Pierwszy. Wesołe miasteczko                  | BlooMooWeb                           |
| Król Maciuś Pierwszy. Wyspa Togo-Pogo                    | BlooMoo                              |
| **Komputerowy świat bajek** / **Gry-Bajki**              |                                      |
| Aladyn                                                   | Piklib 6.1                           |
| Pinokio                                                  | Piklib 6.1                           |
| Piotruś Pan                                              | Piklib 6.1                           |
| Ali Baba i czterdziestu rozbójników                      | Piklib 8 (ale config to BlooMoo.ini) |
| Robinson Crusoe                                          | Piklib 6.1                           |
| Sindbad                                                  | Piklib 6.1                           |
| Robin Hood                                               | Piklib 6.1                           |
| Księga Dżungli                                           | Piklib 6.1                           |
| Doktor Dolittle                                          | Piklib 6.1                           |
| **Gry dla dziewczynek**                                  |                                      |
| Piękna i Bestia                                          | BlooMoo                              |
| Roszpunka                                                | BlooMoo                              |
| **Koziołek Matołek**                                     |                                      |
| Szkoła Koziołka Matołka                                  | Piklib 7.2                           |
| Wesołe przedszkole Koziołka Matołka                      | Piklib 8                             |
| **Edukacja XXI wieku**                                   |                                      |
| Matematyka: Dodawanie i odejmowanie                      | Piklib 4.1                           |
| Alfabet. Nauka czytania i pisania                        | Piklib 4.1                           |
| **Inne gry z Reksiem**                                   |                                      |
| ABC z Reksiem                                            | Piklib 4.1, RPiklib (?)              |
| Liczę z Reksiem                                          | Piklib 6.0                           |
| Reksio i Ortografia                                      | Piklib 7.2                           |
| Wesołe Przedszkole Reksia                                | Piklib 8                             |
| **Pozostałe tytuły**                                     |                                      |
| Wielki Teleturniej Smoka Wawelskiego                     | Piklib 5.0                           |
