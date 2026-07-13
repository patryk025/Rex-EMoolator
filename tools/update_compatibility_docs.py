#!/usr/bin/env python3
"""Add API availability from a compat/1 JSON export to the type reference.

The export is intentionally kept outside the repository: it is a generated,
multi-megabyte analysis artifact. This script turns it into small, reviewable
Markdown annotations and an audit report.
"""

from __future__ import annotations

import argparse
import json
import re
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
METHOD_HEADING = re.compile(r"^### (.+?)(?: \{#[^}]+\})?$", re.MULTILINE)
METHOD_NAME = re.compile(r"[A-Z][A-Z0-9_]+")
METHODS_SECTION = re.compile(r"^## (Metody|Methods)$", re.MULTILINE)
NEXT_SECTION = re.compile(r"^## ", re.MULTILINE)
DYNAMIC_EXTERNAL_TYPES = frozenset({"INERTIA", "KOLOROWANKA", "MATRIX", "WORLD"})
LIBRARY_ORDER = [
    "PIKLIB61.DLL",
    "PIKLIB71.DLL",
    "PIKLIB72.DLL",
    "PIKLIB8.DLL",
    "BlooMooWEB.dll",
    "BlooMooDLL.dll",
]
BASE_CLASS = "CMC"
GENERIC_CLASS = "CMC_Behaviour"
# Intermediate base classes whose methods the export lists separately from the
# concrete class (ADD/SET/... on CMC_Variable, BREAK/CHECK on CMC_BasicCondition).
EXTRA_BASE_CLASSES = {
    "BOOL": "CMC_Variable",
    "DOUBLE": "CMC_Variable",
    "INTEGER": "CMC_Variable",
    "STRING": "CMC_Variable",
    "CONDITION": "CMC_BasicCondition",
    "COMPLEXCONDITION": "CMC_BasicCondition",
}


@dataclass(frozen=True)
class Availability:
    present: int
    variants: int


