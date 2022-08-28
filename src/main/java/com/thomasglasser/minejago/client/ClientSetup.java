package com.thomasglasser.minejago.client;

import com.thomasglasser.minejago.MinejagoMod;
import com.thomasglasser.minejago.client.model.ThrownBambooStaffModel;
import com.thomasglasser.minejago.client.particle.SpinjitzuParticle;
import com.thomasglasser.minejago.client.renderer.entity.ThrownBambooStaffRenderer;
import com.thomasglasser.minejago.client.renderer.entity.ThrownBoneKnifeRenderer;
import com.thomasglasser.minejago.client.model.ThrownBoneKnifeModel;
import com.thomasglasser.minejago.core.particles.MinejagoParticleTypes;
import com.thomasglasser.minejago.world.entity.MinejagoEntityTypes;
import com.thomasglasser.minejago.world.item.MinejagoItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MinejagoMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(ThrownBoneKnifeModel.LAYER_LOCATION, ThrownBoneKnifeModel::createBodyLayer);
        event.registerLayerDefinition(ThrownBambooStaffModel.LAYER_LOCATION, ThrownBambooStaffModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(MinejagoEntityTypes.THROWN_BONE_KNIFE.get(), ThrownBoneKnifeRenderer::new);
        event.registerEntityRenderer(MinejagoEntityTypes.THROWN_BAMBOO_STAFF.get(), ThrownBambooStaffRenderer::new);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event)
    {
        event.register(new ResourceLocation(MinejagoMod.MODID, "item/bamboo_staff_inventory"));
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(IClientItemExtensions.of(MinejagoItems.BAMBOO_STAFF.get()).getCustomRenderer());
    }

    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event)
    {
        event.register(MinejagoParticleTypes.SPINJITZU.get(), SpinjitzuParticle.Provider::new);
    }

    public static void onRegisterColorHandlers(RegisterColorHandlersEvent.Item event)
    {
        event.register(new ItemColor() {
            @Override
            public int getColor(ItemStack pStack, int pTintIndex) {
                return pTintIndex == 0 ? PotionUtils.getColor(pStack): -1;
            }
        }, MinejagoItems.FILLED_TEACUP.get());
    }
}
