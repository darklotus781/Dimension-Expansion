package com.lithiumcraft.dimension_expansion.particle.client;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.particle.ModParticles;
import com.lithiumcraft.dimension_expansion.util.ParticleColorUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public class UpsideDownPortalDripParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public UpsideDownPortalDripParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed) {
        var particle = new UpsideDownPortalDripParticleProvider.FallAndLandParticle(level, x, y, z, Fluids.EMPTY, ModParticles.UPSIDE_DOWN_PORTAL_FRAME_LAND_PARTICLE.get());
        float[] rgb = ParticleColorUtil.getRGBComponents(DimensionExpansion.PORTAL_COLOR);
        particle.isGlowing = true;
        particle.gravity = 0.01F;
        particle.pickSprite(this.spriteSet);
        particle.setColor(rgb[0], rgb[1], rgb[2]);
        return particle;
    }

    @OnlyIn(Dist.CLIENT)
    static class FallAndLandParticle extends FallingParticle {
        protected final ParticleOptions landParticle;
        private float gravity;
        private boolean isGlowing;
        private int lifetime;

        FallAndLandParticle(ClientLevel level, double x, double y, double z, Fluid type, ParticleOptions landParticle) {
            super(level, x, y, z, type);
            this.landParticle = landParticle;
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, (double) 0.0F, (double) 0.0F, (double) 0.0F);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class FallingParticle extends DripParticle {
        FallingParticle(ClientLevel level, double x, double y, double z, Fluid type) {
            this(level, x, y, z, type, (int) ((double) 64.0F / (Math.random() * 0.8 + 0.2)));
        }

        FallingParticle(ClientLevel level, double x, double y, double z, Fluid type, int lifetime) {
            super(level, x, y, z, type);
            this.lifetime = lifetime;
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
            }
        }
    }
}
