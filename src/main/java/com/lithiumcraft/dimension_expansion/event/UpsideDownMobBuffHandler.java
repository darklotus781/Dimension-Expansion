package com.lithiumcraft.dimension_expansion.event;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.worldgen.DimensionExpansionDimensions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = DimensionExpansion.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class UpsideDownMobBuffHandler {

    @SubscribeEvent
    public static void onMobSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = event.getLevel();

        if (!(entity instanceof Mob mob) || level.isClientSide()) return;

        // Only apply in upside_down or deep_beneath
        ResourceKey<Level> dim = level.dimension();
        if (!dim.equals(DimensionExpansionDimensions.UPSIDE_DOWN)) return;

        // Only apply to monsters (not villagers, golems, etc.)
        if (mob.getType().getCategory() != MobCategory.MONSTER) return;

        // Double max health (once)
        AttributeInstance healthAttr = mob.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null && healthAttr.getBaseValue() < 2048.0D) {
            double original = healthAttr.getBaseValue();
            healthAttr.setBaseValue(original * 1.5);
            mob.setHealth((float) healthAttr.getValue());
        }

        // Permanent resistance effect
//        if (entity instanceof LivingEntity living) {
//            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
//        }

        // Equip armor and weapons
        equipMob(mob, level.random, level);
    }

    private static final List<Item> SWORDS = List.of(Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
    private static final List<Item> AXES = List.of(Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
    private static final List<Item> BOWS = List.of(Items.BOW);

    private static void equipMob(Mob mob, RandomSource random, Level level) {
        var enchantments = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        // 1–4 random armor pieces
        List<EquipmentSlot> armorSlots = new ArrayList<>(List.of(
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        ));
        Collections.shuffle(armorSlots, random::nextInt);
        int pieces = 1 + random.nextInt(4);
        for (int i = 0; i < pieces; i++) {
            EquipmentSlot slot = armorSlots.get(i);
            Item armorItem = getArmorItemForSlot(slot, random);
            mob.setItemSlot(slot, new ItemStack(armorItem));
            mob.setDropChance(slot, 0.05F);
        }

        // Always clear offhand
        mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        mob.setDropChance(EquipmentSlot.OFFHAND, 0.05F);

        // 50% chance to equip a weapon
        if (random.nextBoolean()) {
            Item weaponItem;
            if (mob.getType() == EntityType.SKELETON || mob.getType() == EntityType.STRAY || mob.getType() == EntityType.WITHER_SKELETON) {
                weaponItem = pick(random, BOWS);
            } else if (mob.getType() == EntityType.PIGLIN_BRUTE || mob.getType() == EntityType.VINDICATOR) {
                weaponItem = pick(random, AXES);
            } else {
                weaponItem = pick(random, SWORDS);
            }

            ItemStack weapon = new ItemStack(weaponItem);
            Holder<Enchantment> enchantment = (weaponItem == Items.BOW)
                    ? enchantments.getHolderOrThrow(Enchantments.POWER)
                    : enchantments.getHolderOrThrow(Enchantments.SHARPNESS);

            int levelValue = 1 + random.nextInt(3);
            weapon.enchant(enchantment, levelValue);

            mob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
            mob.setDropChance(EquipmentSlot.MAINHAND, 0.05F);
        }
    }

    private static Item getArmorItemForSlot(EquipmentSlot slot, RandomSource random) {
        return switch (slot) {
            case HEAD -> pick(random, Items.GOLDEN_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
            case CHEST -> pick(random, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
            case LEGS -> pick(random, Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
            case FEET -> pick(random, Items.GOLDEN_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
            default -> throw new IllegalArgumentException("Unexpected slot for armor: " + slot);
        };
    }

    @SafeVarargs
    private static <T> T pick(RandomSource random, T... items) {
        return items[random.nextInt(items.length)];
    }

    private static <T> T pick(RandomSource random, List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
