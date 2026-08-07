/*
 * Dimension Expansion
 * Copyright (c) 2025 DarkLotus (DarkLotus781) / LithiumCraft
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lithiumcraft.dimension_expansion.worldgen.feature;

import com.lithiumcraft.dimension_expansion.worldgen.feature.config.CobbleSpikesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class CobbleSpikesFeature extends Feature<CobbleSpikesConfig> {
    private static final BlockState COBBLE = Blocks.COBBLESTONE.defaultBlockState();

    // ~8% skip on ceiling so it isn't a perfect carpet; set to 0 for truly every column
    private static final int CEILING_SKIP_OUT_OF_256 = 20;

    public CobbleSpikesFeature() { super(CobbleSpikesConfig.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<CobbleSpikesConfig> ctx) {
        WorldGenLevel level = ctx.level();
        CobbleSpikesConfig cfg = ctx.config();
        RandomSource rng = ctx.random();

        ChunkPos chunk = new ChunkPos(ctx.origin());
        int minX = chunk.getMinBlockX(), maxX = chunk.getMaxBlockX();
        int minZ = chunk.getMinBlockZ(), maxZ = chunk.getMaxBlockZ();

        boolean placed = false;

        // ---------------- FLOOR pass: sparser blue-noise tiles -> flat/wide blobs ----------------
        final int tf = Math.max(2, cfg.floorTile()); // 2 = dense, 3 = medium, 4 = sparse
        int tx0 = Math.floorDiv(minX, tf), tx1 = Math.floorDiv(maxX, tf);
        int tz0 = Math.floorDiv(minZ, tf), tz1 = Math.floorDiv(maxZ, tf);

        for (int tx = tx0; tx <= tx1; tx++) {
            for (int tz = tz0; tz <= tz1; tz++) {
                long h = Mth.getSeed(tx, 0, tz) ^ level.getSeed();
                int pick = (int) Math.floorMod(h, (long) tf * tf); // choose 1 winning offset in this tile
                int ox = pick % tf;
                int oz = pick / tf;

                int x = tx * tf + ox;
                int z = tz * tf + oz;
                if (x < minX || x > maxX || z < minZ || z > maxZ) continue;

                Integer y = ColumnScan.findFloor(level, x, z, cfg.yMin(), cfg.yMax());
                if (y == null) continue;

                int height = cfg.floorHeight().sample(rng); // 1..3
                int rBase  = cfg.floorRadius().sample(rng); // 1..3
                if ((y - cfg.yMin()) <= 1 && height < 2) height++; // tiny boost near band bottom

                placed |= placeFloorBlob(level, x, y, z, rBase, height, minX, maxX, minZ, maxZ);
            }
        }

        // ---------------- CEILING pass: near-every-column stalactites ----------------
        final int tc = Math.max(1, cfg.ceilingTile()); // 1 => evaluate every XZ column
        int cx0 = Math.floorDiv(minX, tc), cx1 = Math.floorDiv(maxX, tc);
        int cz0 = Math.floorDiv(minZ, tc), cz1 = Math.floorDiv(maxZ, tc);

        for (int tx = cx0; tx <= cx1; tx++) {
            for (int tz = cz0; tz <= cz1; tz++) {
                long h = (Mth.getSeed(tx, 0, tz) ^ ~level.getSeed()); // different hash than floor
                int pick = (int) Math.floorMod(h, (long) tc * tc);
                int ox = pick % tc;
                int oz = pick / tc;

                int x = tx * tc + ox;
                int z = tz * tc + oz;
                if (x < minX || x > maxX || z < minZ || z > maxZ) continue;

                // small deterministic skip so it's not a perfect carpet
                if ((Mth.getSeed(x, 0, z) & 0xFF) < CEILING_SKIP_OUT_OF_256) continue;

                Integer y = ColumnScan.findCeiling(level, x, z, cfg.yMin(), cfg.yMax());
                if (y == null) continue;

                int len = cfg.ceilingHeight().sample(rng); // 6..32
                if ((cfg.yMax() - y) <= 1 && len < 8) len += 2; // tiny boost near band top

                placed |= growDown(level, x, y, z, len, cfg.yMin());
            }
        }

        return placed;
    }

    // -------- floor blob helpers (flat, wide, ragged edges) --------

    private boolean placeFloorBlob(WorldGenLevel level, int cx, int y, int cz,
                                   int rBase, int height, int minX, int maxX, int minZ, int maxZ) {
        boolean any = false;
        rBase = Math.max(1, Math.min(rBase, 3));
        height = Math.max(1, Math.min(height, 3));

        // bottom layer: filled disc with ragged edge
        any |= fillBlobLayer(level, cx, y, cz, rBase, minX, maxX, minZ, maxZ, true);

        int yTop = y;
        int r = rBase - 1;
        for (int i = 1; i < height && r >= 1; i++) {
            int yy = y + i;
            any |= fillBlobLayer(level, cx, yy, cz, r, minX, maxX, minZ, maxZ, true);
            yTop = yy;
            r--;
        }

        // optional short 1-wide stem if we still have height budget
        for (int yy = yTop + 1; yy < y + height; yy++) {
            BlockPos p = new BlockPos(cx, yy, cz);
            if (!level.isEmptyBlock(p)) break;
            level.setBlock(p, COBBLE, 2);
            any = true;
        }
        return any;
    }

    private boolean fillBlobLayer(WorldGenLevel level, int cx, int y, int cz, int r,
                                  int minX, int maxX, int minZ, int maxZ, boolean raggedEdge) {
        boolean any = false;
        int r2 = r * r;

        for (int dz = -r; dz <= r; dz++) {
            for (int dx = -r; dx <= r; dx++) {
                int x = cx + dx, z = cz + dz;
                if (x < minX || x > maxX || z < minZ || z > maxZ) continue;

                int d2 = dx*dx + dz*dz;
                if (d2 > r2) continue;

                // ragged edge only on the outer ring (interior solid)
                if (raggedEdge && d2 > (r - 1) * (r - 1)) {
                    long h = Mth.getSeed(x, y, z) ^ 0xC3A5C85C97CB3127L;
                    if ((h & 0xFF) < 102) continue; // ~40% skip on edge
                }

                BlockPos p = new BlockPos(x, y, z);
                if (level.isEmptyBlock(p)) {
                    level.setBlock(p, COBBLE, 2);
                    any = true;
                }
            }
        }
        return any;
    }

    // -------- stalactite grow-down --------

    private boolean growDown(WorldGenLevel level, int x, int yStart, int z, int len, int yMin) {
        boolean any = false;
        for (int i = 0; i < len && (yStart - i) >= yMin; i++) {
            BlockPos p = new BlockPos(x, yStart - i, z);
            if (!level.isEmptyBlock(p)) break;
            level.setBlock(p, COBBLE, 2);
            any = true;
        }
        return any;
    }

    // -------- floor/ceiling finders --------
}