class Compatibility:
    def __init__(self, payload: dict, documented: set[str]) -> None:
        self.generated_at = payload.get("generated_at", "unknown")
        self.schema = payload.get("schema", "unknown")
        self.documented = documented
        self.type_availability: dict[str, dict[str, Availability]] = defaultdict(dict)
        self.method_availability: dict[tuple[str, str], dict[str, Availability]] = defaultdict(dict)
        self.script_types: set[str] = set()
        self.library_names: list[str] = []
        self.unresolved_types: set[str] = set()
        self._load(payload["libraries"])

    @staticmethod
    def _canonical_classes(libraries: list[dict]) -> dict[str, str]:
        counts: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
        for library in libraries:
            for variant in library["variants"]:
                for entry in variant["api"].get("types", []):
                    script_type = entry.get("script_name")
                    cpp_class = entry.get("cpp_class")
                    if not script_type or not cpp_class:
                        continue
                    if cpp_class == GENERIC_CLASS and script_type != "BEHAVIOUR":
                        continue
                    counts[script_type][cpp_class] += 1
        return {script_type: max(classes, key=classes.get) for script_type, classes in counts.items()}

    def _load(self, libraries: list[dict]) -> None:
        canonical = self._canonical_classes(libraries)
        factory_types = {
            entry.get("script_name")
            for library in libraries
            for variant in library["variants"]
            for entry in variant["api"].get("types", [])
            if entry.get("script_name")
        }
        # Singletons (APPLICATION, SCENE, SYSTEM, ...) are never created via a
        # factory, so they have no `types` entry; match them by class name.
        singleton_types = self.documented - factory_types - DYNAMIC_EXTERNAL_TYPES
        for library in libraries:
            name = library["name"]
            self.library_names.append(name)
            variants = library["variants"]
            type_hits: dict[str, int] = defaultdict(int)
            method_hits: dict[tuple[str, str], int] = defaultdict(int)

            for variant in variants:
                classes = variant["api"].get("classes", {})
                # Some exports (PIKLIB61) carry camelCase C++ names instead of
                # the uppercase script names; the script name is the C++ name
                # uppercased, so normalise with .upper().
                base_methods = {
                    method["name"].upper()
                    for method in classes.get(BASE_CLASS, {}).get("methods", [])
                    if method.get("name")
                }
                for entry in variant["api"].get("types", []):
                    script_type = entry.get("script_name")
                    if not script_type:
                        continue
                    self.script_types.add(script_type)
                    type_hits[script_type] += 1
                    cpp_class = entry.get("cpp_class")
                    # The export sometimes fails to resolve the concrete class
                    # (null, or the generic CMC_Behaviour); fall back to the
                    # class this type maps to in the other variants.
                    if cpp_class in (None, GENERIC_CLASS) and script_type != "BEHAVIOUR":
                        cpp_class = canonical.get(script_type)
                    if cpp_class is None:
                        continue
                    extra_base = EXTRA_BASE_CLASSES.get(script_type)
                    method_names = {
                        method["name"].upper()
                        for owner in (cpp_class, extra_base)
                        for method in classes.get(owner, {}).get("methods", [])
                        if method.get("name")
                    }
                    # Every script object inherits the CMC base methods
                    # (GETNAME, CLONE, ...); the export lists them only on CMC.
                    method_names |= base_methods
                    for method_name in method_names:
                        method_hits[(script_type, method_name)] += 1

                class_by_script = {
                    (cls_name[4:] if cls_name.startswith("CMC_") else cls_name).upper(): cls_name
                    for cls_name in classes
                }
                for script_type in singleton_types:
                    cls_name = class_by_script.get(script_type)
                    if cls_name is None:
                        continue
                    self.script_types.add(script_type)
                    type_hits[script_type] += 1
                    method_names = {
                        method["name"].upper()
                        for method in classes[cls_name].get("methods", [])
                        if method.get("name")
                    }
                    method_names |= base_methods
                    for method_name in method_names:
                        method_hits[(script_type, method_name)] += 1

            total = len(variants)
            for script_type, present in type_hits.items():
                self.type_availability[script_type][name] = Availability(present, total)
            for key, present in method_hits.items():
                self.method_availability[key][name] = Availability(present, total)
        self.unresolved_types = self.script_types - {
            script_type for script_type, _ in self.method_availability
        }
        self.library_names.sort(
            key=lambda name: (
                LIBRARY_ORDER.index(name) if name in LIBRARY_ORDER else len(LIBRARY_ORDER),
                name,
            )
        )

    def describe(self, entries: dict[str, Availability], language: str) -> str:
        if not entries:
            return (
                "brak w przeanalizowanych bibliotekach z `compat.json`"
                if language == "pl"
                else "not present in the libraries analysed in `compat.json`"
            )
        labels = []
        for library in self.library_names:
            availability = entries.get(library)
            if availability is None:
                status = "❌"
            elif availability.present == availability.variants:
                status = "✅"
            else:
                status = f"⚠️ ({availability.present}/{availability.variants})"
            labels.append(f"`{library}` {status}")
        return ", ".join(labels)

    def method_text(self, script_type: str, method: str, language: str) -> str:
        label = "Kompatybilność" if language == "pl" else "Compatibility"
        availability = self.method_availability.get((script_type, method), {})
        if not availability and script_type in DYNAMIC_EXTERNAL_TYPES:
            detail = (
                "typ z doładowywanej biblioteki, poza zakresem bieżącego eksportu `compat.json`"
                if language == "pl"
                else "type from a dynamically loaded library, outside the scope of the current `compat.json` export"
            )
            return f"**{label}:** `{method}` - {detail}."
        if script_type in self.unresolved_types:
            detail = (
                "typ widoczny w eksporcie, ale bez rozpoznanej klasy C++ - brak danych o metodach"
                if language == "pl"
                else "type visible in the export, but with no resolved C++ class - no method data"
            )
            return f"**{label}:** `{method}` - {detail}."
        return f"**{label}:** `{method}` - {self.describe(availability, language)}."


def method_names(heading: str) -> list[str]:
    return METHOD_NAME.findall(heading)


