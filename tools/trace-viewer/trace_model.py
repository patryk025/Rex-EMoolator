"""Read-only, disk-indexed reader for rex-trace v1/v2. No GUI dependencies."""
from __future__ import annotations

from bisect import bisect_right
from dataclasses import dataclass
import json
from pathlib import Path


class TraceError(ValueError):
    pass


@dataclass(slots=True)
class Event:
    offset: int
    sequence: int
    update: int
    kind: str
    scene: str
    call: int
    label: str
    error: bool


class Trace:
    def __init__(self, path):
        self.path = Path(path)
        self.events: list[Event] = []
        self.checkpoints: list[int] = []
        self.calls: dict[int, int] = {}
        self.signals: dict[int, int] = {}
        self.nodes: dict[int, dict] = {}
        self.scripts: dict[int, dict] = {}
        self.header = {}
        self.end = None
        self.warnings: list[str] = []
        self.state: dict = {}
        self.position = -1
        self._file = self.path.open("rb")
        try:
            self._index()
        except Exception:
            self.close()
            raise

    def close(self):
        self._file.close()

    def _index_nodes(self, value, script):
        if isinstance(value, dict):
            if isinstance(value.get("id"), int) and "type" in value:
                self.nodes[value["id"]] = value
                self.scripts[value["id"]] = script
            for child in value.values():
                self._index_nodes(child, script)
        elif isinstance(value, list):
            for child in value:
                self._index_nodes(child, script)

    def _index(self):
        line_number = 0
        while True:
            offset = self._file.tell()
            line = self._file.readline()
            if not line:
                break
            line_number += 1
            if not line.strip():
                continue
            try:
                record = json.loads(line.decode("utf-8-sig"))
                if not isinstance(record, dict):
                    raise TraceError("Rekord nie jest obiektem JSON")
                kind = record.get("kind")
                if not self.header:
                    if kind != "header" or record.get("format") != "rex-trace" or record.get("version") not in (1, 2):
                        raise TraceError("Oczekiwano nagłówka rex-trace w wersji 1 lub 2")
                    self.header = record
                    continue
                if self.end is not None:
                    raise TraceError("Dane po zakończeniu nagrania")
                if kind == "script":
                    if not isinstance(record.get("root"), dict):
                        raise TraceError("Brak AST skryptu")
                    self._index_nodes(record["root"], record)
                elif kind == "end":
                    last = self.events[-1].sequence if self.events else 0
                    if record.get("lastSequence") != last:
                        raise TraceError("Znacznik końca wskazuje inną sekwencję")
                    if record.get("reason") not in ("closed", "limit"):
                        raise TraceError("Nieznany powód zakończenia nagrania")
                    self.end = record
                elif kind in ("enter", "leave", "loaded", "updateBegin", "updateEnd", "signal", "signalEnd", "condition"):
                    self._add_event(offset, record)
                else:
                    raise TraceError(f"Nieznany rodzaj rekordu: {kind!r}")
            except (ValueError, UnicodeError, TypeError, KeyError, RecursionError) as error:
                if not self.header:
                    raise TraceError(f"Linia {line_number}: {error}") from error
                self.warnings.append(f"Linia {line_number}: {error}. Pokazano poprawny początek pliku.")
                break
        if not self.header:
            raise TraceError("Plik nie zawiera nagłówka rex-trace")
        if self.end is None:
            self.warnings.append("Nagranie niekompletne — brak poprawnego znacznika zakończenia.")
        elif self.end["reason"] == "limit":
            self.warnings.append("Nagranie zakończone po osiągnięciu limitu rozmiaru.")

    def _add_event(self, offset, record):
        sequence, update = record.get("sequence"), record.get("update")
        if type(sequence) is not int or sequence <= (self.events[-1].sequence if self.events else 0):
            raise TraceError("Nieprawidłowa kolejność sekwencji")
        if type(update) is not int or update < 0:
            raise TraceError("Nieprawidłowy numer aktualizacji")
        changes = record.get("changes")
        if not isinstance(changes, dict) or not isinstance(record.get("stack"), list):
            raise TraceError("Nieprawidłowy stan lub stos")
        if any(not isinstance(frame, dict) for frame in record["stack"]):
            raise TraceError("Nieprawidłowa ramka stosu")
        for change in changes.values():
            if not isinstance(change, dict) or "before" not in change or "after" not in change:
                raise TraceError("Nieprawidłowy zapis zmiany")
            if any(value is not None and not isinstance(value, dict) for value in change.values()):
                raise TraceError("Nieprawidłowa wartość slotu")
        if "checkpoint" in record and not isinstance(record["checkpoint"], dict):
            raise TraceError("Nieprawidłowy checkpoint")
        if any(not isinstance(value, dict) for value in record.get("checkpoint", {}).values()):
            raise TraceError("Nieprawidłowa wartość w checkpoincie")
        call = record.get("call", 0)
        if type(call) is not int:
            raise TraceError("Nieprawidłowy identyfikator wywołania")
        kind = record["kind"]
        if kind == "enter":
            if call <= 0 or call in self.calls:
                raise TraceError("Powtórzone lub nieprawidłowe wywołanie")
            label = str(record.get("snippet") or record.get("nodeType", "instrukcja"))
            self.calls[call] = len(self.events)
        elif kind == "leave":
            if call not in self.calls:
                raise TraceError("Wyjście bez wejścia w instrukcję")
            label = self.events[self.calls[call]].label
        elif kind == "condition":
            if type(record.get("result")) is not bool:
                raise TraceError("Nieprawidłowy wynik warunku")
            label = f'{record.get("subject", "Warunek")} → {record["result"]}'
        elif kind == "signal":
            signal_id = record.get("signalId")
            if type(signal_id) is not int or signal_id <= 0 or signal_id in self.signals:
                raise TraceError("Nieprawidłowy identyfikator sygnału")
            self.signals[signal_id] = len(self.events)
            label = f'{record.get("source", "?")} → {record.get("emitted", "?")} · ' + (
                f'handler: {record.get("binding")}' if record.get("handled") else 'brak handlera')
        elif kind == "signalEnd":
            signal_id = record.get("signalId")
            if signal_id not in self.signals:
                raise TraceError("Zakończenie bez emisji sygnału")
            label = "Koniec: " + self.events[self.signals[signal_id]].label
        else:
            label = {"loaded": "Załadowano", "updateBegin": "Początek aktualizacji",
                     "updateEnd": "Koniec aktualizacji"}[kind]
        if "checkpoint" in record:
            self.checkpoints.append(len(self.events))
        self.events.append(Event(offset, sequence, update, kind, str(record.get("scene", "")),
                                 call, label, str(record.get("outcome", "")).startswith("error:")))

    def read(self, index):
        self._file.seek(self.events[index].offset)
        return json.loads(self._file.readline())

    def enter_record(self, index):
        event = self.events[index]
        return self.read(self.calls[event.call]) if event.call in self.calls else None

    def seek(self, index):
        if not 0 <= index < len(self.events):
            raise IndexError(index)
        checkpoint_position = bisect_right(self.checkpoints, index) - 1
        checkpoint = self.checkpoints[checkpoint_position] if checkpoint_position >= 0 else -1
        if self.position < 0 or abs(index - self.position) > index - checkpoint:
            self.state = dict(self.read(checkpoint)["checkpoint"]) if checkpoint >= 0 else {}
            self.position = checkpoint
        while self.position < index:
            self.position += 1
            self._apply(self.read(self.position), "after")
        while self.position > index:
            self._apply(self.read(self.position), "before")
            self.position -= 1
        return self.state

    def _apply(self, record, side):
        for key, change in record["changes"].items():
            if change[side] is None:
                self.state.pop(key, None)
            else:
                self.state[key] = change[side]


