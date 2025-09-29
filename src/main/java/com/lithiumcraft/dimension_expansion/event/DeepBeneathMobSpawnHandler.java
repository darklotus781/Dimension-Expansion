package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.worldgen.spawn.MobSpawnRules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.Set;

public class DeepBeneathMobSpawnHandler {

    // Creature types you want to allow alongside monsters
    private static final Set<EntityType<?>> EXTRA_CREATURES = Set.of(
//            EntityType.POLAR_BEAR,
//            EntityType.ZOMBIE_HORSE,
//            EntityType.SKELETON_HORSE
    );

    public static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        for (EntityType<?> rawType : BuiltInRegistries.ENTITY_TYPE) {
            if (!(rawType instanceof EntityType<?> mobType)) continue;
            if (!(rawType.getCategory() == MobCategory.MONSTER || EXTRA_CREATURES.contains(rawType))) {
                continue;
            }

            // Skip slimes here → keep vanilla rules
            if (rawType == EntityType.SLIME) continue;

            @SuppressWarnings("unchecked")
            EntityType<Mob> cast = (EntityType<Mob>) mobType;

            // Get vanilla placement settings
            var placement = net.minecraft.world.entity.SpawnPlacements.getPlacementType(cast);
            var heightmap = net.minecraft.world.entity.SpawnPlacements.getHeightmapType(cast);

            // Fall back if not defined (rare, but safe)
            if (placement == null) placement = net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND;
            if (heightmap == null) heightmap = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

            event.register(
                    cast,
                    placement,
                    heightmap,
                    MobSpawnRules::checkCustomMonsterSpawnRules,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );
        }
    }


    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DeepBeneathMobSpawnHandler::onRegisterSpawnPlacements);
    }
}
