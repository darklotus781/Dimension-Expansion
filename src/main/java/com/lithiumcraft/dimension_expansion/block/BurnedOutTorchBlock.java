package com.lithiumcraft.dimension_expansion.block;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.TorchBlock;

public class BurnedOutTorchBlock extends TorchBlock {
    public BurnedOutTorchBlock(SimpleParticleType flameParticle, Properties properties) {
        super(flameParticle, properties.lightLevel(s -> 0).noOcclusion());
    }

    @Override
    public String getDescriptionId() {
        return "block.dimension_expansion.burned_out_torch"; // safe static string
    }
}
