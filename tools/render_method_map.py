#!/usr/bin/env python3
"""Manually render the README SVG, or the data of the docs page, from an Atlas JSON export.

python3 tools/render_method_map.py tracker.json images/method-map.svg --lang en --date 2026-10-28
python3 tools/render_method_map.py tracker.json docs/assets/method-map/data.json --date 2026-10-28
The JSON feeds the interactive map on docs/{pl,en}/reference/method-map.md (method-map.js).
No server, third-party packages or build integration required.
"""
import argparse
from collections import Counter
from datetime import date
from html import escape
import json
from pathlib import Path


COLORS = {
    'registered': '#39cdb2', 'stub_candidate': '#efb75d', 'missing': '#344255',
    'partial': '#b69aff', 'implemented': '#67aaff',
}
COPY = {
    'en': dict(title='Emulator method map',
               unit='One small square = one method within a type', types='groups', methods='type / method pairs',
               registered='Registered', stub_candidate='Emulator stub', missing='No registration',
               partial='Partial', implemented='Reviewed implementation', verified='ASM / behavior evidence',
               unknown='No method data', metric='Registrations without stub flags / identified methods', nodata='No data', allmissing='No registrations', global_scope='COMMON METHODS', builtin_scope='@ STATEMENTS',
               snapshot='Snapshot', revision='Emulator revision'),
    'pl': dict(title='Mapa metod emulatora',
               unit='Jeden mały kwadrat = jedna metoda w obrębie typu', types='grup', methods='par typ / metoda',
               registered='Zarejestrowana', stub_candidate='Stub w emulatorze', missing='Brak rejestracji',
               partial='Częściowa', implemented='Przejrzana implementacja', verified='Dowód ASM / zachowania',
               unknown='Metody nierozpoznane', metric='Rejestracje bez flagi stubu / rozpoznane metody', nodata='Brak danych', allmissing='Brak rejestracji', global_scope='METODY WSPÓLNE', builtin_scope='INSTRUKCJE @',
               snapshot='Stan na', revision='Rewizja emulatora'),
}


def covered(t):
    return sum(m['status'] not in ('missing', 'stub_candidate') for m in t['methods'])


def groups(data):
    """Types A-Z; the common-methods (`*`) and `@` statement scopes sort before letters."""
    types = [t for t in data['types'] if t['name'] not in ('INSTANCE', 'VARIABLE')]
    return sorted(types, key=lambda t: t['name'])


