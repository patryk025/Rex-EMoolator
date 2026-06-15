#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from dataclasses import asdict, dataclass, field
from itertools import count
from typing import Optional


_TEMP_COUNTER = count()
_MAX_DEPTH = 10


def ghidra_slice(text: str, start: int, end: int) -> str:
    start = max(start, 0)
    end = max(end, 0)
    if end < start:
        return ""
    return text[start:end]


def split_args_like_ctor(args_text: str) -> list[str]:
    parts: list[str] = []
    start = 0
    depth = 0
    for index, char in enumerate(args_text):
        if char == "(":
            depth += 1
        elif char == ")":
            depth -= 1
        elif char == "," and depth == 0:
            parts.append(args_text[start:index])
            start = index + 1
        if index == len(args_text) - 1:
            parts.append(args_text[start:index + 1])
    return [part for part in parts if part]


@dataclass
class ExpressionAst:
    node_type: str
    raw: str
    inner: str
    source_branch: str


@dataclass
class BehaviourBlockAst:
    node_type: str
    raw: str
    body: str
    temp_name: str


@dataclass
class TargetAst:
    kind: str
    raw: str
    expression: Optional[ExpressionAst] = None
    note: Optional[str] = None


@dataclass
class ValueAst:
    kind: str
    raw: str
    text: Optional[str] = None
    expression: Optional[ExpressionAst] = None
    note: Optional[str] = None


@dataclass
class ArgumentAst:
    raw: str
    branch: str
    storage: str
    simple_argument: bool
    expression: Optional[ExpressionAst] = None
    nested_entry: Optional["EntryAst"] = None
    variable_name: Optional[str] = None
    behaviour_block: Optional[BehaviourBlockAst] = None
    trace: list[str] = field(default_factory=list)


@dataclass
class EntryAst:
    node_type: str
    raw: str
    normalized: str
    first_char: str
    runner_name: Optional[str] = None
    target: Optional[TargetAst] = None
    value: Optional[ValueAst] = None
    args_text: str = ""
    direct_args: list[ArgumentAst] = field(default_factory=list)
    entry_args: list[ArgumentAst] = field(default_factory=list)
    flags: dict[str, bool] = field(default_factory=dict)
    trace: list[str] = field(default_factory=list)


def make_expression(raw: str, source_branch: str) -> ExpressionAst:
    inner = raw[1:-1] if raw.startswith("[") and raw.endswith("]") else raw
    return ExpressionAst(
        node_type="expression",
        raw=raw,
        inner=inner,
        source_branch=source_branch,
    )


def extract_call_parts(text: str) -> tuple[int, str]:
    open_paren = text.find("(")
    if open_paren < 0:
        return -1, ""
    close_paren = text.rfind(")")
    if close_paren < 0 or close_paren < open_paren:
        return open_paren, ""
    return open_paren, text[open_paren + 1:close_paren]


def parse_argument(token: str, depth: int) -> ArgumentAst:
    trace: list[str] = []
    has_dollar = "$" in token
    first_char = token[0]

    if first_char == "[" and not has_dollar:
        trace.append("Argument starts with '[' and has no '$': construct CMC_Expression and store it in +0x98.")
        return ArgumentAst(
            raw=token,
            branch="bracket_expression",
            storage="direct_args(+0x98)",
            simple_argument=True,
            expression=make_expression(token, "arg[current_char == '[']"),
            trace=trace,
        )

    if first_char != "@" and not has_dollar and "^" not in token:
        if first_char == "&":
            trace.append("Argument starts with '&' and has no '$'/'^': resolve variable and store it in +0x98.")
            return ArgumentAst(
                raw=token,
                branch="variable_reference",
                storage="direct_args(+0x98)",
                simple_argument=True,
                variable_name=token[1:],
                trace=trace,
            )

        trace.append("Plain argument token: recurse into nested CMC_Behaviour_Entry and store it in +0x9c.")
        return ArgumentAst(
            raw=token,
            branch="nested_entry",
            storage="entry_args(+0x9c)",
            simple_argument=False,
            nested_entry=parse_line(token, depth + 1),
            trace=trace,
        )

    quote_offset = 1 if first_char == '"' else 0
    if len(token) > quote_offset and token[quote_offset] == "{":
        body_start = quote_offset + 1
        body_end = len(token) - 1 - quote_offset
        body = ghidra_slice(token, body_start, body_end)
        temp_name = f"temp_{next(_TEMP_COUNTER)}"
        trace.append(
            "Dynamic or special argument opens an inline behaviour block: create temp behaviour and put temp string in +0x98."
        )
        if has_dollar:
            trace.append("This branch clears the simple-arguments flag because '$' appears inside the inline behaviour token.")
        return ArgumentAst(
            raw=token,
            branch="inline_behaviour_block",
            storage="direct_args(+0x98)",
            simple_argument=not has_dollar,
            behaviour_block=BehaviourBlockAst(
                node_type="behaviour_block",
                raw=token,
                body=body,
                temp_name=temp_name,
            ),
            trace=trace,
        )

    trace.append(
        "Argument is '@'-prefixed or contains '$'/'^': build nested CMC_Behaviour_Entry and store it in +0x9c."
    )
    return ArgumentAst(
        raw=token,
        branch="special_nested_entry",
        storage="entry_args(+0x9c)",
        simple_argument=False,
        nested_entry=parse_line(token, depth + 1),
        trace=trace,
    )


