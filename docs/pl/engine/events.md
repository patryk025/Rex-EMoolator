# Zdarzenia i sygnały

Silnik Piklib/BlooMoo udostępnia reaktywny model sterowania logiką gry oparty o sygnały. Każdy obiekt może emitować nazwane sygnały, do których w skryptach można podłączyć procedury lub bloki kodu. Niniejszy rozdział opisuje, jak działa ten mechanizm i jakie typowe sygnały są dostępne.

## Podłączanie obsługi sygnału

Sygnał obsługiwany jest analogicznie do dowolnej innej właściwości obiektu — w miejscu wartości podaje się nazwę procedury albo blok kodu:

```
NAZWA_OBIEKTU:ONCHANGED=NAZWA_PROCEDURY
NAZWA_OBIEKTU:ONCHANGED={NAZWA_INNEGO_OBIEKTU^PLAY("TADA");}
```

W ramach jednego obiektu można podłączyć po jednej obsłudze do każdej nazwy sygnału.

## Sygnały parametryzowane {#sygnaly-parametryzowane}

Niektóre sygnały, w szczególności [`ONCHANGED`](#onchanged) oraz [`ONBRUTALCHANGED`](#onbrutalchanged), są emitowane razem z wartością. Wartość ta może być wykorzystana do filtrowania — silnik najpierw szuka obsługi dopasowanej do pary `sygnał + wartość`, a dopiero w przypadku jej braku trafia do uniwersalnej obsługi dla samej nazwy sygnału.

Dyskryminator zapisuje się po nazwie sygnału, oddzielony znakiem daszka `^`:

```
NAZWA_OBIEKTU:ONBRUTALCHANGED^3=OBSLUGA_DLA_TROJKI
NAZWA_OBIEKTU:ONBRUTALCHANGED=OBSLUGA_OGOLNA
```

Dla powyższego: wywołanie `OBIEKT^SET(3)` uruchomi `OBSLUGA_DLA_TROJKI`; dla każdej innej wartości — `OBSLUGA_OGOLNA`.

## Wykonywanie obsługi

System sygnałów jest synchroniczny i jednowątkowy. Emisja sygnału natychmiast przekazuje sterowanie do obsługi: bieżąca procedura jest wstrzymywana, wykonywana jest obsługa sygnału, a po jej zakończeniu sterowanie wraca do miejsca, z którego sygnał został wyemitowany. Sygnały nie są kolejkowane.

Sekwencja zagnieżdżonych wywołań sygnał–procedura–sygnał–procedura tworzy drzewo wywołań. Operatory skoku oddziałują na to drzewo w następujący sposób:

- [`@ONEBREAK`](scripts.md#operatory-skoku) — przerywa wyłącznie bieżącą procedurę i wraca do wywołującego.
- [`@BREAK`](scripts.md#operatory-skoku) — przerywa całe drzewo wywołań rozpoczęte przez pierwotną emisję sygnału.

## Argumenty sygnału

Niektóre sygnały emitowane są z dodatkowymi argumentami. Wewnątrz obsługi sygnału argumenty te są dostępne tak samo jak w procedurze — przez `$1`, `$2`, … (numeracja od `1`). Pozwala to obsłudze reagować na konkretną wartość, która wywołała sygnał.

## Sygnały uniwersalne

### ONINIT

Emitowany podczas inicjalizacji zmiennej w trakcie wczytywania jej pliku. Kolejność emisji nie jest przypadkowa — zmienne są inicjalizowane w stałej kolejności typów; szczegóły opisano w sekcji [Inicjalizacja zmiennych](scripts.md#inicjalizacja-zmiennych).

Typowe zastosowania: inicjalizacja [tablic](../reference/index.md), ustawienie początkowego stanu animacji, wczytanie danych z plików zewnętrznych.

### ONSIGNAL

Sygnał ogólnego przeznaczenia, emitowany przez wywołanie globalnej metody `SEND` na rzecz obiektu:

```
NAZWA_OBIEKTU^SEND("MOJ_SYGNAL");
```

Przesłany ciąg znaków jest dostępny w obsłudze sygnału jako pierwszy argument (`$1`). Mechanizm pozwala definiować własne zdarzenia bez konieczności reagowania na zmiany wartości lub zdarzenia animacji.

## Sygnały zmiany wartości

Emitowane są przez typy [prymitywne](../reference/index.md) (oraz inne typy z polem `VALUE`) przy każdej modyfikacji wartości.

### ONCHANGED

Emitowany wyłącznie wtedy, gdy nowa wartość różni się od poprzedniej. Argumentem (`$1`) jest nowa wartość.

### ONBRUTALCHANGED

Emitowany przy każdej modyfikacji wartości, nawet jeżeli nowa wartość jest taka sama jak poprzednia. Argumentem (`$1`) jest nowa wartość.

W praktyce `ONBRUTALCHANGED` przydaje się do wykrywania samego faktu wywołania metody zmieniającej wartość (`SET`, `INC`, `SWITCH`, …), niezależnie od tego, czy wartość faktycznie się zmieniła.

## Sygnały obiektów graficznych i sekwencji

### ONSTARTED

Emitowany po rozpoczęciu odtwarzania animacji lub sekwencji.

- Dla **sekwencji** — emitowany raz, z nazwą bieżącego eventu z pliku `.SEQ` jako argumentem.
- Dla **animacji** — emitowany z nazwą eventu z pliku `.ANN`. Jeżeli animacja sterowana jest przez sekwencję, sygnał może być emitowany wielokrotnie ze względu na zapętlanie.

### ONFINISHED

Emitowany po zakończeniu odtwarzania:

- dla **animacji** — po wyświetleniu ostatniej klatki i zatrzymaniu odtwarzania,
- dla **sekwencji** — po zakończeniu ostatniego eventu (lub dla każdego eventu odtwarzanego po kolei, w zależności od konfiguracji).

W obu przypadkach sygnał jest również emitowany w odpowiedzi na ręczne wywołanie metody `STOP` (bez argumentu lub z `TRUE`).
