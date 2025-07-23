package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BurnableWallTorchBlock extends WallTorchBlock {

    public BurnableWallTorchBlock(BlockBehaviour.Properties properties) {
        super(ParticleTypes.FLAME, properties.randomTicks().noCollission());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));

    }

    @Override
    public String getDescriptionId() {
        return "block.dimension_expansion.burnable_wall_torch"; // safe static string
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
//        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        if (random.nextFloat() < 0.10F) {
            BlockState newState = ModBlocks.BURNED_OUT_WALL_TORCH.get()
                    .defaultBlockState()
                    .setValue(FACING, state.getValue(FACING));
            level.setBlockAndUpdate(pos, newState);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 8;
    }
}
