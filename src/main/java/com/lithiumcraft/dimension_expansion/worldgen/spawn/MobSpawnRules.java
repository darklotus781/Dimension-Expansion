package com.lithiumcraft.dimension_expansion.worldgen.spawn;

import com.lithiumcraft.dimension_expansion.Config;
import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.Set;

public class MobSpawnRules {

    // Match the same extra creatures you allow in MobSpawnRules
    private static final Set<EntityType<?>> EXTRA_CREATURES = Set.of(
//            EntityType.POLAR_BEAR,
//            EntityType.ZOMBIE_HORSE,
//            EntityType.SKELETON_HORSE
    );

    public static <T extends Mob> boolean checkCustomMonsterSpawnRules(
            EntityType<T> type,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;

        if (level instanceof ServerLevelAccessor serverLevel) {
            boolean isDeepBeneath = serverLevel.getLevel().dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH);

            // Monsters OR special creatures in the deep beneath
            if (isDeepBeneath && (type.getCategory() == MobCategory.MONSTER || EXTRA_CREATURES.contains(type))) {
                // Special-case: slimes ignore slime-chunk rules in the deep beneath
                if (type == EntityType.SLIME) {
                    return serverLevel.getBlockState(pos.below())
                            .isValidSpawn(serverLevel, pos.below(), type);
                }

                // Minimal restriction: must have a valid spawn surface below
                if (Config.debugEnabled) {
                    DimensionExpansion.LOGGER.debug("Spawn in DEEP_BENEATH: {} at {} light {}",
                            type.getDescriptionId(), pos, serverLevel.getBrightness(LightLayer.BLOCK, pos));
                }
                return serverLevel.getBlockState(pos.below()).isValidSpawn(serverLevel, pos.below(), type);
            }

            // Fallback: vanilla behavior
            if (Config.debugEnabled) {
                int blockLight = serverLevel.getBrightness(LightLayer.BLOCK, pos);
                DimensionExpansion.LOGGER.debug("Vanilla spawn: {} at {} blockLight={} skyLight={} type={}",
                        type.getDescriptionId(), pos, blockLight,
                        serverLevel.getBrightness(LightLayer.SKY, pos), spawnType);
            }
            return Mob.checkMobSpawnRules(type, serverLevel, spawnType, pos, random);
        }

        return false;
    }
}
