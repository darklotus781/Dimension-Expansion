package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import com.lithiumcraft.dimension_expansion.block.property.WoodType;



public class UpsideDownPortalFrameBlock extends Block {

    public static final EnumProperty<WoodType> WOOD_TYPE = EnumProperty.create("wood", WoodType.class);

    public UpsideDownPortalFrameBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.WOOD)
                .strength(2.0F) // oak log strength
                .requiresCorrectToolForDrops()
        );

        this.registerDefaultState(this.stateDefinition.any().setValue(WOOD_TYPE, WoodType.OAK));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WOOD_TYPE);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            Direction direction = Direction.getRandom(random);
            if (direction != Direction.UP) {
                BlockPos blockpos = pos.relative(direction);
                BlockState blockstate = level.getBlockState(blockpos);
                if (!blockState.canOcclude() || !blockstate.isFaceSturdy(level, blockpos, direction.getOpposite())) {
                    double d0 = direction.getStepX() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getStepX() * 0.6;
                    double d1 = direction.getStepY() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getStepY() * 0.6;
                    double d2 = direction.getStepZ() == 0 ? random.nextDouble() : (double)0.5F + (double)direction.getStepZ() * 0.6;
                    level.addParticle(ModParticles.UPSIDE_DOWN_PORTAL_FRAME_HANG_PARTICLE.get(), (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, (double)0.0F, (double)0.0F, (double)0.0F);
                }
            }
        }
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }
}