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

package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.blockentity.TeleporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID)
public class ServerEvents {

    /** Built once; this handler runs on every right-click in the game. */
    private static final TagKey<Block> TELEPORTER_BLOCKS =
            BlockTags.create(DimensionExpansion.rl("teleporter_blocks"));

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (!state.is(TELEPORTER_BLOCKS)) return;

        // The client has to consume the interaction as well, or the arm never swings and the
        // click falls through to placing whatever is in hand. This handler used to return early
        // on the client, so a teleporter gave no feedback at all when clicked.
        if (level.isClientSide()) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof TeleporterBlockEntity teleporter)) return;

        boolean success = teleporter.teleport(serverPlayer);

        event.setCanceled(true);
        event.setCancellationResult(success ? InteractionResult.SUCCESS : InteractionResult.FAIL);

        if (!success) {
            // Previously only the return teleporter said anything, so an origin teleporter that
            // could not place its destination simply did nothing with no explanation.
            boolean isReturn = state.is(ModBlocks.OVERWORLD_RETURN_TELEPORTER.get());
            level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
            serverPlayer.displayClientMessage(
                    Component.literal(isReturn
                            ? "Teleporter link lost."
                            : "Cannot open a way through -- the destination is blocked."),
                    true
            );
        }
    }
}