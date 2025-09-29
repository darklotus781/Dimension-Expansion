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
            // Silent damage
            float newHealth = Math.max(player.getHealth() - 1.0F, 1.0F);
            player.setHealth(newHealth);

            // Play custom sound (with subtitle)
            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    ModSounds.NIGHTWALKER_DAMAGE.get(),
                    player.getSoundSource(),
                    1.0F,
                    1.0F
            );

            // Flash the screen with a darkness-like flicker
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, true, false));

            // ⏱ Reschedule next damage
            int delay = RANDOM.nextInt(DAMAGE_INTERVAL_TICKS_MAX - DAMAGE_INTERVAL_TICKS_MIN + 1)
                    + DAMAGE_INTERVAL_TICKS_MIN;
            nextDamageTick.put(player, currentTick + delay);
        }

        return true;
    }
}
