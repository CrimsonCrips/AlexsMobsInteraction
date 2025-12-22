package com.crimsoncrips.alexsmobsinteraction.datagen.tags;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.entity.AMIEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;


import java.util.concurrent.CompletableFuture;

public class AMIEntityTagGenerator extends EntityTypeTagsProvider {
	public static final TagKey<EntityType<?>> BURNABLE_DEAD = create(AlexsMobsInteraction.prefix("burnable_dead"));
	public static final TagKey<EntityType<?>> FLY_PESTER = create(AlexsMobsInteraction.prefix("fly_pester"));
	public static final TagKey<EntityType<?>> INSECTS = create(AlexsMobsInteraction.prefix("insects"));
	public static final TagKey<EntityType<?>> NETHER_KILL = create(AlexsMobsInteraction.prefix("nether_kill"));
	public static final TagKey<EntityType<?>> KILL_FISHES = create(AlexsMobsInteraction.prefix("kill_fishes"));
	public static final TagKey<EntityType<?>> CROW_KILL = create(AlexsMobsInteraction.prefix("crow_kill"));
	public static final TagKey<EntityType<?>> HAMMERHEAD_KILL = create(AlexsMobsInteraction.prefix("hammerhead_kill"));
	public static final TagKey<EntityType<?>> MUDSKIPPER_KILL = create(AlexsMobsInteraction.prefix("mudskipper_kill"));
	public static final TagKey<EntityType<?>> ORCA_KILL = create(AlexsMobsInteraction.prefix("orca_kill"));
	public static final TagKey<EntityType<?>> ANACONDA_KILL = create(AlexsMobsInteraction.prefix("anaconda_kill"));
	public static final TagKey<EntityType<?>> SIGNIFICANT_PREY = create(AlexsMobsInteraction.prefix("significant_prey"));
	public static final TagKey<EntityType<?>> STRONG_PREY = create(AlexsMobsInteraction.prefix("strong_prey"));
	public static final TagKey<EntityType<?>> WEAK_PREY = create(AlexsMobsInteraction.prefix("weak_prey"));
	public static final TagKey<EntityType<?>> SHOEBILL_BABY_KILL = create(AlexsMobsInteraction.prefix("shoebill_baby_kill"));
	public static final TagKey<EntityType<?>> GRIZZLY_BEAR_KILL = create(AlexsMobsInteraction.prefix("grizzly_bear_kill"));

	public static final TagKey<EntityType<?>> CENTIPEDE_KILL = create(AlexsMobsInteraction.prefix("centipede_kill"));

