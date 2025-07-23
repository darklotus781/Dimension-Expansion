package com.lithiumcraft.dimension_expansion.mixin;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner.SpawnState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnState.class)
public class SpawnStateMixin {

    @Redirect(
            method = "canSpawnForCategory",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I"
            )
    )
    private int dimensionExpansion$modifyMobCap(MobCategory category) {
        // This method is injected at every getMaxInstancesPerChunk() call
        ServerLevel level = getServerLevelFromThread(); // See below workaround

        if (level != null && (level.dimension().equals(DimensionExpansionDimensions.UPSIDE_DOWN)
                || level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH))
                && category == MobCategory.MONSTER) {

            return category.getMaxInstancesPerChunk() * 2;
        }

        return category.getMaxInstancesPerChunk();
    }

    // ThreadLocal workaround to access ServerLevel if needed
    private ServerLevel getServerLevelFromThread() {
        // Use ThreadLocal or capture context earlier in the tick loop
        return null;
    }
}
