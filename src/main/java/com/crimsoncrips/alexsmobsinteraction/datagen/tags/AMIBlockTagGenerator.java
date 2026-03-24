package com.crimsoncrips.alexsmobsinteraction.datagen.tags;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AMIBlockTagGenerator extends IntrinsicHolderTagsProvider<Block> {
	public static final TagKey<Block> LIGHT_FEAR = BlockTags.create(AlexsMobsInteraction.prefix("light_fear"));

	public AMIBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper helper) {
		super(output, Registries.BLOCK, future, block -> block.builtInRegistryHolder().key(), AlexsMobsInteraction.MODID, helper);
	}

	@Override
	public String getName() {
		return "AMI Block Tags";
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {

		tag(LIGHT_FEAR).add(
				Blocks.SHROOMLIGHT,
				Blocks.JACK_O_LANTERN,
				Blocks.LANTERN,
				Blocks.OCHRE_FROGLIGHT,
				Blocks.VERDANT_FROGLIGHT,
				Blocks.PEARLESCENT_FROGLIGHT,
				Blocks.SEA_LANTERN,
				Blocks.GLOWSTONE,
				Blocks.GLOW_LICHEN,
				Blocks.SOUL_LANTERN,
				Blocks.TORCH,
				Blocks.SOUL_TORCH,
				Blocks.WALL_TORCH,
				Blocks.SOUL_WALL_TORCH
		);

	}
}
