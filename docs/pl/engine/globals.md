# Zmienne globalne i wbudowane

Silnik udostępnia kilka kategorii zmiennych i nazw, do których można odwoływać się z dowolnego miejsca w skryptach, niezależnie od ich poziomu w hierarchii kontekstów. Niniejszy rozdział opisuje wbudowane obiekty, zmienne niejawne, specjalne procedury oraz hierarchię widoczności.

## Hierarchia kontekstów

Każdy wczytywany skrypt tworzy kontekst zmiennych. Konteksty są zagnieżdżone: kontekst sceny dziedziczy po kontekście epizodu, ten po kontekście aplikacji, a ten po kontekście globalnym silnika. Wyszukiwanie zmiennej odbywa się od najniższego kontekstu w górę — zmienne z niższych poziomów przesłaniają zmienne o tej samej nazwie z poziomów wyższych, ale zmienne wyższych poziomów są widoczne z poziomów niższych.

## Obiekty wbudowane

Następujące obiekty są tworzone leniwie przez silnik przy pierwszym odwołaniu i są dostępne z dowolnego kontekstu pod stałymi nazwami:

| Nazwa | Typ | Opis |
|---|---|---|
| `MOUSE` | [`MOUSE`](../reference/index.md) | Stan myszy (pozycja, kliknięcia). |
| `KEYBOARD` | [`KEYBOARD`](../reference/index.md) | Stan klawiatury. |
| `RAND` | [`RAND`](../reference/index.md) | Generator liczb pseudolosowych. Dostępny również pod aliasem `RANDOM`. |
| `SYSTEM` | [`SYSTEM`](../reference/index.md) | Interfejs do funkcji systemowych (czas, środowisko). |

Wszystkie cztery obiekty są singletonami w kontekście globalnym — odwołanie do nich z dowolnego skryptu trafia do tej samej instancji.

## Obiekty z `Application.def`

Obiekty zdefiniowane w pliku `Application.def` — typu [`APPLICATION`](../reference/index.md), [`EPISODE`](../reference/index.md) oraz [`SCENE`](../reference/index.md) — są ładowane do kontekstu globalnego silnika i widoczne ze wszystkich skryptów gry. Pozostałe typy w tym pliku są ignorowane (zobacz [Punkt startowy](scripts.md#punkt-startowy)).

## Zmienne niejawne

Silnik wstrzykuje do skryptów kilka zmiennych nie deklarowanych jawnie.

### `_I_`

Licznik pętli ustawiany przez instrukcję [`@LOOP`](scripts.md#loop). Jest zmienną typu [`INTEGER`](../reference/INTEGER.md) tworzoną lokalnie w kontekście bieżącej iteracji. Wartość zmienia się automatycznie wraz z postępem pętli.

Wewnątrz pętli `@LOOP` `_I_` może być odczytywana w wyrażeniach arytmetycznych i jako argument metod:

```
@LOOP({*["ANIMO_"+_I_]^PLAY();}, 0, 10, 1);
```

Pętla [`@FOR`](scripts.md#for-bloomoo) pozwala podać własną nazwę licznika; w takim przypadku `_I_` nie jest ustawiana.

### `THIS`

Referencja do obiektu, który wyemitował aktualnie obsługiwany sygnał. Dostępna wyłącznie w bloku obsługi sygnału i procedurach z niego wywołanych. Szczegóły jej zachowania opisano w sekcji [Zmienna THIS](scripts.md#zmienna-this).

### `$1`, `$2`, … `$N`

Argumenty procedury lub obsługi sygnału (numeracja od `1`). Dostępne tylko w ciele procedury lub bloku obsługującego sygnał:

```
PROCEDURA:CODE={NAZWA_ZMIENNEJ^SET($1);}
```

Powyższa składnia jest opisana również w sekcji [Argumenty procedur](scripts.md#argumenty-procedur).

## Specjalne procedury

Niektóre nazwy procedur mają znaczenie konwencjonalne — silnik wywołuje je automatycznie w określonych momentach cyklu życia.

### `__ONINIT__`

Procedura wywoływana po zakończeniu inicjalizacji wszystkich zmiennych w wczytanym pliku. Zobacz [Inicjalizacja zmiennych](scripts.md#inicjalizacja-zmiennych). Typowe zastosowanie: ustawienie stanu początkowego sceny po tym, jak wszystkie obiekty są już dostępne.

### `__INIT__`

Procedura wywoływana po załadowaniu sceny i wykonaniu inicjalizacji zmiennych — bezpośrednio przed przekazaniem sterowania do logiki gry. Wykorzystywana do ustawiania stanu sceny zależnego od bieżącego epizodu lub aplikacji.

## Konwencja nazewnictwa

Nazwy poprzedzone i zakończone dwoma podkreślnikami (`__NAZWA__`) są zarezerwowane dla silnika oraz dla konwencjonalnych nazw rozpoznawanych globalnie (np. `__KEYB__`, `__INIT__`, `__ONINIT__`). W praktyce skrypty AidemMedia używają tego formatu również dla własnych zmiennych globalnych, których nie chcą przypadkowo przesłonić w kontekstach lokalnych.
