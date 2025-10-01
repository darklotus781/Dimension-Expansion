package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.property.WoodType;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Locale;

public class ModLangProvider extends LanguageProvider {

    public ModLangProvider(PackOutput output) {
        super(output, DimensionExpansion.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("creativetab.dimension_expansion.blocks", "Dimension Expansion Blocks");
        add("block.dimension_expansion.deep_beneath_teleporter", "Deep Beneath Teleporter");
        add("block.dimension_expansion.mining_teleporter", "Mining Teleporter");
        add("block.dimension_expansion.stone_block_teleporter", "Stone Block Teleporter");
        add("block.dimension_expansion.overworld_return_teleporter", "Overworld Teleporter");
        add("block.dimension_expansion.blank_teleporter", "Teleporter Template");
        add("item.dimension_expansion.ender_gem","Ender Gem");
        add("item.dimension_expansion.ender_cream","Ender Cream");
        add("item.dimension_expansion.ender_crystal_shard","Ender Crystal Shard");
        add("item.dimension_expansion.ender_crystal","Ender Crystal");
        add("effect.dimension_expansion.teleporter_activate", "Teleporter Activates");
        add("effect.dimension_expansion.darkness_approaches", "Something approaches in the darkness...");
        add("effect.dimension_expansion.nightwalker", "The darkness hurts!");
        add("effect.dimension_expansion.nightwalker.description", "In the dark, you may stub your toe and take damage.");
        add("message.dimension_expansion.nightwalker_warning", "Danger waits for you in the darkness!");
        add("death.attack.darkness", "%1$s was consumed by the darkness");
        add("death.attack.darkness.player", "%1$s was consumed by the darkness while fighting %2$s");
        add("subtitles.ambient.darkness", "The shadows are moving");
        add("subtitles.ambient.darkness_whispers", "The shadows are whispering");
    }
}
