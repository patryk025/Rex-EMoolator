#!/usr/bin/env python3
"""Audit internal links in docs/ — dead targets, dead anchors and slug collisions.

`mkdocs build --strict` does not validate heading anchors, so a link like
`SCENE.md#pause` stays silent even when the anchor never existed. This script
resolves every relative link and checks the fragment against the headings the
`toc` extension would actually emit (same slugify, same `_N` suffixing).

It also reports two mistakes that produce a *working* but wrong link:

  * a slug collision — two headings share a name, so the second one is only
    reachable through a positional `_N` suffix that shifts when headings move;
  * a link whose text names a type or method that has its own page or anchor,
    while the link points at the type index or at the top of a page.

With --site it re-verifies the rendered HTML instead of modelling the slugs,
which is the ground truth:

    .venv-docs/bin/mkdocs build --site-dir /tmp/site
    .venv-docs/bin/python tools/check_docs_links.py --site /tmp/site

Exit code is 1 when anything was reported.
"""

from __future__ import annotations

import argparse
import os
import re
import sys
from pathlib import Path
from urllib.parse import unquote, urlparse

from markdown.extensions.toc import slugify

ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"

FENCE = re.compile(r"^\s*(```|~~~)")
HEADING = re.compile(r"^(#{1,6})\s+(.*?)\s*$")
EXPLICIT_ID = re.compile(r"\{#([^}\s]+)[^}]*\}\s*$")
ATTR_ID = re.compile(r"\{#([^}\s]+)")
HTML_ID = re.compile(r"(?:id|name)=[\"']([^\"']+)[\"']")
LINK = re.compile(r"(?<!\!)\[([^\]\n]*)\]\(\s*<?([^)\s>]+)>?(?:\s+\"[^\"]*\")?\s*\)")
CODE_LINK = re.compile(r"(?<!\!)\[`([A-Za-z][A-Za-z0-9_.]*)`\]\(\s*([^)\s]+)\s*\)")
EXTERNAL = re.compile(r"^(https?:|mailto:|tel:|ftp:)")


def markdown_files(root: Path) -> list[Path]:
    return sorted(p for p in root.rglob("*.md"))


def outside_fences(path: Path):
    """Yield (line number, text), blanking out fenced code blocks."""
    in_fence = False
    for number, line in enumerate(path.read_text(encoding="utf-8").split("\n"), 1):
        if FENCE.match(line):
            in_fence = not in_fence
            continue
        yield number, ("" if in_fence else line)


def heading_text(raw: str) -> str:
    text = re.sub(r"\s*\{#[^}]*\}\s*$", "", raw)
    text = re.sub(r"`([^`]*)`", r"\1", text)
    text = re.sub(r"\[([^\]]*)\]\([^)]*\)", r"\1", text)
    return re.sub(r"\*+", "", text).strip()


def collect_anchors(path: Path) -> tuple[dict[str, str], list[tuple[str, list[str]]]]:
    """Return {anchor: heading text} plus the slug collisions found on the page."""
    anchors: dict[str, str] = {}
    seen: dict[str, list[str]] = {}
    for _, line in outside_fences(path):
        heading = HEADING.match(line)
        if heading:
            raw = heading.group(2)
            text = heading_text(raw)
            explicit = EXPLICIT_ID.search(raw)
            if explicit:
                anchors[explicit.group(1)] = text
                continue
            slug = slugify(text, "-")
            bucket = seen.setdefault(slug, [])
            bucket.append(text)
            anchors[slug if len(bucket) == 1 else f"{slug}_{len(bucket) - 1}"] = text
        else:
            for found in ATTR_ID.findall(line):
                anchors[found] = ""
        for found in HTML_ID.findall(line):
            anchors.setdefault(found, "")
    collisions = [(slug, texts) for slug, texts in seen.items() if len(texts) > 1]
    return anchors, collisions


