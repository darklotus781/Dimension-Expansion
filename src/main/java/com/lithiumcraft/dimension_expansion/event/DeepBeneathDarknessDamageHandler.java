package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.effect.NightWalkerEffect;
import com.lithiumcraft.dimension_expansion.registry.ModEffects;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.LightLayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.Random;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID)
public class DeepBeneathDarknessDamageHandler {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        for (ServerPlayer player : level.players()) {
            if (player.isCreative() || player.isSpectator()) {
                return;
            }

            BlockPos pos = player.blockPosition();
            int blockLight = level.getBrightness(LightLayer.BLOCK, pos);

            Registry<MobEffect> registry = player.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT);
            ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, ModEffects.NIGHTWALKER.getId());
            Holder<MobEffect> nightWalker = registry.getHolderOrThrow(key);

            MobEffectInstance instance = player.getEffect(nightWalker);

            if (blockLight == 0) {
                // Only reapply if the effect is missing or about to expire (like beacon behavior)
                if (instance == null || instance.getDuration() <= 40) {
                    player.addEffect(new MobEffectInstance(nightWalker, 200, 0, false, true, false));

                    if (instance == null) {
                        player.displayClientMessage(Component.translatable("message.dimension_expansion.nightwalker_warning"), false);
                    }
                }
            } else {
                // If player is in light but still has long-duration effect, trim it down
                if (instance != null && instance.getDuration() > 40) {
                    player.addEffect(new MobEffectInstance(nightWalker, 30, 0, false, true, false));
                }
            }
        }
    }
}
