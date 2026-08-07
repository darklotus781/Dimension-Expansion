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

package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.Config;
import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

import java.util.Set;

public class MobSpawnRules {

    // Extra creatures you want alongside monsters
    private static final Set<EntityType<?>> EXTRA_CREATURES = Set.of(
            EntityType.POLAR_BEAR
    );

    public static void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        ServerLevelAccessor level = event.getLevel();
        EntityType<?> type = event.getEntityType();
        BlockPos pos = event.getPos();

        // Only override in Deep Beneath
        if (!level.getLevel().dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
            return;
        }

        // Monsters OR whitelisted creatures
        if (type.getCategory() == MobCategory.MONSTER || EXTRA_CREATURES.contains(type)) {
            // Slime chunks do not apply here: every monster uses the same surface test below.
            // All others: ignore light, require only a valid surface
            if (level.getBlockState(pos.below()).isValidSpawn(level, pos.below(), type)) {
                logDebug("Deep Beneath spawn", type, pos, level);
                event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.SUCCEED);
            } else {
                event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
            }
        }
    }

    private static void logDebug(String prefix, EntityType<?> type, BlockPos pos, ServerLevelAccessor level) {
        if (Config.debugEnabled) {
            DimensionExpansion.LOGGER.debug("{}: {} at {} blockLight={} skyLight={}",
                    prefix, type.getDescriptionId(), pos,
                    level.getBrightness(LightLayer.BLOCK, pos),
                    level.getBrightness(LightLayer.SKY, pos));
        }
    }
}
