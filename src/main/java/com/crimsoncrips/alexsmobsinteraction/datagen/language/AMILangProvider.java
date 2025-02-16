package com.crimsoncrips.alexsmobsinteraction.datagen.language;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class AMILangProvider extends LanguageProvider {
	public AMILangProvider(PackOutput output) {
		super(output, AlexsMobsInteraction.MODID, "en_us");
	}

	public void addEffect(String effectKey, String title, String description){
		this.add("effect.alexsmobsinteraction." + effectKey + ".title", title);
		this.add("effect.alexsmobsinteraction." + effectKey + ".description", description);
	}

	public void addDeathMessage(String deathKey, int number, String name) {
		this.add("death.alexsmobsinteraction.attack." + deathKey + "_" + number, name);
	}

	public void addSubtitle(String subtitleKey,String name) {
		this.add("subtitle.alexsmobsinteraction.sound." + subtitleKey,name);
	}

	public void addMisc(String subtitleKey,String name) {
		this.add("misc.alexsmobsinteraction." + subtitleKey,name);
	}


}
