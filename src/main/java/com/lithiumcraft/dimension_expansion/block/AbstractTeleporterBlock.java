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

package com.lithiumcraft.dimension_expansion.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class AbstractTeleporterBlock extends BaseEntityBlock {

    // Shared property factory for all teleporters
    protected static BlockBehaviour.Properties teleporterProps() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                .lightLevel(s -> 15)
                .requiresCorrectToolForDrops()
                .strength(100.0F, 1200.0F)
                .noOcclusion();
    }

    protected AbstractTeleporterBlock(BlockBehaviour.Properties props) {
        super(props);
    }

}
