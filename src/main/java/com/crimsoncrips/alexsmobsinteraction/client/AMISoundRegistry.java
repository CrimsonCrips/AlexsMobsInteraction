package com.crimsoncrips.alexsmobsinteraction.client;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AMISoundRegistry {
    public static final DeferredRegister<SoundEvent> DEF_REG = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AlexsMobsInteraction.MODID);


    public static final DeferredHolder<SoundEvent, SoundEvent> BANANA_SLIP = createSoundEvent("banana_slip");

    private static DeferredHolder<SoundEvent, SoundEvent> createSoundEvent(final String soundName) {
        return DEF_REG.register(soundName, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(AlexsMobsInteraction.MODID, soundName)));
    }
}
