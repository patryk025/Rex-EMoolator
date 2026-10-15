#!/usr/bin/env python3
"""Desktop viewer. Run: python tools/trace-viewer/viewer.py [trace.jsonl]."""
from __future__ import annotations

import argparse
from bisect import bisect_left, bisect_right
import json
from pathlib import Path
import queue
import threading
import tkinter as tk
from tkinter import filedialog, messagebox, ttk

from trace_model import Trace, pretty_script, summary


class Viewer(tk.Tk):
    PAGE = 250

    def __init__(self, initial=None):
        super().__init__()
        self.title("Rex Trace Viewer")
        self.geometry("1440x900")
        self.minsize(950, 620)
        self.configure(bg="#141821")
        self.trace = None
        self.index = -1
        self.page = 0
        self.matches = []
        self.errors = []
        self.updates = []
        self.results = queue.Queue()
        self.generation = 0
        self.loading = False
        self.filter_job = None
        self.values = {}
        self.detail_objects = {}
        self._style()
        self._build()
        self.protocol("WM_DELETE_WINDOW", self.close)
        self.bind("<Control-o>", lambda _: self.open_file())
        self.bind("<Left>", lambda e: self.key_step(e, -1))
        self.bind("<Right>", lambda e: self.key_step(e, 1))
        self.bind("<Shift-Left>", lambda e: self.key_update(e, -1))
        self.bind("<Shift-Right>", lambda e: self.key_update(e, 1))
        self.after(100, self.poll)
        if initial:
            self.after(50, lambda: self.load(initial))

    def _style(self):
        style = ttk.Style(self)
        style.theme_use("clam")
        style.configure(".", background="#1d2330", foreground="#dce4ef", fieldbackground="#141821",
                        bordercolor="#343e50", font=("Segoe UI", 10))
        style.configure("Treeview", background="#141821", fieldbackground="#141821", rowheight=26)
        style.configure("Treeview.Heading", background="#273246", padding=6)
        style.map("Treeview", background=[("selected", "#245c70")], foreground=[("selected", "#ffffff")])
        style.configure("TButton", padding=(10, 6))
        style.configure("TLabelframe.Label", foreground="#77dbca")
        style.configure("TNotebook.Tab", padding=(12, 6))

    def tree(self, parent, columns, widths):
        frame = ttk.Frame(parent)
        tree = ttk.Treeview(frame, columns=columns, show="headings", selectmode="browse")
        for name, width in zip(columns, widths):
            tree.heading(name, text=name)
            tree.column(name, width=width, minwidth=45)
        scroll = ttk.Scrollbar(frame, orient="vertical", command=tree.yview)
        horizontal = ttk.Scrollbar(frame, orient="horizontal", command=tree.xview)
        tree.configure(yscrollcommand=scroll.set, xscrollcommand=horizontal.set)
        tree.grid(row=0, column=0, sticky="nsew")
        scroll.grid(row=0, column=1, sticky="ns")
        horizontal.grid(row=1, column=0, sticky="ew")
        frame.rowconfigure(0, weight=1)
        frame.columnconfigure(0, weight=1)
        frame.pack(fill="both", expand=True)
        return tree

    def text(self, parent):
        frame = ttk.Frame(parent)
        box = tk.Text(frame, bg="#10151e", fg="#dce4ef", insertbackground="white",
                      font=("Consolas", 11), wrap="none", relief="flat", padx=12, pady=10)
        sy = ttk.Scrollbar(frame, command=box.yview)
        sx = ttk.Scrollbar(frame, orient="horizontal", command=box.xview)
        box.configure(yscrollcommand=sy.set, xscrollcommand=sx.set, state="disabled")
        box.grid(row=0, column=0, sticky="nsew")
        sy.grid(row=0, column=1, sticky="ns")
        sx.grid(row=1, column=0, sticky="ew")
        frame.columnconfigure(0, weight=1)
        frame.rowconfigure(0, weight=1)
        frame.pack(fill="both", expand=True)
        return box

    def _build(self):
        top = ttk.Frame(self, padding=10)
        top.pack(fill="x")
        ttk.Button(top, text="Otwórz nagranie…", command=self.open_file).pack(side="left")
        self.filename = ttk.Label(top, text="Rex Trace Viewer · analiza offline", padding=12)
        self.filename.pack(side="left")
        self.status = tk.StringVar(value="Otwórz plik .jsonl. Nagranie pozostaje na Twoim komputerze.")
        ttk.Label(self, textvariable=self.status, padding=(12, 5), wraplength=1300).pack(fill="x")
        nav = ttk.Frame(self, padding=(10, 5))
        nav.pack(fill="x")
        self.nav_buttons = []
        for title, action in [("|◀", lambda: self.select(0)), ("◀ Aktualizacja", lambda: self.update_step(-1)),
                              ("◀ Zdarzenie", lambda: self.step(-1)), ("Zdarzenie ▶", lambda: self.step(1)),
                              ("Aktualizacja ▶", lambda: self.update_step(1)), ("Następny błąd", self.next_error)]:
            button = ttk.Button(nav, text=title, command=action, state="disabled")
            button.pack(side="left", padx=2)
            self.nav_buttons.append(button)
        self.before = tk.BooleanVar()
        ttk.Checkbutton(nav, text="Stan przed zdarzeniem", variable=self.before,
                        command=self.render_values).pack(side="right")
        self.current = tk.StringVar(value="Brak nagrania")
        ttk.Label(self, textvariable=self.current, padding=(12, 6)).pack(fill="x")
        layout = ttk.Panedwindow(self, orient="horizontal")
        layout.pack(fill="both", expand=True, padx=10, pady=5)
        history = ttk.Labelframe(layout, text="Historia", padding=5)
        layout.add(history, weight=2)
        self.search = tk.StringVar()
        ttk.Entry(history, textvariable=self.search).pack(fill="x")
        ttk.Label(history, text="Filtr: scena, instrukcja, numer aktualizacji", padding=(0, 4)).pack(anchor="w")
        self.search.trace_add("write", self.schedule_filter)
        self.history = self.tree(history, ("Seq", "Akt.", "Zdarzenie"), (55, 55, 270))
        self.history.tag_configure("error", foreground="#ff9191")
        self.history.bind("<<TreeviewSelect>>", self.history_selected)
        paging = ttk.Frame(history)
        paging.pack(fill="x")
        ttk.Button(paging, text="◀", command=lambda: self.page_step(-1)).pack(side="left")
        self.page_label = ttk.Label(paging)
        self.page_label.pack(side="left", expand=True)
        ttk.Button(paging, text="▶", command=lambda: self.page_step(1)).pack(side="right")
        right = ttk.Panedwindow(layout, orient="vertical")
        layout.add(right, weight=5)
        code_frame = ttk.Labelframe(right, text="Skrypt i stos", padding=5)
        right.add(code_frame, weight=3)
        self.script_title = ttk.Label(code_frame, text="")
        self.script_title.pack(anchor="w")
        code_layout = ttk.Panedwindow(code_frame, orient="horizontal")
        code_layout.pack(fill="both", expand=True)
        code_tabs = ttk.Notebook(code_layout)
        code_layout.add(code_tabs, weight=3)
        for title, attribute in [("Czytelny AST", "code"), ("Oryginalny kod", "source"), ("Szczegóły zdarzenia", "event_details")]:
            panel = ttk.Frame(code_tabs)
            code_tabs.add(panel, text=title)
            setattr(self, attribute, self.text(panel))
        self.code.tag_configure("active", background="#655024", foreground="#fff3c4")
        stack_frame = ttk.Frame(code_layout)
        code_layout.add(stack_frame, weight=1)
        self.stack = self.tree(stack_frame, ("Stos — najgłębsza ramka na górze",), (270,))
        lower = ttk.Panedwindow(right, orient="horizontal")
        right.add(lower, weight=3)
        state_frame = ttk.Labelframe(lower, text="Stan i zmiany", padding=5)
        lower.add(state_frame, weight=3)
        filter_bar = ttk.Frame(state_frame)
        filter_bar.pack(fill="x")
        self.variable_filter = tk.StringVar()
        ttk.Entry(filter_bar, textvariable=self.variable_filter).pack(side="left", fill="x", expand=True)
        self.changed_only = tk.BooleanVar()
        ttk.Checkbutton(filter_bar, text="Tylko zmienione", variable=self.changed_only,
                        command=self.render_values).pack(side="left")
        self.variable_filter.trace_add("write", lambda *_: self.render_values())
        self.variables = self.tree(state_frame, ("Slot / nazwa", "Typ", "Wartość"), (240, 85, 300))
        self.variables.tag_configure("changed", foreground="#80e1c7")
        self.variables.bind("<<TreeviewSelect>>", self.variable_selected)
        detail_frame = ttk.Labelframe(lower, text="Szczegóły wartości — rozwiń elementy", padding=5)
        lower.add(detail_frame, weight=2)
        self.detail = self.tree(detail_frame, ("Wartość",), (230,))
        self.detail.configure(show="tree headings")
        self.detail.heading("#0", text="Pole / indeks")
        self.detail.column("#0", width=170)
        self.detail.bind("<<TreeviewOpen>>", self.expand_detail)
        ttk.Button(detail_frame, text="Kopiuj zaznaczoną wartość", command=self.copy_detail).pack(fill="x")
        ttk.Label(self, text="← / →: zdarzenie · Shift + ← / →: aktualizacja · Ctrl+O: otwórz · Podświetlenie kodu odnosi się do AST.",
                  padding=8).pack(fill="x")

    def open_file(self):
        path = filedialog.askopenfilename(filetypes=[("Nagrania Rex", "*.jsonl"), ("Wszystkie pliki", "*.*")])
        if path:
            self.load(path)

    def load(self, path):
        if self.loading:
            return
        self.loading = True
        self.generation += 1
        generation = self.generation
        self.status.set("Indeksowanie nagrania…")

        def work():
            try:
                result = Trace(path)
            except Exception as error:
                result = error
            self.results.put((generation, result))

        threading.Thread(target=work, daemon=True).start()

    def poll(self):
        try:
            generation, result = self.results.get_nowait()
        except queue.Empty:
            pass
        else:
            self.loading = False
            if isinstance(result, Exception):
                self.status.set(f"Nie udało się otworzyć nagrania: {result}")
                messagebox.showerror("Nie można otworzyć pliku", str(result))
            elif generation != self.generation:
                result.close()
            else:
                if self.trace:
                    self.trace.close()
                self.trace = result
                self.index = -1
                self.errors = [i for i, event in enumerate(result.events) if event.error]
                self.updates = [i for i, event in enumerate(result.events)
                                if i == 0 or event.update != result.events[i - 1].update]
                self.filename.configure(text=result.path.name)
                metadata = result.header.get("metadata", {})
                status = f'{metadata.get("game", "")} · build {result.header.get("build", "?")} · {len(result.events):,} zdarzeń'
                self.status.set(status + " · " + (" | ".join(result.warnings) or "Nagranie zakończone poprawnie"))
                for button in self.nav_buttons:
                    button.configure(state="normal" if result.events else "disabled")
                self.search.set("")
                self.filter_history()
                self.select(0)
                if not result.events:
                    self.clear_event()
        self.after(100, self.poll)

    def clear_event(self):
        self.current.set("Nagranie nie zawiera obserwacji")
        self.script_title.configure(text="")
        for tree in (self.variables, self.stack, self.detail):
            tree.delete(*tree.get_children())
        self.set_text(self.code, "")
        self.set_text(self.source, "")
        self.set_text(self.event_details, "")

    def schedule_filter(self, *_):
        if self.filter_job:
            self.after_cancel(self.filter_job)
        self.filter_job = self.after(200, self.filter_history)

    def filter_history(self):
        self.filter_job = None
        if not self.trace:
            return
        query = self.search.get().casefold().strip()
        self.matches = [i for i, event in enumerate(self.trace.events)
                        if not query or query in f'{event.sequence} {event.update} {event.scene} {event.kind} {event.label}'.casefold()]
        self.page = 0
        self.render_history()

    def render_history(self):
        self.history.delete(*self.history.get_children())
        for i in self.matches[self.page * self.PAGE:(self.page + 1) * self.PAGE]:
            event = self.trace.events[i]
            prefix = {"enter": "→ ", "leave": "← "}.get(event.kind, "")
            self.history.insert("", "end", iid=str(i), values=(event.sequence, event.update, prefix + event.label[:200]),
                                tags=("error",) if event.error else ())
        pages = max(1, (len(self.matches) + self.PAGE - 1) // self.PAGE)
        self.page_label.configure(text=f"{self.page + 1}/{pages} · {len(self.matches):,}")
        if self.history.exists(str(self.index)):
            self.history.selection_set(str(self.index))
            self.history.see(str(self.index))

    def page_step(self, direction):
        pages = max(1, (len(self.matches) + self.PAGE - 1) // self.PAGE)
        self.page = max(0, min(pages - 1, self.page + direction))
        self.render_history()

    def history_selected(self, _):
        selection = self.history.selection()
        if selection and int(selection[0]) != self.index:
            self.select(int(selection[0]), reveal=False)

    def select(self, index, reveal=True):
        if not self.trace or not self.trace.events:
            return
        index = max(0, min(len(self.trace.events) - 1, index))
        try:
            self.trace.seek(index)
            self.index = index
            event = self.trace.events[index]
            record = self.trace.read(index)
            details = {key: value for key, value in record.items() if key not in ("changes", "checkpoint")}
            self.set_text(self.event_details, json.dumps(details, ensure_ascii=False, indent=2))
            self.current.set(f'Sekwencja {event.sequence} · Aktualizacja {event.update} · Scena {event.scene or "—"} · {event.kind}  {record.get("outcome", "")}')
            self.stack.delete(*self.stack.get_children())
            for frame in record["stack"]:
                self.stack.insert("", "end", values=(f'{frame.get("name", "?")} · {frame.get("instruction", "")} [{frame.get("id", "")}]',))
            enter = self.trace.enter_record(index)
            script = self.trace.scripts.get(enter.get("node")) if enter else None
            if script:
                code, locations = pretty_script(script["root"])
                self.script_title.configure(text=f'{script.get("name", "?")} · wariant AST #{script["root"].get("id")} · linia źródła {enter.get("line", "?")}:{enter.get("column", "?")}')
                self.set_text(self.code, code)
                self.set_text(self.source, script.get("source", ""))
                line = locations.get(enter["node"])
                if line:
                    self.code.tag_add("active", f"{line}.0", f"{line}.end+1c")
                    self.code.see(f"{line}.0")
            else:
                self.script_title.configure(text="Brak definicji skryptu dla tego zdarzenia")
                self.set_text(self.code, enter.get("snippet", "") if enter else event.label)
                self.set_text(self.source, "")
            self.render_values()
            if reveal:
                position = bisect_left(self.matches, index)
                if position < len(self.matches) and self.matches[position] == index:
                    self.page = position // self.PAGE
                    self.render_history()
        except (OSError, ValueError, KeyError, TypeError, RecursionError) as error:
            messagebox.showerror("Błąd odczytu", str(error))

    def render_values(self):
        if not self.trace or self.index < 0:
            return
        record = self.trace.read(self.index)
        self.values = dict(self.trace.state)
        if self.before.get():
            for key, change in record["changes"].items():
                if change["before"] is None:
                    self.values.pop(key, None)
                else:
                    self.values[key] = change["before"]
        old = self.variables.selection()
        self.variables.delete(*self.variables.get_children())
        query = self.variable_filter.get().casefold()
        keys = set(self.values) | set(record["changes"])
        for key in sorted(keys):
            slot = self.values.get(key)
            changed = key in record["changes"]
            if (self.changed_only.get() and not changed) or query not in key.casefold():
                continue
            self.variables.insert("", "end", iid=key, values=(key, slot.get("type", "—") if slot else "—", summary(slot)[:240]),
                                  tags=("changed",) if changed else ())
        self.detail.delete(*self.detail.get_children())
        self.detail_objects.clear()
        if old and self.variables.exists(old[0]):
            self.variables.selection_set(old[0])

    def variable_selected(self, _):
        selected = self.variables.selection()
        if not selected or self.index < 0:
            return
        key = selected[0]
        self.detail.delete(*self.detail.get_children())
        self.detail_objects.clear()
        change = self.trace.read(self.index)["changes"].get(key)
        if change:
            self.add_detail("", "Przed zdarzeniem", change["before"])
            self.add_detail("", "Po zdarzeniu", change["after"])
        else:
            self.add_detail("", "Stan (bez zmiany)", self.values.get(key))

    def add_detail(self, parent, name, value):
        container = isinstance(value, (dict, list))
        label = f"{len(value)} elementów" if container else json.dumps(value, ensure_ascii=False)
        item = self.detail.insert(parent, "end", text=name, values=(label[:1000],))
        self.detail_objects[item] = value
        if container and value:
            self.detail.insert(item, "end", text="…")

    def expand_detail(self, _):
        item = self.detail.focus()
        value = self.detail_objects.get(item)
        if not isinstance(value, (dict, list)):
            return
        for child in self.detail.get_children(item):
            self.detail.delete(child)
        entries = list(value.items()) if isinstance(value, dict) else list(enumerate(value))
        if len(entries) > 200:
            for start in range(0, len(entries), 200):
                self.add_detail(item, f"Elementy {start}–{min(start + 199, len(entries) - 1)}", dict(entries[start:start + 200]))
        else:
            for key, child in entries:
                self.add_detail(item, str(key), child)

    def copy_detail(self):
        selected = self.detail.selection()
        if selected and selected[0] in self.detail_objects:
            self.clipboard_clear()
            self.clipboard_append(json.dumps(self.detail_objects[selected[0]], ensure_ascii=False, indent=2))

    def step(self, direction):
        self.select(self.index + direction)

    def update_step(self, direction):
        if not self.updates:
            return
        pos = bisect_right(self.updates, self.index) if direction > 0 else bisect_left(self.updates, self.index) - 1
        if 0 <= pos < len(self.updates):
            self.select(self.updates[pos])

    def next_error(self):
        pos = bisect_right(self.errors, self.index)
        if pos < len(self.errors):
            self.select(self.errors[pos])
        else:
            messagebox.showinfo("Błędy", "Brak kolejnego zarejestrowanego błędu.")

    def key_step(self, event, direction):
        if not isinstance(event.widget, (ttk.Entry, tk.Text)):
            self.step(direction)
            return "break"

    def key_update(self, event, direction):
        if not isinstance(event.widget, (ttk.Entry, tk.Text)):
            self.update_step(direction)
            return "break"

    @staticmethod
    def set_text(widget, text):
        widget.configure(state="normal")
        widget.delete("1.0", "end")
        widget.insert("1.0", text)
        widget.configure(state="disabled")

    def close(self):
        self.generation += 1
        if self.trace:
            self.trace.close()
        self.destroy()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Przeglądarka nagrań rex-trace v1/v2")
    parser.add_argument("trace", nargs="?", type=Path, help="Plik JSONL (opcjonalny)")
    Viewer(parser.parse_args().trace).mainloop()
