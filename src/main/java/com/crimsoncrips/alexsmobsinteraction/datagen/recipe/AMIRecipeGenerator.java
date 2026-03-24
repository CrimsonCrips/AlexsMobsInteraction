package com.crimsoncrips.alexsmobsinteraction.datagen.recipe;

import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class AMIRecipeGenerator extends AMIRecipeHelper {
	public AMIRecipeGenerator(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AMIItemRegistry.ASMON_CROWN.get(), 1)
				.pattern("w w")
				.pattern("fwf")
				.pattern("fff")
				.define('w', Ingredient.of(AMItemRegistry.COCKROACH_WING.get()))
				.define('f', Ingredient.of(AMItemRegistry.COCKROACH_WING_FRAGMENT.get()))
				.unlockedBy("has_item", has(AMItemRegistry.COCKROACH_WING.get()))
				.save(consumer);






	}
}
