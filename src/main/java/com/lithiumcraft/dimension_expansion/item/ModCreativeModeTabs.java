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
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
