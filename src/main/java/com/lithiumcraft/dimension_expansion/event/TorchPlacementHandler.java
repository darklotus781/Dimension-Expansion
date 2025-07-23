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

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)
                && !level.dimension().equals(DimensionExpansionDimensions.UPSIDE_DOWN)) return;

        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.TORCH)) return;

        event.setCanceled(true); // prevent vanilla placement
        event.setCancellationResult(InteractionResult.SUCCESS); // avoid fail message

        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;
        BlockPos clickedPos = event.getPos();
        Direction face = event.getHitVec().getDirection();

        BlockPos targetPos = clickedPos;
        BlockState targetState = serverLevel.getBlockState(targetPos);

        // If the clicked block is replaceable (like grass), place *into* it
        boolean replaceClicked = targetState.canBeReplaced();
        BlockPos placePos = replaceClicked ? targetPos : clickedPos.relative(face);

        BlockState placedState;
        if (face == Direction.UP || replaceClicked) {
            placedState = ModBlocks.BURNABLE_TORCH.get().defaultBlockState();
        } else if (face.getAxis().isHorizontal()) {
            BlockState tentativeWall = ModBlocks.BURNABLE_WALL_TORCH.get().defaultBlockState()
                    .setValue(WallTorchBlock.FACING, face);

            if (tentativeWall.canSurvive(serverLevel, placePos)) {
                placedState = tentativeWall;
            } else {
                // Fall back: place standing torch on top of the block adjacent to the clicked face
                BlockPos fallbackPos = clickedPos.relative(face);
                BlockState fallbackState = ModBlocks.BURNABLE_TORCH.get().defaultBlockState();

                if (!serverLevel.getBlockState(fallbackPos).canBeReplaced()) return;
                if (!fallbackState.canSurvive(serverLevel, fallbackPos)) return;

                placePos = fallbackPos;
                placedState = fallbackState;
            }
        } else {
            return; // Can't place on bottom
        }

        if (!serverLevel.getBlockState(placePos).canBeReplaced()) return;
        if (!placedState.canSurvive(serverLevel, placePos)) return;

        serverLevel.setBlock(placePos, placedState, 3);

        Player player = event.getEntity();
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        serverLevel.playSound(null, placePos,
                SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

}
