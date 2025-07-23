package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.util.UpsideDownPortalShape;
import com.lithiumcraft.dimension_expansion.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID)
public class UpsideDownPortalEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide()) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getEntity() instanceof Animal)) return;

        BlockPos deathPos = event.getEntity().blockPosition();
        DimensionExpansion.LOGGER.debug("Mob killed at {}", deathPos);

        for (BlockPos checkPos : BlockPos.betweenClosed(deathPos.offset(-1, -1, -1), deathPos.offset(1, 1, 1))) {
            if (level.getBlockState(checkPos).is(ModTags.Blocks.UPSIDE_DOWN_PORTAL_FRAME_BLOCKS)) {
                if (UpsideDownPortalShape.detectAndLightPortal(level, checkPos.above())) {
                    DimensionExpansion.LOGGER.debug("Successfully lit portal near mob death at {}", checkPos);
                    return;
                }
            }
        }

        DimensionExpansion.LOGGER.debug("No valid portal frame found near mob death at {}", deathPos);
    }
}
