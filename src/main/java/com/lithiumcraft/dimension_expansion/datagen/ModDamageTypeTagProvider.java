package com.lithiumcraft.dimension_expansion.datagen;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.registry.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagProvider extends TagsProvider<DamageType> {

    public ModDamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
        super(output, Registries.DAMAGE_TYPE, future, DimensionExpansion.MOD_ID, helper);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.DARKNESS);
        this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(ModDamageTypes.DARKNESS);
        this.tag(DamageTypeTags.NO_KNOCKBACK).add(ModDamageTypes.DARKNESS);
    }
}
