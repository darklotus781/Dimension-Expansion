package com.lithiumcraft.dimension_expansion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue DEBUG_ENABLED = BUILDER
            .comment("Enable debug output to server log.")
            .worldRestart()
            .define("debugEnabled", false);

    static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean debugEnabled;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    static void onLoad(final ModConfigEvent event) {
        debugEnabled = DEBUG_ENABLED.get();
    }
}
