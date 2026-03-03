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

package com.lithiumcraft.dimension_expansion.util.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TeleportUtil {

    public static boolean hasNearbyTeleporter(ServerLevel level, BlockPos center, Block targetTeleporterBlock, int radius) {
        int r = Math.max(1, radius);

        for (BlockPos pos : BlockPos.withinManhattan(center, r, r, r)) {
            if (level.getBlockState(pos).is(targetTeleporterBlock)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPlatformReady(Level level, BlockPos center, Block teleporterBlock) {
        int radius = 16;

        for (BlockPos pos : BlockPos.withinManhattan(center, radius, radius, radius)) {
            if (level.getBlockState(pos).is(teleporterBlock)) {
                return true;
            }
        }

        return false;
    }
}
