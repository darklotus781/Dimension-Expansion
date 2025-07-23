package com.lithiumcraft.dimension_expansion.worldgen.spawn;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

public class MonsterSpawnRules {

    public static <T extends Mob> boolean checkCustomMonsterSpawnRules(EntityType<T> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;

        if (level instanceof ServerLevelAccessor serverLevel) {
            boolean isCustomDim = serverLevel.getLevel().dimension().equals(DimensionExpansionDimensions.UPSIDE_DOWN)
                    || serverLevel.getLevel().dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH);

            if (isCustomDim && type.getCategory() == MobCategory.MONSTER) {
                // Use the vanilla helper that skips light checks for monsters
                return Monster.checkAnyLightMonsterSpawnRules(
                        (EntityType<? extends Monster>) type,
                        serverLevel,
                        spawnType,
                        pos,
                        random
                );
            }

            // Fallback to vanilla
            return Mob.checkMobSpawnRules(type, serverLevel, spawnType, pos, random);
        }

        return false;
    }
}
