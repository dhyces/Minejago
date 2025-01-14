package dev.thomasglasser.minejago.world.entity;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.advancements.MinejagoCriteriaTriggers;
import dev.thomasglasser.minejago.client.MinejagoKeyMappings;
import dev.thomasglasser.minejago.core.particles.MinejagoParticleUtils;
import dev.thomasglasser.minejago.core.particles.SpinjitzuParticleOptions;
import dev.thomasglasser.minejago.core.registries.MinejagoRegistries;
import dev.thomasglasser.minejago.data.tags.MinejagoStructureTags;
import dev.thomasglasser.minejago.network.*;
import dev.thomasglasser.minejago.platform.Services;
import dev.thomasglasser.minejago.sounds.MinejagoSoundEvents;
import dev.thomasglasser.minejago.world.entity.power.MinejagoPowers;
import dev.thomasglasser.minejago.world.entity.power.Power;
import dev.thomasglasser.minejago.world.item.GoldenWeaponItem;
import dev.thomasglasser.minejago.world.item.MinejagoItems;
import dev.thomasglasser.minejago.world.level.gameevent.MinejagoGameEvents;
import dev.thomasglasser.minejago.world.level.saveddata.maps.MinejagoMapDecorations;
import dev.thomasglasser.minejago.world.level.storage.SpinjitzuData;
import net.mehvahdjukaar.moonlight.api.map.MapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.Set;
import java.util.function.Predicate;

public class MinejagoEntityEvents
{
    public static final Predicate<LivingEntity> NO_SPINJITZU = (player ->
            player.isCrouching() ||
            player.getVehicle() != null ||
            player.isVisuallySwimming() ||
            player.isUnderWater() ||
            player.isSleeping() ||
            player.isFreezing() ||
            player.isNoGravity() ||
            player.isInLava() ||
            player.isFallFlying() ||
            player.isBlocking() ||
            player.getActiveEffects().stream().anyMatch((mobEffectInstance -> mobEffectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL)) ||
            player.isInWater() ||
            ((DataHolder)player).getPersistentData().getInt("OffGroundTicks") > 20);

