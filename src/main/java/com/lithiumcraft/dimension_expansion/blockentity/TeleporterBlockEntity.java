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

package com.lithiumcraft.dimension_expansion.blockentity;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.registry.ModSounds;
import com.lithiumcraft.dimension_expansion.structure.StructureBuilder;
import com.lithiumcraft.dimension_expansion.util.teleport.TeleportMarkerData;
import com.lithiumcraft.dimension_expansion.util.teleport.TeleportUtil;
import com.lithiumcraft.dimension_expansion.util.teleport.TeleporterRules;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Optional;

/**
 * Stores the teleport target location (e.g. the overworld destination for a Deep Beneath teleporter).
 *
 * Rules:
 * - OVERWORLD_RETURN_TELEPORTER is the ONLY teleporter that can be "link lost" (teleport returns false).
 * - Origin teleporters (deep_beneath/mining/stone_block) should always create/relink a return teleporter
 *   in the destination dimension at the desired arrival coords.
 * - Never recreate missing origin teleporters when returning (prevents dupes).
 *
 * Return value:
 * - teleport(...) returns true on success, false on failure (caller handles sound/message).
 */
public class TeleporterBlockEntity extends BlockEntity implements INBTSerializable<CompoundTag> {

    private TeleportMarkerData linkedTeleporter = null;

    public TeleporterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @SuppressWarnings("unused")
    private static final Map<Block, ResourceKey<Level>> TELEPORT_TARGETS = Map.of(
            ModBlocks.DEEP_BENEATH_TELEPORTER.get(), DimensionExpansionDimensions.DEEP_BENEATH,
            ModBlocks.MINING_TELEPORTER.get(), DimensionExpansionDimensions.MINING,
            ModBlocks.STONE_BLOCK_TELEPORTER.get(), DimensionExpansionDimensions.STONE_BLOCK
    );

    public static void tickServer(Level level, BlockPos pos, BlockState state, TeleporterBlockEntity be) {
        // No active ticking needed unless you want portal particles or cooldown
    }

    /**
     * Validates the current linkedTeleporter marker.
     * If invalid, clears it and returns false.
     *
     * IMPORTANT: This should only be used to FAIL teleport for the RETURN teleporter.
     * Origin teleporters should not fail just because their marker is missing/stale.
     */
    private boolean validateLinkedMarker(ServerLevel sourceLevel) {
        if (linkedTeleporter == null) return false;

        MinecraftServer server = sourceLevel.getServer();
        ServerLevel targetLevel = server.getLevel(linkedTeleporter.getTargetDimension());
        if (targetLevel == null) {
            linkedTeleporter = null;
            setChanged();
            return false;
        }

        BlockPos targetPos = linkedTeleporter.getTargetPos();
        Block expectedBlock = linkedTeleporter.getSourceBlock();
        BlockState atTarget = targetLevel.getBlockState(targetPos);

        // Link is ONLY valid if the expected block is actually still present.
        if (!atTarget.is(expectedBlock)) {
            linkedTeleporter = null;
            setChanged();
            return false;
        }

        // Also ensure a TeleporterBlockEntity exists at the target (prevents weird mismatches)
        BlockEntity be = targetLevel.getBlockEntity(targetPos);
        if (!(be instanceof TeleporterBlockEntity)) {
            linkedTeleporter = null;
            setChanged();
            return false;
        }

        return true;
    }

