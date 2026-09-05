# tools

Narzędzia deweloperskie i diagnostyczne. Nie są częścią budowanej aplikacji.

| Narzędzie | Do czego służy |
|---|---|
| `check_docs_links.py` | audyt linków w `docs/` — martwe pliki i kotwice, kolizje slugów, linki celujące w spis typów zamiast w stronę typu. Uruchamiaj przez `.venv-docs/bin/python`; `--site KATALOG` weryfikuje wyrenderowany serwis zamiast źródeł. |
| `update_compatibility_docs.py` | przenosi dostępność API z zewnętrznego eksportu compat/1 JSON do referencji typów i generuje `docs/compatibility-audit.md`. |
| `piklib8-probe/` | sonda do `PIKLIB8.DLL` używana przy odtwarzaniu zachowań oryginalnego silnika. |
| `riwc_physics_trace_frida.js` | skrypt Fridy zrzucający przebieg fizyki z oryginalnej gry (por. `PhysicsTraceWriter`). |
