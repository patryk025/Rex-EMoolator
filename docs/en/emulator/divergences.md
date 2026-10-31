# Differences from the original

The original engine is the source of truth, and the emulator aims to match it. The rest of the documentation describes the original, and this page collects the places where the emulator behaves differently.

The list comes from crosstests, which run the same scripts on the real DLLs and in the emulator. It reflects the state of 2026-10-24, for Piklib 7.2, Piklib 8, and BlooMoo. Every item still needs checking: a test detects a difference, but it does not say whether any game relies on it.

## Detected differences

| Area | Original | Emulator |
|---|---|---|
| `[x@0]`, `[x%0]` for `STRING` and `BOOL` | the result is the left operand | the text `NULL` |
| `[x@0]` for `DOUBLE` | `1.#INF0` | the text `NULL` |
| `[x@0]`, `[x%0]` for `INTEGER`, `[x%0]` for `DOUBLE` | engine crash | the text `NULL` |
| [`INTEGER^DIV(0)`](../reference/INTEGER.md#div), [`INTEGER^MOD(0)`](../reference/INTEGER.md#mod) | engine crash | value unchanged |
| [`DOUBLE^DIV(0)`](../reference/DOUBLE.md#div) | stores `1.#INF0` | value unchanged |
| [`INTEGER^LENGTH`](../reference/INTEGER.md#length) | does not store the length in the variable; the state after the call depends on the engine | stores the length |
| Values returned by `SET`, `SUB`, `MUL`, `DIV`, and similar methods | nothing | the new value |
| Result of `RUN` | `STRING`; nothing without `@RETURN` | the value in its original type; the text `__RETURN__` without `@RETURN` |
| `*NAME^SET([PROC^RUN()+5])` when `PROC` returns `2` | `25` | `7` |
| Condition `5!'5\|\|8>'10&&1'1` | true | false |
| Condition `5+5*2'20\|\|1'2&&1'1` | true | false |
| Condition `2+2'2` in Piklib 7.2 and 8 | true | false |

## Missing elements

- The `@FOR`, `@CONTINUE`, `@CREATE`, `@GETAPPLICATIONNAME`, `@GETCURRENTSCENE`, `@MSGBOX`, `@RUNONTIMER`, and `@VALUE` statements.
- Older engines: the emulator does not reproduce the language limits of Piklib 6.1 and 7.1, described in the [Scripts](../engine/scripts.md#loops) and [Arithmetic](../engine/arithmetic.md#order-of-operations) chapters.

The [method map](../reference/method-map.md) shows method coverage.
