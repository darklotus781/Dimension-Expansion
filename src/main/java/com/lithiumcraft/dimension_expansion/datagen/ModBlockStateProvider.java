package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;


public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DimensionExpansion.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.DEEP_BENEATH_TELEPORTER);
        blockWithItem(ModBlocks.MINING_TELEPORTER);
        blockWithItem(ModBlocks.STONE_BLOCK_TELEPORTER);
        blockWithItem(ModBlocks.OVERWORLD_RETURN_TELEPORTER);
        blockWithItem(ModBlocks.BLANK_TELEPORTER);
        blockWithItem(ModBlocks.QUARTZ_ORE);
        blockWithItem(ModBlocks.DEEPSLATE_QUARTZ_ORE);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("dimension_expansion:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("dimension_expansion:block/" + deferredBlock.getId().getPath() + appendix));
    }

}
