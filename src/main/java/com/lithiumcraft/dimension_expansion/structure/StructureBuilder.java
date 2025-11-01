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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class StructureBuilder {

    public static void buildDeepBeneathPlatform(ServerLevel level, BlockPos center) {
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
//        BlockState torch = ModBlocks.BURNABLE_TORCH.get().defaultBlockState();
        BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();

        // Clear space: 5x5 area, 4 blocks high (Y to Y+3)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(target);
                        level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
                }
            }
        }

        // Platform layer at Y
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                level.setBlockAndUpdate(pos, cobble);
            }
        }

        // Torch corners (Y + 1)
//        level.setBlockAndUpdate(center.offset(-2, 1, -2), torch);
//        level.setBlockAndUpdate(center.offset( 2, 1, -2), torch);
//        level.setBlockAndUpdate(center.offset(-2, 1,  2), torch);
//        level.setBlockAndUpdate(center.offset( 2, 1,  2), torch);

        // Teleporter block (Y + 1)
        level.setBlockAndUpdate(center.above(), teleporter);
    }

    public static void buildStoneBlockPlatform(ServerLevel level, BlockPos center) {
        BlockState cobble = Blocks.STONE.defaultBlockState();
        BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();

        // Clear space: 5x5 area, 4 blocks high (Y to Y+3)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(target);
                    level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
                }
            }
        }

        // Platform layer at Y
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                level.setBlockAndUpdate(pos, cobble);
            }
        }

        // Teleporter block (Y + 1)
        level.setBlockAndUpdate(center.above(), teleporter);
    }

    public static void buildMiningPlatform(ServerLevel level, BlockPos center) {
        BlockPos teleporterPos = center; // use resolved surface block
        BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();
        level.setBlockAndUpdate(teleporterPos, teleporter);
    }
}
