package com.lithiumcraft.dimension_expansion.particle;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, DimensionExpansion.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> UPSIDE_DOWN_PORTAL_FRAME_HANG_PARTICLE = PARTICLES.register("upside_down_portal_frame_hang", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> UPSIDE_DOWN_PORTAL_FRAME_DRIP_PARTICLE = PARTICLES.register("upside_down_portal_frame_drip", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> UPSIDE_DOWN_PORTAL_FRAME_LAND_PARTICLE = PARTICLES.register("upside_down_portal_frame_land", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
