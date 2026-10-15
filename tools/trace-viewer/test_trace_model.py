import json
from pathlib import Path
import tempfile
import unittest

from trace_model import Trace, TraceError, pretty_script, summary


def slot(value):
    return {"name": "x", "type": "INTEGER", "supported": True,
            "state": {"type": "INT", "value": value}}


def observation(sequence, before, after, **extra):
    result = {"kind": "updateEnd", "sequence": sequence, "update": sequence,
              "stack": [], "changes": {"context:1:x": {"before": before, "after": after}}}
    result.update(extra)
    return result


HEADER = {"kind": "header", "format": "rex-trace", "version": 1, "metadata": {}}


class TraceTests(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.path = Path(self.directory.name) / "trace.jsonl"

    def load(self, records, tail=""):
        self.path.write_text("\n".join(json.dumps(r, ensure_ascii=False) for r in records) + "\n" + tail,
                             encoding="utf-8")
        trace = Trace(self.path)
        self.addCleanup(trace.close)
        return trace

    def test_forward_backward_and_random_checkpoint_seeks(self):
        records = [HEADER]
        expected = []
        previous = None
        for sequence in range(1, 601):
            current = slot(sequence) if sequence % 7 else None
            record = observation(sequence, previous, current)
            state = {"context:1:x": current} if current else {}
            if sequence == 1 or sequence % 256 == 0:
                record["checkpoint"] = state
            records.append(record)
            expected.append(state)
            previous = current
        records.append({"kind": "end", "reason": "closed", "lastSequence": 600})
        trace = self.load(records)
        for index in [599, 0, 255, 256, 254, 511, 5, 6, 7, 500, 499, 0]:
            self.assertEqual(expected[index], trace.seek(index), index)
        self.assertFalse(trace.warnings)

    def test_checkpoint_is_state_after_event_and_not_applied_twice(self):
        trace = self.load([HEADER, observation(1, slot(0), slot(1), checkpoint={"context:1:x": slot(99)})])
        self.assertEqual(slot(99), trace.seek(0)["context:1:x"])

    def test_nested_leave_resolves_own_entry_and_ast(self):
        root = {"id": 4, "type": "MethodCallNode", "target": {"id": 5, "type": "VariableNode", "name": "DB"},
                "methodName": "FIND", "arguments": []}
        trace = self.load([HEADER, {"kind": "script", "name": "LOAD", "source": "DB^FIND();", "root": root},
                           observation(1, None, None, kind="enter", call=1, node=4, snippet="DB^FIND()"),
                           observation(2, None, None, kind="enter", call=2, node=5),
                           observation(3, None, None, kind="leave", call=2),
                           observation(4, None, slot(7), kind="leave", call=1, outcome="error: problem")])
        self.assertEqual(4, trace.enter_record(3)["node"])
        self.assertEqual("LOAD", trace.scripts[4]["name"])
        self.assertTrue(trace.events[3].error)
        self.assertEqual("DB^FIND()", trace.events[3].label)

    def test_truncated_last_line_preserves_prefix_and_warns(self):
        trace = self.load([HEADER, observation(1, None, slot(1))], '{"kind":"enter"')
        self.assertEqual(1, len(trace.events))
        self.assertGreaterEqual(len(trace.warnings), 2)
        self.assertEqual(slot(1), trace.seek(0)["context:1:x"])

    def test_corrupt_middle_does_not_skip_changes(self):
        trace = self.load([HEADER, observation(1, None, slot(1))], 'not json\n' + json.dumps(observation(3, slot(2), slot(3))))
        self.assertEqual(1, len(trace.events))
        self.assertTrue(trace.warnings)

    def test_limit_and_bad_footer_are_distinct(self):
        trace = self.load([HEADER, {"kind": "end", "reason": "limit", "lastSequence": 0}])
        self.assertEqual("limit", trace.end["reason"])
        self.assertTrue(trace.warnings)
        trace.close()
        trace = self.load([HEADER, {"kind": "end", "reason": "closed", "lastSequence": 99}])
        self.assertIsNone(trace.end)
        self.assertTrue(trace.warnings)

    def test_invalid_headers_and_empty_files_rejected(self):
        for header in ({}, {**HEADER, "version": 3}, {**HEADER, "format": "other"}):
            with self.assertRaises(TraceError):
                self.load([header])
        self.path.write_bytes(b"")
        with self.assertRaises(TraceError):
            Trace(self.path)

    def test_same_name_in_multiple_contexts_and_null_values(self):
        null_slot = {"name": "x", "type": "ARRAY", "supported": True,
                     "state": [{"type": "NULL", "value": None}]}
        record = observation(1, None, slot(1))
        record["changes"]["context:2:x"] = {"before": None, "after": null_slot}
        trace = self.load([HEADER, record])
        self.assertEqual(2, len(trace.seek(0)))
        self.assertEqual(null_slot, trace.state["context:2:x"])

    def test_non_monotonic_sequence_stops_index(self):
        trace = self.load([HEADER, observation(2, None, slot(1)), observation(1, slot(1), slot(2))])
        self.assertEqual(1, len(trace.events))
        self.assertTrue(trace.warnings)

    def test_pretty_ast_preserves_nested_call_line_mapping(self):
        root = {"type": "BlockNode", "id": 1, "statements": [
            {"type": "IfNode", "id": 2, "condition": {"type": "LiteralNode", "id": 3, "value": {"value": True}},
             "thenBranch": {"type": "ReturnNode", "id": 4, "value": {"type": "LiteralNode", "id": 5, "value": {"value": 7}}},
             "elseBranch": {"type": "BreakNode", "id": 6}}]}
        text, lines = pretty_script(root)
        self.assertEqual("    RETURN 7", text.splitlines()[lines[4] - 1])
        self.assertEqual(2, lines[5])
        self.assertEqual("    BREAK", text.splitlines()[lines[6] - 1])
        self.assertEqual("IF true", text.splitlines()[lines[3] - 1])

    def test_summaries_preserve_unsupported_and_database_counts(self):
        self.assertIn("nieobsługiwane", summary({"supported": False}))
        self.assertIn("2 wierszy", summary({"type": "DATABASE", "supported": True,
                                            "state": {"rows": [["a"], ["b"]], "currentRow": 1}}))

    def test_shipped_example_can_be_navigated(self):
        trace = Trace(Path(__file__).with_name("example.jsonl"))
        self.addCleanup(trace.close)
        self.assertFalse(trace.warnings)
        self.assertEqual(12, len(trace.events))
        self.assertEqual(-1, trace.seek(11)["context:1:RESULT"]["state"]["value"])
        self.assertEqual(0, trace.seek(0)["context:1:RESULT"]["state"]["value"])
        self.assertTrue(trace.events[10].error)
        root = trace.scripts[11]["root"]
        text, locations = pretty_script(root)
        self.assertEqual("RESULT^SET(-1)", text.splitlines()[locations[11] - 1])

    def test_v2_signals_conditions_and_changes_are_navigable(self):
        records = [{**HEADER, "version": 2},
                   observation(1, None, slot(0), kind="signal", signalId=1, parentSignal=0,
                               parentCall=0, source="TIMER", emitted="ONTICK^1", binding="ONTICK", handled=True),
                   observation(2, slot(0), slot(0), kind="enter", call=1, node=10, causeSignal=1, snippet="IF"),
                   observation(3, slot(0), slot(0), kind="condition", call=1, node=10, subject="IF", result=False),
                   observation(4, slot(0), slot(1), kind="leave", call=1),
                   observation(5, slot(1), slot(1), kind="signalEnd", signalId=1),
                   {"kind": "end", "lastSequence": 5, "reason": "closed"}]
        trace = self.load(records)
        self.assertFalse(trace.warnings)
        self.assertEqual(0, trace.signals[1])
        self.assertIn("ONTICK^1", trace.events[4].label)
        self.assertEqual("IF → False", trace.events[2].label)
        self.assertEqual(10, trace.enter_record(2)["node"])
        self.assertEqual(slot(1), trace.seek(4)["context:1:x"])
        self.assertEqual(slot(0), trace.seek(0)["context:1:x"])

    def test_v2_guard_error_and_unhandled_signal_labels(self):
        trace = self.load([{**HEADER, "version": 2},
                           observation(1, None, slot(1), kind="condition", subject="BEHAVIOUR:RUN:CHECK",
                                       result=True, outcome="error: evaluation failed"),
                           observation(2, slot(1), slot(1), kind="signal", signalId=1,
                                       source="SOUND", emitted="ONFINISHED", handled=False)])
        self.assertTrue(trace.events[0].error)
        self.assertIn("brak handlera", trace.events[1].label)

    def test_new_adapter_summaries(self):
        self.assertIn("tick 2/3", summary({"type": "TIMER", "supported": True,
                                           "state": {"enabled": True, "intervalMs": 50, "tickCount": 2, "tickLimit": 3}}))
        self.assertIn("pauza", summary({"type": "SOUND", "supported": True,
                                        "state": {"playing": True, "paused": True, "filename": "voice.wav"}}))


if __name__ == "__main__":
    unittest.main()
