# Rex-EMoolator

[Polski](README.pl.md) | **English**

An open-source emulator that recreates the behavior of the Piklib/BlooMoo engine used in Aidem Media games.

## Documentation

Full documentation of the Piklib/BlooMoo engine and of the emulator itself is
published at **<https://patryk025.github.io/Rex-EMoolator/>** — the scripting
language, a reference of every script type, the file formats and the engine
internals. It is available in Polish and English; the sources live in
[`docs/`](docs/). Powered by Material for MkDocs.

## Requirements

### Build Toolchain
- **JDK**: Version 21 or newer
- **Gradle**: Use the wrapper included in the repository

### Desktop
- **Java**: Version 21 or newer
- **Operating System**: Windows, Linux, or macOS
- **Game Files**: Original game files (data from the original CD-ROM or installation)

### Android
- **Android Version**: 7.0 (API level 24) or newer
- **Android SDK**: Installed and configured through `local.properties` (`sdk.dir=...`) or `ANDROID_HOME`
- **Game Files**: Original game files

## Building the Project

This project uses Gradle as its build system.

### Full Build
```bash
# Windows
gradlew.bat build

# Linux/macOS
./gradlew build
```

### Desktop Build

The desktop module produces two distribution artifacts:

- **Single-file shadow JAR** — convenient one-file distribution.
- **Distribution ZIP** — thin JAR + every dependency as a separate file in
  `lib/`, plus startup scripts. Recommended for redistribution because
  individual library JARs (including LGPL-licensed components) can be
  replaced by simply overwriting files in `lib/`.

```bash
# Both artifacts
./gradlew desktop:build

# Shadow JAR only
./gradlew desktop:shadowJar

# Distribution ZIP only
./gradlew desktop:distZip
```

The shadow JAR is created in `desktop/build/libs/` and the distribution ZIP
in `desktop/build/distributions/`.

### Dependency License Maintenance
```bash
# Regenerate THIRD-PARTY-NOTICES.md from current runtime dependencies
./gradlew updateThirdPartyNotices

# Fail if a dependency introduces a new or unapproved license family
./gradlew checkLicense
```

`desktop:build` refreshes `THIRD-PARTY-NOTICES.md` automatically, and both
`check` and `build` fail when the generated notices or allowed license rules
are out of date.

### Android Debug Build
```bash
# Windows
gradlew.bat android:assembleDebug

# Linux/macOS
./gradlew android:assembleDebug
```

The debug APK will be created in `android/build/outputs/apk/debug/`.

### Android Release Build
```bash
# Windows
gradlew.bat android:assembleRelease

# Linux/macOS
./gradlew android:assembleRelease
```

The release APK will be created in `android/build/outputs/apk/release/`.

## Running the Emulator

### Desktop
After building:
```bash
java -jar desktop/build/libs/Rex_EMoolator-desktop.jar
```

Or run directly with Gradle:
```bash
# Windows
gradlew.bat desktop:run

# Linux/macOS
./gradlew desktop:run
```

### Android
Install the generated APK on your Android device and launch the application.

## Supported Games

The emulator targets games based on the Piklib/BlooMoo engine.