def parse_line(raw_line: str, depth: int = 0) -> EntryAst:
    raw_line = raw_line.rstrip("\r\n")
    normalized = raw_line[:-1] if raw_line.endswith(";") else raw_line

    if depth > _MAX_DEPTH:
        return EntryAst(
            node_type="behaviour_entry",
            raw=raw_line,
            normalized=normalized,
            first_char=normalized[:1],
            flags={"max_depth_reached": True},
            trace=["Recursion guard fired. Remaining subtree was not expanded."],
        )

    first_char = normalized[:1]
    entry = EntryAst(
        node_type="behaviour_entry",
        raw=raw_line,
        normalized=normalized,
        first_char=first_char,
        flags={
            "explicit_target": False,
            "this_prefix": False,
            "dynamic_target_before_caret": False,
            "top_level_bracket_runtime_text": False,
            "simple_arguments": True,
        },
    )

    if raw_line.endswith(";"):
        entry.trace.append("Trailing ';' removed before further parsing.")

    open_paren, args_text = extract_call_parts(normalized)
    entry.args_text = args_text

    if not normalized:
        entry.trace.append("Empty input line.")
        return entry

    if first_char == "@":
        runner_end = open_paren if open_paren >= 0 else len(normalized)
        entry.runner_name = ghidra_slice(normalized, 1, runner_end)
        entry.target = TargetAst(
            kind="behaviour_owner",
            raw="@",
            note="Uses owner behaviour as subject (+0x6c) and runner 0 as fallback.",
        )
        entry.trace.append("current_char == '@': subject becomes owner behaviour, runner name is taken from text after '@'.")

    elif first_char == "*":
        close_bracket = normalized.find("]")
        caret_index = normalized.find("^", close_bracket + 1 if close_bracket >= 0 else 0)
        runner_open = open_paren
        if close_bracket >= 0 and open_paren >= 0 and close_bracket + 1 < open_paren:
            runner_open = normalized.find("(", caret_index if caret_index >= 0 else 0)
        else:
            caret_index = normalized.find("^")

        runner_end = runner_open if runner_open >= 0 else len(normalized)
        target_end = caret_index if caret_index >= 0 else runner_end
        target_text = ghidra_slice(normalized, 1, target_end)
        entry.flags["explicit_target"] = True

        if target_text.startswith("["):
            entry.target = TargetAst(
                kind="expression_target",
                raw=target_text,
                expression=make_expression(target_text, "entry['*' explicit target]"),
                note="Sets +0xbe and stores a compiled CMC_Expression in +0xb8.",
            )
            entry.trace.append("'*' branch with '[' target: compile explicit target expression and keep it in +0xb8.")
        else:
            entry.target = TargetAst(
                kind="object_target",
                raw=target_text,
                note="Lookup object by name and store it in +0xb8.",
            )
            entry.trace.append("'*' branch with plain target: resolve explicit object name into +0xb8.")

        if caret_index >= 0:
            entry.runner_name = ghidra_slice(normalized, caret_index + 1, runner_end)
        entry.trace.append("Runner name in '*' branch comes from the slice after '^'.")

    elif normalized.startswith("THIS"):
        runner_end = open_paren if open_paren >= 0 else len(normalized)
        entry.runner_name = ghidra_slice(normalized, 5, runner_end)
        entry.target = TargetAst(
            kind="this_object",
            raw="THIS",
            note="Sets +0xa1 and treats THIS as the callee object.",
        )
        entry.flags["this_prefix"] = True
        entry.trace.append("Line starts with 'THIS': set +0xa1 and take runner text after 'THIS'.")

    elif first_char == "[":
        bracket_text = normalized
        if "$" in bracket_text:
            entry.value = ValueAst(
                kind="runtime_bracket_text",
                raw=bracket_text,
                text=bracket_text,
                note="This branch does not build CMC_Expression. It copies bracketed text into +0xa8 and returns early.",
            )
            entry.flags["top_level_bracket_runtime_text"] = True
            entry.trace.append(
                "Top-level current_char == '[' with '$': keep bracketed text in +0xa8, no CMC_Expression is allocated."
            )
        else:
            entry.value = ValueAst(
                kind="expression_value",
                raw=bracket_text,
                expression=make_expression(bracket_text, "entry[current_char == '[']"),
                note="This branch builds CMC_Expression and stores it in +0xa4.",
            )
            entry.trace.append(
                "Top-level current_char == '[' without '$': construct CMC_Expression and store it in +0xa4."
            )

    else:
        caret_index = normalized.find("^")
        if first_char == '"' or caret_index < 0:
            entry.value = ValueAst(
                kind="atom_or_object_lookup",
                raw=normalized,
                text=normalized,
                note="Runtime decides whether this resolves to an existing object (+0xa4) or remains plain text in +0xa8.",
            )
            entry.trace.append("No '^' (or quoted atom): this stays a leaf value/object lookup.")
        else:
            lhs = normalized[:caret_index]
            runner_end = open_paren if open_paren >= 0 else len(normalized)
            entry.runner_name = ghidra_slice(normalized, caret_index + 1, runner_end)
            if "$" in lhs:
                entry.flags["dynamic_target_before_caret"] = True
                entry.target = TargetAst(
                    kind="dynamic_target",
                    raw=lhs,
                    note="Sets +0xbc; target must be resolved later because '$' appears before '^'.",
                )
                entry.trace.append("Target text before '^' contains '$': set +0xbc and defer target resolution.")
            elif "|" in lhs:
                object_name, field_name = lhs.split("|", 1)
                entry.target = TargetAst(
                    kind="structure_field",
                    raw=lhs,
                    note=f"Resolve object '{object_name}', then field '{field_name}' via CMC_Structure::getField.",
                )
                entry.trace.append("Target before '^' contains '|': treat it as object|field.")
            else:
                entry.target = TargetAst(
                    kind="object_target",
                    raw=lhs,
                    note="Resolve callee object name before selecting the runner.",
                )
                entry.trace.append("Target before '^' is a plain object name.")

    if not entry.args_text:
        return entry

    entry.trace.append("Split argument string exactly like the ctor: commas only count at parenthesis depth 0.")
    for token in split_args_like_ctor(entry.args_text):
        argument = parse_argument(token, depth)
        if argument.storage.startswith("direct_args"):
            entry.direct_args.append(argument)
        else:
            entry.entry_args.append(argument)
        if not argument.simple_argument:
            entry.flags["simple_arguments"] = False

    return entry

