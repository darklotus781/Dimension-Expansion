package com.lithiumcraft.dimension_expansion.registry;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> DARKNESS = create("darkness");

    public static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, name));
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(DARKNESS, new DamageType("darkness", DamageScaling.NEVER, 0.1F));
    }
}
