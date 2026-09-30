package com.crimsoncrips.alexsmobsinteraction.datagen;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class AMIBiomeModifiers {

    public static final ResourceKey<BiomeModifier> WITHERED_SKELEWAG_SPAWNS = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AlexsMobsInteraction.prefix("withered_skelewag_spawns"));

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        context.register(WITHERED_SKELEWAG_SPAWNS, new BiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(BiomeTags.IS_NETHER), List.of(new MobSpawnSettings.SpawnerData(AMEntityRegistry.SKELEWAG.get(), 10, 1, 2))));
    }
}
