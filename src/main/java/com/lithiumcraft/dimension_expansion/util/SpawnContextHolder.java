package com.lithiumcraft.dimension_expansion.util;

import net.minecraft.server.level.ServerLevel;

public final class SpawnContextHolder {
    private static final ThreadLocal<ServerLevel> LEVEL = new ThreadLocal<>();
    public static void push(ServerLevel level) { LEVEL.set(level); }
    public static ServerLevel get() { return LEVEL.get(); }
    public static void clear() { LEVEL.remove(); }
}