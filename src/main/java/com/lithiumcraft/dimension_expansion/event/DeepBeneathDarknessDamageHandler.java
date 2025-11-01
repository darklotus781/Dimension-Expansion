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

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.registry.ModDamageTypes;
import com.lithiumcraft.dimension_expansion.registry.ModEffects;
import com.lithiumcraft.dimension_expansion.registry.ModSounds;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;
import net.neoforged.bus.api.SubscribeEvent;
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

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        for (ServerPlayer player : level.players()) {
            if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) {
                player.removeEffect(ModEffects.NIGHTWALKER);
                darkTimers.remove(player);
                deathTimers.remove(player);
                continue;
            }

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
                if (darkTimer  >= DARKNESS_THRESHOLD_TICKS) {
                    Registry<MobEffect> registry = level.registryAccess().registryOrThrow(Registries.MOB_EFFECT);
                    ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, ModEffects.NIGHTWALKER.getId());
                    Holder<MobEffect> nightWalker = registry.getHolderOrThrow(key);

                    if (!player.hasEffect(nightWalker)) {
                        // Effect itself will handle silent damage via setHealth()
                        player.addEffect(new MobEffectInstance(nightWalker, 200, 0, false, false, false));
                    }
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

    // OLD NIGHTWALKER EFFECT
//    @SubscribeEvent
//    public static void onLevelTick(LevelTickEvent.Post event) {
//        if (!(event.getLevel() instanceof ServerLevel level)) return;
//        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;
//
//        for (ServerPlayer player : level.players()) {
//            if (player.isCreative() || player.isSpectator()) {
//                return;
//            }
//
//            BlockPos pos = player.blockPosition();
//            int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
//
//            Registry<MobEffect> registry = player.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT);
//            ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, ModEffects.NIGHTWALKER.getId());
//            Holder<MobEffect> nightWalker = registry.getHolderOrThrow(key);
//
//            MobEffectInstance instance = player.getEffect(nightWalker);
//
//            if (blockLight == 0) {
//                // Only reapply if the effect is missing or about to expire (like beacon behavior)
//                if (instance == null || instance.getDuration() <= 40) {
//                    player.addEffect(new MobEffectInstance(nightWalker, 200, 0, false, true, false));
//
//                    if (instance == null) {
//                        player.displayClientMessage(Component.translatable("message.dimension_expansion.nightwalker_warning"), false);
//                    }
//                }
//            } else {
//                // If player is in light but still has long-duration effect, trim it down
//                if (instance != null && instance.getDuration() > 40) {
//                    player.addEffect(new MobEffectInstance(nightWalker, 30, 0, false, true, false));
//                }
//            }
//        }
//    }
}
