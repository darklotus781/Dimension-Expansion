package com.lithiumcraft.dimension_expansion.mixin;

import com.lithiumcraft.dimension_expansion.util.SpawnContextHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Inject(method = "spawnForChunk", at = @At("HEAD"))
    private static void pushLevel(ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnState spawnState, boolean spawnFriendlies, boolean spawnMonsters, boolean forcedDespawn, CallbackInfo ci) {
        SpawnContextHolder.push(level);
    }

    @Inject(method = "spawnForChunk", at = @At("RETURN"))
    private static void popLevel(ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnState spawnState, boolean spawnFriendlies, boolean spawnMonsters, boolean forcedDespawn, CallbackInfo ci) {
        SpawnContextHolder.clear();
    }
}
