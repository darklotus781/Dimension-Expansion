package com.lithiumcraft.dimension_expansion.blockentity;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import com.lithiumcraft.dimension_expansion.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DimensionExpansion.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleporterBlockEntity>> DEEP_BENEATH_TELEPORTER =
            BLOCK_ENTITIES.register("deep_beneath_teleporter", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TeleporterBlockEntity(ModBlockEntities.DEEP_BENEATH_TELEPORTER.get(), pos, state),
                            ModBlocks.DEEP_BENEATH_TELEPORTER.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleporterBlockEntity>> MINING_TELEPORTER =
            BLOCK_ENTITIES.register("mining_teleporter", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TeleporterBlockEntity(ModBlockEntities.MINING_TELEPORTER.get(), pos, state),
                            ModBlocks.MINING_TELEPORTER.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleporterBlockEntity>> STONE_BLOCK_TELEPORTER =
            BLOCK_ENTITIES.register("stone_block_teleporter", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TeleporterBlockEntity(ModBlockEntities.STONE_BLOCK_TELEPORTER.get(), pos, state),
                            ModBlocks.STONE_BLOCK_TELEPORTER.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleporterBlockEntity>> OVERWORLD_RETURN_TELEPORTER =
            BLOCK_ENTITIES.register("overworld_return_teleporter", () ->
                    BlockEntityType.Builder.of(
                            (pos, state) -> new TeleporterBlockEntity(ModBlockEntities.OVERWORLD_RETURN_TELEPORTER.get(), pos, state),
                            ModBlocks.OVERWORLD_RETURN_TELEPORTER.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

