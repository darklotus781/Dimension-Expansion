package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.List;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DeepBeneathSpawnRules {

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                EntityType.GHAST,
                null,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Ghast::checkGhastSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        for (EntityType<? extends Monster> type : List.of(
                EntityType.CAVE_SPIDER,
                EntityType.PIGLIN_BRUTE,
                EntityType.VINDICATOR,
                EntityType.ILLUSIONER,
                EntityType.ZOMBIE_VILLAGER,
                EntityType.ZOMBIE,
                EntityType.SKELETON,
                EntityType.ENDERMAN
        )) {
            event.register(
                    type,
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );
        }
    }

}
