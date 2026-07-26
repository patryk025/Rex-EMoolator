"""A deliberately small, extendable Pygments lexer for CNV scripts."""

from __future__ import annotations

import re

from pygments.lexer import RegexLexer, bygroups, words
from pygments.token import (
    Comment,
    Keyword,
    Name,
    Number,
    Operator,
    Punctuation,
    String,
    Text,
    Whitespace,
)


class CnvLexer(RegexLexer):
    """Highlight the common building blocks of Aidem Media scripts.

    This is just a template rather than a complete language definition.
    """

    name = "Aidem Media CNV"
    aliases = ["cnv", "aidem-cnv"]
    filenames = ["*.cnv", "*.def", "*.class", "*.seq"]
    mimetypes = ["text/x-aidem-cnv"]
    url = "https://github.com/patryk025/Rex-EMoolator"

    flags = re.IGNORECASE | re.MULTILINE

    # it's just a starter, coloring works, but it's not 100% correct
    # we need to track brackets nesting and many wild hacks used by Aidem Media
    TYPES = (
        "ANIMO",
        "APPLICATION",
        "ARRAY",
        "BEHAVIOUR",
        "BOOL",
        "BOOLEAN",
        "BUTTON",
        "CANVAS_OBSERVER",
        "CLASS",
        "CNVLOADER",
        "COMPLEXCONDITION",
        "CONDITION",
        "DATABASE",
        "DOUBLE",
        "EPISODE",
        "EXPRESSION",
        "FONT",
        "GROUP",
        "IMAGE",
        "INERTIA",
        "INTEGER",
        "KEYBOARD",
        "MATRIX",
        "MOUSE",
        "MULTIARRAY",
        "PATTERN",
        "RAND",
        "SCENE",
        "SEQUENCE",
        "SOUND",
        "STATICFILTER",
        "STRING",
        "STRUCT",
        "SYSTEM",
        "TEXT",
        "TIMER",
        "VECTOR",
        "VIRTUALGRAPHICSOBJECT",
        "WORLD",
    )

    CONTROL_FUNCTIONS = (
        "BOOL",
        "BREAK",
        "CONTINUE",
        "DOUBLE",
        "FOR",
        "IF",
        "INT",
        "LOOP",
        "ONEBREAK",
        "RETURN",
        "STRING",
        "WHILE",
    )

    tokens = {
        "root": [
            (r"^[ \t]*#.*$", Comment.Single),
            (
                r"^([ \t]*)(OBJECT)([ \t]*)(=)([ \t]*)([A-Z_][A-Z0-9_.]*)",
                bygroups(
                    Whitespace,
                    Keyword.Declaration,
                    Whitespace,
                    Operator,
                    Whitespace,
                    Name.Class,
                ),
            ),
            (
                r"^([ \t]*)([A-Z_][A-Z0-9_.]*)(:)([A-Z_][A-Z0-9_.]*)"
                r"(?:(\^)([^=\s]+))?([ \t]*)(=)",
                bygroups(
                    Whitespace,
                    Name.Variable,
                    Punctuation,
                    Name.Attribute,
                    Punctuation,
                    Name.Label,
                    Whitespace,
                    Operator,
                ),
            ),
            (r'"(?:[^"\n]|"")*"', String.Double),
            (r"![_']", Operator),
            (r"![^;\n]*(?:;|$)", Comment.Single),
            (
                words(CONTROL_FUNCTIONS, prefix=r"@", suffix=r"\b"),
                Keyword.Reserved,
            ),
            (words(TYPES, prefix=r"\b", suffix=r"\b"), Keyword.Type),
            (words(("FALSE", "TRUE"), prefix=r"\b", suffix=r"\b"), Keyword.Constant),
            (r"\$[0-9]+", Name.Variable.Magic),
            (words(("THIS", "_I_"), prefix=r"\b", suffix=r"\b"), Name.Builtin.Pseudo),
            (r"(\^)([A-Z_][A-Z0-9_]*)", bygroups(Punctuation, Name.Function)),
            (r"-?(?:\d+\.\d*|\.\d+|\d+)(?:[ED][+-]?\d+)?", Number),
            (r"&&|\|\||<_|>_|<'|>'|[+\-*/'<>?]", Operator),
            (r"[=:^,;(){}\[\]|@]", Punctuation),
            (r"\*", Operator),
            (r"[A-Z_][A-Z0-9_.]*", Name),
            (r"[ \t\r\n]+", Whitespace),
            # A fallback keeps an unfinished template useful on real-world
            # files: unknown syntax remains plain text, never Token.Error.
            (r".", Text),
        ]
    }

    @staticmethod
    def analyse_text(text: str) -> float:
        """Give Pygments a conservative hint for automatic file detection."""
        if re.search(r"(?im)^[ \t]*OBJECT[ \t]*=", text):
            return 0.8
        return 0.0
