package com.lithiumcraft.dimension_expansion.datagen.worldgen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class PlacedFeatureProvider {

    // ------------------------------
    // Existing feature
    // ------------------------------
    public static final ResourceKey<PlacedFeature> QUARTZ_ORE =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "quartz_ore"));

    // ------------------------------
    // New features
    // ------------------------------
    public static final ResourceKey<PlacedFeature> STALACTITE =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "stalactite"));

    public static final ResourceKey<PlacedFeature> STALAGMITE =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "stalagmite"));

//    public static final ResourceKey<PlacedFeature> CAVE_SPIRE =
//            ResourceKey.create(Registries.PLACED_FEATURE,
//                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "cave_spire"));

    public static final ResourceKey<PlacedFeature> COBBLE_SPIKES_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "cobble_spikes"));

    public static final ResourceKey<PlacedFeature> COBBLE_SPIRE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "cobble_spire"));


    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        // ------------------------------
        // Quartz ore
        // ------------------------------
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

        // ------------------------------
        // Stalactites
        // ------------------------------
        context.register(STALACTITE,
                new PlacedFeature(
                        configured.getOrThrow(ConfiguredFeatureProvider.STALACTITE),
                        List.of(
                                CountPlacement.of(UniformInt.of(20, 40)), // reasonable density
                                InSquarePlacement.spread(),
                                HeightRangePlacement.uniform(
                                        VerticalAnchor.absolute(-64),
                                        VerticalAnchor.absolute(128)
                                ),
                                // Scan downward to find solid ceiling within 12 blocks
                                EnvironmentScanPlacement.scanningFor(
                                        Direction.DOWN,
                                        BlockPredicate.solid(),
                                        BlockPredicate.replaceable(),
                                        12
                                ),
                                RarityFilter.onAverageOnceEvery(1), // ensure one pass per chunk
                                BiomeFilter.biome()
                        )
                )
        );


        // ------------------------------
        // Stalagmites
        // ------------------------------
        context.register(STALAGMITE,
                new PlacedFeature(
                        configured.getOrThrow(ConfiguredFeatureProvider.STALAGMITE),
                        List.of(
                                CountPlacement.of(UniformInt.of(20, 40)),
                                InSquarePlacement.spread(),
                                HeightRangePlacement.uniform(
                                        VerticalAnchor.absolute(-64),
                                        VerticalAnchor.absolute(128)
                                ),
                                // Scan upward to find solid ground within 12 blocks
                                EnvironmentScanPlacement.scanningFor(
                                        Direction.UP,
                                        BlockPredicate.solid(),
                                        BlockPredicate.replaceable(),
                                        12
                                ),
                                RarityFilter.onAverageOnceEvery(1),
                                BiomeFilter.biome()
                        )
                )
        );

//        context.register(CAVE_SPIRE,
//                new PlacedFeature(
//                        configured.getOrThrow(ConfiguredFeatureProvider.CAVE_SPIRE),
//                        List.of(
//                                NoiseBasedCountPlacement.of(1, 400.0D, -0.2D),
//                                CountPlacement.of(UniformInt.of(2, 4)),
//                                InSquarePlacement.spread(),
//                                HeightRangePlacement.uniform(
//                                        VerticalAnchor.absolute(0),
//                                        VerticalAnchor.absolute(256)
//                                ),
//                                BiomeFilter.biome()
//                        )
//                )
//        );

        context.register(PlacedFeatureProvider.COBBLE_SPIKES_PLACED,
                new PlacedFeature(
                        configured.getOrThrow(ConfiguredFeatureProvider.COBBLE_SPIKES),
                        List.of(
                                InSquarePlacement.spread(),
                                CountPlacement.of(1),       // exactly one painter pass per chunk
                                BiomeFilter.biome()
                        )
                )
        );




        // PlacedFeatureProvider
        context.register(COBBLE_SPIRE_PLACED,
                new PlacedFeature(
                        configured.getOrThrow(ConfiguredFeatureProvider.COBBLE_SPIRE),
                        List.of(
                                InSquarePlacement.spread(),
                                RarityFilter.onAverageOnceEvery(16),
                                HeightRangePlacement.uniform(VerticalAnchor.absolute(80), VerticalAnchor.absolute(144)),
                                BiomeFilter.biome()
                        )
                )
        );

    }
}
