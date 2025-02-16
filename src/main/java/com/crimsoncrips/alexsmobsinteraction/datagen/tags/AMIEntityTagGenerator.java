package com.crimsoncrips.alexsmobsinteraction.datagen.tags;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;


import java.util.concurrent.CompletableFuture;

public class AMIEntityTagGenerator extends EntityTypeTagsProvider {
	public static final TagKey<EntityType<?>> BURNABLE_DEAD = create(AlexsMobsInteraction.prefix("burnable_dead"));
	public static final TagKey<EntityType<?>> FLY_PESTER = create(AlexsMobsInteraction.prefix("fly_pester"));


	public AMIEntityTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper helper) {
		super(output, provider, AlexsMobsInteraction.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		super.addTags(provider);

		tag(BURNABLE_DEAD).add(
				EntityType.ZOMBIE,
				EntityType.SKELETON,
				EntityType.ZOMBIE_VILLAGER,
				EntityType.ZOMBIE_HORSE,
				EntityType.ZOGLIN,
				EntityType.ZOMBIFIED_PIGLIN,
				EntityType.PHANTOM,
				EntityType.SKELETON,
				EntityType.SKELETON_HORSE,
				EntityType.WITHER_SKELETON,
				EntityType.WITHER,
				EntityType.STRAY,
				EntityType.PHANTOM,
				EntityType.HUSK,
				EntityType.DROWNED
		);

		tag(FLY_PESTER).add(
				EntityType.COW,
				EntityType.SHEEP,
				EntityType.PIG,
				EntityType.LLAMA,
				EntityType.TRADER_LLAMA,
				EntityType.SNIFFER,
				EntityType.HORSE,
				EntityType.MULE,
				EntityType.DONKEY,
				EntityType.RAVAGER,
				AMEntityRegistry.ELEPHANT.get(),
				AMEntityRegistry.RHINOCEROS.get(),
				AMEntityRegistry.SEAL.get(),
				AMEntityRegistry.BISON.get()
		);

	}

	private static TagKey<EntityType<?>> create(ResourceLocation rl) {
		return TagKey.create(Registries.ENTITY_TYPE, rl);
	}

	@Override
	public String getName() {
		return "AMI Entity Tags";
	}
}