### Playability Status Legend
- ![Playable](https://img.shields.io/badge/Playable-green) Game can be completed from start to finish without blocking bugs
- ![In-game](https://img.shields.io/badge/In--game-yellow) Game runs but cannot be completed due to various bugs
- ![In-intro](https://img.shields.io/badge/In--intro-orange) Game loads but doesn't progress past the intro
- ![Unplayable](https://img.shields.io/badge/Unplayable-red) Game doesn't initialize properly, loads with errors, crashes, or shows a black screen
- ![Unknown](https://img.shields.io/badge/Unknown-lightgrey) Not yet evaluated

Playability estimates are based on the number of scenes that play correctly according to the game scripts until the first point where emulator bugs prevent further progress. Minor bugs and animation issues are not counted—only bugs that prevent scenes from starting/ending or cause crashes. Since this requires playing through titles repeatedly, this information may not be updated frequently. The list of scenes and the assessment of their correctness will be gradually created as part of GitHub Projects. However, since the newer games use dedicated scenes for cutscenes and minigames—which are significantly harder to trace in the code than simple `GOTO` transitions—this process will not be immediate.

### Current Game Status
<table>
<thead>
<tr>
<th>Game Title</th>
<th>Status</th>
<th>Notes</th>
<th>Estimated Playability</th>
<th>Project Tracking</th>
</tr>
</thead>
<tbody>
<tr>
<td>Reksio i Skarb Piratów</td>
<td><img alt="Playable" src="https://img.shields.io/badge/Playable-green"/></td>
<td>Fully playable with minor bugs.</td>
<td>100%</td>
<td><a href="https://github.com/users/patryk025/projects/3">Link</a></td>
</tr>
<tr>
<td>Reksio i Ufo</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>The game seems to be more or less playable from start to finish, but it still needs some minor tweaks and needs to be played through manually. I'm leaving the status as "In-game" until I've verified everything.</td>
<td>100% with some bugs</td>
<td><a href="https://github.com/users/patryk025/projects/4">Link</a></td>
</tr>
<tr>
<td>Reksio i Czarodzieje</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>Blocked at the stage of building teleporters, items cannot be picked up.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/6">Link</a></td>
</tr>
<tr>
<td>Reksio i Wehikuł Czasu</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/8">Link</a></td>
</tr>
<tr>
<td>Reksio i Kapitan Nemo</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>The intro works, but the save slots don't work, only the exit button does.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/10">Link</a></td>
</tr>
<tr>
<td>Reksio i Kretes w Akcji!</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>The cutscenes mostly work (sometimes the characters don't appear). The mini-games try to load. Sometimes there are issues with coordinates, sometimes with scrolling and backgrounds, and in the "Reksio i UFO" level, there are problems caused by the lack of implementation of the Inertia engine.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/11">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Wyprawa po Złote Runo</td>
<td><img alt="Playable" src="https://img.shields.io/badge/Playable-green"/></td>
<td>All minigames are playable.</td>
<td>100%</td>
<td><a href="https://github.com/users/patryk025/projects/12">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Wojna Trojańska</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>The chariot races don't work, probably some trouble with the physics engine (the reimplementation still needs a few fixes).</td>
<td>93.75% (15/16 scenes playable)</td>
<td><a href="https://github.com/users/patryk025/projects/13">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Przygody Odyseusza</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>The food-gathering minigame seems to work, though I have my doubts — it probably doesn't reset its state between game restarts. The quiz hangs and the questions are never read out. The ship raid doesn't work properly: there are no obstacles and no enemies. The raid's cutscene hangs after a mouse click. The raid's level selector doesn't work, and on top of that it crashes when picked manually from the scene selector. The intro to the axe minigame gets stuck in some infinite loop, and its level selector shows no levels.</td>
<td>58.82% (10/17 scenes playable)</td>
<td><a href="https://github.com/users/patryk025/projects/14">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Herkules</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>Minigames are buggy to a greater or lesser extent.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/15">Link</a></td>
</tr>
<tr>
<td>Tezeusz i Nić Ariadny</td>
<td><img alt="In-game" src="https://img.shields.io/badge/In--game-yellow"/></td>
<td>Minigames are buggy to a greater or lesser extent.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/17">Link</a></td>
</tr>
</tbody>
</table>

## Screenshots

Screenshots are collapsed per game — expand the one you are interested in.

<details>
<summary><b>Reksio i Skarb Piratów</b></summary>

<img src="images/risp.jpg" alt="Screenshot from Reksio i Skarb Piratów"/>
<br/><em>Screenshot from Reksio i Skarb Piratów</em>

</details>

<details>
<summary><b>Reksio i Ufo</b></summary>

<img src="images/riu.jpg" alt="Screenshot from Reksio i Ufo"/>
<br/><em>Screenshot from Reksio i Ufo</em>

</details>

<details>
<summary><b>Reksio i Czarodzieje</b></summary>

<img src="images/ric.jpg" alt="Screenshot from Reksio i Czarodzieje"/>
<br/><em>Screenshot from Reksio i Czarodzieje</em>

</details>

<details>
<summary><b>Reksio i Wehikuł Czasu</b></summary>

<img src="images/riwc.jpg" alt="Screenshot from Reksio i Wehikuł Czasu"/>
<br/><em>Screenshot from Reksio i Wehikuł Czasu</em>

</details>

<details>
<summary><b>Reksio i Kapitan Nemo</b></summary>

<table>
<tr>
<td width="50%" valign="top"><img src="images/rikn_menu.jpg" alt="Reksio i Kapitan Nemo menu" width="100%"/><br/><em>Reksio i Kapitan Nemo menu</em></td>
<td width="50%" valign="top"><img src="images/rikn_gra_intro.jpg" alt="Intro after clicking the drawer" width="100%"/><br/><em>Intro after clicking the drawer</em></td>
</tr>
</table>

</details>

<details>
<summary><b>Reksio i Kretes w Akcji</b></summary>

<table>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_intro.jpg" alt="Reksio i Kretes w Akcji intro" width="100%"/><br/><em>Reksio i Kretes w Akcji intro</em></td>
<td width="50%" valign="top"><img src="images/rikwa_menu.jpg" alt="Reksio i Kretes w Akcji menu" width="100%"/><br/><em>Reksio i Kretes w Akcji menu</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_risp_intro.jpg" alt="Reksio i Skarb Piratów intro" width="100%"/><br/><em>Reksio i Skarb Piratów intro</em></td>
<td width="50%" valign="top"><img src="images/rikwa_risp_gra.jpg" alt="Reksio i Skarb Piratów gameplay" width="100%"/><br/><em>Reksio i Skarb Piratów gameplay</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_riu_intro.jpg" alt="Reksio i Ufo intro" width="100%"/><br/><em>Reksio i Ufo intro</em></td>
<td width="50%" valign="top"><img src="images/rikwa_riu_gra.jpg" alt="Reksio i Ufo gameplay" width="100%"/><br/><em>Reksio i Ufo gameplay</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_ric_intro1.jpg" alt="Reksio i Czarodzieje intro" width="100%"/><br/><em>Reksio i Czarodzieje intro</em></td>
<td width="50%" valign="top"><img src="images/rikwa_ric_intro2.jpg" alt="Reksio i Czarodzieje intro" width="100%"/><br/><em>Reksio i Czarodzieje intro</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_ric_gra.jpg" alt="Reksio i Czarodzieje gameplay" width="100%"/><br/><em>Reksio i Czarodzieje gameplay</em></td>
<td width="50%" valign="top"><img src="images/rikwa_riwc_menu.jpg" alt="Reksio i Wehikuł Czasu menu" width="100%"/><br/><em>Reksio i Wehikuł Czasu menu</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_riwc_gra.png" alt="Reksio i Wehikuł Czasu gameplay moments before a crash" width="100%"/><br/><em>Reksio i Wehikuł Czasu gameplay moments before a crash</em></td>
<td width="50%" valign="top"><img src="images/rikwa_shikn_intro.jpg" alt="Super Heros i Kapitan Nemo intro" width="100%"/><br/><em>Super Heros i Kapitan Nemo intro</em></td>
</tr>
<tr>
<td width="50%" valign="top"><img src="images/rikwa_shikn_gra.jpg" alt="Super Heros i Kapitan Nemo gameplay" width="100%"/><br/><em>Super Heros i Kapitan Nemo gameplay</em></td>
<td width="50%"></td>
</tr>
</table>

</details>

<details>
<summary><b>Poznaj Mity: Wyprawa po Złote Runo</b></summary>

<img src="images/wpzr.jpg" alt="Screenshot from Wyprawa po Złote Runo"/>
<br/><em>Screenshot from Wyprawa po Złote Runo</em>

</details>

<details>
<summary><b>Poznaj Mity: Wojna Trojańska</b></summary>

<img src="images/wt.jpg" alt="Screenshot from Wojna Trojańska"/>
<br/><em>Screenshot from Wojna Trojańska</em>

</details>

<details>
<summary><b>Poznaj Mity: Przygody Odyseusza</b></summary>

<img src="images/po.jpg" alt="Screenshot from Przygody Odyseusza"/>
<br/><em>Screenshot from Przygody Odyseusza</em>

</details>

<details>
<summary><b>Poznaj Mity: Herkules</b></summary>

<img src="images/herc.jpg" alt="Screenshot from Herkules"/>
<br/><em>Screenshot from Herkules</em>

</details>

## License

Rex-EMoolator itself is open-source software released under the **MIT
License**. You can find the full license text in the [`LICENSE`](LICENSE)
file.

The project uses several third-party libraries (like libGDX, LWJGL, ODE4J etc.), each
with its own license. For a complete list of these libraries and their
respective licenses, please check [`THIRD-PARTY-NOTICES.md`](THIRD-PARTY-NOTICES.md).

If you're interested in how we handle LGPL-licensed components (like JLayer or
JOrbis) to keep everything compliant, all the details are also in that file.

The MIT license applies to the emulator source code in this repository. Game
data, mods, fan translations, patches, and other content loaded by the
emulator are separate works and remain subject to their own licenses,
permissions, and copyright status.

### A note on game assets

This emulator is created for educational, preservation, and interoperability
purposes. You can use it with original game files, your own backups, fan-made
mods, translations, patches, and other compatible data, as long as you have
the legal right to use those files.

We do not distribute copyrighted graphics, music, or other data from the
original games. The `assets/` directory only contains minimal, technical
fragments used for automated testing to make sure the engine works as expected.

Rex-EMoolator is an independent project and is not affiliated with the original creators or rights holders of the
games. The emulator is provided in good faith, “as is”, without any warranties, as a research and documentation tool.
