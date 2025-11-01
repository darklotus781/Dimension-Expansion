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
