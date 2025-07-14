package com.lithiumcraft.dimension_expansion.util;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> TELEPORTER_BLOCKS = createTag("teleporter_blocks");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, name));
        }

        private static TagKey<Block> createTag(String namespace, String key) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, key));
        }
    }

    public static class Items {
//        public static final TagKey<Item> WRENCHES = createTag("wrenches");
//        public static final TagKey<Item> ANDESITE_ALLOY_INGOTS = createTag("c", "ingots/andesite_alloy");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, name));
        }

        private static TagKey<Item> createTag(String namespace, String key) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, key));
        }
    }
}