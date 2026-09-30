package com.crimsoncrips.alexsmobsinteraction.datagen.advancement;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AMIAdvancementProvider extends AdvancementProvider {

	public AMIAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
		super(output, registries, existingFileHelper, List.of(new AMIAdvancements()));
	}

}