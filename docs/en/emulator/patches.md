# Patches

A patch is an overlay on top of the game files: an official fix, a difficulty change, or a fan modification. The emulator does not change the original data; it mounts the patch's files above it. On read, save games take precedence, then patches, and the original files come last.

## Managing patches

The **Patches** button next to a game opens the patch window. The list combines installed patches with the catalog built into the emulator and the one fetched from the network, but it only shows what fits the game.

| Action | Meaning |
|---|---|
| **Install** | downloads the patch archive (ZIP or RAR) and unpacks it into the patches directory |
| **Enable/Disable** | decides whether the patch is mounted the next time the game starts |
| **Up** / **Down** | changes the mount order when several patches touch the same files |
| **Uninstall** | deletes the patch files |
| **Import local archive** | installs an archive from disk; it must contain `patch.json` |
| **Link work folder** | mounts a folder without copying it, so edits show up the next time the game starts |

The installer skips `.dll`, `.exe`, and `install.ini` files from the archive, because the emulator does not use them.

## Compatibility

The emulator identifies a game's edition by the SHA-1 hash of its engine library and rates each patch on that basis:

| Compatibility | Meaning |
|---|---|
| **Exact** | the patch author prepared it for this edition |
| **Family (needs forcing)** | the patch is for this game, but a different edition; enabling it is a deliberate choice |
| **None** | the patch does not fit the game |

If the engine library was modified (for example by a no-CD patch), its hash will be unknown. The emulator then tries to identify the game by name, and if that is not enough, you can enter the **Patch family** by hand in the game settings, for example `reksio-czarodzieje`.

## Creating a patch

A patch is a directory with a `patch.json` file and a tree of files that mirrors the game's layout:

```
my-patch/
├── patch.json
└── files/
    └── DANE/
        └── scene12/music.cnv
```

An example manifest:

```json
{
  "id": "reksio-ufo-s65-zamek",
  "name": "Reksio i UFO — screen fade fix",
  "description": "Fixes the broken screen fade in scene S65_ZAMEK",
  "author": "Author",
  "version": "1.0.0",
  "targetHashes": ["5F6CBB33576D4F5493F141DDEE70CD108EA0F9D5"],
  "targetFamily": "reksio-ufo",
  "filesRoot": "files",
  "source": { "type": "LOCAL" }
}
```

| Field | Meaning |
|---|---|
| `id` | unique identifier, also used as the patch directory name |
| `targetHashes` | SHA-1 hashes of the engine libraries the patch was tested against |
| `targetFamily` | the game family, which lets the patch be enabled on other editions |
| `filesRoot` | subdirectory holding the overlay files, `files` by default |
| `archiveRoot` | directory inside the archive that is the overlay root; useful when an archive wraps everything in a top folder |
| `source` | where the patch comes from: `LOCAL`, `URL`, or `GDRIVE` (a link anyone can open) |
| `reference` | optional link to a description of the patch |
| `conflicts`, `requires`, `supersedes` | ids of patches this one conflicts with, requires, or replaces |

While working on a patch, **Link work folder** is the convenient option. The folder does not need a `patch.json`: without one, the folder itself is treated as the overlay root.

Family names are in the [`GameFamilies`](https://github.com/patryk025/Rex-EMoolator/blob/main/core/src/pl/genschu/bloomooemulator/logic/GameFamilies.java) class, and the built-in patch catalog is in [`assets/patch_catalog.json`](https://github.com/patryk025/Rex-EMoolator/blob/main/assets/patch_catalog.json). The remote catalog can add new entries and override existing ones with the same `id`.
