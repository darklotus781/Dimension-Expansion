package com.lithiumcraft.dimension_expansion.util;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public final class TorchReplacementHelper {
    private TorchReplacementHelper() {}

    // --- config / small caches ---
    private static final Long2LongOpenHashMap LAST_MSG = new Long2LongOpenHashMap();
    private static final long MSG_COOLDOWN_MS = 500L;

    private static final Set<Block> ALLOWED_VANILLA_TORCHES = Set.of(
            Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH
    );

    // --- dimension gate ---
    public static boolean shouldReplaceIn(Level level) {
        var dim = level.dimension();
        return dim.equals(DimensionExpansionDimensions.DEEP_BENEATH)
                || dim.equals(DimensionExpansionDimensions.UPSIDE_DOWN);
    }

    // --- predicates used by the mixin ---
    /** Our own burned-out torches; always allowed/pass-through. */
    public static boolean isOurTorch(BlockState s) {
        Block b = s.getBlock();
        return b == ModBlocks.BURNABLE_TORCH.get() || b == ModBlocks.BURNABLE_WALL_TORCH.get()
                || b == ModBlocks.BURNED_OUT_TORCH.get() || b == ModBlocks.BURNED_OUT_WALL_TORCH.get();
    }

    /** Vanilla torch types that we allow and then rewrite to our variants. */
    public static boolean isAllowedVanillaTorch(BlockState s) {
        return ALLOWED_VANILLA_TORCHES.contains(s.getBlock());
    }

    /** Disallowed: any TorchBlock that is NOT one of the allowed vanilla ones (catches modded torches). */
    public static boolean isDisallowedTorch(BlockState s) {
        if (isOurTorch(s)) return false; // whitelist ours
        Block b = s.getBlock();
        return b instanceof TorchBlock && !isAllowedVanillaTorch(s);
    }

    /** Disallowed: any lantern (vanilla or modded) and Sea Lantern. */
    public static boolean isLanternLike(BlockState s) {
        Block b = s.getBlock();
        return b instanceof LanternBlock || b == Blocks.LANTERN || b == Blocks.SOUL_LANTERN || b == Blocks.SEA_LANTERN;
    }

    // --- mapping and rewrite helpers ---
    /** Map allowed vanilla torch → our burned-out variants (preserves FACING for wall torches). */
    public static BlockState mapAllowedVanillaTorch(BlockState state) {
        Block b = state.getBlock();
        if (b == Blocks.WALL_TORCH) {
            Direction facing = state.hasProperty(WallTorchBlock.FACING)
                    ? state.getValue(WallTorchBlock.FACING) : Direction.NORTH;
            return ModBlocks.BURNABLE_WALL_TORCH.get().defaultBlockState().setValue(WallTorchBlock.FACING, facing);
        }
        if (b == Blocks.TORCH) {
            return ModBlocks.BURNABLE_TORCH.get().defaultBlockState();
        }
        return state;
    }

    /** Used by @ModifyVariable to keep vanilla pipeline intact (no cancel). */
    public static BlockState rewriteIfAllowed(Level level, BlockState state) {
        if (!shouldReplaceIn(level)) return state;
        if (isOurTorch(state)) return state; // already ours
        return isAllowedVanillaTorch(state) ? mapAllowedVanillaTorch(state) : state;
    }

    // --- UX helper (actionbar), with simple per-pos cooldown to prevent spam ---
    public static void notifyNearestPlayer(Level level, BlockPos pos, Component msg) {
        if (!(level instanceof ServerLevel sl)) return;
        long now = System.currentTimeMillis();
        long key = pos.asLong();
        long last = LAST_MSG.getOrDefault(key, 0L);
        if (now - last < MSG_COOLDOWN_MS) return;
        LAST_MSG.put(key, now);

        Player p = sl.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6.0, false);
        if (p != null) p.displayClientMessage(msg, true);
    }

    public static Component defaultBlockedMessage() {
        return Component.translatable("message.dimension_expansion.light_blocked");
        // lang: "message.dimension_expansion.light_blocked": "That light source doesn't work down here."
    }
}
