package com.crimsoncrips.alexsmobsinteraction.datagen.recipe;

import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class AMIRecipeGenerator extends AMIRecipeHelper {
	public AMIRecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput consumer) {

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
