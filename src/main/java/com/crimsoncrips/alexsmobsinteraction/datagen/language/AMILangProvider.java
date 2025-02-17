package com.crimsoncrips.alexsmobsinteraction.datagen.language;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class AMILangProvider extends LanguageProvider {

	public AMILangProvider(PackOutput output,String id,String locale) {
		super(output, id, locale);
	}

	public void addEffect(String effectKey, String title, String description){
		this.add("effect.alexsmobsinteraction." + effectKey + ".title", title);
		this.add("effect.alexsmobsinteraction." + effectKey + ".description", description);
	}

	public void addEffectPotionMisc(String effectKey, String title){
		this.add("item.minecraft.splash_potion.effect." + effectKey,"Splash Potion of " + title);
		this.add("item.minecraft.tipped_arrow.effect." + effectKey,"Arrow of " + title);
		this.add("item.minecraft.lingering_potion.effect." + effectKey,"Lingering Potion of " + title);
		this.add("item.minecraft.potion.effect." + effectKey,"Potion of " + title);
	}

	public void addEnchantmentDesc(String effectKey, String title,String description){
		this.add("enchantment.alexsmobsinteraction." + effectKey, title);
		this.add("enchantment.alexsmobsinteraction." + effectKey + ".desc", description);
	}

	public void addDeathMessage(String deathKey, int number, String name) {
		this.add("death.attack." + deathKey + "_" + number, name);
	}

	public void addSubtitle(String subtitleKey,String name) {
		this.add("subtitle.alexsmobsinteraction.sound." + subtitleKey,name);
	}

	public void addMisc(String subtitleKey,String name) {
		this.add("misc.alexsmobsinteraction." + subtitleKey,name);
	}


}
