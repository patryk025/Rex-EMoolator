# Format SND — archiwum dźwięków instalatora

`wavs/wav.snd` z płyty **Reksio i Kapitan Nemo** nie jest czytany przez silnik
BlooMoo. To archiwum, które **instalator** (`setup.exe`) rozpakowuje w czasie
instalacji do pojedynczych plików `wavs/*.wav`. Gra uruchomiona prosto z płyty
lub obrazu nie ma więc tych dźwięków — emulator odtwarza krok instalatora sam.

## Budowa

Plik jest ciągiem rekordów bez nagłówka i bez spisu treści. Liczby są
little-endian.

| Pole | Typ | Opis |
|---|---|---|
| typ | `uint32` | `0` — Ogg Vorbis, `1` — Speex |
| rozmiar | `uint32` | długość danych w bajtach |
| długość nazwy | `uint32` | długość nazwy **razem z** kończącym `\0` |
| nazwa | `char[długość nazwy]` | nazwa pliku bez katalogu, np. `Babc_W074.wav` |
| dane | `uint8[rozmiar]` | strumień Ogg Vorbis (lub Speex) |

Następny rekord zaczyna się zaraz po danych poprzedniego. Znane archiwum
zawiera 3274 rekordy, wszystkie typu `0`.

## Rozpakowanie

Dla każdego rekordu instalator:

1. zapisuje dane do pliku tymczasowego,
2. dekoduje go do 16-bitowego PCM (`ov_read`: próbka × 32768, zaokrąglenie
   do najbliższej parzystej, obcięcie do zakresu `int16`),
3. zapisuje wynik jako `<katalog archiwum>\<nazwa>` w układzie `CWaveFile` z
   DirectX SDK: `RIFF`/`WAVE`, `fmt ` (16 bajtów PCM), `fact` (4 bajty,
   wartość `0`), `data` — nagłówek ma 56 bajtów.

Rekord o powtórzonej nazwie nadpisuje wcześniejszy plik.

## W emulatorze

`SndArchiveFileSystem` wyszukuje pliki `*.snd` w katalogu głównym gry i jego
bezpośrednich podkatalogach, a następnie montuje je w VFS **pod** źródłem gry —
istniejące pliki (np. z zainstalowanej kopii) mają pierwszeństwo. Rekordy są
dekodowane leniwie przy pierwszym odczycie (`OggVorbisDecoder` na bazie JOrbis,
`WaveSaver`).

Ograniczenia:

- rekordy Speex nie są udostępniane (brak dekodera; nie występują w znanym archiwum),
- JOrbis nie jest bit w bit zgodny z libvorbis — pojedyncze próbki mogą różnić się o 1.

Szczegóły analizy: `docs/reverse-engineering/nemo-setup-wav-snd.md` w repozytorium.
