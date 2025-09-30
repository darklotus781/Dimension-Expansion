package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.worldgen.spawn.MobSpawnRules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.Set;

public class DeepBeneathMobSpawnHandler {

    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // Breeze
        event.register(
                EntityType.BREEZE,
                SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, // unused here
                (type, level, reason, pos, random) -> true,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        // Piglin Brute
        event.register(
                EntityType.PIGLIN_BRUTE,
                SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> true,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(DeepBeneathMobSpawnHandler::onRegisterSpawnPlacements);
        NeoForge.EVENT_BUS.addListener(MobSpawnRules::onSpawnPlacementCheck);
    }
}
