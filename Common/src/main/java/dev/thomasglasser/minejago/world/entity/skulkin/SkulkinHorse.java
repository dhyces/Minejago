package dev.thomasglasser.minejago.world.entity.skulkin;

import dev.thomasglasser.minejago.world.entity.MinejagoEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.SkeletonTrapGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SkulkinHorse extends SkeletonHorse {

    public SkulkinHorse(EntityType<? extends SkulkinHorse> entityType, Level level) {
        super(entityType, level);
        this.skeletonTrapGoal = new SkulkinTrapGoal(this);
    }


    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return MinejagoEntityTypes.SKULKIN_HORSE.get().create(level);
    }

    public static class SkulkinTrapGoal extends SkeletonTrapGoal
    {
        private final SkulkinHorse horse;

        public SkulkinTrapGoal(SkulkinHorse skeletonHorse) {
            super(skeletonHorse);
            horse = skeletonHorse;
        }

        @Override
        public void tick() {
            if (!horse.isWearingArmor() && equipArmor(horse))
                horse.equipArmor(Items.IRON_HORSE_ARMOR.getDefaultInstance());
            super.tick();
        }

        @Override
        public AbstractHorse createHorse(DifficultyInstance difficulty) {
            SkulkinHorse skeletonHorse = MinejagoEntityTypes.SKULKIN_HORSE.get().create(this.horse.level());
            if (skeletonHorse != null) {
                skeletonHorse.finalizeSpawn((ServerLevel)this.horse.level(), difficulty, MobSpawnType.TRIGGERED, null, null);
                skeletonHorse.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
                skeletonHorse.invulnerableTime = 60;
                skeletonHorse.setPersistenceRequired();
                skeletonHorse.setTamed(true);
                skeletonHorse.equipSaddle(null);
                skeletonHorse.setAge(0);
                if (equipArmor(skeletonHorse))
                    skeletonHorse.equipArmor(Items.IRON_HORSE_ARMOR.getDefaultInstance());
            }

            return skeletonHorse;
        }

        @Override
        public Skeleton createSkeleton(DifficultyInstance difficulty, AbstractHorse horse) {
            Skulkin skeleton = MinejagoEntityTypes.SKULKIN.get().create(horse.level());
            if (skeleton != null) {
                skeleton.setVariant(Skulkin.Variant.BOW);
                skeleton.finalizeSpawn((ServerLevel)horse.level(), difficulty, MobSpawnType.TRIGGERED, null, null);
                skeleton.setPos(horse.getX(), horse.getY(), horse.getZ());
                skeleton.invulnerableTime = 60;
                skeleton.setPersistenceRequired();
                if (skeleton.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && equipArmor(skeleton)) {
                    skeleton.setItemSlot(EquipmentSlot.HEAD, Items.IRON_HELMET.getDefaultInstance());
                }

                skeleton.setItemSlot(
                        EquipmentSlot.MAINHAND,
                        EnchantmentHelper.enchantItem(
                                skeleton.getRandom(),
                                disenchant(skeleton.getMainHandItem()),
                                (int)(5.0F + difficulty.getSpecialMultiplier() * (float)skeleton.getRandom().nextInt(18)),
                                false
                        )
                );
                skeleton.setItemSlot(
                        EquipmentSlot.HEAD,
                        EnchantmentHelper.enchantItem(
                                skeleton.getRandom(),
                                disenchant(skeleton.getItemBySlot(EquipmentSlot.HEAD)),
                                (int)(5.0F + difficulty.getSpecialMultiplier() * (float)skeleton.getRandom().nextInt(18)),
                                false
                        )
                );
            }

            return skeleton;
        }

        private boolean equipArmor(Mob mob)
        {
            return (mob.level().isDay() && mob.level().canSeeSky(mob.blockPosition()));
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SkeletonHorse.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean bl = this.isSunBurnTick();
            if (bl) {
                ItemStack itemStack = this.getItemBySlot(EquipmentSlot.CHEST);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageableItem()) {
                        itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));
                        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    this.setSecondsOnFire(8);
                }
            }
        }

        super.aiStep();
    }

    public void equipArmor(ItemStack itemStack) {
        if (itemStack.getItem() instanceof HorseArmorItem) {
            setItemSlot(EquipmentSlot.CHEST, itemStack);
        }
    }
}