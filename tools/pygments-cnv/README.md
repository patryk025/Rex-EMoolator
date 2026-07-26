# Szablon lexera CNV dla Pygments

To mały, jeszcze niekompletny lexer skryptów Aidem Media. Pygments ładuje go
przez entry point z `pyproject.toml`, a `pymdownx.highlight` używany przez
MkDocs Material odnajduje go automatycznie pod aliasem `cnv`.

## Użycie w dokumentacji

Z katalogu głównego repozytorium:

```console
python -m pip install -r docs/requirements.txt
mkdocs serve
```

Po zmianie kodu lexera zrestartuj `mkdocs serve`, ponieważ lista pluginów
Pygments jest odkrywana przez metadane zainstalowanych pakietów.

W Markdown wystarczy podać alias języka:

````markdown
```cnv
OBJECT=FLAG
FLAG:TYPE=BOOL
FLAG:VALUE=TRUE
```
````

Nie jest potrzebny osobny plugin MkDocs ani wpis w `mkdocs.yml`.

## Tester

Tester używa prawdziwego, niezaszyfrowanego pliku
`assets/test-assets/scripts/Arrajki.cnv`. Sprawdza rejestrację aliasu,
bezstratną tokenizację bez `Token.Error` oraz kilka podstawowych kategorii
kolorowania.

```console
python -m unittest discover -s tools/pygments-cnv/tests -v
```
