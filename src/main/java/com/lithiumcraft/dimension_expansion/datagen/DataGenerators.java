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
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

// Client-side providers
        if (event.includeClient()) {
            generator.addProvider(true, new ModBlockStateProvider(packOutput, existingFileHelper));
            generator.addProvider(true, new ModItemModelProvider(packOutput, existingFileHelper));
            generator.addProvider(true, new ModLangProvider(packOutput));
        }

// Server-side providers
        if (event.includeServer()) {
            ModRegistriesProvider datapackProvider = new ModRegistriesProvider(packOutput, event.getLookupProvider());
            CompletableFuture<HolderLookup.Provider> lookupProvider = datapackProvider.getRegistryProvider();
            generator.addProvider(true, datapackProvider);

            generator.addProvider(true, new LootTableProvider(
                    packOutput,
                    Collections.emptySet(),
                    List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
                    lookupProvider
            ));
            generator.addProvider(true, new ModRecipeProvider(packOutput, lookupProvider));

            BlockTagsProvider blockTagsProvider = new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
            generator.addProvider(true, blockTagsProvider);
            generator.addProvider(event.includeServer(), new ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
            ModDamageTypeTagProvider modDamageTypeTagsProvider = new ModDamageTypeTagProvider(packOutput, lookupProvider, existingFileHelper);
            generator.addProvider(true, modDamageTypeTagsProvider);

        }
    }
}