def prepare_input(code: str) -> str:
    # original used right and left instructions for striping code from braces (yeah, they are unnecessary)
    code = code.strip()
    if code[0] == '{':
        code = code[1::]
    if code[-1] == '}':
        code = code[:-1]
    code = code.strip() # yep, another strip to remove spaces after removing braces
    return code

def initializeBehaviours(code: str) -> str:
    code_lines = []

    depth = 0
    tmp_buffer = ""
    for i, c in enumerate(code):
        if c == '(':
            depth += 1
            tmp_buffer += c
            continue
        elif c == ')':
            depth -= 1
            tmp_buffer += c
            continue
        elif c == ';':
            if depth == 0:
                code_lines.append(tmp_buffer)
                tmp_buffer = ""
                continue
            else:
                tmp_buffer += c
                continue
        elif c == ' ':
            continue
        else:
            tmp_buffer += c

    return code_lines

def main() -> int:
    parser = argparse.ArgumentParser(
        description=(
            "Approximate AST reconstruction for BlooMooDLL::CMC_Behaviour_Entry::CMC_Behaviour_Entry "
            "(thunk 0x1000331e -> body 0x10042670)."
        )
    )
    parser.add_argument(
        "--compact",
        action="store_true",
        help="Emit compact JSON instead of pretty-printed JSON.",
    )
    args = parser.parse_args()

    import sys

    lines = "{@STRING(\"TEST\", \"\");@IF(\"2+3*4'\"2+3*4\"&&1'1\",\"{@RETURN(\"TRUE\");}\",\"{@RETURN(\"FALSE\");}\");@RETURN(TEST);}"

    prepared_code = prepare_input(lines)
    
    print("Prepared code:")
    print(prepared_code)

    print("\nCode lines:")
    lines = initializeBehaviours(prepared_code)

    json_data = []

    for index, line in enumerate(lines):
        if index and not args.compact:
            print()
        tree = parse_line(line)
        
        json_data.append(asdict(tree))

    if args.compact:
        print(json.dumps(json_data, ensure_ascii=True, separators=(",", ":")))
    else:
        print(json.dumps(json_data, ensure_ascii=True, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
