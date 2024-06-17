package com.crimsoncrips.alexsmobsinteraction.effect;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;

public class AMIBrewingRecipes extends BrewingRecipe {

    private final Ingredient input;
    private final Ingredient ingredient;
    private final ItemStack outcome;

    public AMIBrewingRecipes(Ingredient input, Ingredient ingredient, ItemStack output) {
        super(input, ingredient, output);
        this.input = input;
        this.ingredient = ingredient;
        this.outcome = output;
    }


    @Override
    public boolean isInput(@Nonnull ItemStack stack) {
        if (stack == null) {
            return false;
        } else {
            ItemStack[] matchingStacks = input.getItems();
            if (matchingStacks.length == 0) {
                return stack.isEmpty();
            } else {
                for (ItemStack itemstack : matchingStacks) {
                    if (ItemStack.isSameItem(stack, itemstack) && ItemStack.isSameItemSameTags(itemstack, stack)) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

}
