package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.BurnedOutTorchBlock;
import com.lithiumcraft.dimension_expansion.block.BurnedOutWallTorchBlock;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class RelightHandler {

    @SubscribeEvent
    public static void onUseFlintAndSteel(UseItemOnBlockEvent event) {
        UseOnContext context = event.getUseOnContext();
        Level level = event.getLevel();

        // Cancel both client-side and server-side
        if (!(event.getItemStack().is(Items.FLINT_AND_STEEL))) return;

        BlockState state = level.getBlockState(event.getPos());

        if (!(state.getBlock() instanceof BurnedOutTorchBlock
                || state.getBlock() instanceof BurnedOutWallTorchBlock)) return;

        // Cancel BEFORE fire placement visuals
        event.setCanceled(true);
        event.cancelWithResult(ItemInteractionResult.SUCCESS); // or .success(...)

        // Only run this logic server-side
        if (level.isClientSide) return;

        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        InteractionHand hand = event.getHand();
        ItemStack item = event.getItemStack();

        BlockState newState = (state.getBlock() instanceof BurnedOutWallTorchBlock)
                ? ModBlocks.BURNABLE_WALL_TORCH.get().defaultBlockState()
                .setValue(WallTorchBlock.FACING, state.getValue(WallTorchBlock.FACING))
                : ModBlocks.BURNABLE_TORCH.get().defaultBlockState();

        level.setBlock(pos, newState, 3);
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND)
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND;

        item.hurtAndBreak(1, player, slot);
    }

}