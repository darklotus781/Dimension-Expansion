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
        upsideDownPortalFrameRecipes(recipeOutput);

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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLOOD_STONE.get())
                .requires(Items.ROTTEN_FLESH)
                .requires(Items.SLIME_BALL)
                .requires(Items.BONE)
                .requires(Items.LEATHER)
                .requires(Items.EMERALD)
                .requires(ModItems.ENDERMAN_HEART.get())
                .requires(Items.RABBIT_FOOT)
                .requires(Items.GUNPOWDER)
                .requires(Items.FERMENTED_SPIDER_EYE)
                .unlockedBy("has_enderman_heart", has(ModItems.ENDERMAN_HEART.get()))
                .save(recipeOutput, modLoc("items/blood_stone"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ENDER_CREAM.get())
                .requires(ModItems.ENDERMAN_HEART.get())
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_enderman_heart", has(ModItems.ENDERMAN_HEART.get()))
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
                .save(recipeOutput, modLoc("items/ender_cream"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENDER_GEM.get())
                .pattern("CCC")
                .pattern("CBC")
                .pattern("CCC")
                .define('C', ModItems.ENDER_CRYSTAL.get())
                .define('B', ModItems.BLOOD_STONE.get())
                .unlockedBy("has_ender_crystal", has(ModItems.ENDER_CRYSTAL.get()))
                .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                .save(recipeOutput, modLoc("items/ender_gem"));

        smeltingRecipe(recipeOutput, "ender_crystal_shard", ModItems.ENDER_CREAM.get(), ModItems.ENDER_CRYSTAL_SHARD, 0.1F, 200);
    }

    private void upsideDownPortalFrameRecipes(RecipeOutput output) {
        for (WoodType type : WoodType.values()) {
            ItemLike log = switch (type) {
                case OAK -> Items.OAK_WOOD;
                case SPRUCE -> Items.SPRUCE_WOOD;
                case BIRCH -> Items.BIRCH_WOOD;
                case JUNGLE -> Items.JUNGLE_WOOD;
                case ACACIA -> Items.ACACIA_WOOD;
                case DARK_OAK -> Items.DARK_OAK_WOOD;
                case MANGROVE -> Items.MANGROVE_WOOD;
                case CHERRY -> Items.CHERRY_WOOD;
            };

            ItemLike frameBlock = ModBlocks.UPSIDE_DOWN_PORTAL_FRAMES.get(type).get();

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, frameBlock, 14)
                    .define('X', log)
                    .define('#', ModItems.BLOOD_STONE.get())
                    .pattern("XXX")
                    .pattern("X#X")
                    .pattern("XXX")
                    .unlockedBy("has_" + type.name().toLowerCase(), has(log))
                    .save(output, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "upside_down_portal_frame_" + type.name().toLowerCase()));
        }
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