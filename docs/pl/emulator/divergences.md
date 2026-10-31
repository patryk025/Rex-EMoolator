# Rozbieżności z oryginałem

Źródłem prawdy jest oryginalny silnik, a emulator do niego dąży. Reszta dokumentacji opisuje oryginał, a ta strona zbiera miejsca, w których emulator zachowuje się inaczej.

Lista pochodzi z crosstestów, które uruchamiają te same skrypty na prawdziwych bibliotekach DLL i w emulatorze. Stan z 2026-10-24, dla Pikliba 7.2, Pikliba 8 i BlooMoo. Każdą pozycję trzeba jeszcze sprawdzić: test wykrywa różnicę, ale nie mówi, czy jakakolwiek gra na niej polega.

## Wykryte różnice

| Obszar | Oryginał | Emulator |
|---|---|---|
| `[x@0]`, `[x%0]` dla `STRING` i `BOOL` | wynikiem jest lewy operand | tekst `NULL` |
| `[x@0]` dla `DOUBLE` | `1.#INF0` | tekst `NULL` |
| `[x@0]`, `[x%0]` dla `INTEGER`, `[x%0]` dla `DOUBLE` | awaria silnika | tekst `NULL` |
| [`INTEGER^DIV(0)`](../reference/INTEGER.md#div), [`INTEGER^MOD(0)`](../reference/INTEGER.md#mod) | awaria silnika | wartość bez zmian |
| [`DOUBLE^DIV(0)`](../reference/DOUBLE.md#div) | zapisuje `1.#INF0` | wartość bez zmian |
| [`INTEGER^LENGTH`](../reference/INTEGER.md#length) | nie zapisuje długości w zmiennej; stan po wywołaniu zależy od silnika | zapisuje długość |
| Wartości zwracane przez metody `SET`, `SUB`, `MUL`, `DIV` i podobne | nic | nowa wartość |
| Wynik `RUN` | `STRING`; bez `@RETURN` nic | wartość w oryginalnym typie; bez `@RETURN` tekst `__RETURN__` |
| `*NAZWA^SET([PROC^RUN()+5])`, gdy `PROC` zwraca `2` | `25` | `7` |
| Warunek `5!'5\|\|8>'10&&1'1` | prawda | fałsz |
| Warunek `5+5*2'20\|\|1'2&&1'1` | prawda | fałsz |
| Warunek `2+2'2` w Piklibie 7.2 i 8 | prawda | fałsz |

## Brakujące elementy

- Instrukcje `@FOR`, `@CONTINUE`, `@CREATE`, `@GETAPPLICATIONNAME`, `@GETCURRENTSCENE`, `@MSGBOX`, `@RUNONTIMER` i `@VALUE`.
- Starsze silniki: emulator nie odwzorowuje ograniczeń języka Pikliba 6.1 i 7.1, opisanych w rozdziałach [Skrypty](../engine/scripts.md#petle) i [Arytmetyka](../engine/arithmetic.md#kolejnosc-dzialan).

Pokrycie metod pokazuje [mapa metod](../reference/method-map.md).
