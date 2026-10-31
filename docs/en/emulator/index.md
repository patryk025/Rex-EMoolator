# Emulator

Rex-EMoolator runs games built on the Piklib and BlooMoo engines without the original DLLs: it reads the game data itself and recreates the engine's behaviour. It runs on Windows, Linux, macOS, and Android.

!!! note "Game files"
    The emulator does not include any games. You need your own copy: a disc, a disc image, or an installed game.

## Download

Ready-made packages are attached to every [release on GitHub](https://github.com/patryk025/Rex-EMoolator/releases/latest):

| System | File | Notes |
|---|---|---|
| Windows | `Rex-EMoolator-<version>.exe` | installer with a bundled Java runtime |
| macOS | `Rex-EMoolator-<version>.dmg` | disk image with a bundled Java runtime |
| Linux | `rex-emoolator_<version>_amd64.deb` | package with a bundled Java runtime |
| Android 7.0+ | `Rex_Emoolator-android-v<version>-debug.apk` | installed from outside the store |
| any | `Rex_EMoolator-desktop-v<version>.jar` | single file, requires Java 21+ |
| any | `Rex_EMoolator-desktop-v<version>.zip` | libraries as separate files in `lib/`, requires Java 21+ |

The installers are not code-signed, so Windows and macOS may warn before running them. Start the JAR with `java -jar`, and the ZIP version with the script in its `bin/` directory.

You can also [build the emulator from source](development.md).

## Adding a game

The emulator looks for `dane/Application.def`, which every supported game has. It can sit in:

- a folder with the installed game or a copy of the disc,
- an ISO 9660 or UDF disc image,
- a ZIP archive.

Images and archives are read directly, without unpacking. The format is detected from the content, not the file extension.

1. Click **Add game**.
2. Pick the source with **Choose folder** or **Choose image/ZIP**.
3. Fill in **Game subfolder** if needed. Without it, the emulator searches up to four levels deep, and if it finds several games (for example on the *Herkules + Odyseusz* disc), it lets you choose which ones to add.
4. Save, then start the game with **Run game**.

The dialog looks the same on Android. From Android 11 on, the app asks for all-files access on first launch; without it, it cannot read game data from device storage.

## Game settings

Each game has its own settings, available when adding it and through **Edit game**:

| Setting | Meaning |
|---|---|
| Mouse mode | **Touchscreen** or **Physical cursor** |
| Legacy clock granularity | the time resolution scripts see; see [clock profiles](../internals/loop.md#engine-clock) |
| Virtual joystick | controls the cursor with an on-screen joystick |
| Mr Policeman's code hint | shows the code in scenes that check the copy's legitimacy |
| Stretch to fullscreen | ignores the aspect ratio |
| FPS counter | displays frames per second |
| Patch family | identifies the game by hand when the emulator does not recognise its edition; see [Patches](patches.md) |

## Keyboard shortcuts

During a game, the function keys toggle diagnostic tools:

| Key | Action |
|---|---|
| `F1` | variable inspector |
| `F2` | graphics inspector |
| `F3` | button inspector |
| `F4` | graphics bounds |
| `F5` | [`MATRIX`](../reference/MATRIX.md) inspector |
| `F6` | performance statistics |
| `F7` | export scene graphics to `exported_graphics/` |
| `F8` | [`WORLD`](../reference/WORLD.md) inspector |
| `F9` | scene selector |
| `F10` | collision inspector |
| `F11` | pause |
| `F12` | [one engine pulse](../internals/loop.md#pause-and-stepping) |

## Where the emulator keeps its data

On a computer, everything lives in `.rexemoolator` in the user's home folder (on Windows, `%USERPROFILE%\.rexemoolator`):

| Path | Contents |
|---|---|
| `games.json` | the game list and its settings |
| `storage/<game id>/` | save games |
| `patches/` | installed patches |

Game files are never modified. On Android, the same data lives in the app's private directory.

## Next steps

- [Game status](games.md) — what works already and what does not.
- [Screenshots](screenshots.md) — how the games look in the emulator.
- [Patches](patches.md) — official fixes and fan modifications.
- [Building and development](development.md) — compiling, tests, and tools.
