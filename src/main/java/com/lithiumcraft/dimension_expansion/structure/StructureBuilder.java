package com.lithiumcraft.dimension_expansion.structure;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class StructureBuilder {

    public static void buildDeepBeneathPlatform(ServerLevel level, BlockPos center) {
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        BlockState torch = ModBlocks.BURNABLE_TORCH.get().defaultBlockState();
        BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();

        // Clear space: 5x5 area, 4 blocks high (Y to Y+3)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(target);
//                    if (state.is(BlockTags.OVERWORLD_CARVER_REPLACEABLES)) {
                        level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
//                    }
                }
            }
        }

        // Platform layer at Y
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                level.setBlockAndUpdate(pos, cobble);
            }
        }

        // Torch corners (Y + 1)
        level.setBlockAndUpdate(center.offset(-2, 1, -2), torch);
        level.setBlockAndUpdate(center.offset( 2, 1, -2), torch);
        level.setBlockAndUpdate(center.offset(-2, 1,  2), torch);
        level.setBlockAndUpdate(center.offset( 2, 1,  2), torch);

        // Teleporter block (Y + 1)
        level.setBlockAndUpdate(center.above(), teleporter);
    }

    public static void buildStoneBlockPlatform(ServerLevel level, BlockPos center) {
        BlockState cobble = Blocks.STONE.defaultBlockState();
        BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();

        // Clear space: 5x5 area, 4 blocks high (Y to Y+3)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(target);
//                    if (state.is(BlockTags.OVERWORLD_CARVER_REPLACEABLES)) {
                    level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
//                    }
                }
            }
        }

        // Platform layer at Y
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                level.setBlockAndUpdate(pos, cobble);
            }
        }

        // Teleporter block (Y + 1)
        level.setBlockAndUpdate(center.above(), teleporter);
    }

    public static void buildMiningPlatform(ServerLevel level, BlockPos center) {
        BlockPos teleporterPos = center; // use resolved surface block
        BlockState teleporter = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get().defaultBlockState();
        level.setBlockAndUpdate(teleporterPos, teleporter);
    }
}
