/* Interactive emulator method map for docs/{pl,en}/reference/method-map.md.
   Mounts into #method-map; the data is data.json next to this script, written by
   tools/render_method_map.py. Loaded on every page through extra_javascript, so it
   does nothing where the container is missing. */
(function () {
  'use strict';
  const source = document.currentScript && document.currentScript.src;
  const WORDS = {
    pl: {search: 'Typ lub metoda', filter: 'Pokaż', sort: 'Sortowanie', all: 'Wszystkie',
         alpha: 'Nazwa A–Z', coverage: 'Rejestracje ↓', size: 'Liczba metod ↓', reset: 'Wyczyść filtry',
         empty: 'Brak wyników dla wybranych filtrów.', shown: 'widoczne', showing: 'Widoczne',
         groups: 'grup', methods: 'metod', noReviews: 'Bez aktualnego potwierdzenia zgodności',
         legend: 'Legenda', failed: 'Nie udało się wczytać danych mapy'},
    en: {search: 'Type or method', filter: 'Show', sort: 'Sort by', all: 'All',
         alpha: 'Name A–Z', coverage: 'Registrations ↓', size: 'Method count ↓', reset: 'Reset filters',
         empty: 'No results for these filters.', shown: 'shown', showing: 'Showing',
         groups: 'groups', methods: 'methods', noReviews: 'No current evidence of behavioral equivalence',
         legend: 'Legend', failed: 'Could not load the map data'},
  };

  function node(tag, cls, text) {
    const e = document.createElement(tag);
    if (cls) e.className = cls;
    if (text !== undefined) e.textContent = text;
    return e;
  }

  function init() {
    const root = document.getElementById('method-map');
    if (!root || root.dataset.ready) return;
    root.dataset.ready = '1';
    const w = WORDS[(document.documentElement.lang || '').toLowerCase().startsWith('pl') ? 'pl' : 'en'];
    fetch(new URL('data.json', source))
      .then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
      .then(data => mount(root, data, w))
      .catch(e => { root.replaceChildren(node('p', 'mm-error', w.failed + ': ' + e.message)); });
  }

  function mount(root, data, w) {
    const c = data.copy[w === WORDS.pl ? 'pl' : 'en'];
    const order = Object.keys(data.colors);
    const total = data.types.reduce((n, t) => n + t.methods.length, 0);
    const covered = t => t.methods.filter(m => !['missing', 'stub_candidate'].includes(m.status)).length;
    const verified = m => ['asm_reviewed', 'runtime_tested'].includes(m.verification);
    const name = t => ({'*': c.global_scope, '@': c.builtin_scope})[t.name] || t.name;
    const byName = (a, b) => a.t.name < b.t.name ? -1 : a.t.name > b.t.name ? 1 : 0;  // raw: * and @ lead

    const summary = node('p', 'mm-summary');
    summary.append(node('strong', '', data.types.length + ' ' + c.types + ' / ' + total + ' ' + c.methods),
                   document.createTextNode(' · ' + c.snapshot + ': ' + data.date +
                     (data.emulator && data.emulator.commit ? ' · ' + c.revision + ' ' + data.emulator.commit.slice(0, 8) : '')));
    const legend = node('div', 'mm-legend');
    legend.setAttribute('aria-label', w.legend);
    for (const s of [...order, 'unknown', 'verified']) {
      const count = s === 'unknown' ? data.types.filter(t => !t.methods.length).length
        : data.types.flatMap(t => t.methods).filter(m => s === 'verified' ? verified(m) : m.status === s).length;
      if (!count && !['registered', 'missing', 'stub_candidate', 'unknown', 'verified'].includes(s)) continue;
      const item = node('span', 'mm-key'), swatch = node('i', 'mm-swatch mm-' + s);
      if (data.colors[s]) swatch.style.setProperty('--color', data.colors[s]);
      item.append(swatch, document.createTextNode((s === 'unknown' ? c.nodata : c[s]) + ' · ' + count));
      legend.append(item);
    }

    const controls = node('div', 'mm-controls');
    const field = (label, input, cls) => { const l = node('label', cls); l.append(node('span', '', label), input); controls.append(l); return input; };
    const search = field(w.search, node('input'), 'mm-search');
    search.type = 'search'; search.autocomplete = 'off'; search.placeholder = 'ANIMO, SETPOSITION…';
    const filter = field(w.filter, node('select'));
    for (const [value, label] of [['', w.all], ...order.map(s => [s, c[s]]), ['unknown', c.nodata], ['verified', c.verified]])
      filter.add(new Option(label, value));
    const sort = field(w.sort, node('select'));
    for (const [value, label] of [['alpha', w.alpha], ['coverage', w.coverage], ['size', w.size]]) sort.add(new Option(label, value));
    const reset = node('button', 'mm-reset md-button', w.reset);
    reset.type = 'button';
    controls.append(reset);

    const metric = node('p', 'mm-metric', c.metric);
    const result = node('p', 'mm-result');
    result.setAttribute('role', 'status'); result.setAttribute('aria-live', 'polite');
    const cards = node('div', 'mm-cards'), empty = node('p', 'mm-empty', w.empty);
    const tip = node('div', 'mm-tip');
    tip.setAttribute('role', 'tooltip'); tip.id = 'mm-tip'; tip.hidden = true;
    document.body.append(tip);
    root.replaceChildren(summary, legend, controls, metric, result, cards, empty);

    let active = null;
    function hideTip() { if (active) active.removeAttribute('aria-describedby'); active = null; tip.hidden = true; }
    function positionTip() {
      if (!active || tip.hidden) return;
      const r = active.getBoundingClientRect(), tw = tip.offsetWidth, th = tip.offsetHeight;
      if (r.bottom < 0 || r.top > innerHeight) { hideTip(); return; }
      tip.style.left = Math.max(12, Math.min(r.left, innerWidth - tw - 12)) + 'px';
      tip.style.top = Math.max(12, r.bottom + th + 20 < innerHeight ? r.bottom + 10 : r.top - th - 10) + 'px';
    }
    function showTip(button, t, m) {
      hideTip(); active = button; button.setAttribute('aria-describedby', tip.id);
      tip.replaceChildren(node('strong', '', name(t) + '.' + m.name), node('span', '', c[m.status]),
                          node('span', '', verified(m) ? c.verified : w.noReviews));
      tip.hidden = false; positionTip();
    }

    function render() {
      hideTip();
      const q = search.value.trim().toUpperCase(), f = filter.value, s = sort.value;
      let rows = data.types.map(t => ({t, methods: t.methods.filter(m =>
        (!q || name(t).toUpperCase().includes(q) || m.name.toUpperCase().includes(q)) &&
        (!f || (f === 'verified' ? verified(m) : m.status === f)))}));
      rows = rows.filter(({t, methods}) => t.methods.length ? methods.length > 0
        : (!f || f === 'unknown') && (!q || name(t).toUpperCase().includes(q)));
      rows.sort((a, b) => {
        if (s === 'alpha') return byName(a, b);
        if (!a.t.methods.length !== !b.t.methods.length) return a.t.methods.length ? -1 : 1;
        const delta = s === 'size' ? b.t.methods.length - a.t.methods.length
          : (b.t.methods.length ? covered(b.t) / b.t.methods.length : 0) - (a.t.methods.length ? covered(a.t) / a.t.methods.length : 0);
        return delta || byName(a, b);
      });
      const fragment = document.createDocumentFragment();
      let visible = 0;
      for (const {t, methods} of rows) {
        const n = t.methods.length, allMissing = n && t.methods.every(m => m.status === 'missing');
        const card = node('section', 'mm-card' + (!n ? ' mm-unknown' : allMissing ? ' mm-all-missing' : ''));
        const title = node('h3', 'mm-title');
        if (t.ref) { const a = node('a', '', name(t)); a.href = t.ref; title.append(a); } else title.textContent = name(t);
        const meta = node('p', 'mm-meta', n ? covered(t) + ' / ' + n + ' · ' + Math.round(100 * covered(t) / n) + '%' : c.nodata);
        if (allMissing) meta.append(node('span', 'mm-note', c.allmissing));
        if (n && methods.length !== n) meta.append(node('span', 'mm-note', w.shown + ': ' + methods.length + ' / ' + n));
        card.append(title, meta);
        if (!n) { const e = node('div', 'mm-empty-data'); e.append(node('span', 'mm-question', '?'), node('span', '', c.unknown)); card.append(e); }
        const tiles = node('div', 'mm-tiles');
        tiles.setAttribute('aria-label', name(t));
        methods.sort((a, b) => order.indexOf(a.status) - order.indexOf(b.status) || (a.name < b.name ? -1 : a.name > b.name ? 1 : 0));
        methods.forEach((m, i) => {
          const button = node('button', 'mm-tile' + (verified(m) ? ' mm-verified' : ''));
          button.type = 'button'; button.dataset.status = m.status; button.tabIndex = i === 0 ? 0 : -1;
          if (data.colors[m.status]) button.style.setProperty('--color', data.colors[m.status]);
          button.setAttribute('aria-label', name(t) + '.' + m.name + ' — ' + c[m.status]);
          button.addEventListener('pointerenter', e => { if (e.pointerType !== 'touch') showTip(button, t, m); });
          button.addEventListener('pointerleave', () => { if (document.activeElement !== button) hideTip(); });
          button.addEventListener('focus', () => showTip(button, t, m));
          button.addEventListener('blur', hideTip);
          button.addEventListener('click', () => showTip(button, t, m));
          button.addEventListener('keydown', e => {
            const buttons = [...tiles.children], at = buttons.indexOf(button);
            const cols = Math.max(1, getComputedStyle(tiles).gridTemplateColumns.split(' ').length);
            const offsets = {ArrowRight: 1, ArrowLeft: -1, ArrowDown: cols, ArrowUp: -cols};
            const next = e.key === 'Home' ? 0 : e.key === 'End' ? buttons.length - 1
              : e.key in offsets ? Math.max(0, Math.min(buttons.length - 1, at + offsets[e.key])) : null;
            if (next !== null) { e.preventDefault(); buttons.forEach(b => { b.tabIndex = -1; }); buttons[next].tabIndex = 0; buttons[next].focus(); }
          });
          tiles.append(button);
        });
        visible += methods.length;
        card.append(tiles);
        fragment.append(card);
      }
      cards.replaceChildren(fragment);
      empty.hidden = rows.length > 0;
      result.textContent = w.showing + ': ' + rows.length + ' / ' + data.types.length + ' ' + w.groups +
        ' · ' + visible + ' / ' + total + ' ' + w.methods;
    }

    search.addEventListener('input', render);
    filter.addEventListener('change', render);
    sort.addEventListener('change', render);
    reset.addEventListener('click', () => { search.value = ''; filter.value = ''; sort.value = 'alpha'; render(); });
    document.addEventListener('keydown', e => { if (e.key === 'Escape') hideTip(); });
    document.addEventListener('pointerdown', e => { if (!e.target.closest('.mm-tile')) hideTip(); });
    window.addEventListener('scroll', positionTip, true);
    window.addEventListener('resize', positionTip);
    render();
  }

  if (window.document$ && typeof window.document$.subscribe === 'function') window.document$.subscribe(init);
  else init();
})();
