package com.lithiumcraft.dimension_expansion;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.blockentity.ModBlockEntities;
import com.lithiumcraft.dimension_expansion.datagen.loot.ModLootModifiers;
import com.lithiumcraft.dimension_expansion.event.DeepBeneathMobSpawnHandler;
import com.lithiumcraft.dimension_expansion.item.ModCreativeModeTabs;
import com.lithiumcraft.dimension_expansion.item.ModItems;
import com.lithiumcraft.dimension_expansion.registry.ModEffects;
import com.lithiumcraft.dimension_expansion.registry.ModSounds;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DimensionExpansion.MOD_ID)
public class DimensionExpansion
{
    public static final String MOD_ID = "dimension_expansion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DimensionExpansion(IEventBus modEventBus, Dist dist, ModContainer modContainer)
    {
        // --- runtime check for Simple Burnout Torch ---
        if (!ModList.get().isLoaded("simple_burnout_torch")) {
            throw new IllegalStateException("[Dimension Expansion] requires Simple Burnout Torch to be installed! Please add it to your mods folder.");
        }

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);
        ModLootModifiers.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);

        DeepBeneathMobSpawnHandler.register();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(Config::onLoad);
    }

}
