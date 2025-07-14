package com.lithiumcraft.dimension_expansion.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class AbstractTeleporterBlock extends BaseEntityBlock {

    public AbstractTeleporterBlock(BlockBehaviour.Properties props) {
        super(props);
    }

}
