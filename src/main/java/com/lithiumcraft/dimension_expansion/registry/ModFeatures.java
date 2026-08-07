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

import net.minecraft.core.Direction;
import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.worldgen.feature.*;
import com.lithiumcraft.dimension_expansion.worldgen.feature.config.CobbleSpikesConfig;
import com.lithiumcraft.dimension_expansion.worldgen.feature.config.CobbleSpireConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, DimensionExpansion.MOD_ID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> STALACTITE =
            FEATURES.register("stalactite", () -> new StoneSpikeFeature(NoneFeatureConfiguration.CODEC, Direction.DOWN));

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> STALAGMITE =
            FEATURES.register("stalagmite", () -> new StoneSpikeFeature(NoneFeatureConfiguration.CODEC, Direction.UP));

//    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CAVE_SPIRE =
//            FEATURES.register("cave_spire", () -> new CaveSpireFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, Feature<CobbleSpikesConfig>> COBBLE_SPIKES =
            FEATURES.register("cobble_spikes", CobbleSpikesFeature::new);

    public static final DeferredHolder<Feature<?>, Feature<CobbleSpireConfig>> COBBLE_SPIRE =
            FEATURES.register("cobble_spire", CobbleSpireFeature::new);


    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }


}
