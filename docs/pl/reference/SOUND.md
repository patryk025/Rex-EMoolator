# SOUND

Krótki efekt dźwiękowy wczytywany z pliku `.WAV`. Obsługuje sterowanie odtwarzaniem oraz zmianę próbkowania, co pozwala dynamicznie zmieniać wysokość i prędkość odtwarzanego dźwięku.

## Pola

### FILENAME

```
STRING FILENAME
```

Nazwa pliku `.WAV` z dźwiękiem. Jeżeli ścieżka nie zaczyna się od `$`, silnik dokleja prefiks `$WAVS\`. Pole odczytywane podczas inicjalizacji zmiennej; może być również ustawione w trakcie działania metodą [`LOAD`](#load).

### PRELOAD

```
BOOL PRELOAD
```

Określa, czy dane dźwięku mają być załadowane od razu przy inicjalizacji, czy dopiero przed pierwszym odtworzeniem.

## Metody

### ISPLAYING

```
BOOL ISPLAYING()
```

Sprawdza, czy dźwięk jest aktualnie odtwarzany.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli dźwięk jest odtwarzany.

**Przykłady**

```
SNDATGOAL^ISPLAYING();
SNDREX0^ISPLAYING();
```

**Kompatybilność:** `ISPLAYING` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### LOAD

```
void LOAD(STRING path)
```

Wczytuje plik dźwiękowy do zmiennej, zastępując dotychczas załadowany dźwięk. Bieżące odtwarzanie zostaje zatrzymane. Jeżeli ścieżka nie zaczyna się od `$`, dodawany jest prefiks `$WAVS\`.

**Parametry**

- `path` — ścieżka pliku `.WAV` w VFS gry.

**Przykłady**

```
SNDATGOAL^LOAD(VARSTEMP0);
SNDWAV^LOAD("$WAVS\NAR_I000.WAV");
SNDANSWER^LOAD(ARRSEQ^GET(0));
```

**Kompatybilność:** `LOAD` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PAUSE

```
void PAUSE()
```

Pauzuje odtwarzanie dźwięku.

**Przykłady**

```
SND_SHIP_GAZ2^PAUSE();
```

**Kompatybilność:** `PAUSE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PLAY

```
void PLAY()
```

Rozpoczyna odtwarzanie dźwięku. Po starcie emitowany jest sygnał [`ONSTARTED`](#onstarted); po zakończeniu odtwarzania — [`ONFINISHED`](#onfinished). Jeżeli dźwięk już gra, jest najpierw zatrzymywany i odtwarzany od początku.

**Przykłady**

```
SNDTAKE^PLAY();
SNDATGOAL^PLAY();
```

**Kompatybilność:** `PLAY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESUME

```
void RESUME()
```

Wznawia odtwarzanie zatrzymane wcześniej przez [`PAUSE`](#pause).

**Przykłady**

```
SND_SHIP_GAZ2^RESUME();
```

**Kompatybilność:** `RESUME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETFREQ

```
void SETFREQ(INTEGER sampleRate)
```

Ustawia bieżące próbkowanie odtwarzanego dźwięku (w hercach). Wartość różna od próbkowania oryginalnego pliku zmienia wysokość i prędkość odtwarzania proporcjonalnie do stosunku obu wartości. Próbkowanie pliku oryginalnego silnik traktuje domyślnie jako `22050` Hz.

**Parametry**

- `sampleRate` — docelowe próbkowanie w Hz.

**Przykłady**

```
SNDENGINE0^SETFREQ(10025);
```

**Kompatybilność:** `SETFREQ` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### STOP

```
void STOP([BOOL emitSignal])
```

Zatrzymuje odtwarzanie dźwięku.

**Parametry**

- `emitSignal` — (opcjonalnie) jeżeli `FALSE`, sygnał [`ONFINISHED`](#onfinished) nie zostanie wyemitowany. Domyślnie sygnał jest emitowany.

**Przykłady**

```
SNDATGOAL^STOP(FALSE);
SNDIDLEREX^STOP();
```

**Kompatybilność:** `STOP` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONSTARTED

Wywoływany po rozpoczęciu odtwarzania dźwięku przez metodę [`PLAY`](#play).

### ONFINISHED

Wywoływany po zakończeniu odtwarzania (naturalnym lub przez metodę [`STOP`](#stop) bez argumentu `FALSE`).
