# setup.exe (Reksio i Kapitan Nemo) — rozpakowanie `wavs/wav.snd`

## Identyfikacja próbki

| | |
|---|---|
| Plik | `setup.exe` (instalator Aidem Media, MFC) |
| Gra / wydanie | Reksio i Kapitan Nemo, wydanie PL (pliki z obrazu płyty użytkownika, `install.ini`: `program_name = Reksio_i_Kapitan_Nemo.`) |
| Rozmiar | 765 952 B |
| SHA-256 | `305f50b04f9220c496621833dc2788965f20f8acef3e656a0978bc5d6a30b778` |
| Architektura | x86, 32-bit PE |
| Image base | `0x00400000` (VA w analizie = image base + RVA) |
| Ghidra | projekt `wav_snd`, program `/setup.exe` (ten sam SHA-256) |

Archiwum: `wavs/wav.snd`, 82 651 396 B, SHA-256
`636ef93db0e71050e11e0afcb552d17e203883f521bddf5ea1728609489974f9`,
3274 rekordy, wszystkie typu 0 (Ogg Vorbis, vendor enkodera
`Xiph.Org libVorbis I 20020717`).

Offsety pliku nie zostały ustalone (nie mapowano sekcji PE).

## Mapa adresów

| RVA | VA w analizie | offset pliku | obecny symbol | proponowana nazwa | znaczenie / dowody | pewność |
|---|---|---|---|---|---|---|
| `0x08ffa` | `0x00408ffa` | — | `FUN_00408ffa` | `CSetupApp::CSetupApp` | Ścieżka `install.ini` = katalog `setup.exe` + `"\install.ini"` (`0x0049827c`); nazwa sekcji listy plików (`this+0x70`) ustawiona na `"std"` (`0x0049828c`). | wysoka |
| `0x04700` | `0x00404700` | — | `FUN_00404700` | `CInstallModeDlg::CInstallModeDlg` | Okno wyboru trybu (`show_install_mode`) startuje z `"std"` (`0x00498140`); przyciski ustawiają `"max"`/`"min"`/`"std"` (`0x004981b0`…, np. `FUN_00404bad`). | wysoka |
| `0x0306c` | `0x0040306c` | — | `FUN_0040306c` | `CInstallDlg::OnInitDialog` | `GetPrivateProfileString(sekcja, "files")` → `atoi`; wpisy `"%d"` (`0x004980ac`) od 0 do `files-1`; wpis kończący się `"\"` (`0x004980b0`) to katalog do utworzenia, pozostałe trafiają na listę plików. | wysoka |
| `0x0291b` | `0x0040291b` | — | `FUN_0040291b` | `CInstallDlg::CopyNextFile` | Iteruje listę plików; wpis przechodzi `MakeLower` (`FUN_0046eef4` → `_mbslwr` `FUN_0042a33f`), opcjonalny sufiks `";<język>"` jest sprawdzany i odcinany; gdy ścieżka zawiera `".snd"` (`0x00498070`, `CString::Find` → `_mbsstr`), otwiera ją `"rb"`, zapamiętuje rozmiar i przechodzi w tryb rozpakowania; w trybie 1 woła `0x0040357f`, dopóki pozycja < rozmiar. Pozostałe pliki — `CopyFileA`. | wysoka |
| `0x0357f` | `0x0040357f` | — | `FUN_0040357f` | `CInstallDlg::ExtractSndRecord` | `fread` 4× pole: typ, rozmiar, długość nazwy, nazwa, potem dane; zapis do `"%s\\%s%s"` (cel, katalog archiwum, nazwa) z `.wav`→`.tmp`; typ 0 → `0x0040d376`, typ 1 → `0x0040f067`; usuwa `.tmp`; pozycja += `nameLen + 12 + size`. | wysoka |
| `0x0d376` | `0x0040d376` | — | `FUN_0040d376` | `DecodeOggToWave` | `ov_open`, pętla `ov_read(vf, buf, 0x1000, 0, 2, 1, &bs)` (wartości ujemne ignorowane), zapis PCM, następnie przepisanie do WAV przez `CWaveFile` z `WAVEFORMATEX{1, ch, rate, rate*ch*2, ch*2, 16}`. Komunikaty `"Bitstream is %d channel..."`. | wysoka |
| `0x0f067` | `0x0040f067` | — | `FUN_0040f067` | `DecodeSpeexToWave` | Port `speexdec` (komunikat `"This doesn't look like a Speex file"`); nieużywany przez znane archiwum. | wysoka |
| `0x25f50` | `0x00425f50` | — | `FUN_00425f50` | `ov_read` | Dla 16 bit/LE/signed: `FISTP(x * 32768.0)`, clamp `[-32768, 32767]`. | wysoka |
| `0x258e0` | `0x004258e0` | — | `FUN_004258e0` | `_fetch_and_process_packet` (vorbisfile) | Woła `vorbis_synthesis` (`0x004405c0`) i `vorbis_synthesis_blockin` (`0x00440120`). | średnia |
| `0x40120` | `0x00440120` | — | `FUN_00440120` | `vorbis_synthesis_blockin` | Śledzenie `granulepos` jak w libvorbis 1.1.x: bez „dangling else” z 1.0; przycięcie końca (`pcm_current -= extra` przy `eofflag`) i początku (`pcm_returned += extra` przy `sequence == 1`). | średnia |
| `0x07d3b` | `0x00407d3b` | — | `FUN_00407d3b` | `CWaveFile::Open` | `mmioOpen`; tryb zapisu woła `WriteMMIO` i `ResetFile`. | wysoka |
| `0x085a1` | `0x004085a1` | — | `FUN_004085a1` | `CWaveFile::WriteMMIO` | `RIFF/WAVE`, `fmt ` 16 B (dla tagu 1), chunk `fact` z wartością `-1`. | wysoka |
| `0x08113` | `0x00408113` | — | `FUN_00408113` | `CWaveFile::ResetFile` | Tworzy chunk `data`. | wysoka |
| `0x083dd` | `0x004083dd` | — | `FUN_004083dd` | `CWaveFile::Close` | Domyka `data` i `RIFF`, zeruje wartość `fact`. Wynik: nagłówek 56 B. | wysoka |
| `0x10632` | `0x00410632` | — | `FUN_00410632` | `PackWavSnd` (narzędzie autorskie) | Odwrotny kierunek: dla każdego pliku koduje `.wav`→`.ogg`, dopisuje rekord do `"wav.snd"` (`0x00498e30`), usuwa `.ogg`. Potwierdza budowę rekordu (`0, size, strlen+1, name, data`). | wysoka |

