package com.lithiumcraft.dimension_expansion.util;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.registry.ModSounds;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.Optional;
import java.util.function.Predicate;

public class UpsideDownPortalShape {
    public static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    public static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 21;

    private static final StatePredicate FRAME = (state, level, pos) ->
            state.is(ModTags.Blocks.UPSIDE_DOWN_PORTAL_FRAME_BLOCKS);
    private static final Block PORTAL_BLOCK = ModBlocks.UPSIDE_DOWN_PORTAL_BLOCK.get();

    private final LevelAccessor level;
    private final Axis axis;
    private final Direction rightDir;

    private int width;
    private int height;
    private int numPortalBlocks;
    private BlockPos bottomLeft;

    public UpsideDownPortalShape(LevelAccessor level, BlockPos origin, Axis axis) {
        this.level = level;
        this.axis = axis;
        this.rightDir = axis == Axis.X ? Direction.WEST : Direction.SOUTH;

        this.bottomLeft = calculateBottomLeft(origin);

        if (this.bottomLeft != null) {
            this.width = calculateWidth();
            this.height = (this.width > 0) ? calculateHeight() : 0;
        } else {
            this.width = 1;
            this.height = 1;
        }
    }

    public static Optional<UpsideDownPortalShape> findEmptyPortalShape(LevelAccessor level, BlockPos pos, Axis axis) {
        return findPortalShape(level, pos, shape -> shape.isValid() && shape.numPortalBlocks == 0, axis);
    }

    public static Optional<UpsideDownPortalShape> findPortalShape(LevelAccessor level, BlockPos pos, Predicate<UpsideDownPortalShape> predicate, Axis axis) {
        Optional<UpsideDownPortalShape> original = Optional.of(new UpsideDownPortalShape(level, pos, axis)).filter(predicate);
        if (original.isPresent()) return original;

        Axis alternate = (axis == Axis.X) ? Axis.Z : Axis.X;
        return Optional.of(new UpsideDownPortalShape(level, pos, alternate)).filter(predicate);
    }

    private BlockPos calculateBottomLeft(BlockPos pos) {
        int minY = Math.max(this.level.getMinBuildHeight(), pos.getY() - MAX_HEIGHT);
        while (pos.getY() > minY && isEmpty(level.getBlockState(pos.below()))) {
            pos = pos.below();
        }

        Direction backward = this.rightDir.getOpposite();
        int offset = getDistanceUntilFrame(pos, backward) - 1;
        return offset < 0 ? null : pos.relative(backward, offset);
    }

    private int calculateWidth() {
        int w = getDistanceUntilFrame(this.bottomLeft, this.rightDir);
        return (w >= MIN_WIDTH && w <= MAX_WIDTH) ? w : 0;
    }

    private int calculateHeight() {
        MutableBlockPos cursor = new MutableBlockPos();
        int h = getDistanceUntilTop(cursor);
        return (h >= MIN_HEIGHT && h <= MAX_HEIGHT && hasTopFrame(cursor, h)) ? h : 0;
    }

    private int getDistanceUntilFrame(BlockPos origin, Direction dir) {
        MutableBlockPos cursor = new MutableBlockPos();

        for (int i = 0; i <= MAX_WIDTH; i++) {
            cursor.set(origin).move(dir, i);
            BlockState state = level.getBlockState(cursor);
            if (!isEmpty(state)) {
                if (FRAME.test(state, level, cursor)) return i;
                break;
            }

            BlockState below = level.getBlockState(cursor.move(Direction.DOWN));
            if (!FRAME.test(below, level, cursor)) break;
        }
        return 0;
    }

