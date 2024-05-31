# AidemMediaScriptsInterpreter
Prototyp modułu lexera, interpretera skryptów z gier Aidem Media wykorzystujących silnik PikLib oraz Bloomoo oraz próba zaemulowania silnika na urządzeniach z systemem Android.

## INFORMACJA: na ten moment repo jest w przebudowie. Spora część poprzedniej bazy kodowej została przeniesiona, poszczegölne elementy są już tworzone na bieżąco. Stary kod zostanie usunięty, kiedy tylko wszystkie poprzednie testy będą dawać te same wyniki.

Lista TODO (w trakcie tworzenia):
- [x] Arytmetyka
- [x] Obsługa zmiennych
- [ ] Obsługa instrukcji warunkowych (w tym momencie sprawdzane jest, jak silnik porównuje poszczególne typy)
- [x] Obsługa pętli (oprócz FOR, brak jej wykorzystania gdziekolwiek)
- [x] Obsługa funkcji (wywołuje je, na ten moment jeszcze bez parametrów)
- [x] Obsługa procedur (klasa BEHAVIOUR)
- [ ] Obsługa animacji
- [x] Dynamiczne tworzenie zmiennych
- [ ] Ładowanie zmiennych ze skryptów (mam na to kod, ale na razie go nie zapinam)
- [x] Obsługa dynamicznych wskaźników na zmienne (*ZMIENNA)
- [x] Obsługa rzutowania zmiennych
- [x] Obsługa instrukcji RETURN
- [ ] Obsługa BREAK i ONEBREAK (ONEBREAK jest, BREAK jeszcze nie)
- [ ] Obsługa listenerów (ONCHANGED, ONBRUTALCHANGED, ONINIT itp.)
- [ ] Obsługa baz danych (typu DATABASE)
- [ ] Obsługa CNVLoadera
- [ ] Obsługa ładowania obrazów (klasa Image)
- [ ] Obsługa generatora liczb pseudolosowych (klasa Rand)
- [ ] Obsługa macierzy (klasa Matrix)
- [ ] Obsługa wektorów (klasa Vector)
- [ ] Obsługa fontów (klasa Font) (mniejszy priorytet, głównie do testów)
- [ ] Emulacja silników fizycznych Sekai i Inertia
- [ ] Bindy do silnika libGDX
- [ ] Podłączenie funkcji deszyfrujących z bracha main (szyfr skryptów, CRLE, CLZW2)
- [ ] Przygotowanie instrukcji cache'ujących skrypty oraz tłumaczących animacje na format dla libGDX
- [ ] Zapisywanie danych do pliku INI, wraz z ich odczytywaniem
