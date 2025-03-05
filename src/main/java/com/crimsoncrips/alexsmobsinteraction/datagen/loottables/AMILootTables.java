package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Set;

public class AMILootTables {
	//Props to Drull and TF for assistance//
	private static final Set<ResourceLocation> AMI_LOOT_TABLES = Sets.newHashSet();



	public static final ResourceLocation FLUTTER_SHEAR = register("entities/flutter_shear");
	public static final ResourceLocation BANANA_SHEAR = register("entities/banana_shear");
	public static final ResourceLocation GRIZZLY_BRUSH = register("entities/grizzly_brush");
	public static final ResourceLocation WITHERED_SKELEWAG = register("entities/withered_skelewag");
	public static final ResourceLocation SCAVENGE_STRADDLEBOARD = register("entities/scavenge_straddleboard");
	public static final ResourceLocation OBSIDIAN_EXTRACT = register("entities/obsidian_extract");

	public final ResourceLocation lootTable;

	private AMILootTables(String path) {
		this.lootTable = AlexsMobsInteraction.prefix(String.format("chests/%s", path));
	}

	private static ResourceLocation register(String id) {
		return register(AlexsMobsInteraction.prefix(id));
	}

	private static ResourceLocation register(ResourceLocation id) {
		if (AMI_LOOT_TABLES.add(id)) {
			return id;
		} else {
			throw new IllegalArgumentException(id + " loot table already registered");
		}
	}

	public static Set<ResourceLocation> allBuiltin() {
		return Collections.unmodifiableSet(AMI_LOOT_TABLES);
	}


}
