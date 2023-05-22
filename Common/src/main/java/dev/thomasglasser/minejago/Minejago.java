package dev.thomasglasser.minejago;

import dev.thomasglasser.minejago.client.MinejagoClientConfig;
import dev.thomasglasser.minejago.client.MinejagoKeyMappings;
import dev.thomasglasser.minejago.core.particles.MinejagoParticleTypes;
import dev.thomasglasser.minejago.core.registries.MinejagoRegistries;
import dev.thomasglasser.minejago.platform.Services;
import dev.thomasglasser.minejago.server.MinejagoServerConfig;
import dev.thomasglasser.minejago.sounds.MinejagoSoundEvents;
import dev.thomasglasser.minejago.world.effect.MinejagoMobEffects;
import dev.thomasglasser.minejago.world.entity.MinejagoEntityTypes;
import dev.thomasglasser.minejago.world.entity.ai.memory.MinejagoMemoryModuleTypes;
import dev.thomasglasser.minejago.world.entity.decoration.MinejagoPaintingVariants;
import dev.thomasglasser.minejago.world.entity.powers.MinejagoPowers;
import dev.thomasglasser.minejago.world.item.MinejagoCreativeModeTabs;
import dev.thomasglasser.minejago.world.item.MinejagoItems;
import dev.thomasglasser.minejago.world.item.MinejagoTiers;
import dev.thomasglasser.minejago.world.item.armor.MinejagoArmor;
import dev.thomasglasser.minejago.world.item.brewing.MinejagoPotions;
import dev.thomasglasser.minejago.world.item.crafting.MinejagoRecipeSerializers;
import dev.thomasglasser.minejago.world.item.crafting.MinejagoRecipeTypes;
import dev.thomasglasser.minejago.world.level.block.MinejagoBlocks;
import dev.thomasglasser.minejago.world.level.block.entity.MinejagoBannerPatterns;
import dev.thomasglasser.minejago.world.level.block.entity.MinejagoBlockEntityTypes;
import dev.thomasglasser.minejago.world.level.gameevent.MinejagoGameEvents;
import net.minecraft.resources.ResourceLocation;
import net.tslat.tes.api.TESAPI;
import net.tslat.tes.api.util.TESClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class Minejago {

	public static final String MOD_ID = "minejago";
	public static final String MOD_NAME = "Minejago";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static ResourceLocation modLoc(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}

	public static void init()
	{
		initRegistries();

		registerConfigs();

		GeckoLib.initialize();

		if (Dependencies.TSLAT_ENTITY_STATUS.isInstalled())
		{
			TESAPI.addTESHudElement(Minejago.modLoc("power_symbol"), (poseStack, mc, partialTick, entity, opacity, inWorldHud) ->
			{
				if (mc.level != null && Services.DATA.getPowerData(entity) != null) {
					TESClientUtil.prepRenderForTexture(MinejagoPowers.POWERS.get(mc.level.registryAccess()).get(Services.DATA.getPowerData(entity).power()).getIcon());
					poseStack.pushPose();
					poseStack.scale(0.5f, 0.5f, 1.0f);
					TESClientUtil.drawSimpleTexture(poseStack, 0, 0, 32, 32, 0, 0, 32);
					poseStack.popPose();
					return 16;
				}

				return 0;
			});
		}
	}

	private static void initRegistries()
	{
		MinejagoRegistries.init();

		MinejagoRecipeTypes.init();
		MinejagoRecipeSerializers.init();
		MinejagoArmor.init();
		MinejagoTiers.init();
		MinejagoPowers.init();
		MinejagoEntityTypes.init();
		MinejagoItems.init();
		MinejagoParticleTypes.init();
		MinejagoPaintingVariants.init();
		MinejagoBannerPatterns.init();
		MinejagoBlocks.init();
		MinejagoBlockEntityTypes.init();
		MinejagoPotions.init();
		MinejagoSoundEvents.init();
		MinejagoMobEffects.init();
		MinejagoKeyMappings.init();
		MinejagoCreativeModeTabs.init();
		MinejagoGameEvents.init();
		MinejagoMemoryModuleTypes.init();
	}

	private static void registerConfigs()
	{
		MinejagoServerConfig.register();
		MinejagoClientConfig.register();
	}

	public enum Dependencies
	{
		MOONLIGHT_LIB("moonlight"),
		DYNAMIC_LIGHTS("dynamiclights", "lambdynlights"),
		TRIMMED("trimmed"),
		SHARDSAPI("shardsapi"),
		PLAYER_ANIMATOR("playeranimator", "player-animator"),
		REACH_ENTITY_ATTRIBUTES("forge", "reach-entity-attributes"),
		TSLAT_ENTITY_STATUS("tslatentitystatus");

		private final String forge;
		private final String fabric;

		Dependencies(String modId)
		{
			this(modId, modId);
		}
		Dependencies(String forge, String fabric)
		{
			this.forge = forge;
			this.fabric = fabric;
		}

		public String getModId() {
			return Services.PLATFORM.getPlatformName().equals("Forge") ? forge : fabric;
		}

		public boolean isInstalled()
		{
			return Services.PLATFORM.isModLoaded(getModId());
		}
	}

	public enum Expansions
	{
		IMMERSION_PACK("minejago_immersion_pack");

		private final String id;

		Expansions(String id)
		{
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}
}