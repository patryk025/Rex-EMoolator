from __future__ import annotations

import tempfile
import unittest
from pathlib import Path

from check_docs_links import check_site


class RenderedSiteLinkTest(unittest.TestCase):
    def test_reports_missing_navigation_target(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            site = Path(directory)
            (site / "index.html").write_text(
                '<main><a href="missing/">Missing page</a></main>',
                encoding="utf-8",
            )

            problems = check_site(site)

        self.assertEqual(1, len(problems))
        self.assertIn("missing/", problems[0])
        self.assertIn("brak strony docelowej", problems[0])

    def test_accepts_page_and_anchor_targets(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            site = Path(directory)
            (site / "index.html").write_text(
                '<link rel="canonical" href="https://example.test/project/">'
                '<main><a href="child/">Child</a>'
                '<a href="child/#section">Section</a></main>',
                encoding="utf-8",
            )
            child = site / "child"
            child.mkdir()
            (child / "index.html").write_text(
                '<link rel="canonical" href="https://example.test/project/child/">'
                '<main><h1 id="section">Section</h1>'
                '<a href="/project/">Home</a></main>',
                encoding="utf-8",
            )

            problems = check_site(site)

        self.assertEqual([], problems)
