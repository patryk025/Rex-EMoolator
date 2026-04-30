# Rex-EMoolator

[Polski](README.pl.md) | **English**

An open-source emulator that recreates the behavior of the Piklib/BlooMoo engine used in Aidem Media games.

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
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/4">Link</a></td>
</tr>
<tr>
<td>Reksio i Czarodzieje</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/6">Link</a></td>
</tr>
<tr>
<td>Reksio i Wehikuł Czasu</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/8">Link</a></td>
</tr>
<tr>
<td>Reksio i Kapitan Nemo</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/10">Link</a></td>
</tr>
<tr>
<td>Reksio i Kretes w Akcji!</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/11">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Wyprawa po Złote Runo</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/12">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Wojna Trojańska</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/13">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Przygody Odyseusza</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/14">Link</a></td>
</tr>
<tr>
<td>Poznaj Mity: Herkules</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td><a href="https://github.com/users/patryk025/projects/15">Link</a></td>
</tr>
<tr>
<td>Tezeusz i Nić Ariadny</td>
<td><img alt="Unknown" src="https://img.shields.io/badge/Unknown-lightgrey"/></td>
<td>Requires re-evaluation after interpreter refactor.</td>
<td>N/A</td>
<td></td>
</tr>
</tbody>
</table>

## Screenshots
### Reksio i Skarb Piratów
![Screenshot from Reksio i Skarb Piratów](images/risp.jpg)
<br>*Screenshot from Reksio i Skarb Piratów*

### Reksio i Ufo
![Screenshot from Reksio i Ufo](images/riu.jpg)
<br>*Screenshot from Reksio i Ufo*

### Reksio i Czarodzieje
![Screenshot from Reksio i Czarodzieje](images/ric.jpg)
<br>*Screenshot from Reksio i Czarodzieje*

### Reksio i Wehikuł Czasu
![Screenshot from Reksio i Wehikuł Czasu](images/riwc.jpg)
<br>*Screenshot from Reksio i Wehikuł Czasu*

### Reksio i Kapitan Nemo
![Reksio i Kapitan Nemo menu](images/rikn_menu.jpg)
<br>*Reksio i Kapitan Nemo menu*

![Intro after clicking the drawer](images/rikn_gra_intro.jpg)
<br>*Intro after clicking the drawer*

### Reksio i Kretes w Akcji
![Reksio i Kretes w Akcji intro](images/rikwa_intro.jpg)
<br>*Reksio i Kretes w Akcji intro*

![Reksio i Kretes w Akcji menu](images/rikwa_menu.jpg)
<br>*Reksio i Kretes w Akcji menu*

![Reksio i Skarb Piratów intro](images/rikwa_risp_intro.jpg)
<br>*Reksio i Skarb Piratów intro*

![Reksio i Skarb Piratów gameplay](images/rikwa_risp_gra.jpg)
<br>*Reksio i Skarb Piratów gameplay*

![Reksio i Ufo intro](images/rikwa_riu_intro.jpg)
<br>*Reksio i Ufo intro*

![Reksio i Ufo gameplay](images/rikwa_riu_gra.jpg)
<br>*Reksio i Ufo gameplay*

![Reksio i Czarodzieje intro](images/rikwa_ric_intro1.jpg)
<br>*Reksio i Czarodzieje intro*

![Reksio i Czarodzieje intro](images/rikwa_ric_intro2.jpg)
<br>*Reksio i Czarodzieje intro*

![Reksio i Czarodzieje gameplay](images/rikwa_ric_gra.jpg)
<br>*Reksio i Czarodzieje gameplay*

![Reksio i Wehikuł Czasu menu](images/rikwa_riwc_menu.jpg)
<br>*Reksio i Wehikuł Czasu menu*

![Reksio i Wehikuł Czasu gameplay moments before crash](images/rikwa_riwc_gra.png)
<br>*Reksio i Wehikuł Czasu gameplay moments before crash*

![Super Heros i Kapitan Nemo intro](images/rikwa_shikn_intro.jpg)
<br>*Super Heros i Kapitan Nemo intro*

![Super Heros i Kapitan Nemo gameplay](images/rikwa_shikn_gra.jpg)
<br>*Super Heros i Kapitan Nemo gameplay*

### Poznaj Mity: Wyprawa po Złote Runo
![Screenshot from Wyprawa po Złote Runo](images/wpzr.jpg)
<br>*Screenshot from Wyprawa po Złote Runo*

### Poznaj Mity: Wojna Trojańska
![Screenshot from Wojna Trojańska](images/wt.jpg)
<br>*Screenshot from Wojna Trojańska*

### Poznaj Mity: Przygody Odyseusza
![Screenshot from Przygody Odyseusza](images/po.jpg)
<br>*Screenshot from Przygody Odyseusza*

### Poznaj Mity: Herkules
![Screenshot from Herkules](images/herc.jpg)
<br>*Screenshot from Herkules*

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
