package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

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
        add("block.dimension_expansion.burned_out_torch", "Burned Out Torch");
        add("block.dimension_expansion.burned_out_wall_torch", "Burned Out Torch");
        add("block.dimension_expansion.burnable_torch", "Torch");
        add("block.dimension_expansion.burnable_wall_torch", "Torch");
        add("subtitles.entity.player.darkness", "The Darkness Hurts");
        add("death.attack.darkness", "The Darkness Consumed %1$s");
    }

    // Optional helper
    private static String formatDisplayName(String name) {
        String[] words = name.split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }
}
