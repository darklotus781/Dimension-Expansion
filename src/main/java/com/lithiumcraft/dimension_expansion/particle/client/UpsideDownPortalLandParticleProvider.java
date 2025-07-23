package com.lithiumcraft.dimension_expansion.particle.client;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.util.ParticleColorUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class UpsideDownPortalLandParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public UpsideDownPortalLandParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed) {
        DripParticle particle = new UpsideDownPortalLandParticleProvider.DripLandParticle(level, x, y, z, Fluids.EMPTY);
        float[] rgb = ParticleColorUtil.getRGBComponents(DimensionExpansion.PORTAL_COLOR);
        particle.pickSprite(this.spriteSet);
        particle.setColor(rgb[0], rgb[1], rgb[2]);
        return particle;
    }

    @OnlyIn(Dist.CLIENT)
    static class DripLandParticle extends DripParticle {
        private float gravity;
        private boolean isGlowing;
        private int lifetime;

        DripLandParticle(ClientLevel level, double posX, double posY, double posZ, Fluid fluid) {
            super(level, posX, posY, posZ, fluid);
            this.isGlowing = true;
            this.lifetime = (int) ((double) 16.0F / (Math.random() * 0.8 + 0.2));
        }
    }
}

