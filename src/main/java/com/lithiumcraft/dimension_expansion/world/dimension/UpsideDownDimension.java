package com.lithiumcraft.dimension_expansion.world.dimension;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

public class UpsideDownDimension {

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class UpsideDownSpecialEffectsHandler {

        @SubscribeEvent
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
            DimensionSpecialEffects customEffect = new DimensionSpecialEffects(
                    8.0F, // fog distance (in chunks)
                    false, // no ground for light calculation
                    DimensionSpecialEffects.SkyType.NONE,
                    false, // do not force bright lightmap
                    false  // no constant ambient light
            ) {

                @Override
                public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
                    return new Vec3(0.3, 0.05, 0.05); // intense red fog
//                    return new Vec3(0.05, 0.01, 0.01); // deep near-black reddish fog
                }

                @Override
                public boolean isFoggyAt(int x, int y) {
                    return true; // Always foggy, everywhere
                }

                @Override
                public float getCloudHeight() {
                    return Float.MIN_VALUE; // push clouds way down
                }

                @Override
                public float[] getSunriseColor(float partialTicks, float celestialAngle) {
                    return null; // disables sunrise coloring
                }


            };
            event.register(DimensionExpansionDimensions.UPSIDE_DOWN.location(), customEffect);
        }
    }
}