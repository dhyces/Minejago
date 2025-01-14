package dev.thomasglasser.minejago.world.level.block.entity;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.platform.Services;
import dev.thomasglasser.minejago.sounds.MinejagoSoundEvents;
import dev.thomasglasser.minejago.world.item.brewing.MinejagoPotionBrewing;
import dev.thomasglasser.minejago.world.item.brewing.MinejagoPotions;
import dev.thomasglasser.minejago.world.item.crafting.MinejagoRecipeTypes;
import dev.thomasglasser.minejago.world.item.crafting.TeapotBrewingRecipe;
import dev.thomasglasser.minejago.world.level.block.TeapotBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeapotBlockEntity extends BlockEntity implements ItemHolder, Nameable
{
    private ItemStack item = ItemStack.EMPTY;

    private short brewTime;

    private int cups = 0;

    protected float temp;
    protected boolean boiling;
    protected boolean done;
    protected boolean brewing;
    protected boolean heating;
    protected Potion potion;

    private float experiencePerCup = 0;
    private int experienceCups = 6;

    private final RecipeManager.CachedCheck<Container, TeapotBrewingRecipe> quickCheck = RecipeManager.createCheck(MinejagoRecipeTypes.TEAPOT_BREWING.get());

    public TeapotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(MinejagoBlockEntityTypes.TEAPOT.get(), pPos, pBlockState);
    }

    public int getContainerSize() {
        return 1;
    }

    public boolean isEmpty() {
        return item.isEmpty();
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, TeapotBlockEntity pBlockEntity)
    {
        if (!pLevel.getBlockState(pPos.above()).isFaceSturdy(pLevel, pPos.above(), Direction.DOWN, SupportType.CENTER) && (pLevel.getBlockState(pPos.below()).is(BlockTags.FIRE) || pLevel.getBlockState(pPos.below()).is(BlockTags.CAMPFIRES)))
        {
            pLevel.destroyBlock(pPos, true);
        }

        if (pBlockEntity.cups > 0)
        {
            Optional<TeapotBrewingRecipe> recipe = pBlockEntity.quickCheck.getRecipeFor(new SimpleContainer(pBlockEntity.item), pLevel);

            pBlockEntity.cups = Math.min(pBlockEntity.cups, 6);

            if (pBlockEntity.cups < 3)
                pLevel.setBlock(pPos, pState.setValue(TeapotBlock.FILLED, false), Block.UPDATE_ALL);
            else
                pLevel.setBlock(pPos, pState.setValue(TeapotBlock.FILLED, true), Block.UPDATE_ALL);

            if (pBlockEntity.brewing)
            {
                pBlockEntity.brewTime--;
                if (pBlockEntity.brewTime <= 0)
                {
                    pBlockEntity.brewing = false;
                    pBlockEntity.done = true;
                    pLevel.playSound(null, pPos, MinejagoSoundEvents.TEAPOT_WHISTLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

                    ItemStack potionStack = PotionUtils.setPotion(new ItemStack(Items.POTION), pBlockEntity.potion);
                    if (recipe.isPresent())
                    {
                        pBlockEntity.potion = PotionUtils.getPotion(recipe.get().getResultItem(pLevel.registryAccess()));
                        pBlockEntity.experiencePerCup = recipe.get().getExperience() / pBlockEntity.cups;
                        pBlockEntity.experienceCups = pBlockEntity.cups;
                    }
                    else if (MinejagoPotionBrewing.hasTeaMix(PotionUtils.setPotion(new ItemStack(Items.POTION), pBlockEntity.potion), pBlockEntity.item))
                        pBlockEntity.potion = PotionUtils.getPotion(MinejagoPotionBrewing.mix(pBlockEntity.item, potionStack));
                    else if (PotionBrewing.hasPotionMix(PotionUtils.setPotion(new ItemStack(Items.POTION), pBlockEntity.potion), pBlockEntity.item))
                        pBlockEntity.potion = PotionUtils.getPotion(PotionBrewing.mix(pBlockEntity.item, potionStack));
                    pBlockEntity.item.shrink(1);
                    setChanged(pLevel, pPos, pState);
                } else if (pBlockEntity.item.isEmpty()) {
                    pBlockEntity.brewTime = 0;
                    pBlockEntity.brewing = false;
                    pBlockEntity.boiling = true;
                    pLevel.playSound(null, pPos, MinejagoSoundEvents.TEAPOT_WHISTLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                } else if (pBlockEntity.temp < 100) {
                    pBlockEntity.brewing = false;
                }
                setChanged(pLevel, pPos, pState);
            } else if (pBlockEntity.heating) {
                pBlockEntity.temp += 0.1;
                setChanged(pLevel, pPos, pState);
                if (pBlockEntity.temp >= 100.0) {
                    pBlockEntity.heating = false;
                    pBlockEntity.boiling = true;
                    if (!pLevel.isClientSide) pLevel.playSound(null, pPos, MinejagoSoundEvents.TEAPOT_WHISTLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    setChanged(pLevel, pPos, pState);
                }
            } else if (pBlockEntity.temp >= 100 && ((pBlockEntity.potion == Potions.WATER || !pBlockEntity.item.isEmpty()) && (PotionBrewing.hasPotionMix(PotionUtils.setPotion(new ItemStack(Items.POTION), pBlockEntity.potion), pBlockEntity.item) || recipe.isPresent() || (MinejagoPotionBrewing.hasTeaMix(PotionUtils.setPotion(new ItemStack(Items.POTION), pBlockEntity.getPotion()), pBlockEntity.item))))) {
                pBlockEntity.brewTime = (recipe.map(TeapotBrewingRecipe::getCookingTime).orElseGet(() -> RandomSource.create().nextIntBetweenInclusive(1200, 2400))).shortValue();
                pBlockEntity.brewing = true;
                pBlockEntity.boiling = false;
                pBlockEntity.done = false;
            }
            BlockState below = pLevel.getBlockState(pPos.below());
            if (pLevel.dimension() == Level.NETHER)
            {
                if (pBlockEntity.temp < 100)
                {
                    pBlockEntity.temp = 100;
                    pBlockEntity.heating = true;
                    setChanged(pLevel, pPos, pState);
                }
            }
            else if ((below.is(BlockTags.CAMPFIRES) || below.is(BlockTags.FIRE)))
            {
                if (!(pBlockEntity.brewing || pBlockEntity.boiling || pBlockEntity.done))
                {
                    pBlockEntity.heating = true;
                    setChanged(pLevel, pPos, pState);
                }
            }
            else if (pBlockEntity.temp > TeapotBlock.getBiomeTemperature(pLevel, pPos))
            {
                if (pLevel.getBiome(pPos).value().getBaseTemperature() < 2.0)
                    pBlockEntity.temp--;
                pBlockEntity.temp--;
                setChanged(pLevel, pPos, pState);
            }
        }
        else
        {
            pBlockEntity.potion = null;
            pBlockEntity.boiling = false;
            pBlockEntity.done = false;
            pBlockEntity.item = ItemStack.EMPTY;
            pBlockEntity.heating = false;
            pBlockEntity.brewing = false;
            pBlockEntity.brewTime = 0;
            pBlockEntity.temp = TeapotBlock.getBiomeTemperature(pLevel, pPos);
            setChanged(pLevel, pPos, pState);
        }
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        NonNullList<ItemStack> itemList = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, itemList);
        item = itemList.get(0);
        Potion newPotion = BuiltInRegistries.POTION.get(ResourceLocation.of(pTag.getString("Potion"), ':'));
        if (newPotion != potion)
        {
            potion = newPotion;
            if (this.level != null && this.level.isClientSide)
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
        this.temp = pTag.getShort("Temperature");
        this.cups = pTag.getShort("Cups");
        if (this.level != null && this.level.isClientSide)
        {
            this.boiling = pTag.getBoolean("Boiling");
            this.done = pTag.getBoolean("Done");
        }
        this.brewTime = pTag.getShort("BrewTime");
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, NonNullList.withSize(1, item));
        if (this.potion != Potions.EMPTY) {
            pTag.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        }
        pTag.putBoolean("Boiling", boiling);
        pTag.putBoolean("Done", done);
        pTag.putFloat("Temperature", temp);
        pTag.putInt("Cups", cups);
        pTag.putShort("BrewTime", brewTime);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setItem(int pIndex, ItemStack pStack) {
        if (pIndex == 0)
        {
            ItemStack newStack = pStack.copy();
            newStack.setCount(1);
            item = newStack;
        }
        else
        {
            Minejago.LOGGER.error("Teapot index out of bounds!");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundtag = new CompoundTag();
        this.saveAdditional(compoundtag);
        return compoundtag;
    }

    public void take(int count)
    {
        this.cups -= count;
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void setTemperature(int temp) {
        this.temp = temp;
        setChanged(level, getBlockPos(), getBlockState());
    }

    public float getTemperature() {
        return temp;
    }

    public short getBrewTime() {
        return brewTime;
    }

    public boolean isBoiling() {
        return boiling;
    }

    public boolean isDone() {
        return done;
    }

    public Potion getPotion() {
        return potion;
    }

    public int getCups() {
        return cups;
    }

    public boolean tryFill(int cups, Potion potion)
    {
        if (this.cups >= 6 || potion == null)
        {
            return false;
        } else if (potion == this.potion)
        {
            this.cups += cups;
            setChanged(level, getBlockPos(), getBlockState());
            return true;
        } else if (this.potion == null) {
            this.cups = cups;
            this.potion = potion;
            setChanged(level, getBlockPos(), getBlockState());
            return true;
        }
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleTag(CompoundTag tag) {
        Services.BLOCK_ENTITY.handleUpdateTag(this, tag);
        load(tag);
    }

    protected static void setChanged(Level pLevel, BlockPos pPos, BlockState pState) {
        BlockEntity.setChanged(pLevel, pPos, pState);
        pLevel.sendBlockUpdated(pPos, pLevel.getBlockState(pPos), pState, Block.UPDATE_ALL);
    }


    public void setPotion(Potion potion) {
        this.potion = potion;
    }

    @Override
    public Component getName() {
        return this.hasCustomName() ? this.getCustomName() : Component.translatable("container.teapot");
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getInSlot(int slot) {
        return slot == 0 ? item : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insert(int slot, @NotNull ItemStack stack) {
        if (slot == 0)
        {
            (item = stack.copy()).setCount(1);
            return stack.copy().split(1);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack extract(int slot, int amount) {
        return slot == 0 && !item.isEmpty() && amount > 0 ? item.split(amount) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotMax(int slot) {
        return 1;
    }

    @Override
    public boolean isValid(int slot, @NotNull ItemStack stack) {
        return false;
    }

    public boolean hasRecipe(ItemStack item, Level level)
    {
        return quickCheck.getRecipeFor(new SimpleContainer(item), level).isPresent() && potion == MinejagoPotions.REGULAR_TEA.get();
    }

    public void giveExperienceForCup(ServerLevel serverLevel, Vec3 pos)
    {
        if (experienceCups > 0)
        {
            ExperienceOrb.award(serverLevel, pos, (int) experiencePerCup);
            experienceCups--;
        }
    }
}
