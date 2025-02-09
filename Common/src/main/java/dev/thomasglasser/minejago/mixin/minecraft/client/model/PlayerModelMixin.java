package dev.thomasglasser.minejago.mixin.minecraft.client.model;

import dev.thomasglasser.minejago.world.item.armor.GeoArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {
    @Shadow @Final public ModelPart leftSleeve;

    @Shadow @Final public ModelPart rightSleeve;

    @Shadow @Final public ModelPart jacket;

    @Shadow @Final public ModelPart rightPants;

    @Shadow @Final public ModelPart leftPants;

    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        if (entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof GeoArmorItem geoArmorItem && geoArmorItem.isSkintight())
        {
            this.leftSleeve.visible = false;
            this.rightSleeve.visible = false;
            this.jacket.visible = false;
        }
        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof GeoArmorItem geoArmorItem && geoArmorItem.isSkintight())
        {
            this.hat.visible = false;
        }
        if (entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof GeoArmorItem iGeoArmorBoots && iGeoArmorBoots.isSkintight() || entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof GeoArmorItem iGeoArmorLeggings && iGeoArmorLeggings.isSkintight())
        {
            this.rightPants.visible = false;
            this.leftPants.visible = false;
        }
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
}
