package com.lithiumcraft.dimension_expansion.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class StalagmiteFeature extends Feature<NoneFeatureConfiguration> {

    private static final BlockState[] VARIANTS = new BlockState[]{
            Blocks.STONE.defaultBlockState(),
            Blocks.COBBLESTONE.defaultBlockState(),
            Blocks.ANDESITE.defaultBlockState(),
            Blocks.INFESTED_STONE.defaultBlockState()
    };

    public StalagmiteFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        RandomSource random = ctx.random();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos().set(ctx.origin());

        // Find ground (move down until solid)
        while (pos.getY() > level.getMinBuildHeight() + 4 && level.getBlockState(pos).isAir()) {
            pos.move(Direction.DOWN);
        }
        if (!level.getBlockState(pos).isSolid()) return false;
        pos.move(Direction.UP); // move into air above ground

        int height = Mth.nextInt(random, 8, 25);
        int baseRadius = Mth.nextInt(random, 3, 8);
        boolean placed = false;

        // Build upward
        for (int y = 0; y < height; y++) {
            double taper = 1.0 - ((double) y / height);
            int radius = Math.max(1, (int) (baseRadius * taper + 0.5));

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= radius) {
                        BlockPos placePos = pos.offset(x, y, z);
                        if (level.getBlockState(placePos).canBeReplaced()) {
                            BlockState chosen = VARIANTS[random.nextInt(VARIANTS.length)];
                            level.setBlock(placePos, chosen, 2);
                            placed = true;
                        }
                    }
                }
            }
        }

        if (!placed) return false;

        // Decorative tip
        BlockPos tip = pos.offset(0, height, 0);
        if (level.getBlockState(tip).canBeReplaced())
            level.setBlock(tip, Blocks.COBBLESTONE_WALL.defaultBlockState(), 2);

        // Attach stairs/slabs to sides that have air next to solid
        decorateSides(level, pos, height, baseRadius, random);

        return true;
    }

    private void decorateSides(WorldGenLevel level, BlockPos base, int height, int radius, RandomSource random) {
        for (int i = 0; i < 40; i++) {
            int x = Mth.nextInt(random, -radius, radius);
            int y = Mth.nextInt(random, 1, height - 1);
            int z = Mth.nextInt(random, -radius, radius);
            BlockPos target = base.offset(x, y, z);

            if (!level.getBlockState(target).isSolid()) continue;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos side = target.relative(dir);
                if (level.getBlockState(side).isAir()) {
                    if (random.nextBoolean()) {
                        level.setBlock(side,
                                Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                                        .setValue(StairBlock.FACING, dir.getOpposite())
                                        .setValue(StairBlock.HALF, Half.BOTTOM),
                                2);
                    } else {
                        level.setBlock(side, Blocks.COBBLESTONE_SLAB.defaultBlockState(), 2);
                    }
                    break;
                }
            }
        }
    }
}
