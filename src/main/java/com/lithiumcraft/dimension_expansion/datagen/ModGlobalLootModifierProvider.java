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
import com.lithiumcraft.dimension_expansion.loot.RemoveItemModifier;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

/** Wires the Deep Beneath drop tables onto the vanilla entity tables. */
public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, DimensionExpansion.MOD_ID);
    }

    @Override
    protected void start() {
        add("deep_beneath_ghast_blaze_powder", new AddTableLootModifier(
                deepBeneathDrops(EntityType.GHAST.getDefaultLootTable()),
                DeepBeneathEntityLootProvider.GHAST));

        add("deep_beneath_ghast_no_tear", new RemoveItemModifier(
                deepBeneathDrops(EntityType.GHAST.getDefaultLootTable()),
                Items.GHAST_TEAR));

        add("deep_beneath_cave_spider_fermented_eye", new AddTableLootModifier(
                deepBeneathDrops(EntityType.CAVE_SPIDER.getDefaultLootTable()),
                DeepBeneathEntityLootProvider.CAVE_SPIDER));
    }

    /** Applies to one entity's loot table, and only when it is rolled inside the Deep Beneath. */
    private static LootItemCondition[] deepBeneathDrops(ResourceKey<LootTable> table) {
        return new LootItemCondition[]{
                LootTableIdCondition.builder(table.location()).build(),
                LocationCheck.checkLocation(LocationPredicate.Builder.inDimension(
                        DimensionExpansionDimensions.DEEP_BENEATH)).build()
        };
    }
}
