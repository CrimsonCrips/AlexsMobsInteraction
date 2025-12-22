package com.crimsoncrips.alexsmobsinteraction.datagen;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AMInRegistryDataGenerator extends DatapackBuiltinEntriesProvider {

	//Copy from TF
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.DAMAGE_TYPE, AMInDamageTypes::bootstrap);

	public AMInRegistryDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider, BUILDER, Set.of(AlexsMobsInteraction.MODID));
	}
}
