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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class SimpleTeleporter {
    private final ResourceKey<Level> targetDimension;
    private final int targetY;

    public SimpleTeleporter(ResourceKey<Level> targetDimension, int targetY) {
        this.targetDimension = targetDimension;
        this.targetY = targetY;
    }

    public ResourceKey<Level> getTargetDimension() {
        return targetDimension;
    }

    public BlockPos getDestination(BlockPos origin) {
        return new BlockPos(origin.getX(), targetY, origin.getZ());
    }
}
