# Skrypty

Silnik Piklib/BlooMoo wykonuje logikę gry interpretując skrypty tekstowe. W tym rozdziale opisana jest składnia tych skryptów, sposób w jaki silnik je wczytuje oraz kolejność inicjalizacji obiektów.

## Format plików

Skrypty zapisywane są w plikach o rozszerzeniach `.CNV`, `.DEF`, `.CLASS` oraz `.SEQ`. Wszystkie mają tę samą podstawową strukturę tekstową — różnią się jedynie kontekstem użycia.

Silnik traktuje cały kod jak wielkie litery i nie rozróżnia ich wielkości. Konwencjonalnie skrypty zapisuje się wielkimi literami.

### Szyfrowanie

Pliki dystrybuowane z grą są domyślnie zaszyfrowane szyfrem przestawieniowym o zmiennym przesunięciu. Plik zaszyfrowany rozpoczyna się nagłówkiem postaci:

```
{<X:N>}
```

gdzie `X` to litera określająca kierunek przesunięcia (`D` oznacza przesunięcie ujemne), a `N` to wartość przesunięcia. Silnik wykrywa ten nagłówek automatycznie i odszyfrowuje resztę pliku przed parsowaniem. Pliki nieszyfrowane (bez tego nagłówka) są wczytywane bezpośrednio.

## Deklaracja obiektów

Obiekt zaczyna się od linii ze słowem kluczowym `OBJECT`:

```
OBJECT=NAZWA_OBIEKTU
```

Linie pomiędzy kolejnymi `OBJECT=` definiują właściwości aktualnego obiektu. Definicja obiektu trwa do końca pliku lub do napotkania kolejnej linii `OBJECT=`.

Jeżeli ten sam obiekt zostanie zadeklarowany ponownie w tym samym pliku, jego właściwości zostaną scalone — nowsze wpisy nadpisują wcześniejsze.

## Właściwości obiektów

Właściwości zapisuje się po nazwie obiektu i znaku dwukropka:

```
NAZWA_OBIEKTU:WLASCIWOSC=WARTOSC
```

Sygnały mogą przyjmować dodatkowy parametr po znaku daszka `^`:

```
NAZWA_OBIEKTU:ONBRUTALCHANGED^3=NAZWA_PROCEDURY
```

W obu przypadkach silnik akceptuje wokół znaku `=` zarówno brak spacji (`KLUCZ=WARTOSC`), jak i spacje po obu stronach (`KLUCZ = WARTOSC`).

## Typ zmiennej

Typ jest kluczowy — bez niego silnik nie wie, jak obsłużyć obiekt, i najczęściej kończy się to wyjściem do pulpitu. Typ deklaruje się właściwością `TYPE`:

```
NAZWA_OBIEKTU:TYPE=STRING
```

Pełna lista dostępnych typów znajduje się w [Referencji typów](../reference/index.md).

## Literały i ciągi znaków

Sposób interpretacji literału zależy od kontekstu:

- Tekst w cudzysłowach (`"..."`) traktowany jest zawsze jako wartość typu [`STRING`](../reference/STRING.md).
- Tekst bez cudzysłowów najpierw jest sprawdzany jako nazwa istniejącej zmiennej — jeżeli zmienna istnieje, używana jest jej wartość. W przeciwnym razie tekst przyjmowany jest dosłownie.

Liczby zmiennoprzecinkowe akceptują notację standardową (`1.234`) oraz wykładniczą z literą `e` lub `d` (`1.23e4`, `1.23d4`).

## Bloki kodu

Bloki kodu — używane jako wartość sygnału lub jako ciało procedury — zapisuje się w nawiasach klamrowych. Instrukcje rozdzielone są średnikami; ostatnia instrukcja również musi kończyć się średnikiem, w przeciwnym razie może nie zostać wykonana.

```
NAZWA_OBIEKTU:ONCHANGED={ZMIENNA2^PLAY("TADA");}
```

Cały blok kodu musi być zapisany w jednej linii — silnik nie obsługuje wieloliniowych bloków bezpośrednio w pliku skryptu.

## Komentarze

Silnik rozpoznaje dwie formy komentarzy:

- **Komentarz liniowy** — linia zaczynająca się od znaku `#` jest pomijana w całości.
- **Komentarz blokowy** — pojedyncza instrukcja poprzedzona znakiem `!` jest traktowana jako wykomentowana; obowiązuje do najbliższego średnika.

## Wywoływanie metod

Metody wywołuje się przy pomocy znaku `^`:

