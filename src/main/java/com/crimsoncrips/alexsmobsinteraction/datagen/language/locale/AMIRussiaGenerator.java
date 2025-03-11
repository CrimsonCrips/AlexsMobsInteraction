package com.crimsoncrips.alexsmobsinteraction.datagen.language.locale;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.language.AMILangProvider;
import net.minecraft.data.PackOutput;

public class AMIRussiaGenerator extends AMILangProvider {

	public AMIRussiaGenerator(PackOutput output) {
		super(output, AlexsMobsInteraction.MODID,"ru_ru");
	}


	@Override
	protected void addTranslations() {

//		this.addAdvancementDesc("acclamate","Аккламация", "Сделайте неактивного крикуна активным с душой визгуна");
//		this.addAdvancementDesc("alterred","§kОООШИБКААА§r", "§k-§rПодвергнитесь влиянию прозирателя§k-§r");
//		this.addAdvancementDesc("asmongold","Король тараканов", "Дайте контроллер таракану");
//		this.addAdvancementDesc("banana_shear","Резка бананов", "Нарежьте бананового слизняка на бананы");
//		this.addAdvancementDesc("bird_bomb","Птицы-бомбардировщики", "Бросьте взрывчатку и попросите управляемого орла поднять её. Присядьте, чтобы сбросить её");
//		this.addAdvancementDesc("blooded","Земля, запятнанная кровью", "Быть нанесенным с эффектом окровавления");
//		this.addAdvancementDesc("blue_shell","Оболочка смерти", "Запустите бугорчатую черепаху с синим панцирем");
//		this.addAdvancementDesc("brushed","Пушистое расчёсывание", "Смахните немного шерсти с сытого гризли");
//		this.addAdvancementDesc("burrow_away","Тактическое отступление", "Нападите на узкорота и наблюдайте, как она прячется в безопасное место");
//		this.addAdvancementDesc("combustable","Искупление огнём", "Налейте в себя масла и окунитесь во что-нибудь горячее");
//		this.addAdvancementDesc("dart_effect","Шнурованные швырялки", "Нанесите эффект зелья на дротик, который держит капуцин");
//		this.addAdvancementDesc("day_of_judgement","Это судный день.", "Дождитесь конца эффекта визжания, БЕГИТЕ");
//		this.addAdvancementDesc("dimensional_lodestone","Пространственный магнетит", "Используйте намагниченный компас, чтобы создать портал к местоположению магнетита");
//		this.addAdvancementDesc("egg_steal","Яйца всмятку", "Нападите на моба, держа в руках его яйцо");
//		this.addAdvancementDesc("elephant_trample","Стадная паника", "Прокатитесь на слоне, чтобы растоптать более мелких существ");
//		this.addAdvancementDesc("enemy_of_enemy","Враг моего врага", "Спровоцируйте големов, надевая зловещее кимоно");
//		this.addAdvancementDesc("flutter_shear","Расцветающая смерть", "Подстригите овцу");
//		this.addAdvancementDesc("fly_transform","Летающее злодеяние", "Превратите муху в багрового комара с кровяным мешком во время окровавления");
//		this.addAdvancementDesc("gusting","Сквозь вихрь", "Имейте эффект завихрения, ускоряюшего вас и вызывающего вокруг себя мини-торнадо");
//		this.addAdvancementDesc("heavy_carriage","Космавлогическая сила", "Доведите космава до слабости из-за переноски вас в тяжёлых доспехах");
//		this.addAdvancementDesc("interupt_dormancy","Огрызнулся", "Потревожте спящую грифовую черепаху");
//		this.addAdvancementDesc("kill_withered_skelewag","Иссушённая рыба", "Kill a withered version of the skelewag by infusing one with coal");
//		this.addAdvancementDesc("light_warding","Average Gamer", "Ward off a cave centipede with light in your hands");
//		this.addAdvancementDesc("lightweight","Sandbox Carry Weight", "Using the lightweight enchantment, have a cosmaw effortlessly carry you with heavy armor");
//		this.addAdvancementDesc("maggot_fishing","Master Baiter", "Increase your luck with a maggot on your offhand while fishing");
//		this.addAdvancementDesc("mimickry","Mimickry", "Have a mimicube replicate your chat");
//		this.addAdvancementDesc("molten_bath","Molten Bath", "Pour lava on an entity with a lava bottle");
//		this.addAdvancementDesc("moss_propogation","Moss Farming", "Use bonemeal to propogate a snapping turtle's moss accumulation");
//		this.addAdvancementDesc("multidimensional_lodestone","Multi-Dimensional Lodestone", "Make a lodestone portal to go to a different dimension");
//		this.addAdvancementDesc("mutate_frog","School Dissections", "Mutate frogs with weakness and warped funguses");
//		this.addAdvancementDesc("mutate_mosquito","Geared Mosquito", "Mutate a crimson mosquito with warped muscles while weak");
//		this.addAdvancementDesc("observe_dormancy","National Geographic", "Observe a sleeping snapping turtle with a spyglass");
//		this.addAdvancementDesc("obsidian_extract","Obsidian Mining", "Extract obsidian on a solidified lavaithan");
//		this.addAdvancementDesc("poison_bath","Toxic Rain", "Pour poison on an entity with a poison bottle");
//		this.addAdvancementDesc("potoo_vision","Visionary Bird", "Hold a potoo on your hand to give night vision in dark areas");
//		this.addAdvancementDesc("ranged_aggro","Emu Strife", "Aggro an emu with a ranged weapon");
//		this.addAdvancementDesc("rearming_kill","Reloading, Mistake", "Kill a rearming Straddler");
//		this.addAdvancementDesc("reheaded","Re-Beheaded", "Behead a murmur to have its head respawn in a few seconds");
//		this.addAdvancementDesc("repel","Mind Over Matter", "Repel the effects of mind altering attacks");
//		this.addAdvancementDesc("rolling_thunder","Rolling Thunder!", "Roll through liquid with the 'Rolling Thunder' enchantment for your rocky chestplate");
//		this.addAdvancementDesc("root","Alexs Mobs Interaction", "An Interactive Universe");
//		this.addAdvancementDesc("sand_weaver","Sand Weaver", "Weave through a guster with the gusting effect");
//		this.addAdvancementDesc("stomp","Koopa Projectiles", "Jump on a terrapin, causing damage");
//		this.addAdvancementDesc("stretchy_accumulation","Stretchy Accumulation", "Pickup some xp/items with the enchantment 'Stretchy Accumulation' with a tendon whip");
//		this.addAdvancementDesc("sugar_rush","Sugar Rushed", "Speed up a sugar glider with sugar");
//		this.addAdvancementDesc("throw_pupi","Pupigation", "Throw a Leafcutter Pupi to place a new leafcutter colony");
//		this.addAdvancementDesc("trusted_riding","Tuskliking", "Give a tuskling a mushroom stew to have it trust you, just dont attack it..");
//		this.addAdvancementDesc("uncover_roach","Propagation Chambers", "Burst up a cockroach when breaking a leafcutter chamber");
//		this.addAdvancementDesc("vassalized","The Chat", "Have Asmongold vassalize more than 20 cockroaches upon conversion");
//		this.addAdvancementDesc("venomous_cat","Catbi§kt§rch", "Be inflicted with poison by a catfish");
//		this.addAdvancementDesc("void_dweller","Void Dweller", "Ride a endergrade, protecting itself and its rider from the void");
//		this.addAdvancementDesc("voidworm_stun","Shoot Down", "Stun a void worm by inflicting enough damage to it");
//		this.addAdvancementDesc("wally","Emotional Support Alligator", "Name an alligator 'Wally', pacifying it");
//		this.addAdvancementDesc("weight_lifting","Weight Lifting", "Have a guster FAIL to lift you to even be lifted 1 block for being too heavy");
//		this.addAdvancementDesc("withered","Floral Desolation", "Feed a flutter a wither flower");
//		this.addAdvancementDesc("zoglinned","Undeadly Pork", "Convert a tusklin to a zoglin");
//		this.addDeathMessage("banana_slip",0,"%s f§kuc§rking slipped");
//		this.addEffect("blooded","Blooded","Stained Blood");
//		this.addEffect("farseer_icon","§kERRORR","§k------------");
//		this.addEffect("gusting","Gusting","Flowing Sand");
//		this.addEffect("skreeching","Skreeching","Skreech Your Last.");
//		this.addEffectPotionMisc("gusting","Gusting");
//		this.addEffectPotionMisc("health_boost","Health Boost");
//		this.addEffectPotionMisc("long_gusting","Gusting");
//		this.addEffectPotionMisc("skreeching","Skreeching");
//		this.addEnchantmentDesc("lightweight","Lightweight", "Cosmaws suffer no penalty when carrying heavy players");
//		this.addEnchantmentDesc("rolling_thunder","Rolling Thunder", "Pickups Item and XP as it hits entities");
//		this.addEnchantmentDesc("stabilizer","Stabilizer", "Stabilizes the players mind from entities");
//		this.addEnchantmentDesc("stretchy_accumulation","Stretchy Accumulation", "Roll-through liquids for the rocky chestplate");
//		this.addMisc("ami_book.description","Welcome to an exemplified reality!");
//		this.addMisc("ami_book.title","Alexs Caves Exemplified");
//		this.addMisc("feature_disabled", "Feature Disabled");

	}
}
