package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.worldgen.spawn.MobSpawnRules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.Set;

public class DeepBeneathMobSpawnHandler {

    public static void register() {
        NeoForge.EVENT_BUS.addListener(MobSpawnRules::onSpawnPlacementCheck);
    }
}
