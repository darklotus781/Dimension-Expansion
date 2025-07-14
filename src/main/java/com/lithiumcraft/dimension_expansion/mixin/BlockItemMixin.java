package com.lithiumcraft.dimension_expansion.mixin;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void deepbeneath$preventTorchPlacement(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = context.getItemInHand();

        // Only block vanilla torch placement
        if (!stack.is(Items.TORCH)) return;

        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        // Only block in the Deep Beneath dimension
        if (!serverLevel.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        // Cancel torch placement
        cir.setReturnValue(InteractionResult.FAIL);
        cir.cancel();

        // Optional: warn player
        Player player = context.getPlayer();
        if (player != null && player.level().isClientSide) {
            player.displayClientMessage(Component.literal("§cTorches don’t work down here..."), true);
        }
    }
}

