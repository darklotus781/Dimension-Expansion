package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DimensionExpansion.MOD_ID);

    public static final DeferredBlock<Block> DEEP_BENEATH_TELEPORTER = registerBlock("deep_beneath_teleporter",
            DeepBeneathTeleporterBlock::new);

    public static final DeferredBlock<Block> OVERWORLD_RETURN_TELEPORTER = registerBlock("overworld_return_teleporter",
            OverworldReturnTeleporterBlock::new);

    public static final DeferredBlock<Block> MINING_TELEPORTER = registerBlock("mining_teleporter",
            MiningTeleporterBlock::new);

    public static final DeferredBlock<Block> STONE_BLOCK_TELEPORTER = registerBlock("stone_block_teleporter",
            StoneBlockTeleporterBlock::new);

    public static final DeferredBlock<BurnedOutTorchBlock> BURNED_OUT_TORCH = registerBlock("burned_out_torch",
            () -> new BurnedOutTorchBlock(ParticleTypes.SMOKE, BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH)));

    public static final DeferredBlock<BurnedOutWallTorchBlock> BURNED_OUT_WALL_TORCH = registerBlock("burned_out_wall_torch",
            () -> new BurnedOutWallTorchBlock(ParticleTypes.SMOKE, BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH)));

    public static final DeferredBlock<BurnableTorchBlock> BURNABLE_TORCH = registerBlock("burnable_torch",
            () -> new BurnableTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH)));

    public static final DeferredBlock<BurnableWallTorchBlock> BURNABLE_WALL_TORCH = registerBlock("burnable_wall_torch",
            () -> new BurnableWallTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH)));

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
