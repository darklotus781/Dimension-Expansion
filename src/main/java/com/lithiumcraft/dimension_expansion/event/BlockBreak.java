package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.Config;
import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID)
public class BlockBreak {

    @SubscribeEvent
    public static void on(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getPlayer().isCreative() || event.getPlayer().isSpectator()) return;
        if (!Config.denyFakePlayerDeepBeneath) return;

        // Only apply restriction in DEEP_BENEATH
        if (event.getPlayer().level().dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) {
            if (event.getPlayer() instanceof FakePlayer) {
                event.setCanceled(true);
            }
        }
    }
}