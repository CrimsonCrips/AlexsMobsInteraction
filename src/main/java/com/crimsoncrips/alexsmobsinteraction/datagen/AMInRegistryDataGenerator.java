package com.crimsoncrips.alexsmobsinteraction.datagen;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.AMIPortalTexture;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AMInRegistryDataGenerator extends DatapackBuiltinEntriesProvider {

	//Copy from TF
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.DAMAGE_TYPE, AMInDamageTypes::bootstrap)
			.add(Registries.ENCHANTMENT, AMIEnchantmentRegistry::bootstrap)
			.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AMIBiomeModifiers::bootstrap)
			.add(AMIPortalTexture.REGISTRY_KEY, AMIPortalTextures::bootstrap);

	public AMInRegistryDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider, BUILDER, Set.of(AlexsMobsInteraction.MODID));
	}
}