    private int getDistanceUntilTop(MutableBlockPos cursor) {
        for (int y = 0; y < MAX_HEIGHT; y++) {
            cursor.set(bottomLeft).move(Direction.UP, y).move(rightDir, -1);
            if (!FRAME.test(level.getBlockState(cursor), level, cursor)) return y;

            cursor.set(bottomLeft).move(Direction.UP, y).move(rightDir, width);
            if (!FRAME.test(level.getBlockState(cursor), level, cursor)) return y;

            for (int x = 0; x < width; x++) {
                cursor.set(bottomLeft).move(Direction.UP, y).move(rightDir, x);
                BlockState state = level.getBlockState(cursor);
                if (!isEmpty(state)) return y;
                if (state.getBlock() == PORTAL_BLOCK) numPortalBlocks++;
            }
        }
        return MAX_HEIGHT;
    }

    private boolean hasTopFrame(MutableBlockPos cursor, int height) {
        for (int x = 0; x < width; x++) {
            cursor.set(bottomLeft).move(Direction.UP, height).move(rightDir, x);
            if (!FRAME.test(level.getBlockState(cursor), level, cursor)) return false;
        }
        return true;
    }

    public void createPortalBlocks() {
        BlockState portalState = PORTAL_BLOCK.defaultBlockState().setValue(NetherPortalBlock.AXIS, axis);
        BlockPos topRight = bottomLeft.relative(Direction.UP, height - 1).relative(rightDir, width - 1);
        BlockPos.betweenClosed(bottomLeft, topRight).forEach(pos -> {
            level.setBlock(pos, portalState, Block.UPDATE_ALL);
        });
    }

    public boolean isValid() {
        return bottomLeft != null && width >= MIN_WIDTH && width <= MAX_WIDTH && height >= MIN_HEIGHT && height <= MAX_HEIGHT;
    }

    public boolean isComplete() {
        return isValid() && numPortalBlocks == width * height;
    }

    public static Vec3 getRelativePosition(FoundRectangle rect, Axis axis, Vec3 pos, EntityDimensions size) {
        double w = rect.axis1Size - size.width();
        double h = rect.axis2Size - size.height();
        BlockPos corner = rect.minCorner;

        double dx = (w > 0) ? Mth.clamp(Mth.inverseLerp(pos.get(axis) - (corner.get(axis) + size.width() / 2), 0.0, w), 0.0, 1.0) : 0.5;
        double dy = (h > 0) ? Mth.clamp(Mth.inverseLerp(pos.y - corner.getY(), 0.0, h), 0.0, 1.0) : 0.0;

        Axis other = axis == Axis.X ? Axis.Z : Axis.X;
        double dz = pos.get(other) - (corner.get(other) + 0.5);
        return new Vec3(dx, dy, dz);
    }

    public static Vec3 findCollisionFreePosition(Vec3 target, ServerLevel level, Entity entity, EntityDimensions size) {
        if (size.width() > 4.0F || size.height() > 4.0F) return target;

        Vec3 elevated = target.add(0, size.height() / 2.0, 0);
        AABB box = AABB.ofSize(elevated, size.width(), 0.0, size.width()).expandTowards(0, 1.0, 0).inflate(1.0E-6);
        Optional<Vec3> free = level.findFreePosition(entity, Shapes.create(box), elevated, size.width(), size.height(), size.width());
        return free.map(pos -> pos.subtract(0, size.height() / 2.0, 0)).orElse(target);
    }

    private static boolean isEmpty(BlockState state) {
        return state.isAir() || state.getBlock() == PORTAL_BLOCK;
    }

    public static boolean detectAndLightPortal(Level level, BlockPos origin) {
        for (Direction.Axis axis : Direction.Axis.values()) {
            Optional<UpsideDownPortalShape> shape = UpsideDownPortalShape.findEmptyPortalShape(level, origin, axis);
            if (shape.isPresent()) {
                shape.get().createPortalBlocks();
                level.playSound(null, origin, ModSounds.UPSIDE_DOWN_DIMENSION_PORTAL_ACTIVATE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                DimensionExpansion.LOGGER.debug("Portal lit at {} along axis {}", origin, axis);
                return true;
            }
        }

        DimensionExpansion.LOGGER.debug("No valid portal frame found at {}", origin);
        return false;
    }
}
