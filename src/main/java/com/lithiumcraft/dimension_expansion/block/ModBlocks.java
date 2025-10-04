package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DimensionExpansion.MOD_ID);

    public static final DeferredBlock<DeepBeneathTeleporterBlock> DEEP_BENEATH_TELEPORTER = BLOCKS.register("deep_beneath_teleporter",
            DeepBeneathTeleporterBlock::new);

    public static final DeferredBlock<OverworldReturnTeleporterBlock> OVERWORLD_RETURN_TELEPORTER = BLOCKS.register("overworld_return_teleporter",
            OverworldReturnTeleporterBlock::new);

    public static final DeferredBlock<MiningTeleporterBlock> MINING_TELEPORTER = BLOCKS.register("mining_teleporter",
            MiningTeleporterBlock::new);

    public static final DeferredBlock<StoneBlockTeleporterBlock> STONE_BLOCK_TELEPORTER = BLOCKS.register("stone_block_teleporter",
            StoneBlockTeleporterBlock::new);

    public static final DeferredBlock<Block> BLANK_TELEPORTER = BLOCKS.register("blank_teleporter",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));

    public static final DeferredBlock<Block> QUARTZ_ORE = registerBlock("quartz_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F).mapColor(MapColor.STONE).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> DEEPSLATE_QUARTZ_ORE = registerBlock("deepslate_quartz_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F).mapColor(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
