package com.lithiumcraft.dimension_expansion.datagen.worldgen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class PlacedFeatureProvider {

    public static final ResourceKey<PlacedFeature> QUARTZ_ORE =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "quartz_ore"));

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(QUARTZ_ORE,
                new PlacedFeature(
                        configured.getOrThrow(ConfiguredFeatureProvider.QUARTZ_ORE),
                        List.of(
                                CountPlacement.of(20),
                                InSquarePlacement.spread(),
                                HeightRangePlacement.uniform(
                                        VerticalAnchor.absolute(-64),
                                        VerticalAnchor.absolute(320)
                                ),
                                BiomeFilter.biome()
                        )
                )
        );
    }
}
