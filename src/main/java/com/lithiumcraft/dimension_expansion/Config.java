package com.lithiumcraft.dimension_expansion;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue DEBUG_ENABLED = BUILDER
            .comment("Enable debug output to server log.  debug.log")
            .worldRestart()
            .define("debugEnabled", false);

    private static final ModConfigSpec.BooleanValue DENY_FAKE_PLAYER_DEEP_BENEATH = BUILDER
            .comment("Disable / Disallow Fake Players (quarry, builder and miner blocks) use in the Deep Beneath?")
            .worldRestart()
            .define("denyFakePlayerDeepBeneath", true);

    static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean debugEnabled;
    public static boolean denyFakePlayerDeepBeneath;

    static void onLoad(final ModConfigEvent event) {
        debugEnabled = DEBUG_ENABLED.get();
        denyFakePlayerDeepBeneath = DENY_FAKE_PLAYER_DEEP_BENEATH.get();
    }
}
