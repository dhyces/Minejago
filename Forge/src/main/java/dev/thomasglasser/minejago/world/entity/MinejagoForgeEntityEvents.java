package dev.thomasglasser.minejago.world.entity;

import dev.thomasglasser.minejago.platform.Services;
import dev.thomasglasser.minejago.world.entity.character.Zane;
import dev.thomasglasser.minejago.world.level.storage.SpinjitzuCapabilityAttacher;
import dev.thomasglasser.minejago.world.level.storage.SpinjitzuData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class MinejagoForgeEntityEvents
{
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        Player player = event.player;

        MinejagoEntityEvents.onPlayerTick(player);
    }

    public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        MinejagoEntityEvents.onServerPlayerLoggedIn(event.getEntity());
    }

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        for (EntityType<? extends LivingEntity> type : MinejagoEntityTypes.getAllAttributes().keySet())
        {
            event.put(type, MinejagoEntityTypes.getAllAttributes().get(type));
        }
    }

    public static void onLivingTick(LivingEvent.LivingTickEvent event)
    {
        LivingEntity entity = event.getEntity();

        MinejagoEntityEvents.onLivingTick(entity);
    }

    public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        MinejagoEntityEvents.onPlayerEntityInteract(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
    }

    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        event.getOriginal().getCapability(SpinjitzuCapabilityAttacher.SPINJITZU_CAPABILITY).ifPresent(spinjitzuCapability ->
                Services.DATA.setSpinjitzuData(new SpinjitzuData(spinjitzuCapability.isUnlocked(), false), event.getEntity()));
    }

    public static void onSpawnPlacementsRegister(SpawnPlacementRegisterEvent event)
    {
        event.register(MinejagoEntityTypes.ZANE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Zane::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }
}
