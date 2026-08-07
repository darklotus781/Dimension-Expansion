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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;

/**
 * Finding the floor and ceiling of a cave column.
 * <p>
 * Both cobble features carried their own byte-identical copies of this.
 */
final class ColumnScan {

    private ColumnScan() {
    }

    /** Lowest air block in the range that has something solid to stand on, or null. */
    static Integer findFloor(WorldGenLevel level, int x, int z, int yMin, int yMax) {
        for (int y = yMin; y < yMax; y++) {
            BlockPos p = new BlockPos(x, y, z);
            if (level.isEmptyBlock(p)) {
                BlockPos below = p.below();
                if (!level.isEmptyBlock(below)
                        && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                    return y;
                }
            }
        }
        return null;
    }

    /** Highest air block in the range with something solid above it, or null. */
    static Integer findCeiling(WorldGenLevel level, int x, int z, int yMin, int yMax) {
        for (int y = yMax - 1; y >= yMin; y--) {
            BlockPos p = new BlockPos(x, y, z);
            if (level.isEmptyBlock(p)) {
                BlockPos above = p.above();
                if (!level.isEmptyBlock(above)
                        && level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)) {
                    return y;
                }
            }
        }
        return null;
    }
}
