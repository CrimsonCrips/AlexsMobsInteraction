package com.crimsoncrips.alexsmobsinteraction.datagen.advancement;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.citadel.Citadel;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class AMIAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {
	/**
	 * test
	 */
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper helper) {
		Advancement root = Advancement.Builder.advancement().display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/ami_adv_icon.png"),
						Component.translatable("advancement.alexsmobsinteraction.root"),
						Component.translatable("advancement.alexsmobsinteraction.root.desc"),
						new ResourceLocation(AlexsMobsInteraction.MODID, "textures/gui/ami_bg.png"),
						FrameType.TASK,
						false, false, false)
				.addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
				.save(consumer, "alexscavesexemplified:root");

		Advancement combustable = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/combustable.png"),
						Component.translatable("advancement.alexsmobsinteraction.combustable"),
						Component.translatable("advancement.alexsmobsinteraction.combustable.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("combust", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:combustable");

		Advancement molten_bath = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/molten_bath.png"),
						Component.translatable("advancement.alexsmobsinteraction.molten_bath"),
						Component.translatable("advancement.alexsmobsinteraction.molten_bath.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("combust", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:molten_bath");

		Advancement poison_bath = (Advancement.Builder.advancement().parent(molten_bath).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/poison_bath.png"),
						Component.translatable("advancement.alexsmobsinteraction.poison_bath"),
						Component.translatable("advancement.alexsmobsinteraction.poison_bath.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("poison", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:poison_bath");

		Advancement mutate_frog = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/mutate_frog.png"),
						Component.translatable("advancement.alexsmobsinteraction.mutate_frog"),
						Component.translatable("advancement.alexsmobsinteraction.mutate_frog.desc"),
						null, FrameType.CHALLENGE, false, false, false)
				.addCriterion("mutate", new ImpossibleTrigger.TriggerInstance()))
				.rewards(AdvancementRewards.Builder.experience(100))
				.save(consumer, "alexsmobsinteraction:mutate_frog");

		Advancement observe_dormancy = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/observe_dormancy.png"),
						Component.translatable("advancement.alexsmobsinteraction.observe_dormancy"),
						Component.translatable("advancement.alexsmobsinteraction.observe_dormancy.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("observe", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:observe_dormancy");

		Advancement interupt_dormancy = (Advancement.Builder.advancement().parent(observe_dormancy).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/interupt_dormancy.png"),
						Component.translatable("advancement.alexsmobsinteraction.interupt_dormancy"),
						Component.translatable("advancement.alexsmobsinteraction.interupt_dormancy.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("interupt", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:interupt_dormancy");

		Advancement moss_propogation = (Advancement.Builder.advancement().parent(interupt_dormancy).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/moss_propogation.png"),
						Component.translatable("advancement.alexsmobsinteraction.moss_propogation"),
						Component.translatable("advancement.alexsmobsinteraction.moss_propogation.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("propogate", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:moss_propogation");

		Advancement bird_bomb = (Advancement.Builder.advancement().parent(root).display(
						Items.TNT,
						Component.translatable("advancement.alexsmobsinteraction.bird_bomb"),
						Component.translatable("advancement.alexsmobsinteraction.bird_bomb.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("bomb", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:bird_bomb");

		Advancement banana_shear = (Advancement.Builder.advancement().parent(root).display(
						AMItemRegistry.BANANA.get(),
						Component.translatable("advancement.alexsmobsinteraction.banana_shear"),
						Component.translatable("advancement.alexsmobsinteraction.banana_shear.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("banana", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:banana_shear");

		Advancement egg_steal = (Advancement.Builder.advancement().parent(root).display(
						AMIItemRegistry.EGGS.get(),
						Component.translatable("advancement.alexsmobsinteraction.egg_steal"),
						Component.translatable("advancement.alexsmobsinteraction.egg_steal.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("egg_steal", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:egg_steal");

		Advancement dart_effect = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/dart_effect.png"),
						Component.translatable("advancement.alexsmobsinteraction.dart_effect"),
						Component.translatable("advancement.alexsmobsinteraction.dart_effect.desc"),
						null, FrameType.CHALLENGE, false, false, false)
				.addCriterion("dart_effect", new ImpossibleTrigger.TriggerInstance()))
				.rewards(AdvancementRewards.Builder.experience(100))
				.save(consumer, "alexsmobsinteraction:dart_effect");

		Advancement venomous_cat = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/venomous_cat.png"),
						Component.translatable("advancement.alexsmobsinteraction.venomous_cat"),
						Component.translatable("advancement.alexsmobsinteraction.venomous_cat.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("venom", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:venomous_cat");

		Advancement light_warding = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/light_warding.png"),
						Component.translatable("advancement.alexsmobsinteraction.light_warding"),
						Component.translatable("advancement.alexsmobsinteraction.light_warding.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("lighted", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:light_warding");

		Advancement heavy_carriage = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/heavy_carriage.png"),
						Component.translatable("advancement.alexsmobsinteraction.heavy_carriage"),
						Component.translatable("advancement.alexsmobsinteraction.heavy_carriage.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("heavy", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:heavy_carriage");

		Advancement lightweight = (Advancement.Builder.advancement().parent(heavy_carriage).display(
						Items.FEATHER,
						Component.translatable("advancement.alexsmobsinteraction.lightweight"),
						Component.translatable("advancement.alexsmobsinteraction.lightweight.desc"),
						null, FrameType.CHALLENGE, false, false, false)
				.addCriterion("lightweight", new ImpossibleTrigger.TriggerInstance()))
				.rewards(AdvancementRewards.Builder.experience(100))
				.save(consumer, "alexsmobsinteraction:lightweight");

		Advancement blooded = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/mob_effect/blooded.png"),
						Component.translatable("advancement.alexsmobsinteraction.blooded"),
						Component.translatable("advancement.alexsmobsinteraction.blooded.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("blood", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:blooded");

		Advancement fly_transform = (Advancement.Builder.advancement().parent(blooded).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/fly_transform.png"),
						Component.translatable("advancement.alexsmobsinteraction.fly_transform"),
						Component.translatable("advancement.alexsmobsinteraction.fly_transform.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("fly", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:fly_transform");

		Advancement mutate_mosquito = (Advancement.Builder.advancement().parent(fly_transform).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/mutate_mosquito.png"),
						Component.translatable("advancement.alexsmobsinteraction.mutate_mosquito"),
						Component.translatable("advancement.alexsmobsinteraction.mutate_mosquito.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("mutate", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:mutate_mosquito");


		Advancement wally = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/wally.png"),
						Component.translatable("advancement.alexsmobsinteraction.wally"),
						Component.translatable("advancement.alexsmobsinteraction.wally.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("wally", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:wally");


		Advancement elephant_trample = (Advancement.Builder.advancement().parent(root).display(
						AMItemRegistry.ACACIA_BLOSSOM.get(),
						Component.translatable("advancement.alexsmobsinteraction.elephant_trample"),
						Component.translatable("advancement.alexsmobsinteraction.elephant_trample.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("trample", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:elephant_trample");

		Advancement ranged_aggro = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/ranged_aggro.png"),
						Component.translatable("advancement.alexsmobsinteraction.ranged_aggro"),
						Component.translatable("advancement.alexsmobsinteraction.ranged_aggro.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("aggro", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:ranged_aggro");

		Advancement void_dweller = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/void_dweller.png"),
						Component.translatable("advancement.alexsmobsinteraction.void_dweller"),
						Component.translatable("advancement.alexsmobsinteraction.void_dweller.desc"),
						null, FrameType.CHALLENGE, false, false, false)
				.addCriterion("void", new ImpossibleTrigger.TriggerInstance()))
				.rewards(AdvancementRewards.Builder.experience(200))
				.save(consumer, "alexsmobsinteraction:void_dweller");

		Advancement alterred = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/mob_effect/farseer_icon.png"),
						Component.translatable("advancement.alexsmobsinteraction.alterred"),
						Component.translatable("advancement.alexsmobsinteraction.alterred.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("alter", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:alterred");

		Advancement repel = (Advancement.Builder.advancement().parent(alterred).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/repel.png"),
						Component.translatable("advancement.alexsmobsinteraction.repel"),
						Component.translatable("advancement.alexsmobsinteraction.repel.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("repel", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:repel");

		Advancement withered = (Advancement.Builder.advancement().parent(root).display(
						Items.WITHER_ROSE,
						Component.translatable("advancement.alexsmobsinteraction.withered"),
						Component.translatable("advancement.alexsmobsinteraction.withered.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("withered", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:withered");

		Advancement flutter_shear = (Advancement.Builder.advancement().parent(withered).display(
						Items.SPORE_BLOSSOM,
						Component.translatable("advancement.alexsmobsinteraction.flutter_shear"),
						Component.translatable("advancement.alexsmobsinteraction.flutter_shear.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("flutter", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:flutter_shear");


		Advancement maggot_fishing = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/maggot_fishing.png"),
						Component.translatable("advancement.alexsmobsinteraction.maggot_fishing"),
						Component.translatable("advancement.alexsmobsinteraction.maggot_fishing.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("fish", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:maggot_fishing");

		Advancement brushed = (Advancement.Builder.advancement().parent(root).display(
						Items.BRUSH,
						Component.translatable("advancement.alexsmobsinteraction.brushed"),
						Component.translatable("advancement.alexsmobsinteraction.brushed.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("brushed", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:brushed");

		Advancement weight_lifting = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/weight_lifting.png"),
						Component.translatable("advancement.alexsmobsinteraction.weight_lifting"),
						Component.translatable("advancement.alexsmobsinteraction.weight_lifting.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("lift", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:weight_lifting");

		Advancement gusting = (Advancement.Builder.advancement().parent(weight_lifting).display(
						createCitadelIcon("alexsmobsinteraction:textures/mob_effect/gusting.png"),
						Component.translatable("advancement.alexsmobsinteraction.gusting"),
						Component.translatable("advancement.alexsmobsinteraction.gusting.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("gusting", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:gusting");

		Advancement sand_weaver = (Advancement.Builder.advancement().parent(gusting).display(
						AMItemRegistry.GUSTER_EYE.get(),
						Component.translatable("advancement.alexsmobsinteraction.sand_weaver"),
						Component.translatable("advancement.alexsmobsinteraction.sand_weaver.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("weave", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:sand_weaver");

		Advancement obsidian_extract = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/obsidian_extract.png"),
						Component.translatable("advancement.alexsmobsinteraction.obsidian_extract"),
						Component.translatable("advancement.alexsmobsinteraction.obsidian_extract.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("extract", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:obsidian_extract");

		Advancement uncover_roach = (Advancement.Builder.advancement().parent(root).display(
						AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get(),
						Component.translatable("advancement.alexsmobsinteraction.uncover_roach"),
						Component.translatable("advancement.alexsmobsinteraction.uncover_roach.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("uncover", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:uncover_roach");

		Advancement throw_pupi = (Advancement.Builder.advancement().parent(uncover_roach).display(
						AMItemRegistry.LEAFCUTTER_ANT_PUPA.get(),
						Component.translatable("advancement.alexsmobsinteraction.throw_pupi"),
						Component.translatable("advancement.alexsmobsinteraction.throw_pupi.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("throw", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:throw_pupi");

		Advancement mimickry = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/mimickry.png"),
						Component.translatable("advancement.alexsmobsinteraction.mimickry"),
						Component.translatable("advancement.alexsmobsinteraction.mimickry.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("mimic", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:mimickry");

		Advancement reheaded = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/reheaded.png"),
						Component.translatable("advancement.alexsmobsinteraction.reheaded"),
						Component.translatable("advancement.alexsmobsinteraction.reheaded.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("enemies", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:reheaded");

		Advancement enemy_of_enemy = (Advancement.Builder.advancement().parent(reheaded).display(
						AMItemRegistry.UNSETTLING_KIMONO.get(),
						Component.translatable("advancement.alexsmobsinteraction.enemy_of_enemy"),
						Component.translatable("advancement.alexsmobsinteraction.enemy_of_enemy.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("enemies", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:enemy_of_enemy");

		Advancement stretchy_accumulation = (Advancement.Builder.advancement().parent(reheaded).display(
						AMItemRegistry.TENDON_WHIP.get(),
						Component.translatable("advancement.alexsmobsinteraction.stretchy_accumulation"),
						Component.translatable("advancement.alexsmobsinteraction.stretchy_accumulation.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("stretch", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:stretchy_accumulation");

		Advancement potoo_vision = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/potoo_vision.png"),
						Component.translatable("advancement.alexsmobsinteraction.potoo_vision"),
						Component.translatable("advancement.alexsmobsinteraction.potoo_vision.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("vision", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:potoo_vision");

		Advancement burrow_away = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/burrow_away.png"),
						Component.translatable("advancement.alexsmobsinteraction.burrow_away"),
						Component.translatable("advancement.alexsmobsinteraction.burrow_away.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("burrow", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:burrow_away");

		Advancement rolling_thunder = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/rolling_thunder.png"),
						Component.translatable("advancement.alexsmobsinteraction.rolling_thunder"),
						Component.translatable("advancement.alexsmobsinteraction.rolling_thunder.desc"),
						null, FrameType.CHALLENGE, false, false, false)
				.addCriterion("roll", new ImpossibleTrigger.TriggerInstance()))
				.rewards(AdvancementRewards.Builder.experience(50))
				.save(consumer, "alexsmobsinteraction:rolling_thunder");

		Advancement kill_withered_skelewag = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/kill_withered_skelewag.png"),
						Component.translatable("advancement.alexsmobsinteraction.kill_withered_skelewag"),
						Component.translatable("advancement.alexsmobsinteraction.kill_withered_skelewag.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("kill", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:kill_withered_skelewag");


		Advancement acclamate = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/acclamate.png"),
						Component.translatable("advancement.alexsmobsinteraction.acclamate"),
						Component.translatable("advancement.alexsmobsinteraction.acclamate.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("acclemate", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:acclamate");

		Advancement day_of_judgement = (Advancement.Builder.advancement().parent(acclamate).display(
						createCitadelIcon("alexsmobsinteraction:textures/mob_effect/skreeching.png"),
						Component.translatable("advancement.alexsmobsinteraction.day_of_judgement"),
						Component.translatable("advancement.alexsmobsinteraction.day_of_judgement.desc"),
						null, FrameType.CHALLENGE, false, false, false)
				.addCriterion("judgement", new ImpossibleTrigger.TriggerInstance()))
				.rewards(AdvancementRewards.Builder.experience(200))
				.save(consumer, "alexsmobsinteraction:day_of_judgement");

		Advancement rearming_kill = (Advancement.Builder.advancement().parent(root).display(
						AMItemRegistry.STRADDLITE.get(),
						Component.translatable("advancement.alexsmobsinteraction.rearming_kill"),
						Component.translatable("advancement.alexsmobsinteraction.rearming_kill.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("judgement", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:rearming_kill");

		Advancement sugar_rush = (Advancement.Builder.advancement().parent(root).display(
						Items.SUGAR,
						Component.translatable("advancement.alexsmobsinteraction.sugar_rush"),
						Component.translatable("advancement.alexsmobsinteraction.sugar_rush.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("rush", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:sugar_rush");


		Advancement stomp = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/stomp.png"),
						Component.translatable("advancement.alexsmobsinteraction.stomp"),
						Component.translatable("advancement.alexsmobsinteraction.stomp.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("curse", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:stomp");

		Advancement blue_shell = (Advancement.Builder.advancement().parent(stomp).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/blue_shell.png"),
						Component.translatable("advancement.alexsmobsinteraction.blue_shell"),
						Component.translatable("advancement.alexsmobsinteraction.blue_shell.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("curse", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:blue_shell");


		Advancement trusted_riding = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/trusted_riding.png"),
						Component.translatable("advancement.alexsmobsinteraction.trusted_riding"),
						Component.translatable("advancement.alexsmobsinteraction.trusted_riding.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("trust", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:trusted_riding");

		Advancement zoglinned = (Advancement.Builder.advancement().parent(trusted_riding).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/zoglinned.png"),
						Component.translatable("advancement.alexsmobsinteraction.zoglinned"),
						Component.translatable("advancement.alexsmobsinteraction.zoglinned.desc"),
						null, FrameType.CHALLENGE, false, false, false)
				.addCriterion("zoglin", new ImpossibleTrigger.TriggerInstance()))
				.rewards(AdvancementRewards.Builder.experience(100))
				.save(consumer, "alexsmobsinteraction:zoglinned");

		Advancement voidworm_stun = (Advancement.Builder.advancement().parent(root).display(
						AMItemRegistry.VOID_WORM_EYE.get(),
						Component.translatable("advancement.alexsmobsinteraction.voidworm_stun"),
						Component.translatable("advancement.alexsmobsinteraction.voidworm_stun.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("stun", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:voidworm_stun");

		Advancement dimensional_lodestone = (Advancement.Builder.advancement().parent(voidworm_stun).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/dimensional_lodestone.png"),
						Component.translatable("advancement.alexsmobsinteraction.dimensional_lodestone"),
						Component.translatable("advancement.alexsmobsinteraction.dimensional_lodestone.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("lodestone", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:dimensional_lodestone");

		Advancement multidimensional_lodestone = (Advancement.Builder.advancement().parent(dimensional_lodestone).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/multidimensional_lodestone.png"),
						Component.translatable("advancement.alexsmobsinteraction.multidimensional_lodestone"),
						Component.translatable("advancement.alexsmobsinteraction.multidimensional_lodestone.desc"),
						null, FrameType.CHALLENGE, false, false, false)
				.addCriterion("dimension", new ImpossibleTrigger.TriggerInstance()))
				.rewards(AdvancementRewards.Builder.experience(300))
				.save(consumer, "alexsmobsinteraction:multidimensional_lodestone");

		Advancement asmongold = (Advancement.Builder.advancement().parent(root).display(
						createCitadelIcon("alexsmobsinteraction:textures/gui/adv_icon/asmongold.png"),
						Component.translatable("advancement.alexsmobsinteraction.asmongold"),
						Component.translatable("advancement.alexsmobsinteraction.asmongold.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("asmon", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:asmongold");

		Advancement vassalized = (Advancement.Builder.advancement().parent(asmongold).display(
						AMItemRegistry.COCKROACH_OOTHECA.get(),
						Component.translatable("advancement.alexsmobsinteraction.vassalized"),
						Component.translatable("advancement.alexsmobsinteraction.vassalized.desc"),
						null, FrameType.TASK, false, false, false)
				.addCriterion("vassalize", new ImpossibleTrigger.TriggerInstance()))
				.save(consumer, "alexsmobsinteraction:vassalized");

	}

	public ItemStack createCitadelIcon(String value){
		ItemStack itemStack = new ItemStack(Citadel.ICON_ITEM.get());
		itemStack.getOrCreateTag().putString("IconLocation",value);
	    return itemStack;
	}

	private PlayerTrigger.TriggerInstance advancementTrigger(Advancement advancement) {
		return this.advancementTrigger(advancement.getId().getPath());
	}

	private PlayerTrigger.TriggerInstance advancementTrigger(String name) {
		return new PlayerTrigger.TriggerInstance(CriteriaTriggers.TICK.getId(), ContextAwarePredicate.create(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().checkAdvancementDone(AlexsMobsInteraction.prefix(name), true).build())).build()));
	}

}
