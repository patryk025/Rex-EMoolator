# Game status

The emulator targets games built on the Piklib and BlooMoo engines. Below is the status of the titles tracked on an ongoing basis. The full list of known titles and engine versions is in the [Engine](../engine/index.md#games-using-the-engine) chapter.

## Legend

| Status | Meaning |
|---|---|
| ![Playable](https://img.shields.io/badge/Playable-green) | Game can be completed from start to finish without blocking bugs. |
| ![In-game](https://img.shields.io/badge/In--game-yellow) | Game runs but cannot be completed due to various bugs. |
| ![In-intro](https://img.shields.io/badge/In--intro-orange) | Game loads but doesn't progress past the intro. |
| ![Unplayable](https://img.shields.io/badge/Unplayable-red) | Game doesn't initialize properly, loads with errors, crashes, or shows a black screen. |
| ![Unknown](https://img.shields.io/badge/Unknown-lightgrey) | Not yet evaluated. |

## Current status

The notes may be out of date: current scene testing happens in GitHub Projects, and the "Entry changed" column only says when the row last changed, not when the game was tested.

| Game | Status | Playability | Notes | Entry changed | Tracking |
|---|---|---|---|---|---|
| Reksio i Skarb Piratów | ![Playable](https://img.shields.io/badge/Playable-green) | 100% | Fully playable with minor bugs. | 2026-04-27 | [Project](https://github.com/users/patryk025/projects/3) |
| Reksio i UFO | ![In-game](https://img.shields.io/badge/In--game-yellow) | 100% with some bugs | The game seems to be more or less playable from start to finish, but it still needs some minor tweaks and needs to be played through manually. I'm leaving the status as "In-game" until I've verified everything. | 2026-06-08 | [Project](https://github.com/users/patryk025/projects/4) |
| Reksio i Czarodzieje | ![In-game](https://img.shields.io/badge/In--game-yellow) | N/A | Blocked at the stage of building teleporters, items cannot be picked up. | 2026-06-26 | [Project](https://github.com/users/patryk025/projects/6) |
| Reksio i Wehikuł Czasu | ![In-game](https://img.shields.io/badge/In--game-yellow) | N/A | Requires re-evaluation after interpreter refactor. | 2026-04-27 | [Project](https://github.com/users/patryk025/projects/8) |
| Reksio i Kapitan Nemo | ![In-game](https://img.shields.io/badge/In--game-yellow) | N/A | The intro works, but the save slots don't work, only the exit button does. | 2026-07-25 | [Project](https://github.com/users/patryk025/projects/10) |
| Reksio i Kretes w Akcji! | ![In-game](https://img.shields.io/badge/In--game-yellow) | N/A | The cutscenes mostly work (sometimes the characters don't appear). The mini-games try to load. Sometimes there are issues with coordinates, sometimes with scrolling and backgrounds, and in the "Reksio i UFO" level, there are problems caused by the lack of implementation of the Inertia engine. | 2026-07-25 | [Project](https://github.com/users/patryk025/projects/11) |
| Poznaj Mity: Wyprawa po Złote Runo | ![Playable](https://img.shields.io/badge/Playable-green) | 100% | All minigames are playable. | 2026-09-05 | [Project](https://github.com/users/patryk025/projects/12) |
| Poznaj Mity: Wojna Trojańska | ![In-game](https://img.shields.io/badge/In--game-yellow) | 93.75% (15/16 scenes) | The chariot races don't work, probably some trouble with the physics engine (the reimplementation still needs a few fixes). | 2026-09-05 | [Project](https://github.com/users/patryk025/projects/13) |
| Poznaj Mity: Przygody Odyseusza | ![In-game](https://img.shields.io/badge/In--game-yellow) | 58.82% (10/17 scenes) | The food-gathering minigame seems to work, though I have my doubts — it probably doesn't reset its state between game restarts. The quiz hangs and the questions are never read out. The ship raid doesn't work properly: there are no obstacles and no enemies. The raid's cutscene hangs after a mouse click. The raid's level selector doesn't work, and on top of that it crashes when picked manually from the scene selector. The intro to the axe minigame gets stuck in some infinite loop, and its level selector shows no levels. | 2026-09-05 | [Project](https://github.com/users/patryk025/projects/14) |
| Poznaj Mity: Herkules | ![In-game](https://img.shields.io/badge/In--game-yellow) | N/A | Minigames are buggy to a greater or lesser extent. | 2026-06-26 | [Project](https://github.com/users/patryk025/projects/15) |
| Tezeusz i Nić Ariadny | ![In-game](https://img.shields.io/badge/In--game-yellow) | N/A | Minigames are buggy to a greater or lesser extent. | 2026-06-26 | [Project](https://github.com/users/patryk025/projects/17) |

## How playability is estimated

Playability estimates are based on the number of scenes that play correctly according to the game scripts until the first point where emulator bugs prevent further progress. Minor bugs and animation issues are not counted — only bugs that prevent scenes from starting or ending, or cause crashes.

Since this requires playing through titles repeatedly, this information may not be updated frequently. The list of scenes and the assessment of their correctness will be gradually created as part of GitHub Projects. However, since the newer games use dedicated scenes for cutscenes and minigames — which are significantly harder to trace in the code than simple `GOTO` transitions — this process will not be immediate.

The [screenshots](screenshots.md) show how the games look in practice.
