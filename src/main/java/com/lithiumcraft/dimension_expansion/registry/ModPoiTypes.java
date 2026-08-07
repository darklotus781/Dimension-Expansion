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

package com.lithiumcraft.dimension_expansion.registry;

import com.google.common.collect.ImmutableSet;
import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Points of interest, so return teleporters can be found the way nether portals are.
 * <p>
 * A wide search cannot be a block scan -- 128 blocks is sixteen million positions, and reading a
 * block state on a ServerLevel generates the chunk it lands in. The POI index is what vanilla's own
 * portal search uses: it is stored per chunk section, read straight from the region files without
 * generating anything, and NeoForge keeps modded entries in it automatically as blocks are placed
 * and broken. Nothing here needs maintaining by hand.
 */
public class ModPoiTypes {

    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, DimensionExpansion.MOD_ID);

    /**
     * Every return teleporter is a point of interest.
     * <p>
     * maxTickets 0: nothing ever "occupies" one the way a villager claims a bed, so searches use
     * {@code Occupancy.ANY} and the ticket count is irrelevant.
     */
    public static final DeferredHolder<PoiType, PoiType> RETURN_TELEPORTER = POI_TYPES.register(
            "return_teleporter",
            () -> new PoiType(
                    ImmutableSet.copyOf(
                            ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().getStateDefinition().getPossibleStates()),
                    0,
                    1));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
    }
}
