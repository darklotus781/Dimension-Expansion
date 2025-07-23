package com.lithiumcraft.dimension_expansion.mixin;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void overrideFog(
        Camera camera,
        FogRenderer.FogMode fogMode,
        float farPlaneDistance,
        boolean shouldCreateFog,
        float partialTick,
        CallbackInfo ci
    ) {
        if (!(camera.getEntity().level() instanceof ClientLevel level)) return;

//         Only apply in Upside Down dimension
        if (level.dimension().location().equals(DimensionExpansionDimensions.UPSIDE_DOWN.location()) || level.dimension().location().equals(DimensionExpansionDimensions.DEEP_BENEATH.location())) {
            RenderSystem.setShaderFogStart(8.0F);
            RenderSystem.setShaderFogEnd(16.0F);
            RenderSystem.setShaderFogShape(FogShape.SPHERE);

            ci.cancel(); // prevent vanilla from setting it back
        }
    }
}
