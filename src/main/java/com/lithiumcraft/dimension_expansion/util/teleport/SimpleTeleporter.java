package com.lithiumcraft.dimension_expansion.util.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class SimpleTeleporter {
    private final ResourceKey<Level> targetDimension;
    private final int targetY;

    public SimpleTeleporter(ResourceKey<Level> targetDimension, int targetY) {
        this.targetDimension = targetDimension;
        this.targetY = targetY;
    }

    public ResourceKey<Level> getTargetDimension() {
        return targetDimension;
    }

    public BlockPos getDestination(BlockPos origin) {
        return new BlockPos(origin.getX(), targetY, origin.getZ());
    }
}
