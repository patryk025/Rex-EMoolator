from __future__ import annotations

import unittest
from pathlib import Path

from pygments import lex
from pygments.lexers import get_lexer_by_name
from pygments.token import Error, Keyword, Name, String

from rex_cnv_pygments import CnvLexer


REPOSITORY_ROOT = Path(__file__).resolve().parents[3]
SAMPLE_SCRIPT = REPOSITORY_ROOT / "assets/test-assets/scripts/Arrajki.cnv"


class CnvLexerTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.source = SAMPLE_SCRIPT.read_text(encoding="ascii")
        cls.tokens = list(lex(cls.source, CnvLexer()))

    def test_cnv_alias_is_registered_as_a_pygments_plugin(self) -> None:
        lexer = get_lexer_by_name("cnv")
        self.assertIsInstance(lexer, CnvLexer)

    def test_real_asset_is_tokenized_losslessly(self) -> None:
        self.assertEqual(self.source, "".join(value for _, value in self.tokens))
        self.assertFalse(
            any(token is Error for token, _ in self.tokens),
            "The template should leave unknown syntax as plain text, not errors",
        )

    def test_real_asset_exercises_basic_highlighting(self) -> None:
        token_pairs = {(token, value) for token, value in self.tokens}

        self.assertIn((Keyword.Declaration, "OBJECT"), token_pairs)
        self.assertIn((Name.Class, "MOUSE"), token_pairs)
        self.assertIn((Name.Attribute, "TYPE"), token_pairs)
        self.assertIn((Keyword.Type, "KEYBOARD"), token_pairs)
        self.assertIn((Name.Function, "SETTEXT"), token_pairs)
        self.assertIn((String.Double, '"MOUSE_TEST"'), token_pairs)


if __name__ == "__main__":
    unittest.main()
