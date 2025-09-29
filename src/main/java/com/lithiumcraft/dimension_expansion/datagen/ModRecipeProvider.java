package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.block.property.WoodType;
import com.lithiumcraft.dimension_expansion.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLANK_TELEPORTER.get())
                .pattern("DCD")
                .pattern("SNH")
                .pattern("DCD")
                .define('C', Items.CALIBRATED_SCULK_SENSOR)
                .define('S', Items.SCULK_CATALYST)
                .define('H', Items.SCULK_SHRIEKER)
                .define('N', Items.NETHERITE_PICKAXE)
                .define('D', Items.CHISELED_DEEPSLATE)
                .unlockedBy("has_dead_bush", has(Items.DEAD_BUSH))
                .save(recipeOutput, modLoc("blocks/blank_teleporter"));


        smithingTransformRecipe(recipeOutput, "deep_beneath_teleporter",
                ModItems.DEEP_BENEATH_TELEPORTER_ITEM.get(),    // output
                ModItems.ENDER_GEM,                             // template
                ModItems.BLANK_TELEPORTER.get(),                // base
                Items.DEEPSLATE_DIAMOND_ORE                     // addition
        );

        smithingTransformRecipe(recipeOutput, "mining_teleporter",
                ModItems.MINING_TELEPORTER_ITEM.get(),          // output
                ModItems.ENDER_GEM,                             // template
                ModItems.BLANK_TELEPORTER.get(),                // base
                Items.DIAMOND_ORE                               // addition
        );

        smithingTransformRecipe(recipeOutput, "stone_block_teleporter",
                ModItems.STONE_BLOCK_TELEPORTER_ITEM.get(),          // output
                ModItems.ENDER_GEM,                             // template
                ModItems.BLANK_TELEPORTER.get(),                // base
                Items.EMERALD_ORE                               // addition
        );

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ENDER_CRYSTAL.get())
                .requires(ModItems.ENDER_CRYSTAL_SHARD.get(), 4)
                .unlockedBy("has_ender_crystal_shard", has(ModItems.ENDER_CRYSTAL_SHARD.get()))
                .save(recipeOutput, modLoc("items/ender_crystal"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ENDER_CREAM.get())
                .requires(Items.ENDER_EYE)
                .requires(Items.SLIME_BALL)
                .requires(Items.MAGMA_CREAM)
                .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
                .unlockedBy("has_magma_cream", has(Items.MAGMA_CREAM))
                .save(recipeOutput, modLoc("items/ender_cream"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENDER_GEM.get())
                .pattern("CCC")
                .pattern("CEC")
                .pattern("CCC")
                .define('C', ModItems.ENDER_CRYSTAL.get())
                .define('E', Items.EMERALD_BLOCK)
                .unlockedBy("has_ender_crystal", has(ModItems.ENDER_CRYSTAL.get()))
                .unlockedBy("has_emerald_block", has(Items.EMERALD_BLOCK))
                .save(recipeOutput, modLoc("items/ender_gem"));

        smeltingRecipe(recipeOutput, "ender_crystal_shard", ModItems.ENDER_CREAM.get(), ModItems.ENDER_CRYSTAL_SHARD, 0.1F, 200);
    }

    private static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, path);
    }

    private void smeltingRecipe(RecipeOutput recipeOutput, String name, ItemLike input, ItemLike result, float xp, int time) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, result, xp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "smelting/" + name));
    }

    private void blastingRecipe(RecipeOutput recipeOutput, String name, ItemLike input, ItemLike result, float xp, int time) {
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, result, xp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "blasting/" + name));
    }

    private void smokingRecipe(RecipeOutput recipeOutput, String name, ItemLike input, ItemLike result, float xp, int time) {
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.MISC, result, xp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "smoking/" + name));
    }

    private void campfireCookingRecipe(RecipeOutput recipeOutput, String name, ItemLike input, ItemLike result, float xp, int time) {
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.MISC, result, xp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "campfire/" + name));
    }

    private void stonecuttingRecipe(RecipeOutput recipeOutput, String name, ItemLike input, ItemLike result, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, result, count)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "stonecutting/" + name));
    }

    private void smithingTransformRecipe(RecipeOutput recipeOutput, String name,
                                         ItemLike output,
                                         ItemLike template,
                                         ItemLike base,
                                         ItemLike addition) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(template),
                        Ingredient.of(base),
                        Ingredient.of(addition),
                        RecipeCategory.MISC,
                        output.asItem()) // ✅ convert to Item
                .unlocks(getHasName(base), has(base))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "smithing/" + name));
    }


    private void stonecuttingRecipe(RecipeOutput recipeOutput, String name, String tagNamespace, String tagPath, ItemLike result, int count, String unlockName) {
        TagKey<Item> tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath(tagNamespace, tagPath));
        Ingredient ingredient = Ingredient.of(tag);

        SingleItemRecipeBuilder.stonecutting(ingredient, RecipeCategory.BUILDING_BLOCKS, result, count)
                .unlockedBy("has_" + unlockName, has(tag))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "stonecutting/" + name));
    }

}