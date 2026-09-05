# Architektura

Ten rozdział daje mapę całości: z jakich warstw zbudowany jest Rex-EMoolator, jak rozdzielone są odpowiedzialności i jak dane płyną od pliku na dysku do obiektu na ekranie. Szczegóły poszczególnych podsystemów mają własne rozdziały — tu chodzi o obraz z lotu ptaka.

## Warstwy

```mermaid
flowchart TD
    subgraph Platforma
      DESK[desktop · LWJGL3]
      ANDR[android]
    end
    DESK & ANDR --> ENG[BlooMooEngine<br/>LibGDX ApplicationAdapter]
    ENG --> GAME[Game<br/>centralny hub]
    GAME --> MGR[Managery<br/>Input · Update · Render · Debug]
    GAME --> CTX[Konteksty<br/>Application → Episode → Scene]
    GAME --> VFS[VFS<br/>źródła zasobów]
    CTX --> INT[Interpreter v2<br/>zmienne · wartości · AST]
```

Projekt dzieli się na trzy moduły Gradle:

| Moduł | Rola |
|---|---|
| **core** | cała logika emulatora (Java 21) — interpreter, managery, loadery, VFS |
| **desktop** | launcher na LWJGL3 |
| **android** | launcher na Androida (API 24+) |

Warstwa silnika (`engine/`) celowo **nie zależy** od konkretnych klas interpretera — rozmawia z nim przez interfejs [`GameContext`](#konteksty-i-hierarchia) i typ `EngineVariable`. Dzięki temu managery operują na obiektach gry, nie znając ich wewnętrznej reprezentacji.

## Bootstrap i pętla

`BlooMooEngine` (klasa `ApplicationAdapter` LibGDX) w `create()` stawia `SpriteBatch`, kamerę ortograficzną i [viewport](rendering.md) 800×600, po czym tworzy `Game` oraz cztery managery. Każda klatka to przejście `render()`: **Input → Update → Render → Debug**, ze stanem gry posuwanym [stałym krokiem 60 Hz](loop.md).

Szczegóły rytmu klatki opisuje [Pętla i zegar silnika](loop.md).

## Game — centralny hub

Klasa `Game` spina wszystko, co składa się na działającą grę. To ona jest „właścicielem" stanu:

<div class="grid cards" markdown>

- :material-folder-network: **Zasoby** — instancja [VFS](#vfs-wirtualny-system-plikow) i bieżący katalog danych (`DANE`).
- :material-file-tree: **Konteksty** — `definitionContext` (root) oraz bieżące konteksty Application / Episode / Scene.
- :material-map-marker-path: **Stan sceny** — aktualny epizod i scena, zmienne `APPLICATION`/`EPISODE`/`SCENE`, tło, język (`POL` domyślnie).
- :material-clock-outline: **Zegar** — monotoniczny [zegar silnika](loop.md#zegar-silnika) (`engineTimeMsAccum`).
- :material-vector-intersection: **Kolizje** — `QuadTree` (800×600), zbiór monitorowanych obiektów i mapa kolizji.
- :material-music: **Audio i kanwa** — cache muzyki, [grafiki wklejone](rendering.md), zrzut ostatniej klatki dla `CANVAS_OBSERVER`.

</div>

## Managery

Logika klatki rozdzielona jest na managery o jasnych odpowiedzialnościach (wzorzec zbliżony do MVC):

| Manager | Odpowiedzialność |
|---|---|
| `InputManager` | mysz i klawiatura → sygnały |
| `UpdateManager` | postęp stanu gry; deleguje do pod-managerów |
| `RenderManager` | rysowanie sceny (patrz [Renderowanie](rendering.md)) |
| `DebugManager` | nakładka diagnostyczna |

`UpdateManager` dzieli pracę kroku na cztery pod-managery: **Timer**, **Animation**, **Collision**, **Audio** — wykonywane w tej kolejności po posunięciu [zegara](loop.md#zegar-silnika).

## Konteksty i hierarchia

Zmienne (obiekty zdefiniowane w skryptach) żyją w **kontekstach** ułożonych hierarchicznie. Kontekst niższego poziomu widzi swoje zmienne oraz zmienne wszystkich przodków — ale nie odwrotnie:

```mermaid
flowchart TD
    A["definitionContext (root)"] --> APP["Application"]
    APP --> EP["Episode"]
    EP --> SC["Scene"]
    SC -. "widzi w górę" .-> EP
    EP -.-> APP
```

Każdy `Context` zbudowany jest przez **kompozycję** wyspecjalizowanych części:

| Część | Rola |
|---|---|
| `ExecutionContext` | stos wywołań, zmienne lokalne (`THIS`, `$1`–`$N`, `_I_`) |
| `VariableStore` | obiekty zadeklarowane w tym kontekście |
| `VariableResolver` | logika wyszukiwania w hierarchii + buforowane widoki typów |
| `AttributeStore` | surowe atrybuty wczytane ze skryptu |
| `CloneRegistry` | rejestr sklonowanych obiektów (`CLONE`) |

Wyszukanie zmiennej idzie: zmienne lokalne wykonania → lokalny `VariableStore` → konteksty dodatkowe → łańcuch rodziców. Dla managerów `VariableResolver` utrzymuje **buforowane widoki** zbiorące pod uwagę całą hierarchię — np. „wszystkie obiekty graficzne sceny", „wszystkie timery" — żeby render i update nie musiały co klatkę przeszukiwać drzewa.

Kolejność wczytywania skryptów i inicjalizacji obiektów opisuje rozdział [Skrypty](../engine/scripts.md#kolejnosc-wczytywania-skryptow).

### Zakres leksykalny a zakres odtwarzania

Kontekst rozstrzyga, **którą zmienną** widzi skrypt. Nie rozstrzyga natomiast, **na czym** działa silnik: pauza sceny czy wznowienie jej dźwięków dotyczą aktywnej sceny niezależnie od tego, gdzie mieszka wywołujący skrypt. Te dwa zakresy są celowo trzymane osobno.

Po stronie leksykalnej `RUNENV` uruchamia znalezione zachowanie w kontekście, w którym zostało **zdefiniowane**, a nie w najgłębszym kontekście aktywnym. Wyszukiwanie idzie w górę hierarchii, więc zachowanie zadeklarowane na poziomie epizodu odnajdzie się bez problemu — ale wykonane w kontekście sceny rozwiązywałoby swoje pomocnicze nazwy (np. `BFITMP3`) najpierw w `VariableStore` sceny i trafiłoby na cudzy obiekt o tej samej nazwie.

Po stronie odtwarzania [`SCENE.PAUSE`](../reference/SCENE.md#pause), [`RESUME`](../reference/SCENE.md#resume) i [`RESUMEONLY`](../reference/SCENE.md#resumeonly) delegują do `ScenePlaybackController`, którego właścicielem jest `Game`, a nie którykolwiek kontekst. Kontroler zawsze czyta aktywną scenę przez widoki `…ForScheduling` z `VariableResolver`, składające zmienne **po tożsamości** — dwa różne obiekty o tej samej nazwie w różnych kontekstach nie zlewają się więc w jeden.

Przykład z WPZR: `B_PAUSE_START` jest zdefiniowany w epizodzie `PRZYGODA`, ale ma zatrzymać animacje minigry `ORACZEMULTIPLAYER`. Zachowanie wykonuje się w kontekście epizodu, a rodzic nigdy nie widzi zmiennych swoich dzieci — odczyt `ctx.context().getGraphicsVariables()` w ogóle nie obejmuje więc animacji minigry. Dopiero kontroler sięga po właściwy zbiór obiektów.

Pauza rozdziela też **domeny czasu**:

| Domena | Źródło | Zachowanie w pauzie |
|---|---|---|
| czas timerów | `Game.getTimerTimeMs()` | stoi, chyba że wywołano `PAUSE(TRUE)`; korzystają z niego inicjalizacja timerów, `ENABLE`/`RESET`/`SET` i `TimerManager` |
| zegar silnika | `Game.getEngineTimeMs()` | biegnie dalej |
| wejście | `InputManager` | działa dalej |

Dzięki temu okno dialogowe otwarte na spauzowanej scenie może odtwarzać własne animacje i reagować na przyciski. Wznowienie nie nadrabia czasu przerwy: `timerOffset` przesuwa domenę timerów o jej długość, a `SoundVariable` przesuwa tak samo `playStartTime`, więc pauza nigdy nie przyspiesza `ONFINISHED`. Podmiana kontekstu sceny zwalnia blokadę bezwarunkowo — nowa scena nie dziedziczy pauzy poprzedniej.

Podziału pilnuje test `ScenePauseTest`, wraz z rzeczywistym wywołaniem `RUNENV` zachowania zdefiniowanego w rodzicu, sterującego animacją należącą do sceny.

## Zmienne i wartości (interpreter v2)

Reprezentacja danych w skryptach opiera się na **interfejsach zapieczętowanych** (sealed) z wyczerpującym dopasowaniem wzorców:

- **`Variable`** — każdy typ skryptowy ([`INTEGER`](../reference/INTEGER.md), [`STRING`](../reference/STRING.md), [`ANIMO`](../reference/ANIMO.md), [`TIMER`](../reference/TIMER.md), …). Zmienne są **niemutowalne** — `withValue()` zwraca nową instancję (z wyjątkami stanu mutowalnego oznaczonego wewnętrznie, jak stan animacji czy timera).
- **`Value`** — wartości prymitywne (`IntValue`, `DoubleValue`, `StringValue`, `BoolValue`) z metodami konwersji typów.
- **`MethodSpec` / `MethodResult` / `MethodContext`** — deklaratywne definicje metod (`MethodSpec` opakowuje `VariableMethod`). Metoda dostaje **`MethodContext`** — widok na runtime (dostęp do zmiennych, instancji `Game`, uruchamianie behaviourów, rejestr klonów) — i przez niego zmienia świat bezpośrednio. `MethodResult` niesie wartość zwracaną oraz informację o przepływie sterowania (`BREAK` / `ONE_BREAK`) potrzebną do propagacji `@BREAK` / `@ONEBREAK` przez granice procedur.

Skrypty parsowane są przez ANTLR do drzewa AST, które wykonuje `ASTInterpreter`. Składnię języka opisuje rozdział [Skrypty](../engine/scripts.md), a pełny spis typów — [Referencja typów](../reference/index.md).

## VFS — wirtualny system plików

Dostęp do zasobów gry idzie przez `VFS`, który warstwuje kilka źródeł i ukrywa, skąd faktycznie pochodzi plik:

```mermaid
flowchart TD
    R["openRead(ścieżka)"] --> L1{storage<br/>zapisywalna warstwa}
    L1 -->|jest| HIT[zwróć strumień]
    L1 -->|brak| L2{źródła zasobów<br/>ostatnio zamontowane pierwsze}
    L2 -->|jest| HIT
    L2 -->|brak| ERR[brak zasobu]
```

- **Źródła zasobów** montowane są przez `AssetSourceDispatcher` w zależności od typu: katalog → `LocalFileSystem`, plik `.iso` → `IsoFileSystem`, `.zip` → `ZipFileSystem`. Źródła zamontowane później mają wyższy priorytet.
- **Storage** to jedyna warstwa zapisywalna (zapisy gry, pliki tymczasowe); nadpisuje dane gry przy odczycie.
- **Język** — jeśli ustawiony, każda warstwa jest najpierw sprawdzana ze ścieżką `<język>/<ścieżka>`, a dopiero potem z gołą ścieżką. Odtwarza to konwencję lokalizacji oryginału (zobacz [`APPLICATION.SETLANGUAGE`](../reference/APPLICATION.md)).

## Potok ładowania gry

```mermaid
flowchart LR
    A["Game.loadGame()"] --> B["zamontuj źródła w VFS<br/>(AssetSourceDispatcher)"]
    B --> C["Application.def<br/>(APPLICATION/EPISODE/SCENE)"]
    C --> D["plik .CNV per obiekt"]
    D --> E["CNVParser → zmienne"]
    E --> F["ONINIT wg kolejności typów"]
```

Ładowanie startuje od `Application.def` w katalogu `DANE`, a następnie wczytuje pliki `.CNV` dla aplikacji, pierwszego epizodu i pierwszej sceny. Pełną kolejność (i fazy inicjalizacji `ONINIT` / `__ONINIT__`) opisuje [Skrypty → Kolejność wczytywania](../engine/scripts.md#kolejnosc-wczytywania-skryptow).

!!! note "GameLoader to dziś pusty stub"
    Mimo nazwy, logika ładowania mieszka w `Game` (`scanGameDirectory`, `CNVParser`), a nie w klasie `loader/GameLoader`. To miejsce na przyszły refaktor, nie osobny podsystem.

## Powiązane tematy

- [Pętla i zegar silnika](loop.md) — dynamika klatki.
- [Renderowanie](rendering.md), [System animacji](animation.md), [Czas i timery](timers.md) — poszczególne podsystemy.
- [Skrypty](../engine/scripts.md) — język, hierarchia i kolejność wczytywania.
