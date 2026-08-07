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
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A tapering stone spike, grown either down from a ceiling or up from a floor.
 * <p>
 * Stalactites and stalagmites were two classes of 120 lines that differed in twenty, and every one
 * of those differences was a direction flip -- which way to search for rock, which way to stack the
 * layers, and whether the side decoration sits at the top or bottom of its block. They are the same
 * feature, so this is one class with a growth direction and the two are registered against it.
 */
public class StoneSpikeFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState[] VARIANTS = new BlockState[]{
            Blocks.STONE.defaultBlockState(),
            Blocks.COBBLESTONE.defaultBlockState(),
            Blocks.ANDESITE.defaultBlockState(),
            Blocks.INFESTED_STONE.defaultBlockState()
    };

    /** {@link Direction#DOWN} for a stalactite, {@link Direction#UP} for a stalagmite. */
    private final Direction growth;

    public StoneSpikeFeature(Codec<NoneFeatureConfiguration> codec, Direction growth) {
        super(codec);
        this.growth = growth;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        RandomSource random = ctx.random();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos().set(ctx.origin());

        // Walk against the growth direction to find the rock this hangs from or stands on.
        Direction search = growth.getOpposite();
        int limit = growth == Direction.DOWN ? level.getMaxBuildHeight() - 4 : level.getMinBuildHeight() + 4;
        while (beyond(pos.getY(), limit) && level.getBlockState(pos).isAir()) {
            pos.move(search);
        }
        if (!level.getBlockState(pos).isSolid()) return false;
        pos.move(growth); // back into the air

        int height = Mth.nextInt(random, 8, 25);
        int baseRadius = Mth.nextInt(random, 3, 8);
        int step = growth.getStepY();
        boolean placed = false;

        for (int y = 0; y < height; y++) {
            double taper = 1.0 - ((double) y / height);
            int radius = Math.max(1, (int) (baseRadius * taper + 0.5));

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.sqrt(x * x + z * z) > radius) continue;
                    BlockPos placePos = pos.offset(x, y * step, z);
                    if (level.getBlockState(placePos).canBeReplaced()) {
                        level.setBlock(placePos, VARIANTS[random.nextInt(VARIANTS.length)], 2);
                        placed = true;
                    }
                }
            }
        }

        if (!placed) return false;

        BlockPos tip = pos.offset(0, height * step, 0);
        if (level.getBlockState(tip).canBeReplaced()) {
            level.setBlock(tip, Blocks.COBBLESTONE_WALL.defaultBlockState(), 2);
        }

        decorateSides(level, pos, height, baseRadius, random, step);
        return true;
    }

    /** Whether the search has room left to keep going in its direction. */
    private boolean beyond(int y, int limit) {
        return growth == Direction.DOWN ? y < limit : y > limit;
    }

    private void decorateSides(WorldGenLevel level, BlockPos base, int height, int radius,
                               RandomSource random, int step) {
        // A stalactite's trim hangs from the top of its block, a stalagmite's sits on the bottom.
        Half half = growth == Direction.DOWN ? Half.TOP : Half.BOTTOM;

        for (int i = 0; i < 40; i++) {
            int x = Mth.nextInt(random, -radius, radius);
            int y = Mth.nextInt(random, 1, height - 1) * step;
            int z = Mth.nextInt(random, -radius, radius);
            BlockPos target = base.offset(x, y, z);

            if (!level.getBlockState(target).isSolid()) continue;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos side = target.relative(dir);
                if (!level.getBlockState(side).isAir()) continue;

                if (random.nextBoolean()) {
                    level.setBlock(side,
                            Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                                    .setValue(StairBlock.FACING, dir.getOpposite())
                                    .setValue(StairBlock.HALF, half),
                            2);
                } else {
                    level.setBlock(side, Blocks.COBBLESTONE_SLAB.defaultBlockState(), 2);
                }
                break;
            }
        }
    }
}
