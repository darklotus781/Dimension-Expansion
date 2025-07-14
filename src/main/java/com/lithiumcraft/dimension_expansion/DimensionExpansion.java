package com.lithiumcraft.dimension_expansion;

import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import com.lithiumcraft.dimension_expansion.blockentity.ModBlockEntities;
import com.lithiumcraft.dimension_expansion.item.ModCreativeModeTabs;
import com.lithiumcraft.dimension_expansion.item.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DimensionExpansion.MOD_ID)
public class DimensionExpansion
{
    public static final String MOD_ID = "dimension_expansion";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DimensionExpansion(IEventBus modEventBus, ModContainer modContainer)
    {
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModItems.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);
    }
}
