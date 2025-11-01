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
