# Pygments lexer template for CNV

[Polski](README.pl.md) | **English**

A small, still incomplete lexer for Aidem Media scripts. Pygments loads it
through the entry point declared in `pyproject.toml`, and `pymdownx.highlight`
used by MkDocs Material picks it up automatically under the `cnv` alias.

## Use in the documentation

From the repository root:

```console
python -m pip install -r docs/requirements.txt
mkdocs serve
```

After changing the lexer code, restart `mkdocs serve` — the list of Pygments
plugins is discovered from the metadata of installed packages.

In Markdown it is enough to name the language alias:

````markdown
```cnv
OBJECT=FLAG
FLAG:TYPE=BOOL
FLAG:VALUE=TRUE
```
````

No separate MkDocs plugin or `mkdocs.yml` entry is required.

## Token inspector

`lexer_lab.py` is a local editor that queries Pygments on every change and
shows the resulting token stream. The lexer module is reloaded on every
request, so saving `lexer.py` is enough — no server restart needed.

```console
python tools/pygments-cnv/lexer_lab.py
```

Clicking a row selects the matching fragment in the editor, and placing the
caret in the code highlights its token in the list. Suspicious tokens are shown
in red (`Error`, `Other`, and `Text` holding non-whitespace characters) — these
mark the places where the `(r".", Text)` fallback rule silently swallows
unknown syntax.

## Tests

The tests run against a real, unencrypted
`assets/test-assets/scripts/Arrajki.cnv`. They check that the alias is
registered, that tokenization is lossless and free of `Token.Error`, and that a
few basic highlighting categories work.

```console
python -m unittest discover -s tools/pygments-cnv/tests -v
```
