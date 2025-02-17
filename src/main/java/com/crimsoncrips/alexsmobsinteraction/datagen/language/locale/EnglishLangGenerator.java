package com.crimsoncrips.alexsmobsinteraction.datagen.language.locale;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.language.AMILangProvider;
import net.minecraft.data.PackOutput;

public class EnglishLangGenerator extends AMILangProvider {

	public EnglishLangGenerator(PackOutput output) {
		super(output, AlexsMobsInteraction.MODID,"en_us");
	}


	@Override
	protected void addTranslations() {
		this.addMisc("feature_disabled", "Feature Disabled");
		this.addMisc("ami_book.title","Alexs Caves Exemplified");
		this.addMisc("ami_book.description","Welcome to an exemplified reality!");


		this.addEffect("blooded","Blooded","Stained Blood");
		this.addEffect("gusting","Gusting","Flowing Sand");
		this.addEffect("skreeching","Skreeching","Skreech Your Last.");

		this.addEffectPotionMisc("health_boost","Health Boost");
		this.addEffectPotionMisc("skreeching","Skreeching");
		this.addEffectPotionMisc("gusting","Gusting");
		this.addEffectPotionMisc("long_gusting","Gusting");

		this.addDeathMessage("banana_slip",0,"%s f§kuc§rking slipped");

		this.addEnchantmentDesc("stabilizer","Stabilizer", "Stabilizes the players mind from entities");
		this.addEnchantmentDesc("stretchy_accumulation","Stretchy Accumulation", "Roll-through liquids for the rocky chestplate");
		this.addEnchantmentDesc("lightweight","Lightweight", "Cosmaws suffer no penalty when carrying heavy players");
		this.addEnchantmentDesc("rolling_thunder","Rolling Thunder", "Pickups Item and XP as it hits entities");



	}
}