    public boolean teleport(ServerPlayer player) {
        if (player == null || level == null || level.isClientSide() || !(level instanceof ServerLevel sourceLevel)) {
            return false;
        }

        MinecraftServer server = sourceLevel.getServer();
        ResourceKey<Level> sourceDim = sourceLevel.dimension();
        BlockPos sourcePos = this.getBlockPos();

        Block block = getBlockState().getBlock();

        // ------------------------------------------------------------
        // Return teleporter behavior (dimension -> overworld, etc.)
        // ------------------------------------------------------------
        if (block == ModBlocks.OVERWORLD_RETURN_TELEPORTER.get()) {
            // Must have a valid stored link. If missing/stale, do nothing (link lost).
            if (!validateLinkedMarker(sourceLevel)) {
                return false;
            }

            BlockPos targetPos = linkedTeleporter.getTargetPos();
            ResourceKey<Level> targetDim = linkedTeleporter.getTargetDimension();

            ServerLevel targetLevel = server.getLevel(targetDim);
            if (targetLevel == null) {
                linkedTeleporter = null;
                setChanged();
                return false;
            }

            // IMPORTANT: Do NOT recreate the missing origin teleporter. Link must be valid to work.

            BlockPos arrival = findArrivalOffset(targetLevel, targetPos.above());
            player.teleportTo(
                    targetLevel,
                    arrival.getX() + 0.5,
                    arrival.getY(),
                    arrival.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );

            targetLevel.playSound(null, targetPos, ModSounds.TELEPORTER_ACTIVATE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        }

        // ------------------------------------------------------------
        // Dimensional teleporter behavior (overworld <-> dimension)
        // ------------------------------------------------------------
        TeleporterRules rules = TELEPORT_RULES.get(block);
        if (rules == null) {
            player.sendSystemMessage(Component.literal("This teleporter is not linked to a dimension."));
            return false;
        }

        ResourceKey<Level> targetDim = rules.targetDimension();
        boolean goingToTarget = !sourceDim.equals(targetDim);

        ServerLevel targetLevel = server.getLevel(targetDim);
        if (targetLevel == null) return false;

        BlockPos targetTeleporterPos;

        if (goingToTarget) {
            BlockPos baseTargetPos = rules.resolveArrival(targetLevel, sourcePos);

            // Only reuse a return teleporter if it is already linked to THIS origin teleporter.
            Optional<TeleporterBlockEntity> nearby = findReturnTeleporterLinkedToThis(
                    targetLevel,
                    baseTargetPos,
                    ModBlocks.OVERWORLD_RETURN_TELEPORTER.get(),
                    48,
                    sourceLevel.dimension(),
                    sourcePos,
                    block
            );

            if (nearby.isPresent()) {
                targetTeleporterPos = nearby.get().getBlockPos();
            } else {
                // IMPORTANT: Always ensure platform/return teleporter exists at the desired coordinates.
                // Do NOT skip creation just because some other return teleporter exists nearby.
                if (rules.buildPlatform() && rules.platformBuilder() != null) {
                    if (!TeleportUtil.isPlatformReady(targetLevel, baseTargetPos, ModBlocks.OVERWORLD_RETURN_TELEPORTER.get())) {
                        rules.platformBuilder().accept(targetLevel, baseTargetPos);
                    }
                }

                // Ensure the return teleporter block exists at baseTargetPos (or baseTargetPos.above()).
                Block returnBlock = ModBlocks.OVERWORLD_RETURN_TELEPORTER.get();
                BlockPos returnPos = baseTargetPos;

                if (!targetLevel.getBlockState(returnPos).is(returnBlock)) {
                    BlockPos above = baseTargetPos.above();
                    if (targetLevel.getBlockState(above).is(returnBlock)) {
                        returnPos = above;
                    } else {
                        BlockState at = targetLevel.getBlockState(returnPos);
                        if (at.isAir() || at.canBeReplaced()) {
                            targetLevel.setBlockAndUpdate(returnPos, returnBlock.defaultBlockState());
                        } else {
                            BlockState atAbove = targetLevel.getBlockState(above);
                            if (atAbove.isAir() || atAbove.canBeReplaced()) {
                                targetLevel.setBlockAndUpdate(above, returnBlock.defaultBlockState());
                                returnPos = above;
                            } else {
                                return false; // blocked at both positions
                            }
                        }
                    }
                }

                BlockEntity be = targetLevel.getBlockEntity(returnPos);
                if (!(be instanceof TeleporterBlockEntity targetTeleporter)) {
                    return false;
                }

                // Link return teleporter back to THIS origin teleporter
                targetTeleporter.linkedTeleporter = new TeleportMarkerData(sourcePos, sourceLevel.dimension(), block);
                targetTeleporter.setChanged();

                targetTeleporterPos = targetTeleporter.getBlockPos();
            }

            // Store forward link (this origin teleporter -> return teleporter in target dimension)
            this.linkedTeleporter = new TeleportMarkerData(
                    targetTeleporterPos,
                    targetDim,
                    ModBlocks.OVERWORLD_RETURN_TELEPORTER.get()
            );
            this.setChanged();

        } else {
            // Return trip (dimension -> overworld)
            //
            // Rule: origin teleporters should NOT fail with "link lost".
            // If marker is missing/stale (e.g. teleporter moved), fall back to returning to the same XY/Z in overworld
            // without recreating blocks.
            if (linkedTeleporter != null) {
                if (validateLinkedMarker(sourceLevel)) {
                    targetTeleporterPos = linkedTeleporter.getTargetPos();
                } else {
                    // stale marker cleared by validateLinkedMarker
                    targetTeleporterPos = sourcePos;
                }
            } else {
                targetTeleporterPos = sourcePos;
            }

            // IMPORTANT: Do NOT place/recreate the origin teleporter in the overworld.
        }

        // Arrival logic
        BlockPos arrival = findArrivalOffset(targetLevel, targetTeleporterPos.above());

        player.teleportTo(
                targetLevel,
                arrival.getX() + 0.5,
                arrival.getY(),
                arrival.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );

        targetLevel.playSound(null, arrival, ModSounds.TELEPORTER_ACTIVATE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        return true;
    }

    private Optional<TeleporterBlockEntity> findReturnTeleporterLinkedToThis(
            ServerLevel level,
            BlockPos center,
            Block returnTeleporterBlock,
            int radius,
            ResourceKey<Level> expectedTargetDim,
            BlockPos expectedTargetPos,
            Block expectedSourceBlock
    ) {
        for (BlockPos pos : BlockPos.withinManhattan(center, radius, radius, radius)) {
            if (!level.getBlockState(pos).is(returnTeleporterBlock)) continue;

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof TeleporterBlockEntity tbe)) continue;

            TeleportMarkerData marker = tbe.linkedTeleporter;
            if (marker == null) continue;

            if (!marker.getTargetDimension().equals(expectedTargetDim)) continue;
            if (!marker.getTargetPos().equals(expectedTargetPos)) continue;
            if (marker.getSourceBlock() != expectedSourceBlock) continue;

            return Optional.of(tbe);
        }
        return Optional.empty();
    }

