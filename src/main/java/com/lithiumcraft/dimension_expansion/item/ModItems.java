package com.lithiumcraft.dimension_expansion.item;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DimensionExpansion.MOD_ID);

//    public static final DeferredItem<Item> NETHERITE_DUST = ITEMS.register("netherite_dust",
//            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
