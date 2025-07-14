package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class TorchPlacementHandler {

//    @SubscribeEvent // Cancel Torch placement completely!
//    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
//        Level level = event.getLevel();
//        if (!level.dimension().equals(DeepBeneathDimensions.DEEP_BENEATH)) return;
//
//        ItemStack stack = event.getItemStack();
//        if (!stack.is(Items.TORCH)) return;
//
//        // Cancel both client and server side to avoid predictive placement and ghost items
//        event.setCanceled(true);
//        event.setCancellationResult(InteractionResult.FAIL);
//
//        if (level.isClientSide) return; // Do nothing more on client
//
//        // Send message and sound only on server side
//        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
//            serverPlayer.displayClientMessage(Component.literal("Torches don't work in the Deep Beneath."), true);
//            level.playSound(null, serverPlayer.blockPosition(),
//                    SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 1.0F);
//        }
//    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.TORCH)) return;

        event.setCanceled(true); // prevent vanilla placement
        event.setCancellationResult(InteractionResult.SUCCESS); // avoid fail message

        // Handle only on server
        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;
        BlockPos clickedPos = event.getPos();
        Direction face = event.getHitVec().getDirection();
        BlockPos placePos = clickedPos.relative(face);

        BlockState placedState;
        if (face == Direction.UP) {
            placedState = ModBlocks.BURNABLE_TORCH.get().defaultBlockState();
        } else if (face.getAxis().isHorizontal()) {
            placedState = ModBlocks.BURNABLE_WALL_TORCH.get().defaultBlockState()
                    .setValue(WallTorchBlock.FACING, face);
        } else {
            // Invalid placement face
            return;
        }

        if (!serverLevel.getBlockState(placePos).canBeReplaced()) return;

        serverLevel.setBlock(placePos, placedState, 3);

         Player player = event.getEntity();
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        serverLevel.playSound(null, placePos,
                SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

}
