# SYSTEM

Wbudowany obiekt udostępniający informacje o systemie operacyjnym. Dostępny pod globalną nazwą `SYSTEM` z dowolnego kontekstu (zobacz [Obiekty wbudowane](../engine/globals.md#obiekty-wbudowane)).

## Metody

### GETDATE

```
INTEGER GETDATE()
```

Zwraca bieżącą datę zakodowaną jako liczba całkowita w formacie `(rok-2000)*10000 + miesiąc*100 + dzień`. Na przykład `26 marca 2026` to `260326`.

**Zwraca**: zakodowaną datę.

**Kompatybilność:** `GETDATE` - `PIKLIB61.DLL` ✅, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETMHZ

```
INTEGER GETMHZ()
```

Zwraca taktowanie procesora w megahercach.

**Zwraca**: częstotliwość CPU w MHz.

**Kompatybilność:** `GETMHZ` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ❌, `PIKLIB72.DLL` ❌, `PIKLIB8.DLL` ⚠️ (8/10), `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.

### GETSYSTEMTIME

```
INTEGER GETSYSTEMTIME()
```

Zwraca czas pracy systemu operacyjnego w milisekundach.

**Zwraca**: uptime w milisekundach.

**Kompatybilność:** `GETSYSTEMTIME` - `PIKLIB61.DLL` ❌, `PIKLIB71.DLL` ✅, `PIKLIB72.DLL` ✅, `PIKLIB8.DLL` ✅, `BlooMooWEB.dll` ✅, `BlooMooDLL.dll` ✅.
