package com.lithiumcraft.dimension_expansion.worldgen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class DimensionExpansionDimensions {
    public static final ResourceKey<Level> DEEP_BENEATH = ResourceKey.create(
        Registries.DIMENSION,
        ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "deep_beneath")
    );
    public static final ResourceKey<Level> MINING = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "mining")
    );
    public static final ResourceKey<Level> STONE_BLOCK = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "stone_block")
    );
}


