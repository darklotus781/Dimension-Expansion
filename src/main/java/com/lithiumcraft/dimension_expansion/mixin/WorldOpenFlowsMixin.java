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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldOpenFlows.class)
public class WorldOpenFlowsMixin {
    @ModifyVariable(method = "confirmWorldCreation", at = @At("HEAD"), argsOnly = true)
    private static Lifecycle alwaysStable(Lifecycle cycle) {
        return Lifecycle.stable();
    }

    /** Reports world gen settings as stable, suppressing the experimental-datapack prompt. */
    @ModifyExpressionValue(
            method = "openWorldCheckWorldStemCompatibility",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/WorldData;worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;"
            )
    )
    private Lifecycle alwaysStableLifecycle(Lifecycle original) {
        return Lifecycle.stable();
    }
}