def render_document(path: Path, compatibility: Compatibility, language: str) -> str:
    original = path.read_text(encoding="utf-8")
    script_type = path.stem
    methods_start = METHODS_SECTION.search(original)
    if not methods_start:
        return original

    section_end_match = NEXT_SECTION.search(original, methods_start.end())
    section_end = section_end_match.start() if section_end_match else len(original)
    prefix = original[: methods_start.end()]
    methods = original[methods_start.end() : section_end]
    suffix = original[section_end:]

    # Remove the previous generated pass before locating headings. This matters
    # when a document used a custom heading anchor that an older parser skipped.
    methods = re.sub(
        r"\n\*\*(Kompatybilność|Compatibility):\*\*[^\n]*",
        "",
        methods,
    )

    matches = list(METHOD_HEADING.finditer(methods))
    rebuilt: list[str] = []
    cursor = 0
    for index, match in enumerate(matches):
        rebuilt.append(methods[cursor : match.end()])
        body_end = matches[index + 1].start() if index + 1 < len(matches) else len(methods)
        body = methods[match.end() : body_end]
        names = method_names(match.group(1))
        annotations = "\n".join(compatibility.method_text(script_type, name, language) for name in names)
        rebuilt.append(body.rstrip() + "\n\n" + annotations + "\n\n")
        cursor = body_end
    rebuilt.append(methods[cursor:])
    rendered_methods = "".join(rebuilt).rstrip()
    updated = prefix + rendered_methods + ("\n\n" + suffix.lstrip() if suffix else "\n")
    return updated


def documented_types() -> set[str]:
    return {path.stem for path in (DOCS / "pl" / "reference").glob("*.md") if path.name != "index.md"}


def render_audit(compatibility: Compatibility) -> str:
    docs_types = documented_types()
    missing_pages = sorted(compatibility.script_types - docs_types)
    dynamic_documented = sorted(docs_types & DYNAMIC_EXTERNAL_TYPES)
    no_export_data = sorted(docs_types - compatibility.script_types - DYNAMIC_EXTERNAL_TYPES)
    lines = [
        "# Audyt kompatybilności API",
        "",
        "Ten plik jest generowany przez `tools/update_compatibility_docs.py` na podstawie zewnętrznego eksportu `compat.json`.",
        "",
        f"- Schemat eksportu: `{compatibility.schema}`",
        f"- Wygenerowano eksport: `{compatibility.generated_at}`",
        "- Zakres: BlooMooDLL, BlooMooWEB oraz PIKLIB 6.1, 7.1, 7.2 i 8.",
        "",
        "## Typy w eksporcie bez strony referencji",
        "",
        *[f"- `{name}`" for name in missing_pages],
        "",
        "## Opisane typy bez danych w eksporcie",
        "",
        *[f"- `{name}`" for name in no_export_data],
        "",
        "## Typy w eksporcie bez rozpoznanej klasy C++",
        "",
        "Eksport widzi te typy w fabrykach, ale nie potrafił dopasować ich klasy, więc nie ma danych o metodach.",
        "",
        *[f"- `{name}`" for name in sorted(compatibility.unresolved_types)],
        "",
        "## API doładowywane przez `CMC_ExternObject`",
        "",
        "Te biblioteki nie są widoczne jako zwykłe klasy w aktualnym eksporcie. Ich brak w zestawieniu nie mówi nic o dostępności metody.",
        "",
        *[f"- `{name}`" for name in dynamic_documented],
        "- `KOLOROWANKA` (znany typ bez strony referencji)",
        "",
        "Brak w pozostałych sekcjach nie dowodzi, że typ nie istniał. Oznacza tylko, że nie występuje w aktualnym zestawie analizowanych bibliotek.",
        "",
    ]
    return "\n".join(lines)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("compat_json", type=Path, help="path to a compat/1 JSON export")
    parser.add_argument("--check", action="store_true", help="fail if regeneration would change files")
    args = parser.parse_args()

    payload = json.loads(args.compat_json.read_text(encoding="utf-8"))
    if payload.get("schema") != "compat/1":
        raise SystemExit(f"Unsupported compatibility schema: {payload.get('schema')!r}")
    compatibility = Compatibility(payload, documented_types())

    changed = []
    for language in ("pl", "en"):
        for path in sorted((DOCS / language / "reference").glob("*.md")):
            if path.name == "index.md":
                continue
            updated = render_document(path, compatibility, language)
            if updated != path.read_text(encoding="utf-8"):
                changed.append(path)
                if not args.check:
                    path.write_text(updated, encoding="utf-8")
    audit_path = DOCS / "compatibility-audit.md"
    previous_audit = audit_path.read_text(encoding="utf-8") if audit_path.exists() else None
    audit = render_audit(compatibility)
    if audit != previous_audit:
        changed.append(audit_path)
        if not args.check:
            audit_path.write_text(audit, encoding="utf-8")

    if args.check and changed:
        raise SystemExit("Compatibility documentation is stale: " + ", ".join(str(path) for path in changed))
    print(f"Updated {len(changed)} file(s).")


if __name__ == "__main__":
    main()