    private static final Map<Block, TeleporterRules> TELEPORT_RULES = Map.of(
            ModBlocks.DEEP_BENEATH_TELEPORTER.get(), new TeleporterRules(
                    DimensionExpansionDimensions.DEEP_BENEATH,
                    true,
                    (level, base) -> new BlockPos(base.getX(), 150, base.getZ()),
                    StructureBuilder::buildDeepBeneathPlatform
            ),

            ModBlocks.MINING_TELEPORTER.get(), new TeleporterRules(
                    DimensionExpansionDimensions.MINING,
                    true,
                    (level, base) -> findSurfacePosition(level, base.getX(), base.getZ()),
                    StructureBuilder::buildMiningPlatform
            ),

            ModBlocks.STONE_BLOCK_TELEPORTER.get(), new TeleporterRules(
                    DimensionExpansionDimensions.STONE_BLOCK,
                    true,
                    (level, base) -> new BlockPos(base.getX(), 64, base.getZ()),
                    StructureBuilder::buildStoneBlockPlatform
            )
    );

    public static BlockPos findSurfacePosition(ServerLevel level, int x, int z) {
        int y = level.getChunkSource()
                .getGenerator()
                .getFirstFreeHeight(
                        x, z,
                        Heightmap.Types.WORLD_SURFACE,
                        level,
                        level.getChunkSource().randomState()
                );
        return new BlockPos(x, y, z);
    }

    private BlockPos findArrivalOffset(Level level, BlockPos center) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos offset = center.relative(dir);
            BlockState s = level.getBlockState(offset);
            if (s.isAir() || s.canBeReplaced()) {
                return offset;
            }
        }
        return center; // fallback to directly above the teleporter
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        if (linkedTeleporter != null) {
            tag.put("linked_teleporter", linkedTeleporter.save());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        if (tag.contains("linked_teleporter", Tag.TAG_COMPOUND)) {
            linkedTeleporter = TeleportMarkerData.load(tag.getCompound("linked_teleporter"));
        } else {
            linkedTeleporter = null;
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookup) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, lookup);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookup, CompoundTag tag) {
        this.loadAdditional(tag, lookup);
    }

    public TeleportMarkerData getLinkedTeleporter() {
        return linkedTeleporter;
    }

    public void clearLinkedTeleporter() {
        linkedTeleporter = null;
        setChanged();
    }
}