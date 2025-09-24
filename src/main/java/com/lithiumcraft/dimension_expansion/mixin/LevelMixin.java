package com.lithiumcraft.dimension_expansion.mixin;

import com.lithiumcraft.dimension_expansion.util.TorchReplacementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {

    // --- BLOCK disallowed lights (two overloads) ---

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void de_blockDisallowed3(BlockPos pos, BlockState state, int flags,
                                     CallbackInfoReturnable<Boolean> cir) {
        Level self = (Level)(Object)this;
        if (!TorchReplacementHelper.shouldReplaceIn(self)) return;
        if (TorchReplacementHelper.isOurTorch(state)) return;

        // Block: modded torches, lanterns, sea lantern
        if (TorchReplacementHelper.isDisallowedTorch(state) || TorchReplacementHelper.isLanternLike(state)) {
            if (!self.isClientSide) {
                TorchReplacementHelper.notifyNearestPlayer(self, pos, TorchReplacementHelper.defaultBlockedMessage());
            }
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void de_blockDisallowed4(BlockPos pos, BlockState state, int flags, int recursionLeft,
                                     CallbackInfoReturnable<Boolean> cir) {
        // delegate to the 3-arg logic to stay DRY
        de_blockDisallowed3(pos, state, flags, cir);
    }


    // --- REWRITE allowed vanilla torches (two overloads) ---

    @ModifyVariable(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            at = @At("HEAD"),
            ordinal = 0 // BlockState parameter
    )
    private BlockState de_rewriteAllowedTorch3(BlockState state) {
        Level self = (Level)(Object)this;
        return TorchReplacementHelper.rewriteIfAllowed(self, state);
    }

    @ModifyVariable(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("HEAD"),
            ordinal = 0 // BlockState parameter
    )
    private BlockState de_rewriteAllowedTorch4(BlockState state) {
        Level self = (Level)(Object)this;
        return TorchReplacementHelper.rewriteIfAllowed(self, state);
    }
}
