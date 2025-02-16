package com.crimsoncrips.alexsmobsinteraction.datagen.tags;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class AMIItemTagGenerator extends ItemTagsProvider {
	public static final TagKey<Item> LIGHT_FEAR = create("twilight_oak_logs");

    public AMIItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
        super(output, future, provider, AlexsMobsInteraction.MODID, helper);
    }

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.copy(AMIBlockTagGenerator.LIGHT_FEAR, LIGHT_FEAR);
	}

	@Override
	public String getName() {
		return "Twilight Forest Item Tags";
	}


	public static TagKey<Item> create(String tagName) {
		return ItemTags.create(AlexsMobsInteraction.prefix(tagName));
	}

	public static TagKey<Item> makeForgeTag(String tagName) {
		return ItemTags.create(new ResourceLocation("forge", tagName));
	}
}
