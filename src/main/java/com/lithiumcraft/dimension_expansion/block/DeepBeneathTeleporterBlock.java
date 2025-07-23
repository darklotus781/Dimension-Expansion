package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.blockentity.ModBlockEntities;
import com.lithiumcraft.dimension_expansion.blockentity.TeleporterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class DeepBeneathTeleporterBlock extends AbstractTeleporterBlock {
    public DeepBeneathTeleporterBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).lightLevel(s -> 15).requiresCorrectToolForDrops());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeleporterBlockEntity(ModBlockEntities.DEEP_BENEATH_TELEPORTER.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide && type == ModBlockEntities.DEEP_BENEATH_TELEPORTER.get()) {
            return (lvl, pos, blockState, be) -> {
                if (be instanceof TeleporterBlockEntity teleporter) {
                    TeleporterBlockEntity.tickServer(lvl, pos, blockState, teleporter);
                }
            };
        }
        return null;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
