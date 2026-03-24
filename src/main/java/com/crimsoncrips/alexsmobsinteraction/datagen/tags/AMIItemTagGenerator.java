package com.crimsoncrips.alexsmobsinteraction.datagen.tags;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class AMIItemTagGenerator extends ItemTagsProvider {
	public static final TagKey<Item> LIGHT_FEAR = create("light_fear");
	public static final TagKey<Item> EMU_TRIGGER = create("emu_trigger");
	public static final TagKey<Item> HOT = create("hot");

    public AMIItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
        super(output, future, provider, AlexsMobsInteraction.MODID, helper);
    }

	@Override
	protected void addTags(HolderLookup.Provider provider) {

		tag(EMU_TRIGGER).add(
				Items.CROSSBOW,
				Items.BOW
		);

		tag(AMTagRegistry.KOMODO_DRAGON_TAMEABLES).add(
				Items.ROTTEN_FLESH
		);

		tag(LIGHT_FEAR).add(
				Items.SHROOMLIGHT,
				Items.JACK_O_LANTERN,
				Items.LANTERN,
				Items.OCHRE_FROGLIGHT,
				Items.VERDANT_FROGLIGHT,
				Items.PEARLESCENT_FROGLIGHT,
				Items.SEA_LANTERN,
				Items.GLOWSTONE,
				Items.SOUL_LANTERN,
				Items.TORCH,
				Items.SOUL_TORCH
		);

		tag(HOT).add(
				Items.FIRE_CHARGE,
				Items.LAVA_BUCKET,
				Items.MAGMA_BLOCK,
				AMItemRegistry.LAVA_BOTTLE.get(),
				Items.MAGMA_CREAM
		);

	}

	@Override
	public String getName() {
		return "AMI Item Tags";
	}


	public static TagKey<Item> create(String tagName) {
		return ItemTags.create(AlexsMobsInteraction.prefix(tagName));
	}

	public static TagKey<Item> makeForgeTag(String tagName) {
		return ItemTags.create(new ResourceLocation("forge", tagName));
	}
}
