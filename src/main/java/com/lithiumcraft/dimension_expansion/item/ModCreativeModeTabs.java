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

package com.lithiumcraft.dimension_expansion.item;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DimensionExpansion.MOD_ID);

    public static final Supplier<CreativeModeTab> MISC_TABS = CREATIVE_MODE_TAB.register("dimension_expansion_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.DEEP_BENEATH_TELEPORTER.get()))
                    .title(Component.translatable("creativetab.dimension_expansion.blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.DEEP_BENEATH_TELEPORTER);
                        output.accept(ModBlocks.MINING_TELEPORTER);
                        output.accept(ModBlocks.STONE_BLOCK_TELEPORTER);
                        output.accept(ModBlocks.BLANK_TELEPORTER);
                        output.accept(ModItems.ENDER_GEM.get());
                        output.accept(ModItems.ENDER_CREAM.get());
                        output.accept(ModItems.ENDER_CRYSTAL.get());
                        output.accept(ModItems.ENDER_CRYSTAL_SHARD.get());
                    }).build());

    public static final Supplier<CreativeModeTab> ORES = CREATIVE_MODE_TAB.register("dimension_expansion_ores_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.QUARTZ_ORE.get()))
                    .title(Component.translatable("creativetab.dimension_expansion.ores"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.QUARTZ_ORE.get());
                        output.accept(ModBlocks.DEEPSLATE_QUARTZ_ORE.get());
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