```
NAZWA_OBIEKTU^METODA(arg1, arg2);
```

## Wyrażenia arytmetyczne

Wyrażenia obliczeniowe zapisuje się w nawiasach kwadratowych:

```
NAZWA_ZMIENNEJ^SET([NAZWA_ZMIENNEJ^GET()+"2"]);
```

Szczegóły operatorów i typowania znajdują się w rozdziale [Arytmetyka](arithmetic.md).

## Wskaźniki tekstowe

Znak `*` przed nazwą zmiennej lub wyrażeniem oznacza, że wartość ma zostać użyta jako nazwa innej zmiennej. Pozwala to dynamicznie odwoływać się do zmiennych skonstruowanych z tekstu:

```
*NAZWA_ZMIENNEJ^PLAY();
*["ANIMO_"+_I_]^PLAY();
```

W pierwszym przypadku `NAZWA_ZMIENNEJ` powinna być typu [`STRING`](../reference/STRING.md) i zawierać nazwę faktycznego obiektu. W drugim — nazwa obiektu konstruowana jest z wyrażenia arytmetycznego.

## Argumenty procedur

Wewnątrz ciała procedury argumenty dostępne są przez znak dolara z numerem (numeracja od `1`):

```
PROCEDURA:CODE={NAZWA_ZMIENNEJ^SET($1);}
```

## Zmienna THIS

W bloku obsługującym sygnał dostępna jest niejawna zmienna `THIS`, ustawiona na referencję do obiektu, który sygnał wywołał. Zmienna jest dostępna również w procedurach zagnieżdżonych wewnątrz takiego bloku.

`THIS` zachowuje się nietypowo: na żądanie nazwy (`GETNAME`) zwraca ciąg `"temp"`, co sugeruje, że pod spodem jest to obiekt tymczasowy. Bezpiecznie działają na niej:

- metody `GET` i `SET` dla typów prymitywnych,
- metody `SHOW`, `HIDE`, `PLAY`, `PAUSE`, `STOP` i `RESUME` dla obiektów graficznych ([`ANIMO`](../reference/index.md)).

Wywołanie innej metody specyficznej dla typu obiektu (np. `GETCFRAMEINEVENT` na [`ANIMO`](../reference/index.md)) zazwyczaj kończy się błędem silnika. Aby tego uniknąć, w skryptach AidemMedia stosowane było obejście: nazwa obiektu była najpierw zapisywana do zmiennej typu [`STRING`](../reference/STRING.md), a następnie wywoływana była `^RUN(nazwa_zmiennej, nazwa_metody)`, która wewnętrznie rozwiązuje wskaźnik tekstowy do faktycznego obiektu.

## Pętle

### @LOOP

```
@LOOP(BEHAVIOUR code, INTEGER start, INTEGER delta, INTEGER increment)
```

Wykonuje `code` dla wartości licznika `_I_` z przedziału `[start, start + delta)` z krokiem `increment`. W pseudokodzie:

```
for (int _I_ = start; _I_ < start + delta; _I_ += increment) {
    code;
}
```

### @FOR (BlooMoo)

```
@FOR(INTEGER counter, BEHAVIOUR code, INTEGER start, INTEGER delta, INTEGER increment)
```

Identyczna do `@LOOP`, z tą różnicą, że pierwszy argument wskazuje zmienną pełniącą rolę licznika zamiast domyślnej `_I_`.

### @WHILE

```
@WHILE(mixed value1, STRING comparator, mixed value2, BEHAVIOUR code)
```

