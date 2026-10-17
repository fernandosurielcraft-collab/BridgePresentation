# Bridge Presentation — Minecraft Forge 1.20.1

A first working scaffold for a 3D bridge-presentation mod.

## Current prototype features
- Forge 1.20.1 / Java 17 project.
- A `Slide Remote` item. The currently held remote displays only one slide.
- Up to 9 slides, selected by the item's `Slide` NBT tag.
- Press **O** to open the in-game annotation editor.
- Aim at a block, then add an arrow, line, text label, or wireframe box.
- Annotations are saved in `config/bridgepresentation.json`.
- Selecting a remote with a different `Slide` value changes the displayed annotation group.

## Important prototype limitations
This is a starter prototype, not yet a polished release. The visual editor currently creates preset labels and simple vertical arrows/lines at the targeted position; freehand drag drawing, arbitrary endpoint placement, text editing fields, multiplayer synchronization, and polished localization are not implemented yet. The line renderer is a basic world-space renderer and may need small compatibility fixes during compilation/testing.

## Build
1. Install a JDK 17.
2. Install/prepare Forge MDK 1.20.1, or let Gradle download the configured ForgeGradle plugin and dependencies.
3. Run `gradlew genIntellijRuns` (optional) and `gradlew build`.
4. The jar should appear in `build/libs/`.

If you don't have the Gradle wrapper files, generate them with an installed Gradle 8.x using `gradle wrapper`, then run `gradlew build`.

## Give yourself the remote
```mcfunction
/give @p bridgepresentation:slide_remote{Slide:1}
/give @p bridgepresentation:slide_remote{Slide:2}
/give @p bridgepresentation:slide_remote{Slide:3}
/give @p bridgepresentation:slide_remote{Slide:4}
/give @p bridgepresentation:slide_remote{Slide:5}
/give @p bridgepresentation:slide_remote{Slide:6}
/give @p bridgepresentation:slide_remote{Slide:7}
/give @p bridgepresentation:slide_remote{Slide:8}
/give @p bridgepresentation:slide_remote{Slide:9}
```

Place the differently tagged remotes in separate hotbar slots. Select one to show its slide. Hold a remote and press **O** to open the editor. Aim at a block to anchor new annotations there.

## Config format
`config/bridgepresentation.json` is a JSON array. Each object contains `slide`, `type` (`arrow`, `line`, `text`, `box`), `x1/y1/z1`, `x2/y2/z2`, `color`, and `text`. You can edit this file while the game is closed.


## Automated build with GitHub Actions
This project includes `.github/workflows/build.yml`. Upload the project contents to a GitHub repository, then open the repository's **Actions** tab and run **Build Forge mod** (or push to `main`). If the build succeeds, download the `bridgepresentation-jar` artifact from that workflow run. This only automates compilation; it does not replace in-game testing, and this prototype may need compile fixes.
