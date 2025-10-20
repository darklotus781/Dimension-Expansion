// com.lithiumcraft.dimension_expansion.worldgen.feature.config.CobbleSpikesConfig
package com.lithiumcraft.dimension_expansion.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record CobbleSpikesConfig(
        IntProvider floorHeight,    // e.g., 1..3
        IntProvider floorRadius,    // e.g., 1..3   (new)
        IntProvider ceilingHeight,  // e.g., 6..32  (long!)
        int yMin,                   // 80
        int yMax,                   // 144 (exclusive)
        int floorTile,              // spacing (blue-noise) for floor, e.g., 3
        int ceilingTile             // spacing (blue-noise) for ceiling, e.g., 3–4
) implements FeatureConfiguration {
    public static final Codec<CobbleSpikesConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            IntProvider.CODEC.fieldOf("floor_height").forGetter(CobbleSpikesConfig::floorHeight),
            IntProvider.CODEC.fieldOf("floor_radius").forGetter(CobbleSpikesConfig::floorRadius),
            IntProvider.CODEC.fieldOf("ceiling_height").forGetter(CobbleSpikesConfig::ceilingHeight),
            Codec.INT.fieldOf("y_min").forGetter(CobbleSpikesConfig::yMin),
            Codec.INT.fieldOf("y_max").forGetter(CobbleSpikesConfig::yMax),
            Codec.INT.fieldOf("floor_tile").forGetter(CobbleSpikesConfig::floorTile),
            Codec.INT.fieldOf("ceiling_tile").forGetter(CobbleSpikesConfig::ceilingTile)
    ).apply(i, CobbleSpikesConfig::new));
}
