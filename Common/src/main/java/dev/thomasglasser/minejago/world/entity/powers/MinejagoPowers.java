package dev.thomasglasser.minejago.world.entity.powers;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.core.particles.MinejagoParticleTypes;
import dev.thomasglasser.minejago.core.particles.SpinjitzuParticleOptions;
import dev.thomasglasser.minejago.core.registries.MinejagoRegistries;
import dev.thomasglasser.minejago.core.registries.MinejagoRegistryKeys;
import dev.thomasglasser.minejago.registration.RegistrationProvider;
import dev.thomasglasser.minejago.registration.RegistryObject;
import dev.thomasglasser.minejago.world.item.armor.MinejagoArmor;
import dev.thomasglasser.minejago.world.item.armor.MinejagoArmorMaterials;
import dev.thomasglasser.minejago.world.item.armor.TrainingGiItem;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public class MinejagoPowers {
    public static final RegistrationProvider<Power> POWERS = RegistrationProvider.get(Minejago.modLoc("power"), Minejago.MOD_ID);
    public static final Supplier<Registry<Power>> REGISTRY = POWERS.registryBuilder().build();

    public static RegistryObject<Power> NONE = register("none", () -> new Power(Minejago.modLoc("none")), false);
    public static RegistryObject<Power> FIRE = register("fire", () -> new Power(Minejago.modLoc("fire"), SpinjitzuParticleOptions.ELEMENT_ORANGE, SpinjitzuParticleOptions.ELEMENT_YELLOW, MinejagoParticleTypes.SPARKS, true), true);
    public static RegistryObject<Power> EARTH = register("earth", () -> new Power(Minejago.modLoc("earth"), SpinjitzuParticleOptions.ELEMENT_BROWN, SpinjitzuParticleOptions.ELEMENT_TAN, MinejagoParticleTypes.ROCKS, true), true);
    public static RegistryObject<Power> LIGHTNING = register("lightning", () -> new Power(Minejago.modLoc("lightning"), SpinjitzuParticleOptions.ELEMENT_BLUE, SpinjitzuParticleOptions.ELEMENT_LIGHT_BLUE, MinejagoParticleTypes.BOLTS, true), true);
    public static RegistryObject<Power> ICE = register("ice", () -> new Power(Minejago.modLoc("ice"), SpinjitzuParticleOptions.ELEMENT_LIGHT_BLUE, SpinjitzuParticleOptions.ELEMENT_WHITE, MinejagoParticleTypes.SNOWS, true), true);

    public static RegistryObject<Power> register(String name, Supplier<Power> powerSupplier, boolean makeSets)
    {
        RegistryObject<Power> power = POWERS.register(name, powerSupplier);
        if (makeSets) MinejagoArmor.PoweredArmorSet.create(name, "training_gi", MinejagoArmorMaterials.TRAINING_GI, TrainingGiItem.class);
        return power;
    }

    public static void init() {}
}