OPERATORS = {"ADD": "+", "SUBTRACT": "-", "MULTIPLY": "*", "DIVIDE": "/",
             "MODULO": "%", "EQUAL": "==", "NOT_EQUAL": "!=", "LESS": "<",
             "LESS_EQUAL": "<=", "GREATER": ">", "GREATER_EQUAL": ">=",
             "AND": "&&", "OR": "||", "INSTANCE_OF": "?"}


def expression(node):
    if node is None:
        return ""
    kind = node.get("type")
    if kind == "LiteralNode":
        return json.dumps(node.get("value", {}).get("value"), ensure_ascii=False)
    if kind == "VariableNode":
        return node.get("name", "?")
    if kind == "MethodCallNode":
        return f'{expression(node.get("target"))}^{node.get("methodName", "?")}({", ".join(expression(n) for n in node.get("arguments", []))})'
    if kind in ("ArithmeticNode", "ComparisonNode", "LogicalNode"):
        op = node.get("operator", "?")
        return f'({expression(node.get("left"))} {OPERATORS.get(op, op)} {expression(node.get("right"))})'
    if kind == "PointerDerefNode":
        return "*" + expression(node.get("expression"))
    if kind == "StructAccessNode":
        return f'{node.get("structName")}.{node.get("fieldName")}'
    if kind == "ConvNode":
        return f'CONV({expression(node.get("variable"))}, {node.get("targetType")})'
    return f'<{kind or "nieznany węzeł"}>'


