package com.crimsoncrips.alexsmobsinteraction.datagen.language.locale;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.language.AMILangProvider;
import net.minecraft.data.PackOutput;

public class AMIEnglishGenerator extends AMILangProvider {

	public AMIEnglishGenerator(PackOutput output) {
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
		this.addEffect("farseer_icon","§kERRORR","§k------------");

		this.addEffectPotionMisc("health_boost","Health Boost");
		this.addEffectPotionMisc("skreeching","Skreeching");
		this.addEffectPotionMisc("gusting","Gusting");
		this.addEffectPotionMisc("long_gusting","Gusting");

		this.addDeathMessage("banana_slip",0,"%s f§kuc§rking slipped");

		this.addEnchantmentDesc("stabilizer","Stabilizer", "Stabilizes the players mind from entities");
		this.addEnchantmentDesc("stretchy_accumulation","Stretchy Accumulation", "Roll-through liquids for the rocky chestplate");
		this.addEnchantmentDesc("lightweight","Lightweight", "Cosmaws suffer no penalty when carrying heavy players");
		this.addEnchantmentDesc("rolling_thunder","Rolling Thunder", "Pickups Item and XP as it hits entities");

		this.addAdvancementDesc("root","Alexs Mobs Interaction", "An Interactive Universe");
		this.addAdvancementDesc("combustable","Trials of Flames", "Pour oil in yourself and step into something hot");
		this.addAdvancementDesc("molten_bath","Molten Bath", "Pour lava on an entity with a lava bottle");
		this.addAdvancementDesc("poison_bath","Toxic Rain", "Pour poison on an entity with a poison bottle");
		this.addAdvancementDesc("mutate_frog","School Dissections", "Mutate frogs with weakness and mungal spores");
		this.addAdvancementDesc("observe_dormancy","National Geographic", "Observe a sleeping snapping turtle with a spyglass");
		this.addAdvancementDesc("interupt_dormancy","Snapped", "Disturb a sleeping snapping turtle");
		this.addAdvancementDesc("moss_propogation","Moss Farming", "Use bonemeal to propogate a snapping turtle's moss accumulation");
		this.addAdvancementDesc("bird_bomb","Bomber Birds", "Drop an explosive, and have a controlled falcon pick it up. Sneak to drop it");
		this.addAdvancementDesc("banana_shear","Banana Shearing", "Shear a banana slug for banana");
		this.addAdvancementDesc("egg_steal","Eggscape", "Aggro a mob while holding their eggs");
		this.addAdvancementDesc("dart_effect","Laced Throwables", "Apply a potion effect onto a dart held by a capuchin monkey");
		this.addAdvancementDesc("venomous_cat","Catbi§kt§rch", "Be inflicted with poison by a catfish");
		this.addAdvancementDesc("light_warding","Average Gamer", "Ward off a cave centipede with light in your hands");
		this.addAdvancementDesc("heavy_carriage","Cosmawlogical Strength", "Inflict your cosmaw weakness, when carrying you due to heavy armors");
		this.addAdvancementDesc("lightweight","Sandbox Carry Weight", "Using the lightweight enchantment, have a cosmaw effortlessly carry you with heavy armor");
		this.addAdvancementDesc("mutate_mosquito","Geared Mosquito", "Mutate a crimson mosquito with mungus spores while weak");
		this.addAdvancementDesc("blooded","Blood Stained Earth", "Be inflicted with Blooded effect");
		this.addAdvancementDesc("fly_transform","Flying Atrocity", "Mutate a fly into a crimson mosquito with mungus spores while blooded");
		this.addAdvancementDesc("wally","Emotional Support Alligator", "Name an alligator 'Wally', pacifying it");
		this.addAdvancementDesc("elephant_trample","Stampeding Herd", "Ride an elephant, to trample smaller entities");
		this.addAdvancementDesc("ranged_aggro","Emu Strife", "Aggro an emu with a ranged weapon");
		this.addAdvancementDesc("void_dweller","Void Dweller", "Ride a endergrade, protecting itself and its rider from the void");
		this.addAdvancementDesc("alterred","§kEEERRORRR", "§k-§rGet altered by a farseer.§k-§r");
		this.addAdvancementDesc("repel","Mind Over Matter", "Repel the effects of mind altering attacks");
		this.addAdvancementDesc("withered","Floral Desolation", "Feed a flutter a wither flower");
		this.addAdvancementDesc("flutter_shear","Blossoming Death", "Shear a flutter");
		this.addAdvancementDesc("maggot_fishing","Master Baiter", "Increase your luck with a maggot on your offhand while fishing");
		this.addAdvancementDesc("brushed","Furryous Brushing", "Brush off some hair from a sated grizzly");
		this.addAdvancementDesc("weight_lifting","Weight Lifting", "Have a guster lift you while being heavily armored");
		this.addAdvancementDesc("gusting","Gusting Through", "Have the gusting effect, speeding you up and summoning mini tornadoes around you");
		this.addAdvancementDesc("sand_weaver","Sand Weaver", "Weave through a guster with the gusting effect");
		this.addAdvancementDesc("obsidian_extract","Obsidian Mining", "Extract obsidian on a solidified lavaithan");
		this.addAdvancementDesc("uncover_roach","Propagation Chambers", "Burst up a cockroach when breaking a leafcutter chamber");
		this.addAdvancementDesc("throw_pupi","Pupigation", "Throw a Leafcutter Pupi to place a new leafcutter colony");
		this.addAdvancementDesc("mimickry","Mimickry", "Have a mimicube replicate your chat");
		this.addAdvancementDesc("reheaded","Re-Beheaded", "Behead a murmur to have its head respawn in a few seconds");
		this.addAdvancementDesc("enemy_of_enemy","Enemy Of My Enemy", "Aggro golems while wearing the unsettling kemono");
		this.addAdvancementDesc("stretchy_accumulation","Stretchy Accumulation", "Pickup some xp/items with the enchantment 'Stretchy Accumulation' with a tendon whip");
		this.addAdvancementDesc("potoo_vision","Visionary Bird", "Hold a potoo on your hand to give night vision in dark areas");
		this.addAdvancementDesc("burrow_away","Tactically Retreating", "Attack a rain frog and watch it burrow away to safety");
		this.addAdvancementDesc("rolling_thunder","Rolling Thunder!", "Roll through liquid with the 'Rolling Thunder' enchantment for your rocky chestplate");
		this.addAdvancementDesc("kill_withered_skelewag","Withered Fish", "Kill a withered version of the skelewag");
		this.addAdvancementDesc("acclamate","Acclamation", "Make an inactive shreaker active with a skreecher soul");
		this.addAdvancementDesc("day_of_judgement","It Is Day Of Judgement.", "Reach the end of the Skreeching effect, R U N.");
		this.addAdvancementDesc("rearming_kill","Reloading, Mistake", "Kill a rearming Straddler");
		this.addAdvancementDesc("sugar_rush","Sugar Rushed", "Speed up a sugar glider with sugar");
		this.addAdvancementDesc("stomp","Koopa Projectiles", "Jump on a terrapin, causing damage");
		this.addAdvancementDesc("blue_shell","Shell Of Death", "Launch a volatile blue shell terrapin");
		this.addAdvancementDesc("trusted_riding","Tuskliking", "Give a tuskling a mushroom stew to have it trust you, just dont attack it..");
		this.addAdvancementDesc("zoglinned","Undeadly Pork", "Convert a tusklin to a zoglin");
		this.addAdvancementDesc("voidworm_stun","Shoot Down", "Stun a void worm by inflicting enough damage to it");
		this.addAdvancementDesc("dimensional_lodestone","Dimensional Lodestone", "Use a lodestone compass to make a portal to the held lodestone's position");
		this.addAdvancementDesc("multidimensional_lodestone","Multi-Dimensional Lodestone", "Make a lodestone portal to go to a different dimension");
		this.addAdvancementDesc("asmongold","The Roach King", "Give a controller to a cockroach");
		this.addAdvancementDesc("vassalized","The Chat", "Have Asmongold vassalize more than 20 cockroaches upon conversion");


	}
}