## Semantyka

- Rozpakowywane są wyłącznie wpisy listy plików z `install.ini [std]` (przy
  `show_install_mode = true` użytkownik może wybrać `min`/`max`; domyślnie
  i bez okna — `std`), których ścieżka po zamianie na małe litery zawiera `.snd`.
  W Nemo jedynym takim wpisem jest `30 = wavs\wav.snd`.
- Rekordy są czytane sekwencyjnie bez spisu treści; kolejny zaczyna się po danych.
  Powtórzona nazwa nadpisuje plik (zapis `"wb"` w tej samej ścieżce).
- Nazwa jest łączona z katalogiem archiwum, więc `wavs\wav.snd` → `<instalacja>\wavs\<nazwa>`.
- Zapis WAV: PCM 16-bit; dane PCM są najpierw zapisywane do pliku docelowego,
  potem wczytywane i przepisywane przez `CWaveFile` (ten sam plik).
- Rekord `_pusty_sfx*.wav` ma jeden 1-bajtowy pakiet audio i `granulepos` 0 —
  dekoduje się do 0 próbek (WAV z samym nagłówkiem). Potwierdzone ffmpeg 7.1.1
  (dekoder natywny i libvorbis).

## Powiązanie z emulatorem

- `engine/filesystem/SndArchiveFileSystem` — wykrywanie przez `install.ini [std]`,
  leniwy indeks czytany strumieniowo, montowanie pod źródłem gry. Archiwum jest
  czytane tylko przez `IFileSystem.open()` (z pominięciem do offsetu rekordu),
  nigdy przez `openSource()`, które na ZIP/pofragmentowanym UDF kopiuje cały plik
  do pamięci. Filtr języka `";<język>"` nie jest modelowany (sufiks jest odcinany).
- `encoding/OggVorbisDecoder` — dekodowanie (JOrbis) + konwersja `ov_read`.
- `saver/WaveSaver` — układ `CWaveFile`.
- Testy: `SndArchiveFileSystemTest`, `OggVorbisDecoderTest`, `WaveSaverTest`.

Walidacja na pełnym archiwum (poza testami automatycznymi): wszystkie 3274 rekordy
dekodują się; długości `Babc_W074.wav` (78 973 próbek) i `bach05.wav`
(1 294 585 próbek) zgodne z ffmpeg; wartości próbek różnią się od natywnego
dekodera ffmpeg maksymalnie o 1 (99,96–99,99% identycznych). Żaden rekord nie
spełnia warunku gałęzi „partial first frame” (`sequence == 1`), której JOrbis nie ma.

## Otwarte pytania

- Brak porównania z plikami WAV faktycznie wygenerowanymi przez instalator
  (wymagałoby instalacji na Windows); bit-zgodność próbek z libvorbis setup.exe
  nie jest potwierdzona.
- Inne gry: w obrazach Czarodziejów, Kretesa, Skarbu piratów, UFO (dwa wydania),
  Wehikułu czasu, Miasta Sekretów, Złotego Runa, Wojny Trojańskiej oraz
  Herkulesa + Odyseusza `install.ini` (jeśli jest) nie zawiera wpisów `.snd`.
  Mechanizm występuje więc tylko w Kapitanie Nemo z tych próbek.
- Dokładna semantyka filtra `";<język>"` (porównanie nazwy sekcji językowej
  z wpisem po `MakeLower`) nie została sprawdzona; Nemo z niego nie korzysta.

Analiza wyboru sekcji i listy plików: REA (Ghidra 12.1.2, sesja tylko do odczytu)
na tej samej próbce `setup.exe` (SHA-256 jak wyżej).