Wykonuje `code` tak długo, jak prawdziwy jest warunek `value1 comparator value2`. Listę komparatorów opisano poniżej w [Instrukcji warunkowej](#instrukcja-warunkowa).

## Instrukcja warunkowa

Silnik udostępnia dwa warianty instrukcji `@IF`.

### Warunek prosty

```
@IF(mixed value1, STRING comparator, mixed value2, BEHAVIOUR codeTrue, BEHAVIOUR codeFalse)
```

Dostępne komparatory:

| Komparator | Znaczenie |
|---|---|
| `_` | równa się |
| `!_` | różne niż |
| `<` | mniejsze niż |
| `<_` | mniejsze lub równe |
| `>` | większe niż |
| `>_` | większe lub równe |

### Warunek złożony {#warunek-zlozony}

```
@IF(STRING condition, BEHAVIOUR codeTrue, BEHAVIOUR codeFalse)
```

W warunku złożonym dostępne są operatory logiczne:

- `&&` — koniunkcja (i)
- `||` — alternatywa (lub)

W warunku złożonym znak równości jest zapisywany jako apostrof (`'`) zamiast podkreślnika (`_`):

| Komparator | Znaczenie |
|---|---|
| `'` | równa się |
| `!'` | różne niż |
| `<` | mniejsze niż |
| `<'` | mniejsze lub równe |
| `>` | większe niż |
| `>'` | większe lub równe |

## Dynamiczne tworzenie zmiennych

Wewnątrz bloku kodu można utworzyć zmienną na bieżąco:

```
@INT(STRING name, INTEGER value)
@DOUBLE(STRING name, DOUBLE value)
@STRING(STRING name, STRING value)
@BOOL(STRING name, BOOL value)
```

Każda z instrukcji tworzy zmienną odpowiedniego typu o podanej nazwie i wartości początkowej.

## Operatory skoku

Wewnątrz pętli oraz procedur można sterować przepływem instrukcjami:

- `@CONTINUE()` — pomija pozostałe instrukcje w bieżącej iteracji pętli i przechodzi do następnej.
- `@BREAK()` — przerywa całe drzewo wywołań rozpoczęte przez bieżący sygnał lub wywołanie.
- `@ONEBREAK()` — przerywa wyłącznie bieżącą procedurę.
- `@RETURN(mixed value)` — ustawia wartość zwracaną przez procedurę, ale nie przerywa jej wykonywania.

## Kolejność wczytywania skryptów

Skrypty silnika są zorganizowane hierarchicznie: skrypty z niższych poziomów hierarchii widzą zmienne swoje i wszystkich przodków, ale nie odwrotnie.

### Punkt startowy

Silnik rozpoczyna od pliku `Application.def` w podkatalogu `dane`. Plik ten zawiera definicje obiektów typu [`APPLICATION`](../reference/index.md), [`EPISODE`](../reference/index.md) oraz [`SCENE`](../reference/index.md) — pozostałe typy w tym pliku są ignorowane.

Przykładowa zawartość:

```
OBJECT=GAME
GAME:TYPE=APPLICATION
GAME:PATH=GAME
GAME:EPISODES=PRZYGODA
GAME:STARTWITH=PRZYGODA

OBJECT=PRZYGODA
PRZYGODA:TYPE=EPISODE
PRZYGODA:PATH=GAME\PRZYGODA
PRZYGODA:SCENES=START,CREDITS,LEBIODKA
PRZYGODA:STARTWITH=START

OBJECT=START
START:TYPE=SCENE
START:PATH=GAME\PRZYGODA\START
```

### Ładowanie kolejnych plików {#ladowanie-kolejnych-plikow}

Po wczytaniu `Application.def` silnik ładuje plik `.CNV` dla każdego zdefiniowanego obiektu. Ścieżkę pliku konstruuje z atrybutu `PATH` obiektu (relatywnie do katalogu `dane`), nazwy obiektu i rozszerzenia `.CNV`. Jeśli plik nie istnieje, jego ładowanie jest pomijane bez błędu.

Kolejność ładowania:

1. Plik powiązany z obiektem `APPLICATION`.
2. Plik pierwszego epizodu (atrybut `STARTWITH` w `APPLICATION`).
3. Plik pierwszej sceny tego epizodu (atrybut `STARTWITH` w `EPISODE`).

Przy lokalizowaniu plików silnik dodatkowo uwzględnia aktualnie ustawiony język (zobacz [`APPLICATION.SETLANGUAGE`](../reference/APPLICATION.md#setlanguage)) — wybrany identyfikator języka wskazuje podkatalog z lokalizowanymi zasobami, konsultowany podczas wczytywania plików gry.

### Inicjalizacja zmiennych

W ramach każdego pliku zmienne są tworzone i inicjalizowane w stałej kolejności typów:

1. Procedury.
2. Typy prymitywne ([`STRING`](../reference/STRING.md), [`DOUBLE`](../reference/DOUBLE.md), [`INTEGER`](../reference/INTEGER.md), [`BOOL`](../reference/BOOL.md)).
3. Tablice oraz warunki.
4. Animacje, obrazy, dźwięki i fonty.
5. Przyciski, pola tekstowe, sekwencje, mysz, klawiatura, obserwator kanwy.

Dla każdej zmiennej w tej fazie wywoływany jest sygnał `ONINIT`. Na koniec, po zakończeniu inicjalizacji wszystkich zmiennych, wywoływana jest procedura `__INIT__` (dotyczy BlooMoo), jeśli została zdefiniowana.
