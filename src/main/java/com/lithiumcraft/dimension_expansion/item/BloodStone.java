package com.lithiumcraft.dimension_expansion.item;

import com.lithiumcraft.dimension_expansion.util.UpsideDownPortalShape;
import com.lithiumcraft.dimension_expansion.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BloodStone extends Item {
    public BloodStone(Properties properties) {
        super(properties.durability(2).rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.dimension_expansion.blood_stone").withStyle(ChatFormatting.GOLD));
    }

    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        stack.setDamageValue(stack.getDamageValue() + 1);
        if (stack.getDamageValue() >= stack.getMaxDamage()) stack.setCount(0);
        return stack.copy();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        BlockState state = level.getBlockState(clicked);
        Player player = context.getPlayer();

        if (!state.is(ModTags.Blocks.UPSIDE_DOWN_PORTAL_FRAME_BLOCKS)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide && player != null) {
            BlockPos checkAbove = clicked.above();
            UpsideDownPortalShape.detectAndLightPortal(level, checkAbove);
        }

        context.getItemInHand().hurtAndBreak(1, context.getPlayer(), EquipmentSlot.MAINHAND);
        context.getPlayer().getCooldowns().addCooldown(this, 60);

        return InteractionResult.SUCCESS;
    }
}
