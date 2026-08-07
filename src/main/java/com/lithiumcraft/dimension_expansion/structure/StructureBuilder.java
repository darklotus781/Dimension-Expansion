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

package com.lithiumcraft.dimension_expansion.structure;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class StructureBuilder {

    /**
     * Place a block without the neighbour-update cascade.
     * <p>
     * These builders fill thousands of positions -- the stone-block dome alone walks 21^3 twice --
     * and {@code setBlockAndUpdate} asks every one of them to notify its neighbours. For bulk fill
     * we only need clients told, which is what flag 2 does.
     */
    private static void setQuiet(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_CLIENTS);
    }

    public static void buildDeepBeneathPlatform(ServerLevel level, BlockPos center) {
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();

        // Clear space: 5x5 area, 4 blocks high (Y to Y+3)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    setQuiet(level, target, Blocks.AIR.defaultBlockState());
                }
            }
        }

        // Platform layer at Y
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                setQuiet(level, pos, cobble);
            }
        }


        // Teleporter block (Y + 1)
        level.setBlockAndUpdate(center.above(), teleporter);
    }

    public static void buildStoneBlockPlatform(ServerLevel level, BlockPos center) {
        final BlockState shell = Blocks.STONE.defaultBlockState();
        final BlockState floor = Blocks.STONE.defaultBlockState();
        final BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();

        final int DH = 4;  // dome vertical offset
        final int R  = 9;  // radius

        // “WorldEdit + tolerance” thresholds
        final double outer = R + 0.5;
        final double inner = R - 0.5;
        final double smooth = 0.25;  // small safety margin to prevent voxel skips
        final double rOuter2 = (outer + smooth) * (outer + smooth);
        final double rInner2 = (inner - smooth) * (inner - smooth);

        // --- 1. Carve interior (everything strictly inside inner surface) ---
        for (int x = -R - 1; x <= R + 1; x++) {
            for (int y = 0; y <= R + DH + 1; y++) {
                for (int z = -R - 1; z <= R + 1; z++) {
                    double d2 = x * x + (y - DH) * (y - DH) + z * z;
                    if (d2 < rInner2) {
                        setQuiet(level, center.offset(x, y, z), Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }

        // --- 2. Floor and teleporter ---
        for (int dx = -R; dx <= R; dx++) {
            for (int dz = -R; dz <= R; dz++) {
                setQuiet(level, center.offset(dx, 0, dz), floor);
            }
        }
        level.setBlockAndUpdate(center, teleporter);

        // --- 3. Shell (top hemisphere) ---
        for (int x = -R - 1; x <= R + 1; x++) {
            for (int y = 1; y <= R; y++) {
                for (int z = -R - 1; z <= R + 1; z++) {
                    double dist2 = x * x + (y - DH) * (y - DH) + z * z;

                    // Allow ±smooth band to connect diagonal voxels
                    if (dist2 >= rInner2 - (smooth * smooth)
                            && dist2 <= rOuter2 + (smooth * smooth)) {
                        setQuiet(level, center.offset(x, y, z), shell);
                    }
                }
            }
        }
    }

    public static void buildMiningPlatform(ServerLevel level, BlockPos center) {
        BlockPos teleporterPos = center; // use resolved surface block
        BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();
        level.setBlockAndUpdate(teleporterPos, teleporter);
    }
}
