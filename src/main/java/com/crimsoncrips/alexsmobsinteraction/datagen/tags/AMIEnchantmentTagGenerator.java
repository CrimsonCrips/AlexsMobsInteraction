package com.crimsoncrips.alexsmobsinteraction.datagen.tags;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class AMIEnchantmentTagGenerator extends EnchantmentTagsProvider {

	public AMIEnchantmentTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
		super(output, provider, AlexsMobsInteraction.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(EnchantmentTags.NON_TREASURE).add(
				AMIEnchantmentRegistry.LIGHTWEIGHT,
				AMIEnchantmentRegistry.ROLLING_THUNDER,
				AMIEnchantmentRegistry.STABILIZER,
				AMIEnchantmentRegistry.STRETCHY_ACCUMULATION
		);
	}

	@Override
	public String getName() {
		return "AMI Enchantment Tags";
	}
}
