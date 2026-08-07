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

package com.lithiumcraft.dimension_expansion.datagen.worldgen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.registry.ModFeatures;
import com.lithiumcraft.dimension_expansion.worldgen.feature.config.CobbleSpikesConfig;
import com.lithiumcraft.dimension_expansion.worldgen.feature.config.CobbleSpireConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;


public class ConfiguredFeatureProvider {

    // ------------------------------
    // Quartz ore
    // ------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> QUARTZ_ORE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "quartz_ore"));

    // ------------------------------
    // Stalactite / stalagmite
    // ------------------------------
    public static final ResourceKey<ConfiguredFeature<?, ?>> STALACTITE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "stalactite"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> STALAGMITE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "stalagmite"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> CAVE_SPIRE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "cave_spire"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> COBBLE_SPIKES =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "cobble_spikes"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> COBBLE_SPIRE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "cobble_spire"));


    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // ------------------------------
        // Quartz Ore (existing)
        // ------------------------------
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> targets = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.QUARTZ_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_QUARTZ_ORE.get().defaultBlockState())
        );

        context.register(QUARTZ_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(targets, 28, 0.5F)));

        // ------------------------------
        // Stalactite / Stalagmite
        // ------------------------------
        context.register(STALACTITE,
                new ConfiguredFeature<>(ModFeatures.STALACTITE.get(), NoneFeatureConfiguration.INSTANCE));

        context.register(STALAGMITE,
                new ConfiguredFeature<>(ModFeatures.STALAGMITE.get(), NoneFeatureConfiguration.INSTANCE));

//        context.register(CAVE_SPIRE,
//                new ConfiguredFeature<>(ModFeatures.CAVE_SPIRE.get(), NoneFeatureConfiguration.INSTANCE));

        var spikesCfg = new CobbleSpikesConfig(
                UniformInt.of(1, 3),   // floor height
                UniformInt.of(1, 3),   // floor radius
                UniformInt.of(6, 32),  // ceiling length
                80, 144,               // cavern band
                5,                     // floor_tile -> density knob (3 is “every couple of blocks”)
                1                      // ceiling_tile -> 1 = evaluate every block
        );
        context.register(ConfiguredFeatureProvider.COBBLE_SPIKES,
                new ConfiguredFeature<>(ModFeatures.COBBLE_SPIKES.get(), spikesCfg));

        // --- Mega Spire (large pillar) using your CAVE_SPIRE key ---
        var spireCfg = new CobbleSpireConfig(
                UniformInt.of(4, 7), // fits; 7 is the absolute max that still fits
                80, 144
        );
        context.register(COBBLE_SPIRE,
                new ConfiguredFeature<>(com.lithiumcraft.dimension_expansion.registry.ModFeatures.COBBLE_SPIRE.get(), spireCfg)
        );
    }
}
