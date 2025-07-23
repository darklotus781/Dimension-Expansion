package com.lithiumcraft.dimension_expansion;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.blockentity.ModBlockEntities;
import com.lithiumcraft.dimension_expansion.datagen.loot.ModLootModifiers;
import com.lithiumcraft.dimension_expansion.item.ModCreativeModeTabs;
import com.lithiumcraft.dimension_expansion.item.ModItems;
import com.lithiumcraft.dimension_expansion.particle.ModParticles;
import com.lithiumcraft.dimension_expansion.registry.ModSounds;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DimensionExpansion.MOD_ID)
public class DimensionExpansion
{
    public static final String MOD_ID = "dimension_expansion";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Integer PORTAL_COLOR = 8912904;

    public DimensionExpansion(IEventBus modEventBus, ModContainer modContainer)
    {
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModItems.register(modEventBus);
        ModParticles.register(modEventBus);
        ModSounds.register(modEventBus);

        ModLootModifiers.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