def check_sources(docs: Path) -> list[str]:
    files = markdown_files(docs)
    anchors: dict[Path, dict[str, str]] = {}
    problems: list[str] = []

    for path in files:
        page_anchors, collisions = collect_anchors(path)
        anchors[path.resolve()] = page_anchors
        for slug, texts in collisions:
            problems.append(
                f"{path.relative_to(ROOT)}: kolizja slugu '{slug}' "
                f"({', '.join(texts)}) — drugie wystąpienie jest osiągalne tylko "
                f"jako '{slug}_1'; nadaj mu jawną kotwicę {{#{slug}-1}}"
            )

    type_pages = {
        lang: {p.stem for p in (docs / lang / "reference").glob("*.md") if p.stem != "index"}
        for lang in ("en", "pl")
        if (docs / lang / "reference").is_dir()
    }

    for path in files:
        lang = path.relative_to(docs).parts[0]
        for number, line in outside_fences(path):
            for text, target in LINK.findall(line):
                if EXTERNAL.match(target):
                    continue
                file_part, _, anchor = target.partition("#")
                resolved = (path.parent / file_part).resolve() if file_part else path.resolve()
                where = f"{path.relative_to(ROOT)}:{number}"
                if not resolved.exists():
                    problems.append(f"{where}: [{text}]({target}) — plik nie istnieje")
                    continue
                if resolved.is_dir():
                    problems.append(f"{where}: [{text}]({target}) — cel jest katalogiem")
                    continue
                if anchor and resolved.suffix == ".md" and anchor not in anchors.get(resolved, {}):
                    problems.append(f"{where}: [{text}]({target}) — brak takiej kotwicy w celu")

            for text, target in CODE_LINK.findall(line):
                file_part = target.split("#")[0]
                where = f"{path.relative_to(ROOT)}:{number}"
                if not file_part or EXTERNAL.match(target):
                    continue
                if text in type_pages.get(lang, ()) and Path(file_part).name != f"{text}.md":
                    problems.append(
                        f"{where}: [`{text}`]({target}) — typ ma własną stronę {text}.md"
                    )
                    continue
                if "#" in target:
                    continue
                resolved = (path.parent / file_part).resolve()
                if resolved.stem == text:
                    # A link to the page that documents `text` itself; its H1
                    # carries the same slug, but the page top is the right target.
                    continue
                slug = slugify(text, "-")
                if resolved.suffix == ".md" and slug in anchors.get(resolved, {}):
                    problems.append(
                        f"{where}: [`{text}`]({target}) — istnieje kotwica #{slug}, "
                        f"link prowadzi na górę strony"
                    )
    return problems


def check_site(site: Path) -> list[str]:
    pages: dict[Path, set[str]] = {}
    bodies: dict[Path, str] = {}
    for path in sorted(site.rglob("*.html")):
        html = path.read_text(encoding="utf-8", errors="replace")
        body = html.split("<main", 1)[-1].split("</main>", 1)[0]
        pages[path.resolve()] = set(re.findall(r'id="([^"]+)"', body))
        bodies[path.resolve()] = body

    problems: list[str] = []
    for path, body in bodies.items():
        for href in re.findall(r'<a[^>]+href="([^"]+)"', body):
            url = urlparse(href)
            if url.scheme or url.netloc or not url.fragment:
                continue
            if url.path:
                target = (path.parent / unquote(url.path)).resolve()
                if target.is_dir():
                    target = target / "index.html"
            else:
                target = path
            where = os.path.relpath(path, site)
            if target not in pages:
                problems.append(f"{where}: {href} — brak strony docelowej")
            elif unquote(url.fragment) not in pages[target]:
                problems.append(f"{where}: {href} — brak takiego id na stronie docelowej")
    return problems


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    parser.add_argument(
        "--site",
        type=Path,
        help="verify a rendered mkdocs site instead of the Markdown sources",
    )
    parser.add_argument("--docs", type=Path, default=DOCS, help="docs directory")
    args = parser.parse_args()

    problems = check_site(args.site) if args.site else check_sources(args.docs)
    for problem in problems:
        print(problem)
    scope = "site" if args.site else "źródła"
    print(f"{scope}: {len(problems)} problemów")
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
