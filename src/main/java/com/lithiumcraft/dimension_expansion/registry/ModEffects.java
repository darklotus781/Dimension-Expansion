package com.lithiumcraft.dimension_expansion.registry;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.effect.NightWalkerEffect;
import com.lithiumcraft.dimension_expansion.effect.GenericMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, DimensionExpansion.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> NIGHTWALKER_RESISTANCE = EFFECTS.register("nightwalker_resistance", () -> new GenericMobEffect(MobEffectCategory.BENEFICIAL, 0x660066));
    public static final DeferredHolder<MobEffect, MobEffect> NIGHTWALKER = EFFECTS.register("nightwalker", () -> new NightWalkerEffect(MobEffectCategory.HARMFUL, 0x660066));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }

}
