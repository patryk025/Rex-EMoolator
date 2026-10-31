# Mapa metod emulatora

Każdy kwadrat to jedna metoda skryptowa w obrębie typu. Kolor mówi, czy emulator ją rejestruje: turkusowy to rejestracja, bursztynowy to stub, szary to brak dopasowania. Najedź na kwadrat, przejdź do niego klawiaturą albo go dotknij, aby zobaczyć nazwę metody. Nazwa typu prowadzi do jego opisu w referencji.

<div id="method-map" class="method-map"></div>
<noscript>Mapa wymaga JavaScriptu.</noscript>

!!! note "Jak czytać mapę"

    - Kolory opisują kod emulatora, nie poprawność względem oryginalnego silnika. Stub może dotyczyć tylko jednej gałęzi metody, a brak dopasowania nie oznacza pewnego braku implementacji.
    - Metody oryginału pochodzą z analizy DLL silników PIKLIB 6.1–8 i BlooMoo oraz modułów sceny (`SCENE:DLLS=`: WORLD, KOLOROWANKA, MATRIX, TEST, INERTIA, BOARD). Typ → runner pochodzi z fabryki runnerów silnika albo z obiektu modułu.
    - Metody bazowe (`CLONE`, `SEND`, …) są raz, w karcie „Metody wspólne”, która nie jest typem skryptowym. „Instrukcje @” to `@IF`, `@LOOP` i pozostałe instrukcje wbudowane.
    - Wskaźnik karty to rejestracje bez flagi stubu / rozpoznane metody, liczony dla całej grupy także po filtrowaniu. To nie procent zgodności. Karta „Brak danych” nie oznacza 0%.
    - Pominięto pomocniczy typ emulatora `INSTANCE`.

Mapę odświeża ręcznie zewnętrzny analizator DLL, którego nie ma w tym repozytorium. Data i rewizja emulatora są nad legendą.
