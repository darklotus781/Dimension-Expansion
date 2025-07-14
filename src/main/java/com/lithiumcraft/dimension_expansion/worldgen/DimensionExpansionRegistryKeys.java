package com.lithiumcraft.dimension_expansion.worldgen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class DimensionExpansionRegistryKeys {
    public static final ResourceKey<DimensionType> DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, rl("deep_beneath"));
    public static final ResourceKey<LevelStem> LEVEL_STEM = ResourceKey.create(Registries.LEVEL_STEM, rl("deep_beneath"));
    public static final ResourceKey<NoiseGeneratorSettings> NOISE = ResourceKey.create(Registries.NOISE_SETTINGS, rl("deep_beneath"));
    public static final ResourceKey<Biome> ABANDONED_MINES = ResourceKey.create(Registries.BIOME, rl("abandoned_mines"));
    public static final ResourceKey<Biome> MINING = ResourceKey.create(Registries.BIOME, rl("mining"));
    public static final ResourceKey<Biome> STONE_BLOCK = ResourceKey.create(Registries.BIOME, rl("stone_block"));

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, path);
    }
}