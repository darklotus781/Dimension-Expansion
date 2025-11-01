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

