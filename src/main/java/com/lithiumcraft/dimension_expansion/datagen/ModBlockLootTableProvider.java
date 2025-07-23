package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.block.property.WoodType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(ModBlocks.BURNED_OUT_TORCH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.TORCH))));

        this.add(ModBlocks.BURNED_OUT_WALL_TORCH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.TORCH))));

        this.add(ModBlocks.BURNABLE_TORCH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.TORCH))));

        this.add(ModBlocks.BURNABLE_WALL_TORCH.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.TORCH))));

        this.dropSelf(ModBlocks.DEEP_BENEATH_TELEPORTER.get());
        this.dropSelf(ModBlocks.MINING_TELEPORTER.get());
        this.dropSelf(ModBlocks.STONE_BLOCK_TELEPORTER.get());
        this.dropSelf(ModBlocks.BLANK_TELEPORTER.get());
        for (WoodType woodType : WoodType.values()) {
            String name = "upside_down_portal_frame_" + woodType.name();
            Block block = ModBlocks.getUpsideDownPortalFrame(woodType).get();
            this.dropSelf(block);
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }

//    @Override
//    protected Iterable<Block> getKnownBlocks() {
//        List<Block> result = new ArrayList<>();
//
//        for (DeferredHolder<Block, ? extends Block> holder : ModBlocks.BLOCKS.getEntries()) {
//            Block block = holder.get();
//
//            // Skip blocks with no BlockItem registered (meaning no item form)
//            if (BlockItem.BY_BLOCK.get(block) == null) continue;
//
//            // Skip blocks with a loot table explicitly set to minecraft:empty
//            ResourceKey<LootTable> lootKey = block.getLootTable();
//            if (lootKey.location().equals(ResourceLocation.fromNamespaceAndPath("minecraft", "empty"))) continue;
//
//            result.add(block);
//        }
//
//        return result;
//    }
}
