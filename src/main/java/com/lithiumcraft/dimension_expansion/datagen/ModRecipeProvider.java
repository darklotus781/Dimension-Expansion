package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DEEP_BENEATH_TELEPORTER.get())
                .pattern("DCD")
                .pattern("SNH")
                .pattern("DCD")
                .define('C', Items.CALIBRATED_SCULK_SENSOR)
                .define('S', Items.SCULK_CATALYST)
                .define('H', Items.SCULK_SHRIEKER)
                .define('N', Items.NETHERITE_PICKAXE)
                .define('D', Items.CHISELED_DEEPSLATE)
                .unlockedBy("has_dead_bush", has(Items.DEAD_BUSH))
                .save(recipeOutput, modLoc("blocks/deep_beneath_teleporter"));
    }

    private static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, path);
    }
}