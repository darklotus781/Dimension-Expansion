// com.lithiumcraft.dimension_expansion.worldgen.feature.config.CobbleSpireConfig.java
package com.lithiumcraft.dimension_expansion.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record CobbleSpireConfig(
        IntProvider radius,  // e.g., 2..4
        int yMin,
        int yMax
) implements FeatureConfiguration {
    public static final Codec<CobbleSpireConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            IntProvider.CODEC.fieldOf("radius").forGetter(CobbleSpireConfig::radius),
            Codec.INT.fieldOf("y_min").forGetter(CobbleSpireConfig::yMin),
            Codec.INT.fieldOf("y_max").forGetter(CobbleSpireConfig::yMax)
    ).apply(i, CobbleSpireConfig::new));
}
