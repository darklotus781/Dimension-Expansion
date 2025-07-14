package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import com.lithiumcraft.dimension_expansion.registry.ModDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.Random;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DeepBeneathDarknessDamageHandler {

    private static final int DAMAGE_INTERVAL_TICKS_MIN = 100;
    private static final int DAMAGE_INTERVAL_TICKS_MAX = 400;
    private static final Random RANDOM = new Random();

    // Track each player's next scheduled tick for damage
    private static final Map<ServerPlayer, Integer> playerNextDamageTick = new WeakHashMap<>();

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        for (ServerPlayer player : level.players()) {
            int currentTick = player.tickCount;

            // Get or set the next eligible tick
            int nextTick = playerNextDamageTick.getOrDefault(player, -1);
            if (nextTick >= 0 && currentTick < nextTick) continue;

            BlockPos pos = player.blockPosition();
            int blockLight = level.getBrightness(LightLayer.BLOCK, pos);

            if (blockLight == 0) {
                // Deal damage
                Holder<DamageType> darknessHolder = level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ModDamageTypes.DARKNESS);

                DamageSource source = new DamageSource(darknessHolder);
                player.hurt(source, 1.0F); // ½ heart

                // Schedule next damage time randomly between min and max
                int delay = RANDOM.nextInt(DAMAGE_INTERVAL_TICKS_MAX - DAMAGE_INTERVAL_TICKS_MIN + 1) + DAMAGE_INTERVAL_TICKS_MIN;
                playerNextDamageTick.put(player, currentTick + delay);
            } else {
                // If player returns to light, push damage further away
                playerNextDamageTick.put(player, currentTick + DAMAGE_INTERVAL_TICKS_MIN);
            }
        }
    }
}
