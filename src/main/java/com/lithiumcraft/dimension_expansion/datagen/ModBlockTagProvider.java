package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.block.property.WoodType;
import com.lithiumcraft.dimension_expansion.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
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

        for (WoodType woodType : WoodType.values()) {
            String name = "upside_down_portal_frame_" + woodType.name().toLowerCase(Locale.ROOT);
            Block block = ModBlocks.getUpsideDownPortalFrame(woodType).get();
            tag(BlockTags.MINEABLE_WITH_AXE)
                    .add(block);
            tag(ModTags.Blocks.UPSIDE_DOWN_PORTAL_FRAME_BLOCKS)
                    .add(block);
        }

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DEEP_BENEATH_TELEPORTER.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.DEEP_BENEATH_TELEPORTER.get());
    }

}