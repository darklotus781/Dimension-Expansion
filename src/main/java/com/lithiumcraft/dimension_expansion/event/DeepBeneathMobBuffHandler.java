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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID)
public class DeepBeneathMobBuffHandler {

    /** Modifiers are keyed by id and saved with the entity, so re-applying is a no-op. */
    private static final ResourceLocation HEALTH_BUFF_ID =
            ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "deep_beneath_health");

    /** Older versions multiplied the base value in place and set this flag. */
    private static final String LEGACY_BUFF_FLAG = "deep_beneath.buff_applied";

    @SubscribeEvent
    public static void onMobSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = event.getLevel();

        if (!(entity instanceof Mob mob) || level.isClientSide()) return;

// Only apply in deep_beneath
        if (!level.dimension().equals(DimensionExpansionDimensions.DEEP_BENEATH)) return;

// Only apply to monsters
        if (mob.getType().getCategory() != MobCategory.MONSTER) return;

// Modify Stats
        AttributeInstance healthAttr = mob.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            // ADD_MULTIPLIED_BASE adds amount * base, so 3 quadruples and 1 doubles.
            double bonus = mob.getType() == EntityType.ZOMBIE ? 3.0D : 1.0D;

            // Undo the old baked-in multiplier, or the modifier stacks on top of it.
            CompoundTag tag = mob.getPersistentData();
            boolean migrated = tag.getBoolean(LEGACY_BUFF_FLAG);
            if (migrated) {
                tag.remove(LEGACY_BUFF_FLAG);
                healthAttr.setBaseValue(healthAttr.getBaseValue() / (bonus + 1.0D));
            }

            if (!healthAttr.hasModifier(HEALTH_BUFF_ID)) {
                healthAttr.addPermanentModifier(new AttributeModifier(
                        HEALTH_BUFF_ID, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                // A migrated mob keeps its max health, so healing it is a free top-up each load.
                if (!migrated) mob.setHealth(mob.getMaxHealth());
            }
        }
        // Absolute values, so setting them again is a no-op.
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
