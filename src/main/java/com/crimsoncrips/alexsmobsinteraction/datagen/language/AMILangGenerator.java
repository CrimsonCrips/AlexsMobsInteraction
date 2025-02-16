package com.crimsoncrips.alexsmobsinteraction.datagen.language;

import net.minecraft.data.PackOutput;

public class AMILangGenerator extends AMILangProvider {

	public AMILangGenerator(PackOutput output) {
		super(output);
	}


	@Override
	protected void addTranslations() {
		this.addMisc("feature_disabled", "Feature Disabled");
		this.addMisc("ace_book.title","Alexs Caves Exemplified");
		this.addMisc("ace_book.description","Welcome to an exemplified reality!");
		this.addMisc("locator_protection","Welcome to an exemplified reality!");


		this.addEffect("rabial","Rabial Infection","Deathly Nervous Disease");
		this.addEffect("sugar_crash","Sugar Crash","Sugar Crashed");

		this.addDeathMessage("rabial_water",0,"%s suffered from water with Rabies");
		this.addDeathMessage("rabial_end",0,"%s died to Rabies");
		this.addDeathMessage("sugar_crash",0,"%s died from a sugar overdose");
		this.addDeathMessage("sugar_crash",1,"%s died from a chocoverdose");
		this.addDeathMessage("stomach_damage",0,"%s's stomach exploded");
		this.addDeathMessage("depth_crush",0,"%s died from the crushing depths");
		this.addDeathMessage("depth_crush",1,"%s got Ocean Gated");
		this.addDeathMessage("depth_crush",2,"%s imploded");
		this.addDeathMessage("depth_crush",3,"%s flattened as a pancake");
		this.addDeathMessage("sweet_punish",0,"%s disobeyed");
		this.addDeathMessage("sweet_punish",1,"%s got slippered to death");
		this.addDeathMessage("sweet_punish",2,"%s got spanked for disobedience");
		this.addDeathMessage("sweet_punish",3,"%s punihsment was bitter-sweet");
		this.addDeathMessage("sweet_punish",4,"%s got their sweet relief");

		this.addSubtitle("tesla_fire","Tesla fired");
		this.addSubtitle("tesla_powerup","Tesla powering");
		this.addSubtitle("tesla_exploding","Tesla exploding");
		this.addSubtitle("caramel_eat","Caramel Cube assimilated");
		this.addSubtitle("sweet_punished","Comeuppance served");
		this.addSubtitle("pspspsps","Pspspspspsps");




	}
}
