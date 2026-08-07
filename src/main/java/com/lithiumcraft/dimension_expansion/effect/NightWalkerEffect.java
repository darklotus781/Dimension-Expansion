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

package com.lithiumcraft.dimension_expansion.effect;

import com.lithiumcraft.dimension_expansion.registry.ModEffects;
import com.lithiumcraft.dimension_expansion.registry.ModSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Random;
import java.util.WeakHashMap;

public class NightWalkerEffect extends MobEffect {
    private static final int DAMAGE_INTERVAL_TICKS_MIN = 100;
    private static final int DAMAGE_INTERVAL_TICKS_MAX = 400;
    private static final Random RANDOM = new Random();
    private static final WeakHashMap<Player, Integer> nextDamageTick = new WeakHashMap<>();

    public NightWalkerEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)
                || entity.level().isClientSide()
                || player.isCreative()
                || player.isSpectator()) {
            return false;
        }

        // Resistance check
        var mobEffectRegistry = player.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT);
        var resistanceKey = ResourceKey.create(Registries.MOB_EFFECT, ModEffects.NIGHTWALKER_RESISTANCE.getId());
        Holder<MobEffect> resistanceHolder = mobEffectRegistry.getHolderOrThrow(resistanceKey);
        if (player.hasEffect(resistanceHolder) || player.getHealth() <= 1.0F) {
            nextDamageTick.remove(player);
            return false;
        }

        int currentTick = player.tickCount;
        int next = nextDamageTick.getOrDefault(player, -1);

        if (next == -1 || currentTick >= next) {
            // Silent damage only once per scheduled interval
            float current = player.getHealth();
            if (current > 1.0F) {
                player.setHealth(current - 1.0F);
            }

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.NIGHTWALKER_DAMAGE.get(),
                    player.getSoundSource(),
                    1.0F,
                    1.0F);

            if (!player.hasEffect(MobEffects.DARKNESS)) {
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 50, 0, false, false, false));
            }

            // Reschedule
            int delay = RANDOM.nextInt(DAMAGE_INTERVAL_TICKS_MAX - DAMAGE_INTERVAL_TICKS_MIN + 1)
                    + DAMAGE_INTERVAL_TICKS_MIN;
            nextDamageTick.put(player, currentTick + delay);
        }

        return true;
    }
}
