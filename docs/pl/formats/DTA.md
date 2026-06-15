# Format DTA — baza danych

Plik `.DTA` to **tekstowa** namiastka bazy danych, używana przez obiekty [`DATABASE`](../reference/DATABASE.md). Przechowuje wiersze i kolumny rozdzielone prostymi separatorami.

## Budowa

- **Wiersze** rozdziela znak końca linii w stylu Windows: `\r\n` (CRLF).
- **Kolumny** w obrębie wiersza rozdziela znak potoku: `|`.

```
wartość11|wartość12|wartość13␍␊
wartość21|wartość22|wartość23␍␊
```

## Schemat (MODEL)

Struktura bazy — liczba kolumn i ich typy — nie jest zapisana w samym pliku `.DTA`, lecz w polu `MODEL` obiektu [`DATABASE`](../reference/DATABASE.md). `MODEL` jest zmienną typu [`STRUCT`](../reference/STRUCT.md) i to ono nadaje surowym wartościom z pliku konkretne typy kolumn.

!!! note "Plik to same dane"
    Sam `.DTA` nie zawiera nagłówka ani typów — jest „głupim" magazynem tekstu. Bez powiązanego `MODEL` nie da się jednoznacznie zinterpretować kolumn.

## Pokrewne formaty

W serii *Poznaj Mity* rolę plików `.DTA` pełnią w minigrach pliki `.TXT` o analogicznej, tekstowej strukturze.

## Zobacz też

- [`DATABASE`](../reference/DATABASE.md) — obiekt skryptowy operujący na `.DTA`.
- [`STRUCT`](../reference/STRUCT.md) — definicja schematu w polu `MODEL`.
