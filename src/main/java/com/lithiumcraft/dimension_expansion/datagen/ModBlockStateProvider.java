package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.WallTorchBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import static com.lithiumcraft.dimension_expansion.block.ModBlocks.*;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DimensionExpansion.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(DEEP_BENEATH_TELEPORTER);
        blockWithItem(MINING_TELEPORTER);
        blockWithItem(STONE_BLOCK_TELEPORTER);
        blockWithItem(ModBlocks.OVERWORLD_RETURN_TELEPORTER);

        // BURNED OUT STANDING TORCH (uses torch model)
        ModelFile torchModel = models()
                .withExistingParent("burned_out_torch", mcLoc("block/template_torch"))
                .texture("torch", modLoc("block/burned_out_torch"));

        getVariantBuilder(ModBlocks.BURNED_OUT_TORCH.get())
                .partialState()
                .modelForState()
                .modelFile(torchModel)
                .addModel();

// Register block item model for standing torch
        simpleBlockItem(ModBlocks.BURNED_OUT_TORCH.get(), torchModel);

// BURNED OUT WALL TORCH (uses wall_torch model with y-rotation)
        ModelFile wallTorchModel = models()
                .withExistingParent("burned_out_wall_torch", mcLoc("block/template_torch_wall"))
                .texture("torch", modLoc("block/burned_out_torch"));

        getVariantBuilder(ModBlocks.BURNED_OUT_WALL_TORCH.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(WallTorchBlock.FACING);
                    int yRot = switch (facing) {
                        case EAST -> 0;
                        case NORTH -> 270;
                        case SOUTH -> 90;
                        case WEST -> 180;
                        default -> 0;
                    };
                    return ConfiguredModel.builder()
                            .modelFile(wallTorchModel)
                            .rotationY(yRot)
                            .build();
                });

// Register block item model for wall torch
        simpleBlockItem(ModBlocks.BURNED_OUT_WALL_TORCH.get(), torchModel);

// Burnable torch (standalone)
        getVariantBuilder(ModBlocks.BURNABLE_TORCH.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(new ModelFile.UncheckedModelFile("minecraft:block/torch"))
                        .build()
                );

// Burnable wall torch (facing rotation)
        getVariantBuilder(ModBlocks.BURNABLE_WALL_TORCH.get())
                .forAllStates(state -> {
                    int yRot = switch (state.getValue(WallTorchBlock.FACING)) {
                        case NORTH -> 270;
                        case SOUTH -> 90;
                        case WEST -> 180;
                        default -> 0;
                    };
                    return ConfiguredModel.builder()
                            .modelFile(new ModelFile.UncheckedModelFile("minecraft:block/wall_torch"))
                            .rotationY(yRot)
                            .build();
                });

// Item model only for the standing torch
        simpleBlockItem(ModBlocks.BURNABLE_TORCH.get(),
                new ModelFile.UncheckedModelFile("minecraft:block/torch"));
        simpleBlockItem(ModBlocks.BURNABLE_WALL_TORCH.get(),
                new ModelFile.UncheckedModelFile("minecraft:block/torch"));

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
