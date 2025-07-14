package com.lithiumcraft.dimension_expansion.util;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;

public class TorchReplacementHelper {

    public static BlockItem getReplacementTorch(UseOnContext context) {
        if (!context.getLevel().dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return null;

        Direction face = context.getClickedFace();
        if (face.getAxis().isHorizontal()) {
            return (BlockItem) ModBlocks.BURNABLE_WALL_TORCH.get().asItem();
        } else {
            return (BlockItem) ModBlocks.BURNABLE_TORCH.get().asItem();
        }
    }
}
