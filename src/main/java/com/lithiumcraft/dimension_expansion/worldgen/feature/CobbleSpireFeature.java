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

import com.lithiumcraft.dimension_expansion.worldgen.feature.config.CobbleSpireConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class CobbleSpireFeature extends Feature<CobbleSpireConfig> {
    private static final BlockState COBBLE = Blocks.COBBLESTONE.defaultBlockState();

    public CobbleSpireFeature() {
        super(CobbleSpireConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<CobbleSpireConfig> ctx) {
        WorldGenLevel level = ctx.level();
        RandomSource rng   = ctx.random();
        CobbleSpireConfig cfg = ctx.config();

        // Radius that fits entirely inside one 16x16 chunk (avoid far-chunk writes)
        int baseR  = Mth.clamp(cfg.radius().sample(rng), 2, 6); // recommend 4..6 in configured_feature
        int safety = baseR;

        // Use placement origin (already randomized by InSquarePlacement), then clamp to chunk interior
        BlockPos origin = ctx.origin();
        ChunkPos chunk  = new ChunkPos(origin); // block -> chunk
        int minX = chunk.getMinBlockX(), maxX = chunk.getMaxBlockX();
        int minZ = chunk.getMinBlockZ(), maxZ = chunk.getMaxBlockZ();

        int cx = Mth.clamp(origin.getX(), minX + safety, maxX - safety);
        int cz = Mth.clamp(origin.getZ(), minZ + safety, maxZ - safety);

        // Find cavern floor/ceiling air cells inside the band
        Integer floorY = ColumnScan.findFloor(level, cx, cz, cfg.yMin(), cfg.yMax());
        Integer ceilY  = ColumnScan.findCeiling(level, cx, cz, cfg.yMin(), cfg.yMax());
        if (floorY == null || ceilY == null || ceilY <= floorY + 2) return false;

        boolean placed = false;

        // Fuse into cobble skins (no air seam at either end)
        if (floorY - 1 >= level.getMinBuildHeight()) {
            placed |= placeLayer(level, cx, floorY - 1, cz, baseR, baseR, true,  minX, maxX, minZ, maxZ);
        }
        if (ceilY + 1 < level.getMaxBuildHeight()) {
            placed |= placeLayer(level, cx, ceilY + 1, cz, baseR, baseR, true,  minX, maxX, minZ, maxZ);
        }

        // Body: solid core + holey shell; thicker near floor/ceiling
        for (int y = floorY; y <= ceilY; y++) {
            int m = Math.min(y - floorY, ceilY - y);                    // distance to nearest boundary
            int thickBoost = (m < 9) ? (int) Math.ceil(Math.sqrt(9 - m)) : 0; // flare near ends

            int rHard = Math.max(1, baseR - 1 + thickBoost); // solid interior
            int rSoft = Math.max(rHard, baseR + thickBoost); // surface ring (holey)

            // mild ±1 jitter on the core to avoid dead-straight walls
            if (rng.nextFloat() < 0.15f) {
                rHard = Mth.clamp(rHard + (rng.nextBoolean() ? 1 : -1), 1, 7);
            }
            rSoft = Math.min(Math.max(rSoft, rHard), 7); // never exceed safe radius

            placed |= placeLayer(level, cx, y, cz, rHard, rSoft, false, minX, maxX, minZ, maxZ);
        }

        return placed;
    }

    // ---- helpers -------------------------------------------------------------

    /**
     * Place one horizontal layer:
     *  - filled disc up to rHard (solid core),
     *  - ring (rHard, rSoft] with ~50% keep for holes on the surface,
     * clipped to the current chunk; overwriteSolid forces setting even into non-air.
     */
    private boolean placeLayer(WorldGenLevel level, int cx, int y, int cz,
                               int rHard, int rSoft, boolean overwriteSolid,
                               int minX, int maxX, int minZ, int maxZ) {
        boolean any = false;
        int hard2 = rHard * rHard, soft2 = rSoft * rSoft;

        for (int dz = -rSoft; dz <= rSoft; dz++) {
            for (int dx = -rSoft; dx <= rSoft; dx++) {
                int d2 = dx * dx + dz * dz;
                if (d2 > soft2) continue;

                int x = cx + dx, z = cz + dz;
                if (x < minX || x > maxX || z < minZ || z > maxZ) continue; // never cross-chunk

                BlockPos p = new BlockPos(x, y, z);

                if (d2 <= hard2) {
                    if (overwriteSolid || level.isEmptyBlock(p)) {
                        level.setBlock(p, COBBLE, 2);
                        any = true;
                    }
                    continue;
                }

                // surface ring: ~50% keep -> holes on the surface, solid inside
                if (((Mth.getSeed(x, y, z) >>> 1) & 1L) != 0L) continue;

                if (overwriteSolid || level.isEmptyBlock(p)) {
                    level.setBlock(p, COBBLE, 2);
                    any = true;
                }
            }
        }
        return any;
    }
}
