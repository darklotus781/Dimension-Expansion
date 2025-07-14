package com.lithiumcraft.dimension_expansion.datagen;

import com.google.gson.JsonObject;
import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModDamageTypeProvider implements DataProvider {
    private final PackOutput output;

    public ModDamageTypeProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        JsonObject json = new JsonObject();
        json.addProperty("message_id", "darkness");
        json.addProperty("scaling", DamageScaling.NEVER.getSerializedName());
        json.addProperty("exhaustion", 0.1F);

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, "darkness");
        Path path = output.getOutputFolder()
            .resolve("data/dimension_expansion/damage_type/" + id.getPath() + ".json");

        return DataProvider.saveStable(cache, json, path);
    }

    @Override
    public String getName() {
        return "Deep Beneath DamageTypes";
    }
}
