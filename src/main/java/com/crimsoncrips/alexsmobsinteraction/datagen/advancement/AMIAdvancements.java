package com.crimsoncrips.alexsmobsinteraction.datagen.advancement;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.citadel.Citadel;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class AMIAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {

	@Override
	public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper helper) {
		Advancement root = Advancement.Builder.advancement().display(
				createCitadelIcon("alexscaves:textures/misc/advancement/icon/ride_gum_worm.png"),
				Component.translatable("advancement.alexsmobsinteraction.root"),
				Component.translatable("advancement.alexsmobsinteraction.root.desc"),
				new ResourceLocation(AlexsMobsInteraction.MODID, "textures/entity/nucleeper/nucleeper_rusted_glow.png"),
				FrameType.TASK,
						false, false, false)
				.addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
				.save(consumer, "alexsmobsinteraction:root");


	}

	public ItemStack createCitadelIcon(String value){
		ItemStack itemStack = new ItemStack(Citadel.ICON_ITEM.get());
		itemStack.getOrCreateTag().putString("IconLocation",value);
	    return itemStack;
	}

}
