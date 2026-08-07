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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class TeleportUtil {

    /**
     * Whether the return teleporter for <em>this arrival point</em> is already there.
     * <p>
     * This used to scan a radius of 16 -- 35,937 positions -- for any return teleporter at all, and
     * report ready if it found one. Standing up a second teleporter near an existing one therefore
     * saw the neighbour's platform, skipped building its own, then failed to place a teleporter into
     * solid rock. The caller's own comment says not to skip creation because some other teleporter
     * is nearby; this now matches that.
     */
    public static boolean isPlatformReady(Level level, BlockPos center, Block teleporterBlock) {
        return level.getBlockState(center).is(teleporterBlock)
                || level.getBlockState(center.above()).is(teleporterBlock);
    }
}
