# Minigierki

Gry Aidem Media przeplatają zwykłe sceny **minigrami** — samodzielnymi wstawkami z własną logiką, sterowaniem i warunkami zwycięstwa, napisanymi w całości w skryptach silnika. Ten dział rozbiera je od środka: jak zbierają wejście gracza, jak rozpoznają jego intencję i jak wywołują efekty.

W odróżnieniu od działów [Silnik](../engine/index.md) i [Wnętrze silnika](../internals/index.md), które opisują mechanizmy ogólne, tu patrzymy na **konkretne sceny** jako studia przypadku — z odwołaniami do rzeczywistych plików `.cnv` i danych na dysku.

!!! note "Dział w budowie"
    Na razie opisana jest jedna minigra (pojedynek zaklęć). Struktura jest celowo otwarta — kolejne minigry dopisujemy jako osobne strony według tego samego schematu: *przegląd → wejście → rozpoznanie → efekty*.

## Rozdziały

<div class="grid cards" markdown>

-   :material-auto-fix:{ .lg .middle } __Pojedynek zaklęć__

    ---

    Zaklęcia „rysowane" kursorem w powietrzu: jak gest zamieniany jest w sekwencję kątów i dopasowywany do wzorców oraz katalog zaklęć.

    [:octicons-arrow-right-24: Czytaj](magic.md)

</div>

## Zobacz też

- [Format ARR](../formats/ARR.md) — binarny zapis tablic, w tym punktowych „śladów" gestów.
- [Współrzędne i kotwice](../internals/coordinates.md) — układ kanwy 800×600 i oś Y skierowana w dół, kluczowe dla interpretacji kątów gestu.
- [Referencja typów](../reference/index.md) — obiekty (`ARRAY`, `MOUSE`, `BEHAVIOUR`) używane przez logikę minigier.
