/*
 * Dimension Expansion
 * Copyright (c) 2025 DarkLotus (DarkLotus781) / LithiumCraft
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DimensionExpansion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.TELEPORTER_BLOCKS)
                .add(ModBlocks.DEEP_BENEATH_TELEPORTER.get())
                .add(ModBlocks.MINING_TELEPORTER.get())
                .add(ModBlocks.STONE_BLOCK_TELEPORTER.get())
                .add(ModBlocks.OVERWORLD_RETURN_TELEPORTER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DEEP_BENEATH_TELEPORTER.get())
                .add(ModBlocks.MINING_TELEPORTER.get())
                .add(ModBlocks.STONE_BLOCK_TELEPORTER.get())
                .add(ModBlocks.QUARTZ_ORE.get())
                .add(ModBlocks.DEEPSLATE_QUARTZ_ORE.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.DEEP_BENEATH_TELEPORTER.get())
                .add(ModBlocks.MINING_TELEPORTER.get())
                .add(ModBlocks.STONE_BLOCK_TELEPORTER.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.QUARTZ_ORE.get())
                .add(ModBlocks.DEEPSLATE_QUARTZ_ORE.get());

        tag(BlockTags.OVERWORLD_CARVER_REPLACEABLES)
                .add(ModBlocks.QUARTZ_ORE.get())
                .add(ModBlocks.DEEPSLATE_QUARTZ_ORE.get());

        tag(BlockTags.FEATURES_CANNOT_REPLACE)
                .addTag(ModTags.Blocks.TELEPORTER_BLOCKS);

        tag(ModTags.Blocks.RELOCATION_NOT_SUPPORTED)
                .addTag(ModTags.Blocks.TELEPORTER_BLOCKS);

        tag(BlockTags.WITHER_IMMUNE)
                .addTag(ModTags.Blocks.TELEPORTER_BLOCKS);

        tag(BlockTags.DRAGON_IMMUNE)
                .addTag(ModTags.Blocks.TELEPORTER_BLOCKS);

        tag(ModTags.Blocks.BLACKLISTED_SPATIAL)
                .addTag(ModTags.Blocks.TELEPORTER_BLOCKS);

        tag(ModTags.Blocks.ORES)
                .add(ModBlocks.QUARTZ_ORE.get(), ModBlocks.DEEPSLATE_QUARTZ_ORE.get());

        tag(ModTags.Blocks.ORES_QUARTZ)
                .add(ModBlocks.QUARTZ_ORE.get(), ModBlocks.DEEPSLATE_QUARTZ_ORE.get());

        tag(ModTags.Blocks.ORES_IN_GROUND_STONE)
                .add(ModBlocks.QUARTZ_ORE.get());

        tag(ModTags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                .add(ModBlocks.DEEPSLATE_QUARTZ_ORE.get());
    }

}