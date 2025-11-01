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
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Locale;

public class ModLangProvider extends LanguageProvider {

    public ModLangProvider(PackOutput output) {
        super(output, DimensionExpansion.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("creativetab.dimension_expansion.blocks", "Dimension Expansion");
        add("creativetab.dimension_expansion.ores", "Dimension Expansion Ores");
        add("block.dimension_expansion.deep_beneath_teleporter", "Deep Beneath Teleporter");
        add("block.dimension_expansion.mining_teleporter", "Mining Teleporter");
        add("block.dimension_expansion.stone_block_teleporter", "Stone Block Teleporter");
        add("block.dimension_expansion.overworld_return_teleporter", "Overworld Teleporter");
        add("block.dimension_expansion.blank_teleporter", "Teleporter Template");
        add("item.dimension_expansion.ender_gem", "Ender Gem");
        add("item.dimension_expansion.ender_cream", "Ender Cream");
        add("item.dimension_expansion.ender_crystal_shard", "Ender Crystal Shard");
        add("item.dimension_expansion.ender_crystal", "Ender Crystal");
        add("effect.dimension_expansion.teleporter_activate", "Teleporter Activates");
        add("effect.dimension_expansion.darkness_approaches", "Something approaches in the darkness...");
        add("effect.dimension_expansion.nightwalker", "The darkness hurts!");
        add("effect.dimension_expansion.nightwalker.description", "In the dark, you may stub your toe and take damage.");
        add("message.dimension_expansion.nightwalker_warning", "Danger waits for you in the darkness!");
        add("death.attack.darkness", "%1$s was consumed by the darkness");
        add("death.attack.darkness.player", "%1$s was consumed by the darkness while fighting %2$s");
        add("subtitles.ambient.darkness", "The shadows are moving");
        add("subtitles.ambient.darkness_whispers", "The shadows are whispering");
        add("block.dimension_expansion.quartz_ore", "Quartz Ore");
        add("block.dimension_expansion.deepslate_quartz_ore", "Deepslate Quartz Ore");
    }
}
