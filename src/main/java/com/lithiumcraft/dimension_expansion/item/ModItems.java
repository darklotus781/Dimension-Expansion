package com.lithiumcraft.dimension_expansion.item;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DimensionExpansion.MOD_ID);

    public static final DeferredItem<Item> DEEP_BENEATH_TELEPORTER_ITEM = registerBlockItem("deep_beneath_teleporter", ModBlocks.DEEP_BENEATH_TELEPORTER);
    public static final DeferredItem<Item> OVERWORLD_RETURN_TELEPORTER_ITEM = registerBlockItem("overworld_return_teleporter", ModBlocks.OVERWORLD_RETURN_TELEPORTER);
    public static final DeferredItem<Item> MINING_TELEPORTER_ITEM = registerBlockItem("mining_teleporter", ModBlocks.MINING_TELEPORTER);
    public static final DeferredItem<Item> STONE_BLOCK_TELEPORTER_ITEM = registerBlockItem("stone_block_teleporter", ModBlocks.STONE_BLOCK_TELEPORTER);
    public static final DeferredItem<Item> BLANK_TELEPORTER = registerBlockItem("blank_teleporter", ModBlocks.BLANK_TELEPORTER);

    public static final DeferredItem<Item> ENDER_GEM = ITEMS.register("ender_gem", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENDER_CREAM = ITEMS.register("ender_cream", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENDER_CRYSTAL_SHARD = ITEMS.register("ender_crystal_shard", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENDER_CRYSTAL = ITEMS.register("ender_crystal", () -> new Item(new Item.Properties()));

    private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, Supplier<T> blockSupplier) {
        return ITEMS.register(name, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
