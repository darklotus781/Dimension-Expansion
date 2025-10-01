package com.lithiumcraft.dimension_expansion.registry;

import com.lithiumcraft.dimension_expansion.DimensionExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, DimensionExpansion.MOD_ID);

    public static final Supplier<SoundEvent> TELEPORTER_ACTIVATE = registerSoundEvent("teleporter_activate");
    public static final Supplier<SoundEvent> NIGHTWALKER_DAMAGE = registerSoundEvent("nightwalker_damage");
    public static final Supplier<SoundEvent> DARKNESS_APPROACHES = registerSoundEvent("darkness_approaches");
    public static final Supplier<SoundEvent> AMBIENT_DARKNESS = registerSoundEvent("ambient_darkness");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
