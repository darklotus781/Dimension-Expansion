package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.worldgen.spawn.MonsterSpawnRules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.List;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DimensionExpansionMobSpawnRules {

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        for (EntityType<?> rawType : BuiltInRegistries.ENTITY_TYPE) {
            if (rawType.getCategory() != MobCategory.MONSTER) continue;

            @SuppressWarnings("unchecked")
            EntityType<Mob> mobType = (EntityType<Mob>) rawType;

            if (rawType == EntityType.SLIME) continue;

            if (rawType == EntityType.GHAST) {
                event.register(
                        mobType,
                        SpawnPlacementTypes.ON_GROUND, // avoid NO_RESTRICTIONS — fails in overworld
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        MonsterSpawnRules::checkCustomMonsterSpawnRules,
                        RegisterSpawnPlacementsEvent.Operation.REPLACE
                );
                continue;
            }

            // Special-case WITHER_SKELETON
            if (rawType == EntityType.WITHER_SKELETON) {
                event.register(
                        mobType,
                        SpawnPlacementTypes.ON_GROUND, // use same as overworld mobs
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        MonsterSpawnRules::checkCustomMonsterSpawnRules,
                        RegisterSpawnPlacementsEvent.Operation.REPLACE
                );
                continue;
            }

            if (rawType.equals(EntityType.GUARDIAN)) {
                event.register(
                        mobType,
                        SpawnPlacementTypes.IN_WATER,
                        Heightmap.Types.OCEAN_FLOOR,
                        (type, level, reason, pos, random) ->
                                level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE
                );
                continue;
            }
            event.register(
                    mobType,
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MonsterSpawnRules::checkCustomMonsterSpawnRules,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );

        }
    }

//    @SubscribeEvent
//    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
//
//        event.register(
//                EntityType.GHAST,
//                SpawnPlacementTypes.NO_RESTRICTIONS,
//                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                Ghast::checkGhastSpawnRules,
//                RegisterSpawnPlacementsEvent.Operation.REPLACE
//        );
//
//        event.register(
//                EntityType.BREEZE,
//                SpawnPlacementTypes.NO_RESTRICTIONS,
//                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                Breeze::checkMonsterSpawnRules,
//                RegisterSpawnPlacementsEvent.Operation.REPLACE
//        );
//
//        event.register(
//                EntityType.PIGLIN_BRUTE,
//                SpawnPlacementTypes.NO_RESTRICTIONS,
//                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                PiglinBrute::checkMonsterSpawnRules,
//                RegisterSpawnPlacementsEvent.Operation.REPLACE
//        );
//
//        for (EntityType<? extends Monster> type : List.of(
//                EntityType.CAVE_SPIDER,
//                EntityType.VINDICATOR,
//                EntityType.ILLUSIONER,
//                EntityType.ZOMBIE_VILLAGER,
//                EntityType.ZOMBIE,
//                EntityType.SKELETON,
//                EntityType.ENDERMAN
//        )) {
//            event.register(
//                    type,
//                    SpawnPlacementTypes.ON_GROUND,
//                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
//                    Monster::checkMonsterSpawnRules,
//                    RegisterSpawnPlacementsEvent.Operation.REPLACE
//            );
//        }
//    }

}
