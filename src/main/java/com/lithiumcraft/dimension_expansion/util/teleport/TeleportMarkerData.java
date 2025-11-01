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

package com.lithiumcraft.dimension_expansion.util.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.jetbrains.annotations.Nullable;

/**
 * Stores the teleport target location (e.g. the overworld destination for a Deep Beneath teleporter).
 */
public class TeleportMarkerData {

    private BlockPos targetPos;
    private ResourceKey<Level> targetDim;
    private ResourceLocation sourceBlockId;

    public TeleportMarkerData(BlockPos targetPos, ResourceKey<Level> targetDim, Block sourceBlock) {
        this.targetPos = targetPos;
        this.targetDim = targetDim;
        this.sourceBlockId = BuiltInRegistries.BLOCK.getKey(sourceBlock);
    }

    public BlockPos getTargetPos() {
        return targetPos;
    }

    public ResourceKey<Level> getTargetDimension() {
        return targetDim;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", targetPos.getX());
        tag.putInt("y", targetPos.getY());
        tag.putInt("z", targetPos.getZ());
        tag.putString("dimension", targetDim.location().toString());
        tag.putString("source_block", sourceBlockId.toString());
        return tag;
    }

    public static @Nullable TeleportMarkerData load(CompoundTag tag) {
        if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z") || !tag.contains("dimension"))
            return null;

        BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(tag.getString("dimension")));

        ResourceLocation blockId = tag.contains("source_block")
                ? ResourceLocation.tryParse(tag.getString("source_block"))
                : null;

        Block sourceBlock = blockId != null ? BuiltInRegistries.BLOCK.get(blockId) : Blocks.AIR;
        return new TeleportMarkerData(pos, dim, sourceBlock);
    }

    public Block getSourceBlock() {
        return BuiltInRegistries.BLOCK.get(sourceBlockId);
    }
}
