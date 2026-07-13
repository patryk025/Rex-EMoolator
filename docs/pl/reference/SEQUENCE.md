# SEQUENCE

Sekwencja animacji. Plik `.SEQ` zawiera **zdarzenia sekwencji** — opisy ciągów animacji ([`ANIMO`](ANIMO.md)) odtwarzanych jednocześnie z towarzyszącymi im efektami dźwiękowymi ([`SOUND`](SOUND.md)). Pozwala synchronizować ze sobą obraz i dźwięk w jednej, sterowanej skryptowo jednostce.

## Pola

### FILENAME

```
STRING FILENAME
```

Ścieżka do pliku `.SEQ` z definicją sekwencji.

## Metody

### GETEVENTNAME

```
STRING GETEVENTNAME()
```

Zwraca nazwę aktualnie odtwarzanego zdarzenia sekwencji.

**Zwraca**: nazwa zdarzenia.

**Przykłady**

```
SEQSFX^GETEVENTNAME();
```

**Kompatybilność:** `GETEVENTNAME` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETPLAYING

```
STRING GETPLAYING()
```

Zwraca nazwę zmiennej typu [`ANIMO`](ANIMO.md) odtwarzanej w ramach aktualnie aktywnego zdarzenia. Jeżeli żadne zdarzenie nie jest aktywne, zwracany jest pusty ciąg.

**Zwraca**: nazwa animacji lub `""`.

**Kompatybilność:** `GETPLAYING` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### HIDE

```
void HIDE()
```

Ukrywa wszystkie animacje, które należą do sekwencji.

**Przykłady**

```
SEQJEAN^HIDE();
SEQKRET^HIDE();
```

**Kompatybilność:** `HIDE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### ISPLAYING

```
BOOL ISPLAYING()
```

Sprawdza, czy sekwencja jest aktualnie odtwarzana.

**Zwraca**: [`BOOL`](BOOL.md) — `TRUE`, jeżeli sekwencja jest w trakcie odtwarzania.

**Przykłady**

```
SEQBLANK^ISPLAYING();
SEQMANDOLINA^ISPLAYING();
```

**Kompatybilność:** `ISPLAYING` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PAUSE

```
void PAUSE()
```

Wstrzymuje odtwarzanie sekwencji.

**Przykłady**

```
SEQCS^PAUSE();
```

**Kompatybilność:** `PAUSE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### PLAY

```
void PLAY(STRING eventName)
```

Rozpoczyna odtwarzanie zdarzenia sekwencji o podanej nazwie. Po starcie emitowany jest sygnał [`ONSTARTED`](#onstarted) z nazwą zdarzenia.

**Parametry**

- `eventName` — nazwa zdarzenia z pliku `.SEQ`.

**Przykłady**

```
GADAJA2^PLAY("KOGF2");
SEQNARRATOR^PLAY(VARSTRING0);
SEQLAB^PLAY(["PLAYER"+VARINT0]);
SEQREKSIO^PLAY($1);
```

**Kompatybilność:** `PLAY` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### RESUME

```
void RESUME()
```

Wznawia sekwencję wstrzymaną przez [`PAUSE`](#pause).

**Przykłady**

```
SEQCS^RESUME();
```

**Kompatybilność:** `RESUME` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETFREQ

```
void SETFREQ(INTEGER sampleRate)
```

Ustawia próbkowanie odtwarzania dźwięku przypisanego do aktualnie aktywnego zdarzenia sekwencji. Działanie równoważne wywołaniu [`SETFREQ`](SOUND.md#setfreq) na obiekcie [`SOUND`](SOUND.md) tego zdarzenia.

**Parametry**

- `sampleRate` — docelowe próbkowanie w Hz.

**Kompatybilność:** `SETFREQ` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETPAN

```
void SETPAN(INTEGER pan)
```

Ustawia panoramę stereo (rozkład lewy–prawy) dźwięku aktywnego zdarzenia. Wartość `400` odpowiada panoramie wycentrowanej, `0` — pełnemu kanałowi lewemu, `800` — pełnemu kanałowi prawemu.

**Parametry**

- `pan` — wartość panoramy w zakresie `0–800`.

**Kompatybilność:** `SETPAN` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SETVOLUME

```
void SETVOLUME(INTEGER volume)
```

Ustawia głośność dźwięku aktywnego zdarzenia. Wartość `1600` odpowiada maksymalnej głośności, `0` — wyciszeniu.

**Parametry**

- `volume` — wartość głośności w zakresie `0–1600`.

**Kompatybilność:** `SETVOLUME` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### SHOW

```
void SHOW()
```

Pokazuje wszystkie animacje należące do sekwencji.

**Kompatybilność:** `SHOW` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### STOP

```
void STOP([BOOL emitSignal])
```

Zatrzymuje odtwarzanie sekwencji.

**Parametry**

- `emitSignal` — (opcjonalnie) jeżeli `FALSE`, sygnał [`ONFINISHED`](#onfinished) nie zostanie wyemitowany. Domyślnie sygnał jest emitowany.

**Przykłady**

```
SEQBLANK^STOP(FALSE);
SEQMENU^STOP(TRUE);
SEQZMIANAWAGIREX^STOP();
```

**Kompatybilność:** `STOP` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSTARTED

Wywoływany po rozpoczęciu odtwarzania zdarzenia sekwencji. Argumentem (`$1`) jest nazwa rozpoczętego zdarzenia.

### ONFINISHED

Wywoływany po zakończeniu odtwarzania zdarzenia sekwencji (naturalnym lub przez metodę [`STOP`](#stop) bez argumentu `FALSE`). Argumentem (`$1`) jest nazwa zakończonego zdarzenia. Sygnał jest [parametryzowany](../engine/events.md#sygnaly-parametryzowane) tą nazwą, więc można podpiąć obsługę pod konkretne zdarzenie:

```
SEKWENCJA:ONFINISHED^IDLE=BEHAFTERIDLE
```

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
