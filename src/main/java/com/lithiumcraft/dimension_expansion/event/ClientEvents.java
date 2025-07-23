package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.item.ModItems;
import com.lithiumcraft.dimension_expansion.particle.ModParticles;
import com.lithiumcraft.dimension_expansion.particle.client.UpsideDownPortalHangParticleProvider;
import com.lithiumcraft.dimension_expansion.particle.client.UpsideDownPortalLandParticleProvider;
import com.lithiumcraft.dimension_expansion.particle.client.UpsideDownPortalDripParticleProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.stream.Stream;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BURNED_OUT_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BURNED_OUT_WALL_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BURNABLE_TORCH.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BURNABLE_WALL_TORCH.get(), RenderType.cutout());
            ModBlocks.UPSIDE_DOWN_PORTAL_FRAMES.values().forEach(holder ->
                    ItemBlockRenderTypes.setRenderLayer(holder.get(), RenderType.cutout())
            );
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.UPSIDE_DOWN_PORTAL_BLOCK.get(), RenderType.translucent());

        });
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, tintIndex) -> tintIndex == 0 ? DimensionExpansion.PORTAL_COLOR : -1,
                Stream.concat(
                        Stream.of(ModBlocks.UPSIDE_DOWN_PORTAL_BLOCK.get()),
                        ModBlocks.UPSIDE_DOWN_PORTAL_FRAMES.values().stream().map(DeferredHolder::get)
                ).toArray(Block[]::new)
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> tintIndex == 0 ? DimensionExpansion.PORTAL_COLOR : -1,
                ModItems.UPSIDE_DOWN_PORTAL_FRAME_ITEMS.values().stream()
                        .map(DeferredItem::get)
                        .toArray(Item[]::new)
        );
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        engine.register(ModParticles.UPSIDE_DOWN_PORTAL_FRAME_DRIP_PARTICLE.get(), UpsideDownPortalDripParticleProvider::new);
        engine.register(ModParticles.UPSIDE_DOWN_PORTAL_FRAME_HANG_PARTICLE.get(), UpsideDownPortalHangParticleProvider::new);
        engine.register(ModParticles.UPSIDE_DOWN_PORTAL_FRAME_LAND_PARTICLE.get(), UpsideDownPortalLandParticleProvider::new);
    }
}