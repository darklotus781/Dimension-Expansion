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
        add("creativetab.dimension_expansion.blocks", "Dimensional Expansion Blocks");
        add("block.dimension_expansion.deep_beneath_teleporter", "Deep Beneath Teleporter");
        add("block.dimension_expansion.mining_teleporter", "Mining Teleporter");
        add("block.dimension_expansion.stone_block_teleporter", "Stone Block Teleporter");
        add("block.dimension_expansion.overworld_return_teleporter", "Overworld Teleporter");
        add("block.dimension_expansion.blank_teleporter", "Teleporter Template");
        add("block.dimension_expansion.burned_out_torch", "Burned Out Torch");
        add("block.dimension_expansion.burned_out_wall_torch", "Burned Out Torch");
        add("block.dimension_expansion.burnable_torch", "Torch");
        add("block.dimension_expansion.burnable_wall_torch", "Torch");
        add("block.dimension_expansion.upside_down_portal", "Upside Down Portal");
        add("subtitles.entity.player.darkness", "The Darkness Hurts");
        add("death.attack.darkness", "The Darkness Consumed %1$s");
        for (WoodType woodType : WoodType.values()) {
            String name = "upside_down_portal_frame_" + woodType.name().toLowerCase(Locale.ROOT);
            add("block.dimension_expansion." + name, this.formatDisplayName(name));

        }
        add("item.dimension_expansion.ender_gem","Ender Gem");
        add("item.dimension_expansion.ender_cream","Ender Cream");
        add("item.dimension_expansion.ender_crystal_shard","Ender Crystal Shard");
        add("item.dimension_expansion.ender_crystal","Ender Crystal");
        add("item.dimension_expansion.enderman_heart","Enderman Heart");
        add("item.dimension_expansion.blood_stone","Blood Stone");
        add("tooltip.dimension_expansion.blood_stone", "A Crafting Item, and an Upside Down Portal Activator");
        add("sounds.dimension_expansion.teleporter_activate", "Teleporter Activates");
        add("sounds.dimension_expansion.upside_down_portal_activate", "Upside Down Portal Activates");
        add("sounds.dimension_expansion.upside_down_dimension_portal_ambient", "Upside Down Portal Growls");
    }

    // Optional helper
    private static String formatDisplayName(String name) {
        String[] words = name.split("[_\\s]+"); // split on underscores or spaces
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

}
