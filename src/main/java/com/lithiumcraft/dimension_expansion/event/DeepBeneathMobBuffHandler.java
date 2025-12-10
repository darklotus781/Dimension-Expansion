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
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID)
public class DeepBeneathMobBuffHandler {

//    @SubscribeEvent
//    public static void onMobSpawn(EntityJoinLevelEvent event) {
//        Entity entity = event.getEntity();
//        Level level = event.getLevel();
//
//        if (!(entity instanceof Mob mob) || level.isClientSide()) return;
//        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;
//
//        // Double max health
//        AttributeInstance healthAttr = mob.getAttribute(Attributes.MAX_HEALTH);
//        if (healthAttr != null && healthAttr.getBaseValue() < 2048.0D) { // avoid stacking
//            double original = healthAttr.getBaseValue();
//            healthAttr.setBaseValue(original * 2);
//            mob.setHealth((float) healthAttr.getValue()); // heal to full
//        }
//
//        // Apply Resistance effect (Level 1 = 20% damage reduction)
//        if (mob instanceof LivingEntity living) {
//            if (entity.getType() == EntityType.GHAST) {
//                living.setCustomName(Component.literal("Overseer"));
//                living.setCustomNameVisible(false); // optional: always show
//            }
//            if (entity.getType() == EntityType.PIGLIN_BRUTE) {
//                living.setCustomName(Component.literal("Protector of the Deep"));
//                living.setCustomNameVisible(false); // optional: always show
//            }
//            if (entity.getType() == EntityType.VINDICATOR) {
//                living.setCustomName(Component.literal("Guardian of the Shadows"));
//                living.setCustomNameVisible(false); // optional: always show
//            }
//            if (entity.getType() == EntityType.ILLUSIONER) {
//                living.setCustomName(Component.literal("Wizard"));
//                living.setCustomNameVisible(false); // optional: always show
//            }
//
//            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
//        }
//    }

    @SubscribeEvent
    public static void onMobSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = event.getLevel();

        if (!(entity instanceof Mob mob) || level.isClientSide()) return;

// Only apply in deep_beneath
        ResourceKey<Level> dim = level.dimension();
        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

// Only apply to monsters
        if (mob.getType().getCategory() != MobCategory.MONSTER) return;

// Use persistent data to avoid reapplying buffs
        CompoundTag tag = mob.getPersistentData();
        if (tag.getBoolean("deep_beneath.buff_applied")) return;
        tag.putBoolean("deep_beneath.buff_applied", true);

// Modify Stats
        AttributeInstance healthAttr = mob.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            double original = healthAttr.getBaseValue();
            if (mob.getType() == EntityType.ZOMBIE) {
                healthAttr.setBaseValue(original * 4);
            } else {
                healthAttr.setBaseValue(original * 2);
            }
            mob.setHealth((float) healthAttr.getValue());
        }
        AttributeInstance followRange = mob.getAttribute(Attributes.FOLLOW_RANGE);
        AttributeInstance knockbackResistance = mob.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (followRange != null) {
            // Double their range, or set to a hard value
            followRange.setBaseValue(80.0D); // default ~35
        }
        if (knockbackResistance != null)
        {
            // Eliminate Knockback
            knockbackResistance.setBaseValue(1.0D);
        }
        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));

        if (entity.getType() == EntityType.GHAST) {
            mob.setCustomName(Component.literal("Overseer"));
            mob.setCustomNameVisible(false); // optional: always show
        }
        if (entity.getType() == EntityType.PIGLIN_BRUTE) {
            mob.setCustomName(Component.literal("Protector of the Deep"));
            mob.setCustomNameVisible(false); // optional: always show
        }
        if (entity.getType() == EntityType.VINDICATOR) {
            mob.setCustomName(Component.literal("Guardian of the Shadows"));
            mob.setCustomNameVisible(false); // optional: always show
        }
        if (entity.getType() == EntityType.ILLUSIONER) {
            mob.setCustomName(Component.literal("Wizard"));
            mob.setCustomNameVisible(false); // optional: always show
        }
    }
}
