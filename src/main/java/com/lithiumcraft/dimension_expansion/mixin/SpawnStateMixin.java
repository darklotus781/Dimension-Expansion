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

package com.lithiumcraft.dimension_expansion.mixin;

import com.lithiumcraft.dimension_expansion.util.SpawnContextHolder;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin {
    // Vanilla uses a 17×17 chunk area for cap normalization
    private static final int AREA = 17 * 17; // = 289

    @Inject(
            method = "canSpawnForCategory(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/ChunkPos;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void doubleCap(MobCategory category, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (category != MobCategory.MONSTER) return;

        ServerLevel level = SpawnContextHolder.get();
        if (level == null || !level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        NaturalSpawner.SpawnState self = (NaturalSpawner.SpawnState)(Object)this;

        int current = self.getMobCategoryCounts().getInt(category);
        int spawnable = self.getSpawnableChunkCount();

        int baseCap = category.getMaxInstancesPerChunk() * spawnable / AREA;
        int doubledCap = Math.max(1, baseCap * 2);

        if (current < doubledCap) {
            cir.setReturnValue(true);
        }
    }
}
