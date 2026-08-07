# Changelog

All released versions of Dimension Expansion, for Minecraft 1.21.1.

The per-version files under `changelog/` are what CurseForge and Modrinth display, and are kept in
sync with this file.

## [1.2.9]

**Added**
- The mod list entry now shows a logo and links to the project page and issue tracker.

**Changed**
- Return teleporters are found and reused within 128 blocks, the way nether portals work, instead of leaving a new one behind on every trip.
- A reused teleporter keeps the destination it already had, so a portal always sends you where it used to.
- Deep Beneath mob drops now respect Looting.
- The Overworld Teleporter is now the Return Teleporter, since it sends you back to whichever dimension you left from.
- Updated to NeoForge 21.1.248.
- Marked as Minecraft 1.21.1 only, instead of claiming support for 1.21.2 and later.

**Fixed**
- Entering a dimension no longer freezes the server while it looks for a return teleporter.
- Arriving no longer drops you inside a block.
- Placing a teleporter near an existing one no longer fails silently.
- Clicking a teleporter now swings your arm, and no longer places the block you are holding.

## [1.2.8]

**Added**
- The Overworld return teleporter can be broken, though it does not drop itself.

**Fixed**
- Teleporters stayed linked when one end was broken.

## [1.2.7]

**Added**
- Block tags for Quartz Ore.

## [1.2.6]

**Added**
- Item tags for Quartz Ore.

## [1.2.5]

**Added**
- Item tags for the teleporters.

## [1.2.4]

**Fixed**
- Mob spawn health doubled on every world load.

## [1.2.3]

**Fixed**
- Incorrect tag: `data/dimension_expansion/tags/worldgen/placed_feature/mining/all_ores.json`.
- The Mining dimension was always daytime.
- Cloud height in the Mining dimension.
- The Stone Block spawn area is a large sphere rather than a cube.
- Assorted worldgen and datagen problems.

## [1.2.2]

**Added**
- Default tags for the teleporter blocks.
- A placeholder image for effects, to satisfy JEED.

**Fixed**
- Missing language entries for the biomes.

## [1.2.1]

**Changed**
- Removed vanilla ore generation. Use the datapack or KubeJS method instead, documented at
  https://github.com/darklotus781/Dimension-Expansion/wiki/Adding-Ores-to-Deep-Beneath-and-Mining-Dim-via-KubeJS-instead!
