# Referencja typów

Spis typów danych dostępnych w skryptach silnika Piklib/BlooMoo, pogrupowanych tematycznie.

## Kompatybilność

Pod opisem każdej metody znajduje się lista bibliotek, w których wykrył ją automatyczny porównywacz API. Zapis `PIKLIB8.DLL (5/10 wariantów)` oznacza, że metoda występuje w pięciu z dziesięciu rozpoznanych wariantów tej biblioteki. API doładowywane przez `CMC_ExternObject`, takie jak `WORLD` i `INERTIA`, nie jest widoczne w tym eksporcie i jest oznaczone osobno. Pełna lista luk oraz ograniczeń jest w [`compatibility-audit.md`](../../compatibility-audit.md).

## Typy używane w skryptach

### Prymitywne

- [BOOL](BOOL.md) — wartość logiczna.
- [DOUBLE](DOUBLE.md) — liczba zmiennoprzecinkowa o podwójnej precyzji.
- [INTEGER](INTEGER.md) — liczba całkowita ze znakiem.
- [STRING](STRING.md) — ciąg znaków.

### Kolekcje

- [ARRAY](ARRAY.md) — tablica jednowymiarowa.
- [MULTIARRAY](MULTIARRAY.md) — tablica wielowymiarowa z automatycznym rozszerzaniem.

### Warunki logiczne

- [CONDITION](CONDITION.md) — porównanie dwóch operandów.
- [COMPLEXCONDITION](COMPLEXCONDITION.md) — kombinacja dwóch warunków operatorem `AND`/`OR`.

### Struktura kodu

- [BEHAVIOUR](BEHAVIOUR.md) — procedura.
- [CLASS](CLASS.md) — definicja klasy obiektów.

### Sceniczne

- [APPLICATION](APPLICATION.md) — najwyższy poziom hierarchii skryptów.
- [EPISODE](EPISODE.md) — logiczny segment gry.
- [SCENE](SCENE.md) — pojedyncza scena.

### Interakcja i kompozycja

- [BUTTON](BUTTON.md) — interaktywny przycisk z trzema stanami wizualnymi.
- [CANVAS_OBSERVER](CANVAS_OBSERVER.md) — operacje na kanwie i tle.
- [CNVLOADER](CNVLOADER.md) — dynamiczne ładowanie plików `.CNV`.
- [GROUP](GROUP.md) — grupa zmiennych z delegowanymi wywołaniami metod.
- [PATTERN](PATTERN.md) — wielowarstwowa plansza kafelkowa.
- [STATICFILTER](STATICFILTER.md) — filtr graficzny (rotacja, skalowanie, blur).
- [VIRTUALGRAPHICSOBJECT](VIRTUALGRAPHICSOBJECT.md) — wirtualny obiekt graficzny.

### Dane

- [DATABASE](DATABASE.md) — baza danych z kursorem.

### Fizyka 3D

- [WORLD](WORLD.md) — interfejs 3D silnika fizycznego opartego na ODE.

### Wbudowane obiekty I/O

- [KEYBOARD](KEYBOARD.md) — stan klawiatury.
- [MOUSE](MOUSE.md) — stan myszy.
- [RAND](RAND.md) — generator liczb pseudolosowych.
- [SYSTEM](SYSTEM.md) — informacje systemowe.

### Media

- [ANIMO](ANIMO.md) — animacja z pliku `.ANN`.
- [FONT](FONT.md) — definicja czcionki bitmapowej.
- [IMAGE](IMAGE.md) — statyczny obraz.
- [SEQUENCE](SEQUENCE.md) — sekwencja animacji z synchronizowanym dźwiękiem.
- [SOUND](SOUND.md) — krótki efekt dźwiękowy.
- [TEXT](TEXT.md) — tekst wyświetlany na ekranie.

### Matematyczne i narzędziowe

- [EXPRESSION](EXPRESSION.md) — wyrażenie arytmetyczne dwuargumentowe.
- [INERTIA](INERTIA.md) — interfejs wbudowanego silnika fizycznego 2D.
- [MATRIX](MATRIX.md) — siatka pól z systemem fizyki kamieni.
- [STRUCT](STRUCT.md) — struktura danych z nazwanymi polami.
- [TIMER](TIMER.md) — cykliczny licznik czasu.
- [VECTOR](VECTOR.md) — N-wymiarowy wektor liczb zmiennoprzecinkowych.
