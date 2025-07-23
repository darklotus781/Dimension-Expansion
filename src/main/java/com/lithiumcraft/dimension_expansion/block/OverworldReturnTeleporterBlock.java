package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.blockentity.ModBlockEntities;
import com.lithiumcraft.dimension_expansion.blockentity.TeleporterBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class OverworldReturnTeleporterBlock extends AbstractTeleporterBlock {
    public OverworldReturnTeleporterBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK).lightLevel(s -> 15));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeleporterBlockEntity(ModBlockEntities.OVERWORLD_RETURN_TELEPORTER.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide && type == ModBlockEntities.OVERWORLD_RETURN_TELEPORTER.get()) {
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
