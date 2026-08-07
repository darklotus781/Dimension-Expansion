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

import com.lithiumcraft.dimension_expansion.registry.ModDamageTypes;
import com.lithiumcraft.dimension_expansion.registry.ModEffects;
import com.lithiumcraft.dimension_expansion.registry.ModSounds;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;
import net.neoforged.bus.api.SubscribeEvent;
import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.WeakHashMap;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID)
public class DeepBeneathDarknessDamageHandler {

    private static final WeakHashMap<ServerPlayer, Integer> darkTimers = new WeakHashMap<>();
    private static final WeakHashMap<ServerPlayer, Integer> deathTimers = new WeakHashMap<>();
    private static final int DARKNESS_THRESHOLD_TICKS = 100; // ~5s before damage starts
    private static final int DEATH_THRESHOLD_TICKS = 1200;    // 60s before instant kill
    private static final int WARNING_START_TICKS = 600;      // start playing sounds after 30s
    private static final int DECAY_RATE = 5; // how quickly timer drops in light

    /**
     * Drop the effect and timers for anyone outside the Deep Beneath.
     * <p>
     * Keyed off the effect rather than the timers: stepping into light clears the timers straight
     * away, but the effect still has up to its full duration left, so a player leaving during that
     * window would carry it out of the dimension.
     */
    private static void clearElsewhere(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            darkTimers.remove(player);
            deathTimers.remove(player);
            if (player.hasEffect(ModEffects.NIGHTWALKER)) {
                player.removeEffect(ModEffects.NIGHTWALKER);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        // Only the Deep Beneath is dark. This does not depend on the player, so testing it per
        // player meant every dimension paid for it on every tick.
        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) {
            clearElsewhere(level);
            return;
        }

        for (ServerPlayer player : level.players()) {
            if (player.isCreative() || player.isSpectator()) continue;

            BlockPos pos = player.blockPosition();
            int blockLight = level.getBrightness(LightLayer.BLOCK, pos);

            int darkTimer = darkTimers.getOrDefault(player, 0);
            int deathTimer = deathTimers.getOrDefault(player, 0);

            if (blockLight == 0) {
                if (darkTimer  == 0) {
                    // First entry into darkness → warning message
                    player.displayClientMessage(
                            Component.translatable("message.dimension_expansion.nightwalker_warning"),
                            false
                    );
                }

                darkTimer ++;
                deathTimer++;

                // Once timer passes threshold, apply the effect
                if (darkTimer  >= DARKNESS_THRESHOLD_TICKS && !player.hasEffect(ModEffects.NIGHTWALKER)) {
                    // Effect itself will handle silent damage via setHealth()
                    player.addEffect(new MobEffectInstance(ModEffects.NIGHTWALKER, 200, 0, false, false, false));
                }

                // Play an ominous sound...
                if (deathTimer == WARNING_START_TICKS) {
                    level.playSound(
                            null,                                // null → play for all nearby players
                            player.blockPosition(),
                            ModSounds.DARKNESS_APPROACHES.get(), // your registered sound
                            player.getSoundSource(),
                            1.0F,                                // volume
                            1.0F                                 // pitch
                    );
                }

                if (deathTimer == WARNING_START_TICKS + 200) {
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 500, 0, false, false, false));
                }

                // Kill player if darkness exposure too long
                if (deathTimer >= DEATH_THRESHOLD_TICKS) {
                    Holder<DamageType> type = level.registryAccess()
                            .registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(ModDamageTypes.DARKNESS);

                    player.hurt(new DamageSource(type), Float.MAX_VALUE);
                }
            } else if (blockLight > 1) {
                // Fully reset both timers in the light
                darkTimer = 0;
                deathTimer = 0;
            } else {
                // If blockLight == 1 → just decay the damage timer slowly
                if (darkTimer > 0) darkTimer = Math.max(0, darkTimer - DECAY_RATE);
                // but do not reset deathTimer unless light > 1
            }

            if (darkTimer > 0) darkTimers.put(player, darkTimer);
            else darkTimers.remove(player);

            if (deathTimer > 0) deathTimers.put(player, deathTimer);
            else deathTimers.remove(player);
        }
    }

}
