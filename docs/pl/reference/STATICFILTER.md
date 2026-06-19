# STATICFILTER

Filtr graficzny — efekt nakładany na zmienne [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md). Każda instancja filtra ma jeden rodzaj efektu (pole [`ACTION`](#action)) i zbiór parametrów ustawianych metodą [`SETPROPERTY`](#setproperty). Filtr wiąże się z konkretną grafiką przez [`LINK`](#link) — od tego momentu wszystkie zmiany właściwości będą wpływały na widok grafiki w czasie rzeczywistym.

Jeden obiekt `STATICFILTER` można powiązać równocześnie z wieloma grafikami.

## Pola

### ACTION

```
STRING ACTION
```

Rodzaj efektu filtra. Dostępne wartości (na podstawie `sub_100A4490` w bibliotece Piklib8):

| Wartość | Efekt |
| --- | --- |
| `COLORCHANNEL` | manipulacja kanałami RGB |
| `GRAYSCALE` | konwersja na odcienie szarości |
| `BLUR` | rozmycie |
| `ROTATE` | obrót |
| `SCALE` | skalowanie |
| `NEGATIVE` | negatyw |
| `RANDOMJITTER` | losowe drgania pikseli |
| `WAVES` | efekt fali |

## Metody

### LINK

```
void LINK(STRING objectName)
```

Wiąże filtr ze zmienną [`ANIMO`](ANIMO.md) lub [`IMAGE`](IMAGE.md). Bieżące ustawienia parametrów filtra zostają przekazane do nowo utworzonego efektu i zaczynają działać natychmiast.

**Parametry**

- `objectName` — nazwa zmiennej graficznej.

**Przykłady**

```
FJITTER^LINK("IMGZOOM");
FROTATE^LINK(ARRCARS^GET(0));
```

### SETPROPERTY

```
void SETPROPERTY(STRING propertyName, mixed value)
```

Ustawia parametr filtra. Akceptowane nazwy parametrów zależą od wybranej akcji ([`ACTION`](#action)):

| Parametr | Typ | Filtry |
| --- | --- | --- |
| `CANUNDO` | [`BOOL`](BOOL.md) | wszystkie |
| `CURRENTFRAME` | [`BOOL`](BOOL.md) | wszystkie |
| `CHANNELS` | [`STRING`](STRING.md) | `COLORCHANNEL` |
| `MAXJITTER` | [`INTEGER`](INTEGER.md) | `RANDOMJITTER` |
| `BLUR` | [`INTEGER`](INTEGER.md) | `BLUR` |
| `ANGLE` | [`INTEGER`](INTEGER.md) | `ROTATE` |
| `BYCENTER` | [`BOOL`](BOOL.md) | `ROTATE`, `SCALE` |
| `FACTORX` | [`INTEGER`](INTEGER.md) | `SCALE` |
| `FACTORY` | [`INTEGER`](INTEGER.md) | `SCALE` |

**Parametry**

- `propertyName` — nazwa ustawianego parametru.
- `value` — nowa wartość parametru.

**Przykłady**

```
FCOLOR^SETPROPERTY("CANUNDO","TRUE");
FCOLOR^SETPROPERTY("CHANNELS","B");
FJITTER^SETPROPERTY("MAXJITTER",7);
FROTATE^SETPROPERTY("ANGLE",IKONANGLE);
```

### UNLINK

```
void UNLINK(STRING objectName)
```

Zrywa powiązanie filtra ze zmienną graficzną — usuwa efekt z grafiki.

**Parametry**

- `objectName` — nazwa zmiennej graficznej.

**Przykłady**

```
FROTATE^UNLINK("ANNKON");
FROTATE^UNLINK(ARRCARS^GET(VARPLAYER));
```

## Sygnały

### ONINIT

Wywoływany w momencie inicjalizacji obiektu.

### ONSIGNAL

Wywoływany po otrzymaniu sygnału (zobacz [Zdarzenia i sygnały](../engine/events.md#onsignal)).
