package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BurnableTorchBlock extends TorchBlock {
    public BurnableTorchBlock(BlockBehaviour.Properties properties) {
        super(ParticleTypes.FLAME, properties.randomTicks().noCollission());
    }

    @Override
    public String getDescriptionId() {
        return "block.dimension_expansion.burnable_torch"; // safe static string
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
//        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        // 0.1% chance to extinguish
        if (random.nextFloat() < 0.10F) {
            BlockState newState = ModBlocks.BURNED_OUT_TORCH.get().defaultBlockState();
            level.setBlockAndUpdate(pos, newState);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 8; // Same as vanilla torch
    }
}
