package com.lithiumcraft.dimension_expansion.item;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.block.property.WoodType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DimensionExpansion.MOD_ID);

    public static final DeferredItem<Item> DEEP_BENEATH_TELEPORTER_ITEM = registerBlockItem("deep_beneath_teleporter", ModBlocks.DEEP_BENEATH_TELEPORTER);
    public static final DeferredItem<Item> OVERWORLD_RETURN_TELEPORTER_ITEM = registerBlockItem("overworld_return_teleporter", ModBlocks.OVERWORLD_RETURN_TELEPORTER);
    public static final DeferredItem<Item> MINING_TELEPORTER_ITEM = registerBlockItem("mining_teleporter", ModBlocks.MINING_TELEPORTER);
    public static final DeferredItem<Item> STONE_BLOCK_TELEPORTER_ITEM = registerBlockItem("stone_block_teleporter", ModBlocks.STONE_BLOCK_TELEPORTER);
    public static final DeferredItem<Item> BLANK_TELEPORTER = registerBlockItem("blank_teleporter", ModBlocks.BLANK_TELEPORTER);

    public static final DeferredItem<Item> BURNED_OUT_TORCH_ITEM = registerBlockItem("burned_out_torch", ModBlocks.BURNED_OUT_TORCH);
    public static final DeferredItem<Item> BURNED_OUT_WALL_TORCH_ITEM = registerBlockItem("burned_out_wall_torch", ModBlocks.BURNED_OUT_WALL_TORCH);

    public static final DeferredItem<Item> BURNABLE_TORCH_ITEM = registerBlockItem("burnable_torch", ModBlocks.BURNABLE_TORCH);
    public static final DeferredItem<Item> BURNABLE_WALL_TORCH_ITEM = registerBlockItem("burnable_wall_torch", ModBlocks.BURNABLE_WALL_TORCH);

    public static final DeferredItem<Item> ENDER_GEM = ITEMS.register("ender_gem", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENDER_CREAM = ITEMS.register("ender_cream", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENDER_CRYSTAL_SHARD = ITEMS.register("ender_crystal_shard", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENDER_CRYSTAL = ITEMS.register("ender_crystal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENDERMAN_HEART = ITEMS.register("enderman_heart", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLOOD_STONE = ITEMS.register("blood_stone", () -> new BloodStone(new Item.Properties().stacksTo(1).fireResistant()));

    public static final Map<WoodType, DeferredItem<Item>> UPSIDE_DOWN_PORTAL_FRAME_ITEMS =
            Arrays.stream(WoodType.values()).collect(Collectors.toUnmodifiableMap(
                    Function.identity(),
                    type -> registerBlockItem("upside_down_portal_frame_" + type.name().toLowerCase(Locale.ROOT), ModBlocks.getUpsideDownPortalFrame(type))
            ));

    private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, Supplier<T> blockSupplier) {
        return ITEMS.register(name, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
