package com.lithiumcraft.dimension_expansion.registry;

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
            FEATURES.register("stalactite", () -> new StalactiteFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> STALAGMITE =
            FEATURES.register("stalagmite", () -> new StalagmiteFeature(NoneFeatureConfiguration.CODEC));

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
