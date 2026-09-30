package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.Set;

public class AMILootTables {
	//Props to Drull and TF for assistance//
	private static final Set<ResourceKey<LootTable>> AMI_LOOT_TABLES = Sets.newHashSet();



	public static final ResourceKey<LootTable> FLUTTER_SHEAR = register("entities/flutter_shear");
	public static final ResourceKey<LootTable> BANANA_SHEAR = register("entities/banana_shear");
	public static final ResourceKey<LootTable> GRIZZLY_BRUSH = register("entities/grizzly_brush");
	public static final ResourceKey<LootTable> WITHERED_SKELEWAG = register("entities/withered_skelewag");
	public static final ResourceKey<LootTable> SCAVENGE_STRADDLEBOARD = register("entities/scavenge_straddleboard");
	public static final ResourceKey<LootTable> OBSIDIAN_EXTRACT = register("entities/obsidian_extract");

	//Book Lootables
	public static final ResourceKey<LootTable> STABILIZER_ADDITION = register("glm/stabilizer_addition");
	public static final ResourceKey<LootTable> LIGHTWEIGHT_ADDITION = register("glm/lightweight_addition");
	public static final ResourceKey<LootTable> STRETCHY_ADDITION = register("glm/stretchy_addition");
	public static final ResourceKey<LootTable> ROLLING_ADDITION = register("glm/rolling_addition");

	private static ResourceKey<LootTable> register(String id) {
		return register(ResourceKey.create(Registries.LOOT_TABLE, AlexsMobsInteraction.prefix(id)));
	}

	private static ResourceKey<LootTable> register(ResourceKey<LootTable> id) {
		if (AMI_LOOT_TABLES.add(id)) {
			return id;
		} else {
			throw new IllegalArgumentException(id + " loot table already registered");
		}
	}

	public static Set<ResourceKey<LootTable>> allBuiltin() {
		return Collections.unmodifiableSet(AMI_LOOT_TABLES);
	}


}
