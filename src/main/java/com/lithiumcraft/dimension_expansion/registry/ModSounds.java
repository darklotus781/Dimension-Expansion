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

    public static final Supplier<SoundEvent> UPSIDE_DOWN_DIMENSION_PORTAL_ACTIVATE = registerSoundEvent("upside_down_dimension_portal_activate");
    public static final Supplier<SoundEvent> UPSIDE_DOWN_DIMENSION_PORTAL_AMBIENT = registerSoundEvent("upside_down_dimension_portal_ambient");
    public static final Supplier<SoundEvent> TELEPORTER_ACTIVATE = registerSoundEvent("teleporter_activate");
    public static final Supplier<SoundEvent> UPSIDE_DOWN_MOOD = registerSoundEvent("ambient.upside_down.mood");
    public static final Supplier<SoundEvent> UPSIDE_DOWN_ADDITIONS = registerSoundEvent("ambient.upside_down.additions");
    public static final Supplier<SoundEvent> UPSIDE_DOWN_MUSIC = registerSoundEvent("music.upside_down.upside_down");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(DimensionExpansion.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
