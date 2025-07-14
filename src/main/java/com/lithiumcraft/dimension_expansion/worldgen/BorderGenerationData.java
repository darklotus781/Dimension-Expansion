package com.lithiumcraft.dimension_expansion.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class BorderGenerationData extends SavedData {
    public static final String ID = "stone_block_border_generated";
    public boolean borderGenerated = false;

    public BorderGenerationData() {}

    public static BorderGenerationData load(CompoundTag tag, HolderLookup.Provider provider) {
        BorderGenerationData data = new BorderGenerationData();
        data.borderGenerated = tag.getBoolean("Generated");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putBoolean("Generated", this.borderGenerated);
        return tag;
    }

    public static SavedData.Factory<BorderGenerationData> factory() {
        return new SavedData.Factory<>(BorderGenerationData::new, BorderGenerationData::load);
    }
}

