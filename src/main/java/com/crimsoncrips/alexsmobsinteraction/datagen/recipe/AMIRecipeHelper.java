package com.crimsoncrips.alexsmobsinteraction.datagen.recipe;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public abstract class AMIRecipeHelper extends RecipeProvider {
	public AMIRecipeHelper(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	//placeholder for future methods

}
