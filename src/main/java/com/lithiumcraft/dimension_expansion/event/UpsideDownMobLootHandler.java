package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

import java.util.ArrayList;
import java.util.List;

public class UpsideDownMobLootHandler {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (!level.isClientSide && level.dimension().equals(DimensionExpansionDimensions.UPSIDE_DOWN)) {
            List<ItemEntity> originalDrops = new ArrayList<>(event.getDrops());

            for (ItemEntity drop : originalDrops) {
                ItemStack stack = drop.getItem();

                // Skip equipment and other items that only stack to 1
                if (stack.getMaxStackSize() == 1)
                    continue;

                ItemStack copy = stack.copy();
                ItemEntity duplicate = new ItemEntity(
                        level,
                        drop.getX(), drop.getY(), drop.getZ(),
                        copy
                );
                event.getDrops().add(duplicate);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Level level = event.getEntity().level();

        if (!level.isClientSide && level.dimension().equals(DimensionExpansionDimensions.UPSIDE_DOWN)) {
            int originalXp = event.getOriginalExperience();
            event.setDroppedExperience(originalXp * 2);
        }
    }
}
