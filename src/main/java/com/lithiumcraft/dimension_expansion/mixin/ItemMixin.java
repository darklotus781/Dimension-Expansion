package com.lithiumcraft.dimension_expansion.mixin;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void deepbeneath$blockTorchInDeepBeneath(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (!serverLevel.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        // Cancel vanilla torch placement
        cir.setReturnValue(InteractionResult.FAIL);
        cir.cancel();

        // Send message to player
        Player player = context.getPlayer();
        if (player != null && player.level().isClientSide) {
            player.displayClientMessage(Component.literal("§cTorches don’t work down here..."), true);
        }
    }
}