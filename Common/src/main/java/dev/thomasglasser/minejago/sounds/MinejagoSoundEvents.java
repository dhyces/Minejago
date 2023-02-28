package dev.thomasglasser.minejago.sounds;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.registration.RegistrationProvider;
import dev.thomasglasser.minejago.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class MinejagoSoundEvents
{
    public static final RegistrationProvider<SoundEvent> SOUND_EVENTS = RegistrationProvider.get(Registries.SOUND_EVENT, Minejago.MOD_ID);

    public static final RegistryObject<SoundEvent> TEAPOT_WHISTLE = SOUND_EVENTS.register("teapot_whistle", () -> SoundEvent.createVariableRangeEvent(Minejago.modLoc("teapot_whistle")));
    public static final RegistryObject<SoundEvent> SPINJITZU_START = SOUND_EVENTS.register("spinjitzu_start", () -> SoundEvent.createVariableRangeEvent(Minejago.modLoc("spinjitzu_start")));
    public static final RegistryObject<SoundEvent> SPINJITZU_ACTIVE = SOUND_EVENTS.register("spinjitzu_active", () -> SoundEvent.createVariableRangeEvent(Minejago.modLoc("spinjitzu_active")));
    public static final RegistryObject<SoundEvent> SPINJITZU_STOP = SOUND_EVENTS.register("spinjitzu_stop", () -> SoundEvent.createVariableRangeEvent(Minejago.modLoc("spinjitzu_stop")));

    public static void init() {}
}
