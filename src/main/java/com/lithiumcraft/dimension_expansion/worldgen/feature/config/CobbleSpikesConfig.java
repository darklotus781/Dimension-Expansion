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
