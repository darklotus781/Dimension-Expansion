package com.lithiumcraft.dimension_expansion.block;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.property.WoodType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
