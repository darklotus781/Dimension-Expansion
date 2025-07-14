package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Iterator;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DeepBeneathMobLootHandler {

    @SubscribeEvent
    public static void onMobDrops(LivingDropsEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

        LivingEntity entity = event.getEntity();

        // Ghast: drop 1–3 blaze powder
        if (entity.getType() == EntityType.GHAST) {
            Iterator<ItemEntity> it = event.getDrops().iterator();
            while (it.hasNext()) {
                ItemEntity drop = it.next();
                if (drop.getItem().getItem() == Items.GHAST_TEAR) {
                    it.remove(); // Remove the drop
                }
            }
            int count = 1 + level.random.nextInt(3);
            event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(Items.BLAZE_POWDER, count)));
        }

        // Cave Spider: drop 1–2 fermented spider eyes
        else if (entity.getType() == EntityType.CAVE_SPIDER) {
            int count = 1 + level.random.nextInt(2);
            event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(Items.FERMENTED_SPIDER_EYE, count)));
        }

        // Add more entity types here...
    }
}
