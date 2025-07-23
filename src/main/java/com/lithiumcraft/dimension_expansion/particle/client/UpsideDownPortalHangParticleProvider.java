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

public class UpsideDownPortalHangParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public UpsideDownPortalHangParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed) {
        var particle = new DripHangParticle(level, x, y, z, Fluids.EMPTY, ModParticles.UPSIDE_DOWN_PORTAL_FRAME_DRIP_PARTICLE.get());
        float[] rgb = ParticleColorUtil.getRGBComponents(DimensionExpansion.PORTAL_COLOR);
        particle.isGlowing = true;
        particle.gravity *= 0.01F;
        particle.lifetime = 100;
        particle.pickSprite(this.spriteSet);
        particle.setColor(rgb[0], rgb[1], rgb[2]);
        return particle;
    }

    @OnlyIn(Dist.CLIENT)
    static class DripHangParticle extends DripParticle {
        private final ParticleOptions fallingParticle;
        private float gravity;
        private boolean isGlowing;
        private int lifetime;

        DripHangParticle(ClientLevel level, double x, double y, double z, Fluid type, ParticleOptions fallingParticle) {
            super(level, x, y, z, type);
            this.fallingParticle = fallingParticle;
            this.gravity *= 0.01F;
            this.lifetime = 100;
        }

        protected void preMoveUpdate() {
            if (this.lifetime-- <= 0) {
                this.remove();
                this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
        }

        protected void postMoveUpdate() {
            this.xd *= 0.02;
            this.yd *= 0.02;
            this.zd *= 0.02;
        }
    }
}

