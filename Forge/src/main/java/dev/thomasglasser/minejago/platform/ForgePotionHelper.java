package dev.thomasglasser.minejago.platform;

import dev.thomasglasser.minejago.platform.services.PotionHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ForgePotionHelper implements PotionHelper
{

    @Override
    public void addMix(List<PotionBrewing.Mix<Potion>> list, Potion pPotionEntry, Ingredient pPotionIngredient, Potion pPotionResult) {
        list.add(new PotionBrewing.Mix<>(ForgeRegistries.POTIONS, pPotionEntry, pPotionIngredient, pPotionResult));
    }

    @Override
    public boolean isTeaMix(PotionBrewing.Mix<Potion> mix, Potion original, ItemStack reagent) {
        return mix.from.get() == original && mix.ingredient.test(reagent);
    }

    @Override
    public ItemStack mix(PotionBrewing.Mix<Potion> mix, ItemStack potion) {
        Potion to = mix.to.get();
        return PotionUtils.setPotion(new ItemStack(potion.getItem()), to);
    }
}
