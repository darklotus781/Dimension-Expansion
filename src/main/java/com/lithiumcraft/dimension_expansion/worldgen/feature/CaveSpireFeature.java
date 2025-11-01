/*
 * Dimension Expansion
 * Copyright (c) 2025 DarkLotus (DarkLotus781) / LithiumCraft
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lithiumcraft.dimension_expansion.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import javax.annotation.Nullable;
import java.util.List;

public class CaveSpireFeature extends Feature<NoneFeatureConfiguration> {

    private static final List<BlockState> BASE_BLOCKS = List.of(
            Blocks.STONE.defaultBlockState(),
            Blocks.COBBLESTONE.defaultBlockState(),
            Blocks.ANDESITE.defaultBlockState(),
            Blocks.INFESTED_STONE.defaultBlockState(),
            Blocks.MOSSY_COBBLESTONE.defaultBlockState()
    );

    public CaveSpireFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        LevelAccessor level = ctx.level();
        RandomSource random = ctx.random();
        BlockPos origin = ctx.origin();
        boolean downward = random.nextBoolean();

        BlockPos anchor = findValidAnchor(level, origin, downward);
        if (anchor == null) return false;

        // Start one block *inside* the solid surface
        final BlockPos start = downward ? anchor : anchor;

        int height = 8 + random.nextInt(14);
        int baseRadius = 6 + random.nextInt(4);
        final int yMin = level.getMinBuildHeight() + 2;
        final int yMax = level.getMaxBuildHeight() - 2;

        // Abort if there’s already solid material both above and below (inside another spire)
        if (!level.isEmptyBlock(anchor.above()) && !level.isEmptyBlock(anchor.below())) {
            return false;
        }

        // --- allow minor overlap so tips can nearly touch ---
        int maxSolidCount = 2;  // tolerate up to 2 solid hits before aborting
        int solidHits = 0;
        int checkRange = height + 3;

        for (int dy = 0; dy < checkRange; dy++) {
            BlockPos check = downward ? start.below(dy) : start.above(dy);
            BlockState checkState = level.getBlockState(check);

            if (!checkState.isAir() && !checkState.canBeReplaced()) {
                solidHits++;
                if (solidHits > maxSolidCount) {
                    return false; // too much blockage → skip this spire
                }
            }
        }

        // clamp placement height range to within world build limits
        int maxSafeHeight = downward ? Math.min(anchor.getY(), level.getMaxBuildHeight() - 5)
                : Math.max(anchor.getY(), level.getMinBuildHeight() + 5);

        // limit spire growth if we’re near bottom or top
        if (downward && anchor.getY() - height < level.getMinBuildHeight() + 5)
            height = anchor.getY() - (level.getMinBuildHeight() + 5);
        if (!downward && anchor.getY() + height > level.getMaxBuildHeight() - 5)
            height = (level.getMaxBuildHeight() - 5) - anchor.getY();

        // skip if no space
        if (height <= 0) return false;
        if (downward && anchor.getY() < 40) return false;
        if (!downward && anchor.getY() > 280) return false;

        // --- Spire placement ---
        for (int i = 0; i < height; i++) {
            int dy = downward ? -i : i;
            BlockPos layerCenter = start.offset(0, dy, 0);
            if (i == 0 && !downward) {
                // For stalagmites, embed base one block into floor
                layerCenter = layerCenter.below();
            }
            if (i == 0 && downward) {
                // For stalactites, embed tip one block into ceiling
                layerCenter = layerCenter.above();
            }

            if (layerCenter.getY() <= yMin || layerCenter.getY() >= yMax)
                break;

            if (!isReplaceable(level.getBlockState(layerCenter)))
                break;

            int radius = Math.max(1, (int) Math.round(baseRadius * (1.0 - (double) i / height)));

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius)
                        continue;

                    BlockPos target = layerCenter.offset(dx, 0, dz);
                    if (isReplaceable(level.getBlockState(target))) {
                        level.setBlock(target, chooseBlock(random), 2);
                    }
                }
            }
        }
        return true;
    }

    private static boolean isReplaceable(BlockState state) {
        // Allow replacing air, fluids, and any replaceable materials
        return state.isAir() || state.canBeReplaced();
    }

    private BlockState chooseBlock(RandomSource random) {
        int r = random.nextInt(100);

        if (r < 55) return Blocks.STONE.defaultBlockState();              // 55% stone
        if (r < 75) return Blocks.ANDESITE.defaultBlockState();           // 20% andesite
        if (r < 90) return Blocks.COBBLESTONE.defaultBlockState();        // 15% cobblestone
        if (r < 97) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();  // 7% mossy cobble
        return Blocks.INFESTED_STONE.defaultBlockState();                 // 3% infested stone (rare)
    }

    /**
     * Checks for a 3×3 patch of mostly-solid blocks centered one block below (for floor)
     * or one block above (for ceiling). This allows placement embedded into the surface.
     */
    private boolean isSolidPatch(LevelAccessor level, BlockPos center, boolean downward) {
        int solidCount = 0;
        BlockPos base = downward ? center.above() : center.below();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos check = base.offset(dx, 0, dz);
                BlockState state = level.getBlockState(check);
                if (state.isSolid() && !isSpireBlock(state)) {
                    solidCount++;
                }
            }
        }

        if (solidCount < 7) return false; // must be mostly solid

        // Require open air immediately where the spire starts
        BlockPos airCheck = downward ? center.below() : center.above();
        return level.getBlockState(airCheck).isAir();
    }

    /**
     * Finds a nearby valid ceiling/floor anchor within ~48 blocks vertically.
     * Anchor is returned on the surface face where the spire should start embedding.
     */
    @Nullable
    private BlockPos findValidAnchor(LevelAccessor level, BlockPos origin, boolean downward) {
        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
        int searchRadius = 8;
        int minY = level.getMinBuildHeight() + 8;
        int maxY = level.getMaxBuildHeight() - 8;
        int yStart = Math.max(minY, origin.getY() - 48);
        int yEnd = Math.min(maxY, origin.getY() + 48);

        for (int tries = 0; tries < 32; tries++) {
            int dx = origin.getX() + level.getRandom().nextIntBetweenInclusive(-searchRadius, searchRadius);
            int dz = origin.getZ() + level.getRandom().nextIntBetweenInclusive(-searchRadius, searchRadius);

            if (downward) {
                // ceiling: solid above patch, air below
                for (int y = yEnd; y >= yStart; y--) {
                    check.set(dx, y, dz);
                    if (isSolidPatch(level, check, true)) {
                        return check.immutable();
                    }
                }
            } else {
                // floor: solid below patch, air above
                for (int y = yStart; y <= yEnd; y++) {
                    check.set(dx, y, dz);
                    if (isSolidPatch(level, check, false)) {
                        return check.immutable();
                    }
                }
            }
        }
        return null;
    }



    /** Checks if the block is one of our typical spire materials. */
    private static boolean isSpireBlock(BlockState state) {
        return state.is(Blocks.STONE) ||
                state.is(Blocks.COBBLESTONE) ||
                state.is(Blocks.ANDESITE) ||
                state.is(Blocks.MOSSY_COBBLESTONE) ||
                state.is(Blocks.INFESTED_STONE);
    }

}
