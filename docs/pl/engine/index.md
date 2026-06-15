# Silnik

**Piklib** (później **BlooMoo**) to 32-bitowy silnik graficzny stworzony przez firmę Aidem Media na potrzeby polskich gier przygodowych z lat 2000. Niniejsza dokumentacja opisuje wewnętrzną logikę silnika i sposób, w jaki wykonuje on skrypty gry.

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

| Gra | Wersja silnika |
|---|---|
| Reksio i Skarb Piratów | Piklib 8 |
| Reksio i Ufo | Piklib 7.1, Piklib 8 |
| Reksio i Czarodzieje | Piklib 8 |
| Reksio i Wehikuł Czasu | Piklib 8 |
| Reksio i Kapitan Nemo | BlooMoo |
| Reksio i Kretes w Akcji | BlooMoo |
| Poznaj Mity: Wyprawa po Złote Runo | Piklib 7.1 |
| Poznaj Mity: Wojna Trojańska | Piklib 7.2 |
| Poznaj Mity: Przygody Odyseusza | Piklib 8 |
| Poznaj Mity: Herkules | Piklib 8 |