def pretty_script(root):
    """Readable pseudocode, with AST ids mapped to display lines (not source lines)."""
    lines, locations = [], {}

    def mark(node, line):
        if isinstance(node, dict):
            if "id" in node:
                locations[node["id"]] = line
            for child in node.values():
                mark(child, line)
        elif isinstance(node, list):
            for child in node:
                mark(child, line)

    def emit(node, depth=0):
        if not node:
            return
        kind = node.get("type")
        mark(node, len(lines) + 1)
        prefix = "    " * depth
        if kind == "BlockNode":
            for statement in node.get("statements", []):
                emit(statement, depth)
        elif kind == "IfNode":
            lines.append(prefix + "IF " + expression(node.get("condition")))
            emit(node.get("thenBranch"), depth + 1)
            if node.get("elseBranch"):
                lines.append(prefix + "ELSE")
                emit(node["elseBranch"], depth + 1)
            lines.append(prefix + "ENDIF")
        elif kind in ("LoopNode", "WhileNode"):
            title = ("WHILE " + expression(node.get("condition"))) if kind == "WhileNode" else (
                "LOOP " + ", ".join(expression(node.get(key)) for key in ("start", "diff", "step")))
            lines.append(prefix + title)
            emit(node.get("body"), depth + 1)
            lines.append(prefix + "ENDLOOP")
        elif kind == "VarDefNode":
            lines.append(prefix + f'{node.get("varType")} {node.get("varName")} = {expression(node.get("initialValue"))}')
        elif kind == "ReturnNode":
            lines.append(prefix + "RETURN " + expression(node.get("value")))
        elif kind in ("BreakNode", "OneBreakNode"):
            lines.append(prefix + ("BREAK" if kind == "BreakNode" else "ONEBREAK"))
        else:
            lines.append(prefix + expression(node))

    emit(root)
    return "\n".join(lines), locations


def summary(slot):
    if slot is None:
        return "— poza obserwacją —"
    if not slot.get("supported", False):
        return "szczegóły nieobsługiwane"
    state = slot.get("state")
    if isinstance(state, dict) and "value" in state:
        value = state["value"]
        if isinstance(value, str) and len(value) > 240:
            value = value[:240] + "…"
        return json.dumps(value, ensure_ascii=False)
    if slot.get("type") == "DATABASE" and isinstance(state, dict):
        return f'{len(state.get("rows", []))} wierszy · bieżący: {state.get("currentRow")}'
    if isinstance(state, dict) and slot.get("type") == "TIMER":
        return f'{"aktywny" if state.get("enabled") else "wyłączony"} · {state.get("intervalMs")} ms · tick {state.get("tickCount")}/{state.get("tickLimit") or "∞"}'
    if isinstance(state, dict) and slot.get("type") in ("SOUND", "SEQUENCE"):
        playback = "pauza" if state.get("paused") else "odtwarzanie" if state.get("playing") else "zatrzymany"
        return f'{playback} · {state.get("currentEvent") or state.get("filename", "")}'
    if isinstance(state, list):
        return f'{len(state)} elementów'
    return json.dumps(state, ensure_ascii=False)
