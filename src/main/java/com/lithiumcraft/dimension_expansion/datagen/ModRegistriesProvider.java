package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.datagen.worldgen.ConfiguredFeatureProvider;
import com.lithiumcraft.dimension_expansion.datagen.worldgen.PlacedFeatureProvider;
import com.lithiumcraft.dimension_expansion.registry.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModRegistriesProvider extends DatapackBuiltinEntriesProvider {
    public ModRegistriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, future, BUILDER, Set.of("minecraft", DimensionExpansion.MOD_ID));
    }

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatureProvider::bootstrap)
            .add(Registries.PLACED_FEATURE, PlacedFeatureProvider::bootstrap);
}
