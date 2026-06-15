# Rex-EMoolator

Nieoficjalna dokumentacja silników skryptowych **Piklib** oraz **BlooMoo**, używanych w serii gier *Przygody Reksia*, oraz emulatora **Rex-EMoolator**.

## Mapa dokumentacji

<div class="grid cards" markdown>

-   :material-code-braces:{ .lg .middle } __Silnik__

    ---

    Język skryptowy widziany przez autora treści: składnia, arytmetyka, zdarzenia i sygnały, zmienne globalne oraz niestandardowe zachowania.

    [:octicons-arrow-right-24: Silnik](engine/index.md)

-   :material-engine-outline:{ .lg .middle } __Wnętrze silnika__

    ---

    Jak silnik działa pod spodem: pętla i zegar, renderowanie (dziś i w oryginale), system animacji oraz czas i timery.

    [:octicons-arrow-right-24: Wnętrze silnika](internals/index.md)

-   :material-file-tree:{ .lg .middle } __Formaty plików__

    ---

    Budowa plików bajt po bajcie: animacje (`ANN`), obrazy (`IMG`), czcionki (`FNT`), tablice (`ARR`), dane (`DTA`) i więcej.

    [:octicons-arrow-right-24: Formaty plików](formats/index.md)

-   :material-format-list-bulleted-type:{ .lg .middle } __Referencja typów__

    ---

    Alfabetyczny spis ~45 typów dostępnych w skryptach wraz z polami, metodami i sygnałami każdego z nich.

    [:octicons-arrow-right-24: Referencja typów](reference/index.md)

</div>

## Czego można się tu dowiedzieć

- jak zbudowany jest język skryptowy silnika i jak wykonuje on logikę gry,
- jakie typy danych i obiekty są dostępne w skryptach oraz jakie udostępniają metody, pola i sygnały,
- jak działa renderowanie, animacja i czas w silniku — i czym różniły się od oryginału,
- jak zakodowane są zasoby gry na dysku (formaty plików).

## Status

Dokumentacja jest cały czas uzupełniana. Część informacji powstaje na podstawie analizy oryginalnych skryptów oraz reverse-engineeringu silnika; tam, gdzie zachowanie nie zostało jeszcze potwierdzone, znajdują się odpowiednie adnotacje. Opisy techniczne są w miarę możliwości zestawiane z faktyczną implementacją emulatora.
