package com.crimsoncrips.alexsmobsinteraction.datagen.recipe;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
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

		pupaVariant(consumer, Items.SWEET_BERRIES, 1, "red");
		pupaVariant(consumer, Items.COAL, 2, "black");






	}

	private static void pupaVariant(RecipeOutput consumer, ItemLike catalyst, int variant, String name) {
		ItemStack result = new ItemStack(AMItemRegistry.LEAFCUTTER_ANT_PUPA.get());
		CompoundTag tag = new CompoundTag();
		tag.putInt(AMIUtils.PUPA_VARIANT, variant);
		result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result)
				.requires(AMItemRegistry.LEAFCUTTER_ANT_PUPA.get())
				.requires(catalyst)
				.unlockedBy("has_item", has(AMItemRegistry.LEAFCUTTER_ANT_PUPA.get()))
				.save(consumer, AlexsMobsInteraction.prefix("leafcutter_ant_pupa_" + name));
	}
}
