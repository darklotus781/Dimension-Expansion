package com.lithiumcraft.dimension_expansion.util.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public record TeleporterRules(
        ResourceKey<Level> targetDimension,
        boolean buildPlatform,
        BiFunction<ServerLevel, BlockPos, BlockPos> arrivalPositionFunction,
        @Nullable BiConsumer<ServerLevel, BlockPos> platformBuilder
) {
    public BlockPos resolveArrival(ServerLevel level, BlockPos basePos) {
        return arrivalPositionFunction.apply(level, basePos);
    }

    public void maybeBuildPlatform(ServerLevel level, BlockPos center, Block teleporterBlock) {
        if (!buildPlatform || platformBuilder == null) return;

        if (TeleportUtil.hasNearbyTeleporter(level, center, teleporterBlock, 48)) return;

        if (!TeleportUtil.isPlatformReady(level, center, teleporterBlock)) {
            platformBuilder.accept(level, center);
        }
    }
}
