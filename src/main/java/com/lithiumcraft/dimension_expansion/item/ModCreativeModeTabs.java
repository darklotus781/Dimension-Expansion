package com.lithiumcraft.dimension_expansion.item;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DimensionExpansion.MOD_ID);

    public static final Supplier<CreativeModeTab> GEODE_ITEM_TABS = CREATIVE_MODE_TAB.register("dimension_expansion_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.DEEP_BENEATH_TELEPORTER.get()))
                    .title(Component.translatable("creativetab.dimension_expansion.blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.DEEP_BENEATH_TELEPORTER);
                        output.accept(ModBlocks.MINING_TELEPORTER);
                        output.accept(ModBlocks.STONE_BLOCK_TELEPORTER);
                        output.accept(ModBlocks.BLANK_TELEPORTER);

                        ModBlocks.UPSIDE_DOWN_PORTAL_FRAMES.values().forEach(holder ->
                                output.accept(holder.get())
                        );
                        output.accept(ModItems.ENDER_GEM.get());
                        output.accept(ModItems.ENDERMAN_HEART.get());
                        output.accept(ModItems.ENDER_CREAM.get());
                        output.accept(ModItems.ENDER_CRYSTAL.get());
                        output.accept(ModItems.ENDER_CRYSTAL_SHARD.get());
                        output.accept(ModItems.BLOOD_STONE.get());
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
