package com.lithiumcraft.dimension_expansion.util;

import com.google.common.collect.ImmutableSet;
import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.block.property.WoodType;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Comparator;
import java.util.Optional;

@EventBusSubscriber(bus = Bus.MOD)
public class UpsideDownPortalTeleporter {

    public static Holder<PoiType> POI = null;
    private final ServerLevel level;

    BlockState OAK_FRAME_STATE = ModBlocks.UPSIDE_DOWN_PORTAL_FRAMES
            .get(WoodType.OAK)
            .get()
            .defaultBlockState();

    @SubscribeEvent
    public static void registerPoiType(RegisterEvent event) {
        event.register(Registries.POINT_OF_INTEREST_TYPE, helper -> {
            PoiType poiType = new PoiType(
                    ImmutableSet.copyOf(ModBlocks.UPSIDE_DOWN_PORTAL_BLOCK.get().getStateDefinition().getPossibleStates()), 0, 1
            );
            helper.register(ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "upside_down_portal"), poiType);
            POI = BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(poiType);
        });
    }

    public UpsideDownPortalTeleporter(ServerLevel level) {
        this.level = level;
    }

    public Optional<BlockPos> findClosestPortalPosition(BlockPos origin, boolean isNear, WorldBorder border) {
        PoiManager poiManager = level.getPoiManager();
        int searchRadius = isNear ? 16 : 128;

        poiManager.ensureLoadedAndValid(level, origin, searchRadius);

        return poiManager
                .getInSquare(record -> record.is(POI.unwrapKey().get()), origin, searchRadius, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos)
                .filter(border::isWithinBounds)
                .filter(pos -> level.getBlockState(pos).hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                .min(Comparator.<BlockPos>comparingDouble(pos -> pos.distSqr(origin)).thenComparingInt(BlockPos::getY));
    }

    public Optional<FoundRectangle> createPortal(BlockPos origin, Direction.Axis axis) {
        Direction forward = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        WorldBorder border = level.getWorldBorder();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        // Step 1: resolve surface position
        BlockPos surface = resolveSurface(level, origin).above(); // guarantee on top of terrain
        BlockPos frameOrigin = surface.relative(forward.getOpposite());
//        frameOrigin = border.clampToBounds(frameOrigin);
        if (frameOrigin.getY() + 4 >= level.getMaxBuildHeight()) {
            frameOrigin = new BlockPos(frameOrigin.getX(), level.getMaxBuildHeight() - 5, frameOrigin.getZ());
        }

        // Step 2: ensure the portal fits at this height
        int maxY = level.getMaxBuildHeight();
        if (frameOrigin.getY() + 4 >= maxY) {
            frameOrigin = new BlockPos(frameOrigin.getX(), maxY - 5, frameOrigin.getZ());
        }

        // Step 3: build 4x5 frame (including floor and ceiling)
        for (int dx = -1; dx < 3; dx++) {
            for (int dy = -1; dy < 4; dy++) {
                if (dx == -1 || dx == 2 || dy == -1 || dy == 3) {
                    cursor.setWithOffset(frameOrigin, dx * forward.getStepX(), dy, dx * forward.getStepZ());
                    level.setBlock(cursor, OAK_FRAME_STATE, Block.UPDATE_ALL);
                }
            }
        }

        // Step 4: fill 2x3 interior with portal blocks
        BlockState portalState = ModBlocks.UPSIDE_DOWN_PORTAL_BLOCK.get()
                .defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, axis);

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                cursor.setWithOffset(frameOrigin, x * forward.getStepX(), y, x * forward.getStepZ());
                level.setBlock(cursor, portalState, Block.UPDATE_ALL);
                level.getPoiManager().add(cursor, POI);
            }
        }

        return Optional.of(new FoundRectangle(frameOrigin, 2, 3));
    }


    private boolean canHostFrame(BlockPos origin, Direction forward, int offset) {
        Direction right = forward.getClockWise();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = -1; x < 3; x++) {
            for (int y = -1; y < 4; y++) {
                cursor.setWithOffset(origin, forward.getStepX() * x + right.getStepX() * offset, y, forward.getStepZ() * x + right.getStepZ() * offset);

                BlockState state = level.getBlockState(cursor);
                if (y < 0 && !state.isSolid()) return false;
                if (y >= 0 && (!state.canBeReplaced() || !state.getFluidState().isEmpty())) return false;
            }
        }

        return true;
    }

    private boolean canPortalReplace(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced() && state.getFluidState().isEmpty();
    }

    public static BlockPos resolveSurface(ServerLevel level, BlockPos origin) {
        // Force-load the chunk before querying height
        level.getChunk(origin); // ensures heightmap is valid

        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ());

        // Clamp very low positions
        y = Mth.clamp(y, level.getMinBuildHeight() + 4, level.getMaxBuildHeight() - 5);

        return new BlockPos(origin.getX(), y, origin.getZ());
    }


}