    public static void onPlayerTick(Player player)
    {
        SpinjitzuData spinjitzu = Services.DATA.getSpinjitzuData(player);
        int waitTicks = ((DataHolder)(player)).getPersistentData().getInt("SpinjitzuWaitTicks");

        if (player instanceof ServerPlayer serverPlayer)
        {
            if (!player.onGround())
                ((DataHolder)player).getPersistentData().putInt("OffGroundTicks", ((DataHolder)player).getPersistentData().getInt("OffGroundTicks") + 1);
            else
                ((DataHolder)player).getPersistentData().putInt("OffGroundTicks", 0);
            if (spinjitzu.unlocked()) {
                if (spinjitzu.active()) {
                    if (NO_SPINJITZU.test(serverPlayer)) {
                        stopSpinjitzu(spinjitzu, serverPlayer, !serverPlayer.isCrouching());
                        return;
                    }
                    MinejagoCriteriaTriggers.DO_SPINJITZU.trigger(serverPlayer);
                    if (player.tickCount % 20 == 0)
                    {
                        serverPlayer.level().playSound(null, serverPlayer.blockPosition(), MinejagoSoundEvents.SPINJITZU_ACTIVE.get(), SoundSource.PLAYERS);
                        serverPlayer.level().gameEvent(serverPlayer, MinejagoGameEvents.SPINJITZU.get(), serverPlayer.blockPosition());
                    }
                    Power power = player.level().registryAccess().registryOrThrow(MinejagoRegistries.POWER).getHolderOrThrow(Services.DATA.getPowerData(player).power()).value();
                    if (!power.is(MinejagoPowers.NONE)) {
                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, power.getMainSpinjitzuColor(), power.getAltSpinjitzuColor(), 10.5, false);
                        if (power.getBorderParticle() != null)
                            MinejagoParticleUtils.renderNormalSpinjitzuBorder(power.getBorderParticle(), serverPlayer, 4, false);
                    } else if (serverPlayer.getTeam() != null) {
                        switch (serverPlayer.getTeam().getColor()) {
                            case RED -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_RED, SpinjitzuParticleOptions.TEAM_RED, 10.5, false);
                            }
                            case AQUA -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_AQUA, SpinjitzuParticleOptions.TEAM_AQUA, 10.5, false);
                            }
                            case BLUE -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_BLUE, SpinjitzuParticleOptions.TEAM_BLUE, 10.5, false);
                            }
                            case GOLD -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_GOLD, SpinjitzuParticleOptions.TEAM_GOLD, 10.5, false);
                            }
                            case GRAY -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_GRAY, SpinjitzuParticleOptions.TEAM_GRAY, 10.5, false);
                            }
                            case BLACK -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_BLACK, SpinjitzuParticleOptions.TEAM_BLACK, 10.5, false);
                            }
                            case GREEN -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_GREEN, SpinjitzuParticleOptions.TEAM_GREEN, 10.5, false);
                            }
                            case WHITE -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_WHITE, SpinjitzuParticleOptions.TEAM_WHITE, 10.5, false);
                            }
                            case YELLOW -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_YELLOW, SpinjitzuParticleOptions.TEAM_YELLOW, 10.5, false);
                            }
                            case DARK_RED -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_RED, SpinjitzuParticleOptions.TEAM_DARK_RED, 10.5, false);
                            }
                            case DARK_AQUA -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_AQUA, SpinjitzuParticleOptions.TEAM_DARK_AQUA, 10.5, false);
                            }
                            case DARK_BLUE -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_BLUE, SpinjitzuParticleOptions.TEAM_DARK_BLUE, 10.5, false);
                            }
                            case DARK_GRAY -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_GRAY, SpinjitzuParticleOptions.TEAM_DARK_GRAY, 10.5, false);
                            }
                            case DARK_GREEN -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_GREEN, SpinjitzuParticleOptions.TEAM_DARK_GREEN, 10.5, false);
                            }
                            case DARK_PURPLE -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_DARK_PURPLE, SpinjitzuParticleOptions.TEAM_DARK_PURPLE, 10.5, false);
                            }
                            case LIGHT_PURPLE -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.TEAM_LIGHT_PURPLE, SpinjitzuParticleOptions.TEAM_LIGHT_PURPLE, 10.5, false);
                            }
                            default -> {
                                MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.DEFAULT, SpinjitzuParticleOptions.DEFAULT, 10.5, false);
                            }
                        }
                    } else {
                        MinejagoParticleUtils.renderNormalSpinjitzu(serverPlayer, SpinjitzuParticleOptions.DEFAULT, SpinjitzuParticleOptions.DEFAULT, 10.5, false);
                    }
                }
            } else if (spinjitzu.active()) {
                stopSpinjitzu(spinjitzu, serverPlayer, true);
            }
        }
        else if (waitTicks > 0)
        {
            ((DataHolder)player).getPersistentData().putInt("SpinjitzuWaitTicks", --waitTicks);
        }
        else if (player.level().isClientSide && MinejagoKeyMappings.ACTIVATE_SPINJITZU.isDown())
        {
            if (spinjitzu.active())
            {
                Services.NETWORK.sendToServer(ServerboundStopSpinjitzuPacket.class);
            }
            else
            {
                Services.NETWORK.sendToServer(ServerboundStartSpinjitzuPacket.class);
            }
            ((DataHolder)player).getPersistentData().putInt("SpinjitzuWaitTicks", 5);
        }
    }

    public static void onServerPlayerLoggedIn(Player player)
    {
        for (ServerPlayer serverPlayer : ((ServerLevel) player.level()).getPlayers(serverPlayer -> true))
        {
            Services.NETWORK.sendToAllClients(ClientboundRefreshVipDataPacket.class, serverPlayer.getServer());
        }
    }

    public static void onLivingTick(LivingEntity entity)
    {
        if (entity instanceof Player player)
        {
            Inventory i = player.getInventory();

            if (i.contains(MinejagoItems.SCYTHE_OF_QUAKES.get().getDefaultInstance()) /*&& i.contains(MinejagoElements.SHURIKENS_OF_ICE.get().getDefaultInstance()) && i.contains(MinejagoElements.NUNCHUCKS_OF_LIGHTNING.get().getDefaultInstance()) && i.contains(MinejagoElements.SWORD_OF_FIRE.get().getDefaultInstance())*/)
                GoldenWeaponItem.overload(entity);
        }
        else if (entity instanceof InventoryCarrier carrier)
        {
            boolean f = false, e = false, i = false, l = false;

            for (ItemStack stack : entity.getAllSlots())
            {
                if (stack.getItem() == MinejagoItems.SCYTHE_OF_QUAKES.get())
                {
                    e = true;
                }
                /*else if (stack.getItem() == MinejagoElements.SWORD_OF_FIRE.get())
                {
                    f = true;
                }
                else if (stack.getItem() == MinejagoElements.NUNCHUCKS_OF_LIGHTNING.get())
                {
                    l = true;
                }
                else if (stack.getItem() == MinejagoElements.SHURIKENS_OF_ICE.get())
                {
                    i = true;
                }*/
            }

            SimpleContainer inventory = carrier.getInventory();

            if ((inventory.hasAnyOf(Set.of(MinejagoItems.SCYTHE_OF_QUAKES.get())) || e) /*&& (inventory.hasAnyOf(Set.of(MinejagoElements.SWORD_OF_FIRE.get())) || f) && (inventory.hasAnyOf(Set.of(MinejagoElements.NUNCHUCKS_OF_LIGHTNING.get())) || l) && (inventory.hasAnyOf(Set.of(MinejagoElements.SHURIKENS_OF_ICE.get())) || i)*/)
                GoldenWeaponItem.overload(entity);
        }
        else {
            boolean f = false, e = false, i = false, l = false;

            for (ItemStack stack : entity.getAllSlots())
            {
                if (stack.getItem() == MinejagoItems.SCYTHE_OF_QUAKES.get())
                {
                    e = true;
                }
                /*else if (stack.getItem() == MinejagoElements.SWORD_OF_FIRE.get())
                {
                    f = true;
                }
                else if (stack.getItem() == MinejagoElements.NUNCHUCKS_OF_LIGHTNING.get())
                {
                    l = true;
                }
                else if (stack.getItem() == MinejagoElements.SHURIKENS_OF_ICE.get())
                {
                    i = true;
                }*/
            }

            if (f && e && i && l)
                GoldenWeaponItem.overload(entity);
        }
    }

    public static void onPlayerEntityInteract(Player player, Level world, InteractionHand hand, Entity entity)
    {
        if (world instanceof ServerLevel serverLevel && hand == InteractionHand.MAIN_HAND && entity instanceof Painting painting && painting.getVariant().is(Minejago.modLoc( "four_weapons")) && !((DataHolder)painting).getPersistentData().getBoolean("MapTaken"))
        {
            ItemStack itemstack = MapItem.create(world, (int)entity.getX(), (int)entity.getZ(), (byte)4, true, true);
            MapItem.renderBiomePreviewMap((ServerLevel) world, itemstack);
            /* TODO: Find lightning */ BlockPos pos1 = serverLevel.findNearestMapStructure(StructureTags.VILLAGE, player.getOnPos(), Integer.MAX_VALUE, false);
            /* TODO: Find fire */ BlockPos pos2 = serverLevel.findNearestMapStructure(StructureTags.RUINED_PORTAL, player.getOnPos(), Integer.MAX_VALUE, false);
            BlockPos earth = serverLevel.findNearestMapStructure(MinejagoStructureTags.CAVE_OF_DESPAIR, player.getOnPos(), Integer.MAX_VALUE, false);
            /* TODO: Find ice */ BlockPos pos4 = serverLevel.findNearestMapStructure(StructureTags.MINESHAFT, player.getOnPos(), Integer.MAX_VALUE, false);
            if (Minejago.Dependencies.MOONLIGHT_LIB.isInstalled())
            {
                if (pos1 != null) MapHelper.addDecorationToMap(itemstack, pos1, MinejagoMapDecorations.NUNCHUCKS, -1);
                if (pos2 != null) MapHelper.addDecorationToMap(itemstack, pos2, MinejagoMapDecorations.SWORD, -1);
                if (earth != null) MapHelper.addDecorationToMap(itemstack, earth, MinejagoMapDecorations.SCYTHE, -1);
                if (pos4 != null) MapHelper.addDecorationToMap(itemstack, pos4, MinejagoMapDecorations.SHURIKENS, -1);
            }
            else
            {
                if (pos1 != null) MapItemSavedData.addTargetDecoration(itemstack, pos1, "lightning", MapDecoration.Type.BANNER_BLUE);
                if (pos2 != null) MapItemSavedData.addTargetDecoration(itemstack, pos2, "fire", MapDecoration.Type.BANNER_RED);
                if (earth != null) MapItemSavedData.addTargetDecoration(itemstack, earth, "earth", MapDecoration.Type.BANNER_BROWN);
                if (pos4 != null) MapItemSavedData.addTargetDecoration(itemstack, pos4, "ice", MapDecoration.Type.BANNER_WHITE);
            }
            itemstack.setHoverName(Component.translatable(Items.FILLED_MAP.getDescriptionId() + ".golden_weapons"));
            itemstack.getOrCreateTag().putBoolean("IsFourWeaponsMap", true);
            player.addItem(itemstack);
            ((DataHolder)painting).getPersistentData().putBoolean("MapTaken", true);
        }
    }

    public static void stopSpinjitzu(SpinjitzuData spinjitzu, ServerPlayer serverPlayer, boolean fail)
    {
        if (spinjitzu.active())
        {
            Services.DATA.setSpinjitzuData(new SpinjitzuData(spinjitzu.unlocked(), false), serverPlayer);
            if (fail)
                Services.NETWORK.sendToAllClients(ClientboundFailSpinjitzuPacket.class, ClientboundFailSpinjitzuPacket.toBytes(serverPlayer.getUUID()), serverPlayer.getServer());
            else
                Services.NETWORK.sendToAllClients(ClientboundStopAnimationPacket.class, ClientboundStopAnimationPacket.toBytes(serverPlayer.getUUID()), serverPlayer.getServer());
            AttributeInstance speed = serverPlayer.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null && speed.hasModifier(SpinjitzuData.SPEED_MODIFIER))
                speed.removeModifier(SpinjitzuData.SPEED_MODIFIER);
            AttributeInstance kb = serverPlayer.getAttribute(Attributes.ATTACK_KNOCKBACK);
            if (kb != null && kb.hasModifier(SpinjitzuData.KNOCKBACK_MODIFIER))
                kb.removeModifier(SpinjitzuData.KNOCKBACK_MODIFIER);
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), MinejagoSoundEvents.SPINJITZU_STOP.get(), SoundSource.PLAYERS);
        }
    }
}
