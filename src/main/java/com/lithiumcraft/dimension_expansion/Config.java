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
            .comment("Enable debug output to server log.  debug.log")
            .worldRestart()
            .define("debugEnabled", false);

    static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean debugEnabled;

    static void onLoad(final ModConfigEvent event) {
        debugEnabled = DEBUG_ENABLED.get();
    }
}
