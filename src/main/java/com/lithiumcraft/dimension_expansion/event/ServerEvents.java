package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.blockentity.TeleporterBlockEntity;
import com.lithiumcraft.dimension_expansion.worldgen.BorderGenerationData;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer) || level.isClientSide) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        ResourceLocation tagId = ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "teleporter_blocks");
        TagKey<Block> teleportTag = BlockTags.create(tagId);

        // Return if not a teleporter block.
        if (!state.is(teleportTag)) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TeleporterBlockEntity teleporter) {
            teleporter.teleport(serverPlayer);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

//    @SubscribeEvent
//    public static void onLevelLoad(LevelEvent.Load event) {
//        if (!(event.getLevel() instanceof ServerLevel level)) return;
//
//        if (!level.dimension().equals(DimensionExpansionDimensions.STONE_BLOCK)) return;
//
//        BorderGenerationData data = level.getDataStorage().computeIfAbsent(
//                BorderGenerationData.factory(),
//                BorderGenerationData.ID
//        );
//
//        if (!data.borderGenerated) {
//            System.out.println("Generating bedrock border wall for STONE_BLOCK...");
//            generateBedrockWorldBorder(level, 250);
//            data.borderGenerated = true;
//            data.setDirty();
//        }
//    }


//    public static void generateBedrockWorldBorder(ServerLevel level, int radius) {
//        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
//
//        int min = -radius;
//        int max = radius;
//
//        for (int x = min; x <= max; x++) {
//            for (int z = min; z <= max; z++) {
//                boolean isEdge = x == min || x == max || z == min || z == max;
//                if (!isEdge) continue;
//
//                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
//                    pos.set(x, y, z);
//                    level.setBlockAndUpdate(pos, Blocks.BEDROCK.defaultBlockState());
//                }
//            }
//        }
//    }




//    @SubscribeEvent
//    public static void onLevelTick(LevelTickEvent.Post event) {
//        if (!(event.getLevel() instanceof ServerLevel level)) return;
//
//        if (!level.dimension().equals(DeepBeneathDimensions.DEEP_BENEATH)) return;
//
//        // Iterate over players and surrounding torches
//        for (ServerPlayer player : level.players()) {
//            BlockPos playerPos = player.blockPosition();
//
//            BlockPos.betweenClosed(playerPos.offset(-8, -4, -8), playerPos.offset(8, 4, 8))
//                    .forEach(pos -> {
//                        BlockState state = level.getBlockState(pos);
//
//                        if (state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)) {
//                            if (level.random.nextFloat() < 0.001F) { // 0.1% chance per tick per torch
//                                BlockState newState = (state.is(Blocks.TORCH))
//                                        ? ModBlocks.BURNED_OUT_TORCH.get().defaultBlockState()
//                                        : ModBlocks.BURNED_OUT_WALL_TORCH.get().defaultBlockState()
//                                        .setValue(WallTorchBlock.FACING, state.getValue(WallTorchBlock.FACING));
//
//                                level.setBlock(pos, newState, 3);
//                                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.0f);
//                            }
//                        }
//                    });
//        }
//    }
}

