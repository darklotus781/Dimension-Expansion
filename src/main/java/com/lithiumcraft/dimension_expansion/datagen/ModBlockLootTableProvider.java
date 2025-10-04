package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.DEEP_BENEATH_TELEPORTER.get());
        this.dropSelf(ModBlocks.MINING_TELEPORTER.get());
        this.dropSelf(ModBlocks.STONE_BLOCK_TELEPORTER.get());
        this.dropSelf(ModBlocks.BLANK_TELEPORTER.get());

        // Quartz Ore
        this.add(ModBlocks.QUARTZ_ORE.get(),
                (block) -> createOreDrop(block, Items.QUARTZ));

        // Deepslate Quartz Ore
        this.add(ModBlocks.DEEPSLATE_QUARTZ_ORE.get(),
                (block) -> createOreDrop(block, Items.QUARTZ));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