	public AMIEntityTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper helper) {
		super(output, provider, AlexsMobsInteraction.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(EntityTypeTags.IMPACT_PROJECTILES).add(AMIEntityRegistry.LEAFCUTTER_PUPA.get());

		tag(ANACONDA_KILL).add(
				EntityType.ZOMBIE,
				EntityType.SKELETON,
				EntityType.ZOMBIE_VILLAGER,
				EntityType.ZOMBIE_HORSE,
				EntityType.ZOMBIFIED_PIGLIN,
				EntityType.HUSK,
				EntityType.DROWNED,
				AMEntityRegistry.RATTLESNAKE.get()

		).addTag(KILL_FISHES).addTag(SIGNIFICANT_PREY).addTag(STRONG_PREY).addTag(WEAK_PREY);

		tag(GRIZZLY_BEAR_KILL).add(
				AMEntityRegistry.BISON.get(),
				AMEntityRegistry.MOOSE.get()
		).addTag(KILL_FISHES).addTag(SIGNIFICANT_PREY).addTag(WEAK_PREY);

		tag(BURNABLE_DEAD).add(
				EntityType.ZOMBIE,
				EntityType.SKELETON,
				EntityType.ZOMBIE_VILLAGER,
				EntityType.ZOMBIE_HORSE,
				EntityType.ZOGLIN,
				EntityType.ZOMBIFIED_PIGLIN,
				EntityType.PHANTOM,
				EntityType.SKELETON,
				EntityType.SKELETON_HORSE,
				EntityType.WITHER_SKELETON,
				EntityType.WITHER,
				EntityType.STRAY,
				EntityType.HUSK,
				EntityType.DROWNED
		);

		tag(SHOEBILL_BABY_KILL).add(
				AMEntityRegistry.CROCODILE.get(),
				AMEntityRegistry.CAIMAN.get(),
				AMEntityRegistry.ANACONDA.get()
		);

		tag(SIGNIFICANT_PREY).add(
				EntityType.COW,
				EntityType.PIG,
				EntityType.HORSE,
				EntityType.MULE,
				EntityType.SHEEP,
				EntityType.DONKEY,
				EntityType.SNIFFER,
				EntityType.LLAMA,
				EntityType.TRADER_LLAMA,
				EntityType.FOX,
				EntityType.CHICKEN,
				EntityType.GOAT,
				EntityType.MOOSHROOM,
				EntityType.CAT,
				EntityType.OCELOT,
				EntityType.PANDA,
				EntityType.VILLAGER,
				EntityType.WANDERING_TRADER,
				AMEntityRegistry.MUDSKIPPER.get(),
				AMEntityRegistry.RACCOON.get(),
				AMEntityRegistry.GAZELLE.get(),
				AMEntityRegistry.SHOEBILL.get(),
				AMEntityRegistry.CROW.get(),
				AMEntityRegistry.BALD_EAGLE.get(),
				AMEntityRegistry.TASMANIAN_DEVIL.get(),
				AMEntityRegistry.MANED_WOLF.get(),
				AMEntityRegistry.TOUCAN.get(),
				AMEntityRegistry.CAPUCHIN_MONKEY.get(),
				AMEntityRegistry.TERRAPIN.get(),
				AMEntityRegistry.POTOO.get(),
				AMEntityRegistry.SKUNK.get()
		);

		tag(WEAK_PREY).add(
				EntityType.RABBIT,
				EntityType.CHICKEN,
				AMEntityRegistry.JERBOA.get(),
				EntityType.FROG,
				AMEntityRegistry.RAIN_FROG.get()
		).addTag(INSECTS);

		tag(STRONG_PREY).add(
				EntityType.HOGLIN,
				EntityType.ZOGLIN,
				AMEntityRegistry.BISON.get(),
				AMEntityRegistry.RHINOCEROS.get(),
				AMEntityRegistry.MOOSE.get(),
				AMEntityRegistry.TUSKLIN.get(),
				AMEntityRegistry.ANTEATER.get(),
				AMEntityRegistry.GELADA_MONKEY.get()
		).addTag(EntityTypeTags.RAIDERS);

		tag(FLY_PESTER).add(
				EntityType.COW,
				EntityType.SHEEP,
				EntityType.PIG,
				EntityType.LLAMA,
				EntityType.TRADER_LLAMA,
				EntityType.SNIFFER,
				EntityType.HORSE,
				EntityType.MULE,
				EntityType.DONKEY,
				EntityType.RAVAGER,
				AMEntityRegistry.ELEPHANT.get(),
				AMEntityRegistry.RHINOCEROS.get(),
				AMEntityRegistry.SEAL.get(),
				AMEntityRegistry.BISON.get()
		);

		tag(INSECTS).add(
				AMEntityRegistry.FLY.get(),
				AMEntityRegistry.SUGAR_GLIDER.get(),
				AMEntityRegistry.BANANA_SLUG.get(),
				AMEntityRegistry.RAIN_FROG.get(),
				AMEntityRegistry.COCKROACH.get(),
				EntityType.FROG,
				EntityType.SILVERFISH,
				EntityType.BEE
		);

		tag(CENTIPEDE_KILL).add(
				EntityType.SPIDER,
				EntityType.CAVE_SPIDER,
				EntityType.BAT,
				EntityType.VILLAGER,
				EntityType.WANDERING_TRADER
		).addTag(INSECTS);

		tag(CROW_KILL).add(
				AMEntityRegistry.FLY.get(),
				AMEntityRegistry.JERBOA.get(),
				AMEntityRegistry.MUDSKIPPER.get(),
				AMEntityRegistry.SUGAR_GLIDER.get(),
				AMEntityRegistry.BANANA_SLUG.get(),
				AMEntityRegistry.RAIN_FROG.get(),
				AMEntityRegistry.COCKROACH.get(),
				AMEntityRegistry.RATTLESNAKE.get(),
				AMEntityRegistry.TRIOPS.get(),
				EntityType.SILVERFISH
		).addTag(WEAK_PREY);

		tag(NETHER_KILL).add(
				EntityType.HOGLIN,
				EntityType.PIGLIN,
				EntityType.PIGLIN_BRUTE,
				AMEntityRegistry.DROPBEAR.get(),
				AMEntityRegistry.WARPED_TOAD.get(),
				AMEntityRegistry.SOUL_VULTURE.get()
		);

		tag(KILL_FISHES).add(
				EntityType.COD,
				EntityType.SALMON,
				EntityType.TROPICAL_FISH,
				EntityType.SQUID,
				EntityType.GLOW_SQUID,
				EntityType.PUFFERFISH,
				AMEntityRegistry.DEVILS_HOLE_PUPFISH.get(),
				AMEntityRegistry.FLYING_FISH.get(),
				AMEntityRegistry.BLOBFISH.get(),
				AMEntityRegistry.CATFISH.get()
		);

		tag(HAMMERHEAD_KILL).add(
				AMEntityRegistry.LOBSTER.get(),
				AMEntityRegistry.MANTIS_SHRIMP.get()
		).addTag(KILL_FISHES);

		tag(MUDSKIPPER_KILL).add(
				AMEntityRegistry.LOBSTER.get()
		).addTag(INSECTS);

		tag(ORCA_KILL).add(
				AMEntityRegistry.FRILLED_SHARK.get(),
				AMEntityRegistry.HAMMERHEAD_SHARK.get(),
				EntityType.DROWNED,
				AMEntityRegistry.GIANT_SQUID.get()
		).addTag(KILL_FISHES);


	}

	private static TagKey<EntityType<?>> create(ResourceLocation rl) {
		return TagKey.create(Registries.ENTITY_TYPE, rl);
	}

	@Override
	public String getName() {
		return "AMI Entity Tags";
	}
}