def render(data, lang, stamp):
    c = COPY[lang]
    # INSTANCE is an emulator helper; CMC_Variable is a native base class, not a script type.
    types = groups(data)
    methods = [m for t in types for m in t['methods']]
    counts = Counter(m['status'] for m in methods)
    unknown = set(counts) - COLORS.keys()
    if unknown:
        raise ValueError(f'Unrecognized statuses: {unknown}')
    verified = lambda m: m['verification'] in ('asm_reviewed', 'runtime_tested')
    width, margin, gap, columns = 1440, 40, 16, 5
    card_width = (width - 2 * margin - gap * (columns - 1)) // columns
    grid_columns, step, square = 17, 13, 10
    parts = []

    def text(x, y, value, size=14, fill='#a9bacd', weight=400):
        parts.append(f'<text x="{x}" y="{y}" font-size="{size}" fill="{fill}" font-weight="{weight}">{escape(str(value))}</text>')

    def rect(x, y, w, h, fill, radius=0, extra=''):
        parts.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{radius}" fill="{fill}" {extra}/>')

    text(40, 64, c['title'], 36, '#edf4fc', 700)
    text(40, 96, c['unit'], 17)
    # Include any future review statuses without silently recoloring them.
    legend = [(s, COLORS[s], counts[s]) for s in COLORS if counts[s] or s in ('registered', 'stub_candidate', 'missing')]
    legend.append(('nodata', '#c4b5fd', sum(not t['methods'] for t in types)))
    legend.append(('verified', '#f4f8ff', sum(map(verified, methods))))
    for i, (status, color, count) in enumerate(legend):
        x, y = 40 + (i % 4) * 345, 134 + (i // 4) * 30
        rect(x, y - 11, 11, 11, 'none' if status == 'nodata' else color, 2,
             'stroke="#c4b5fd" stroke-dasharray="2 2"' if status == 'nodata' else '')
        if status == 'verified':
            rect(x + 3, y - 8, 5, 5, '#0b1420', 1)
        text(x + 21, y, f"{c[status]} · {count}", 15)
    y = 166 + ((len(legend) - 1) // 4) * 30
    order = {s: i for i, s in enumerate(COLORS)}
    for start in range(0, len(types), columns):
        row = types[start:start + columns]
        heights = [max(100, 63 + ((len(t['methods']) + grid_columns - 1) // grid_columns) * step) for t in row]
        height = max(heights)
        for col, t in enumerate(row):
            x = margin + col * (card_width + gap)
            rect(x, y, card_width, height, '#191b31' if not t['methods'] else '#132031', 10,
                 'stroke="#9b87d5" stroke-dasharray="5 4"' if not t['methods'] else 'stroke="#29394c"')
            name = {'*': c['global_scope'], '@': c['builtin_scope']}.get(t['name'], t['name'])
            text(x + 16, y + 26, name, 12 if len(name) > 21 else 14, '#edf4fc', 700)
            n = len(t['methods'])
            text(x + 16, y + 46, f'{covered(t)} / {n} · {round(100 * covered(t) / n)}%' if n else c['nodata'], 12, '#a9bacd' if n else '#c4b5fd')
            if not t['methods']:
                text(x + 16, y + 79, '?', 24, '#c4b5fd', 700)
                text(x + 41, y + 76, c['unknown'], 12, '#c4b5fd')
            for i, m in enumerate(sorted(t['methods'], key=lambda m: (order[m['status']], m['name']))):
                sx, sy = x + 16 + (i % grid_columns) * step, y + 59 + (i // grid_columns) * step
                title = escape(f"{t['name']}.{m['name']} — {c[m['status']]}" + (f"; {c['verified']}" if verified(m) else ''))
                parts.append(f'<g class="method" data-status="{m["status"]}"><title>{title}</title>')
                rect(sx, sy, square, square, COLORS[m['status']], 2,
                     'stroke="#f4f8ff" stroke-width="2"' if verified(m) else '')
                parts.append('</g>')
        y += height + gap
    text(40, y + 23, f"{c['snapshot']}: {stamp}", 12, '#8297af')
    height = y + 50
    metadata = escape(json.dumps({'date': stamp, 'emulator': {k: v for k, v in data['emulator'].items() if k != 'root'},
                                 'types': len(types), 'methods': len(methods), 'statuses': counts}, ensure_ascii=False))
    return (f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}" role="img" aria-labelledby="title desc">'
            f'<title id="title">{escape(c["title"])}</title><desc id="desc">{escape(c["unit"])}</desc>'
            f'<metadata>{metadata}</metadata><rect width="100%" height="100%" fill="#0b1420"/>'
            '<g font-family="DejaVu Sans,Arial,sans-serif">' + ''.join(parts) + '</g></svg>\n')


# Docs pages a card links to, where the reference names the type differently.
REFERENCE = Path(__file__).resolve().parents[1] / 'docs' / 'pl' / 'reference'
REFERENCE_ALIASES = {'BOOLEAN': 'BOOL'}


def reference_link(name):
    page = REFERENCE_ALIASES.get(name, name)
    return f'../{page}/' if (REFERENCE / f'{page}.md').exists() else None


def render_docs_data(data, stamp):
    """Dataset of the docs page: both languages' labels, statuses and the A-Z groups. Only
    presentation fields, no machine-local paths."""
    payload = {'date': stamp, 'emulator': {k: v for k, v in data['emulator'].items() if k in ('commit', 'dirty')},
               'copy': COPY, 'colors': COLORS,
               'types': [{'name': t['name'], 'ref': reference_link(t['name']),
                          'methods': [{k: m[k] for k in ('name', 'status', 'verification')} for m in t['methods']]}
                         for t in groups(data)]}
    return json.dumps(payload, ensure_ascii=False, separators=(',', ':')) + '\n'


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('input', type=Path)
    p.add_argument('output', type=Path)
    p.add_argument('--lang', choices=COPY, default='en')
    p.add_argument('--date', default=date.today().isoformat())
    args = p.parse_args()
    data = json.loads(args.input.read_text())
    if args.output.suffix.lower() == '.json':
        args.output.write_text(render_docs_data(data, args.date), encoding='utf-8')
    else:
        args.output.write_text(render(data, args.lang, args.date), encoding='utf-8')


if __name__ == '__main__':
    main()
