package com.lithiumcraft.dimension_expansion.blockentity;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Optional;

public class TeleporterBlockEntity extends BlockEntity implements INBTSerializable<CompoundTag> {
    private BlockPos linkedPos = null;
    private String linkedDim = null;

    public TeleporterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private static final Map<Block, ResourceKey<Level>> TELEPORT_TARGETS = Map.of(
            ModBlocks.DEEP_BENEATH_TELEPORTER.get(), DimensionExpansionDimensions.DEEP_BENEATH,
            ModBlocks.MINING_TELEPORTER.get(), DimensionExpansionDimensions.MINING,
            ModBlocks.STONE_BLOCK_TELEPORTER.get(), DimensionExpansionDimensions.STONE_BLOCK
    );

    public static void tickServer(Level level, BlockPos pos, BlockState state, TeleporterBlockEntity be) {
        // No active ticking needed unless you want portal particles or cooldown
    }

    private TeleportMarkerData linkedTeleporter = null;

    public void teleport(ServerPlayer player) {
        if (player == null || level == null || level.isClientSide || !(level instanceof ServerLevel sourceLevel)) return;

        MinecraftServer server = sourceLevel.getServer();
        ResourceKey<Level> sourceDim = sourceLevel.dimension();
        BlockPos sourcePos = this.getBlockPos();

        Block block = getBlockState().getBlock();
        BlockState linkedBlockState = linkedTeleporter != null
                ? linkedTeleporter.getSourceBlock().defaultBlockState()
                : null;

        // Return teleporter: always uses linked data
        if (block == ModBlocks.OVERWORLD_RETURN_TELEPORTER.get()) {
            if (linkedTeleporter == null) {
                player.sendSystemMessage(Component.literal("This return teleporter is not linked."));
                return;
            }

            BlockPos targetPos = linkedTeleporter.getTargetPos();
            ResourceKey<Level> targetDim = linkedTeleporter.getTargetDimension();
            ServerLevel targetLevel = server.getLevel(targetDim);
            if (targetLevel == null) return;

            BlockState atTarget = targetLevel.getBlockState(targetPos);
            if ((atTarget.isAir() || atTarget.canBeReplaced()) && linkedBlockState != null) {
                targetLevel.setBlockAndUpdate(targetPos, linkedBlockState);
            }

            BlockPos arrival = targetPos.above();
            player.teleportTo(
                    targetLevel,
                    arrival.getX() + 0.5,
                    arrival.getY(),
                    arrival.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
            );
            return;
        }

        // Dimensional teleporter
        TeleporterRules rules = TELEPORT_RULES.get(block);
        if (rules == null) {
            System.err.println("Missing TeleporterRules for block: " + block);
            player.sendSystemMessage(Component.literal("This teleporter is not linked to a dimension."));
            return;
        }

        ResourceKey<Level> targetDim = rules.targetDimension();
        boolean goingToTarget = !sourceDim.equals(targetDim);
        ServerLevel targetLevel = server.getLevel(targetDim);
        if (targetLevel == null) return;

        BlockPos targetTeleporterPos;

        if (goingToTarget) {
            BlockPos baseTargetPos = rules.resolveArrival(targetLevel, sourcePos);
            targetTeleporterPos = baseTargetPos;

            // Try to reuse nearby return teleporter
            Optional<TeleporterBlockEntity> nearby = findLinkedTeleporterNearby(
                    targetLevel, baseTargetPos, ModBlocks.OVERWORLD_RETURN_TELEPORTER.get(), 48
            );

            if (nearby.isPresent()) {
                targetTeleporterPos = nearby.get().getBlockPos();
            } else {
                // Build platform + return teleporter
                rules.maybeBuildPlatform(targetLevel, baseTargetPos, ModBlocks.OVERWORLD_RETURN_TELEPORTER.get());

                // Link return teleporter back to this block
                BlockEntity be = targetLevel.getBlockEntity(baseTargetPos);
                if (!(be instanceof TeleporterBlockEntity)) {
                    be = targetLevel.getBlockEntity(baseTargetPos.above());
                }
                if (be instanceof TeleporterBlockEntity targetTeleporter) {
                    targetTeleporter.linkedTeleporter = new TeleportMarkerData(sourcePos, sourceLevel.dimension(), block);
                    targetTeleporter.setChanged();
                }
            }

            // Store forward link to destination
            this.linkedTeleporter = new TeleportMarkerData(targetTeleporterPos, targetDim, ModBlocks.OVERWORLD_RETURN_TELEPORTER.get());
            this.setChanged();

        } else {
            // Return trip
            targetTeleporterPos = linkedTeleporter != null
                    ? linkedTeleporter.getTargetPos()
                    : sourcePos;

            if (!targetLevel.getBlockState(targetTeleporterPos).is(block)) {
                targetLevel.setBlockAndUpdate(targetTeleporterPos, block.defaultBlockState());
            }
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
    }

    private Optional<TeleporterBlockEntity> findLinkedTeleporterNearby(ServerLevel level, BlockPos center, Block returnTeleporterBlock, int radius) {
        for (BlockPos pos : BlockPos.withinManhattan(center, radius, radius, radius)) {
            if (!level.getBlockState(pos).is(returnTeleporterBlock)) continue;
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TeleporterBlockEntity tbe) {
                return Optional.of(tbe); // accept any match
            }
        }
        return Optional.empty();
    }

    private static final Map<Block, TeleporterRules> TELEPORT_RULES = Map.of(
            ModBlocks.DEEP_BENEATH_TELEPORTER.get(), new TeleporterRules(
                    DimensionExpansionDimensions.DEEP_BENEATH,
                    true,
                    (level, base) -> new BlockPos(base.getX(), 256, base.getZ()),
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
                    (level, base) -> new BlockPos(base.getX(), 192, base.getZ()),
                    StructureBuilder::buildStoneBlockPlatform
            )
    );

    public static BlockPos findSurfacePosition(ServerLevel level, int x, int z) {
        int y = level.getChunkSource()
                .getGenerator()
                .getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE, level, level.getChunkSource().randomState());
        return new BlockPos(x, y, z);
    }


    private BlockPos findArrivalOffset(Level level, BlockPos center) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos offset = center.relative(dir);
            if (level.getBlockState(offset).isAir() || level.getBlockState(offset).canBeReplaced()) {
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
}
