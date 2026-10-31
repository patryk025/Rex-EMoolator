# Building and development

The project builds with Gradle. The wrapper is in the repository, so JDK 21 or newer is all you need; on Windows, use `gradlew.bat` instead of `./gradlew`.

## Modules

| Module | Role |
|---|---|
| `core` | all emulator logic: interpreter, managers, loaders, VFS |
| `desktop` | the Swing launcher and running games through LWJGL3 |
| `android` | the Android app |
| `android-api-check` | a lint-only module that exposes the `core` sources as an Android library |

The code structure is described in [Architecture](../internals/architecture.md).

## Desktop

```bash
./gradlew desktop:run        # run the emulator
./gradlew desktop:build      # JAR and distribution ZIP
./gradlew desktop:shadowJar  # JAR only
./gradlew desktop:distZip    # ZIP only
./gradlew desktop:jpackage   # installer for the current system
```

| Artifact | Path | Use |
|---|---|---|
| JAR | `desktop/build/libs/Rex_EMoolator-desktop-<version>.jar` | convenient single-file distribution |
| ZIP | `desktop/build/distributions/Rex_EMoolator-desktop-<version>.zip` | recommended for redistribution, because libraries (including LGPL ones) can be replaced by overwriting files in `lib/` |
| installer | `desktop/build/jpackage/` | `.exe`, `.dmg`, or `.deb`, depending on the system |

Without the `RELEASE_VERSION` variable the version is `0.1-SNAPSHOT`, so a local JAR is started with:

```bash
java -jar desktop/build/libs/Rex_EMoolator-desktop-0.1-SNAPSHOT.jar
```

The `desktop:jpackage` task uses the `jpackage` tool from the current JDK or from `JPACKAGE_JAVA_HOME`. On Linux it needs the `fakeroot` package, and on Windows the WiX tools.

## Android

You need the Android SDK, pointed to by `local.properties` (`sdk.dir=...`) or `ANDROID_HOME`.

```bash
./gradlew android:assembleDebug
```

The APK lands in `android/build/outputs/apk/debug/`. The GitHub releases ship exactly this `debug` variant, signed with a CI key.

`android:assembleRelease` builds a variant optimised by R8, but the project defines no key for it, so the resulting APK is unsigned and has to be signed by hand before installing.

## Tests and quality checks

```bash
./gradlew :core:test                               # unit tests
./gradlew :android:lintDebug -PandroidApiCheck     # API 24 compatibility of :android
./gradlew :android-api-check:lintDebug             # API 24 compatibility of :core
./gradlew :core:jmh -PjmhIncludeRegex='.*Benchmark.*'   # JMH benchmarks
```

CI runs the tests for pull requests and for commits whose message starts with `fix`, `feat`, `refactor`, `perf`, or `test`. `docs:` and `chore:` commits only go through the API check. Benchmarks run on `perf:` commits.

## Dependency licenses

```bash
./gradlew updateThirdPartyNotices   # regenerate THIRD-PARTY-NOTICES.md
./gradlew checkLicense              # fail on a new or unapproved license family
```

`desktop:build` refreshes `THIRD-PARTY-NOTICES.md` automatically, and both `check` and `build` fail when the file or the allowed license rules are out of date.

## Documentation

The site is built from `docs/` with Material for MkDocs. Pages exist in Polish and English, and their structure should stay in sync.

```bash
python -m venv .venv_docs
.venv_docs/bin/python -m pip install -r docs/requirements.txt   # Windows: .venv_docs\Scripts\python
.venv_docs/bin/python -m mkdocs serve
```

`tools/check_docs_links.py` checks links and anchors. Run it with the Python from the same environment.

## Execution tracing

The emulator can record how scripts run. The JVM properties `rex.trace.enabled=true` and, optionally, `rex.trace.path=<file>` turn this on. Open a recording with the viewer: `python tools/trace-viewer/viewer.py`.

The other tools are described in [`tools/README.md`](https://github.com/patryk025/Rex-EMoolator/blob/main/tools/README.md).

## Releases

A release is created by pushing a `v*` tag or by running the `Build and Release` workflow manually. The workflow builds installers for three systems, the JAR, the ZIP, and the APK, and publishes them with a changelog generated from the commit history.
