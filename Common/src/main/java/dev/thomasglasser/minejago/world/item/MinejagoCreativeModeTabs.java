package dev.thomasglasser.minejago.world.item;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.platform.Services;
import dev.thomasglasser.minejago.registration.RegistrationProvider;
import dev.thomasglasser.minejago.registration.RegistryObject;
import dev.thomasglasser.minejago.world.entity.power.MinejagoPowers;
import dev.thomasglasser.minejago.world.item.armor.IGeoArmorItem;
import dev.thomasglasser.minejago.world.item.armor.MinejagoArmors;
import dev.thomasglasser.minejago.world.item.armor.PoweredArmorItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

public class MinejagoCreativeModeTabs
{
    public static final RegistrationProvider<CreativeModeTab> CREATIVE_MODE_TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, Minejago.MOD_ID);

    public static final RegistryObject<CreativeModeTab> GI = CREATIVE_MODE_TABS.register("gi", () -> Services.ITEM.newTab(Component.translatable(Minejago.modLoc("gi").toLanguageKey("item_group")), () -> MinejagoArmors.BLACK_GI_SET.HEAD.get().getDefaultInstance(), (parameters, output) ->
    {
        MinejagoArmors.ARMOR_SETS.forEach(armorSet ->
        {
            armorSet.getAll().forEach(armor ->
            {
                if (!(armor.get() instanceof PoweredArmorItem) && armor.get() instanceof IGeoArmorItem iGeoArmorItem && iGeoArmorItem.isGi())
                    output.accept(armor::get);
            });
        });

        final RegistryAccess.Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        final RegistrySetBuilder builder = new RegistrySetBuilder();
        MinejagoPowers.POWERS.addToSet(builder);
        HolderLookup.Provider provider = builder.build(access);
        output.acceptAll(MinejagoPowers.getArmorForAll(provider));
    }, CreativeModeTabs.COMBAT));

    public static void init() {}
}
