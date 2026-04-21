package com.crimsoncrips.alexsmobsinteraction.datagen.patchouli;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCreativeTabRegistry;
import com.reimnop.pgen.PGenBookProvider;
import com.reimnop.pgen.builder.PGenEntryBuilder;
import com.reimnop.pgen.builder.page.PGenSpotlightPageBuilder;
import com.reimnop.pgen.data.PGenMultiblock;
import com.reimnop.pgen.data.page.PGenSpotlightPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.RegistryObject;


import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("Deprecated")
public class AMInBookProvider extends PGenBookProvider {

    public AMInBookProvider(String modId, CompletableFuture<HolderLookup.Provider> lookupProvider, PackOutput packOutput) {
        super(modId, lookupProvider, packOutput);
    }

    @Override
    protected void generate(HolderLookup.Provider provider) {
        addBook("amiwiki",
                "         AMI Wiki",
                "Welcome to an interactive universe!",
                true,
                book -> {
                    book.withBookTexture("textures/gui/ami_gui_book.png").withModel("ami_book").withNameplateColor("e3cccc").withSubtitle("Bastion of Interaction").withCreativeTab(AMCreativeTabRegistry.TAB.getId())
                            .addLanguage("en_us", lang -> {
                                //General Category
                                lang.addCategory("general",
                                                "General",
                                                "General Additions",
                                                new ResourceLocation("alexsmobsinteraction:textures/gui/adv_icon/ami_adv_icon.png"),
                                                category -> category.withSortnum(0))
                                        .addEntry("food_fx",
                                                "Food Effects",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "general"),
                                                entry -> {
                                                    entry
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/general/food_fx.png")
                                                                        .withText("Animals eating dropped food will inherit the food's effects")
                                                                        .withTitle("Food Effects");
                                                            });
                                                })
                                        .addEntry("add_targets",
                                                "Add Targets",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "general"),
                                                entry -> {
                                                    entry
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/general/add_targets.png")
                                                                        .withText("Adds extra targets for mobs to improve immersion")
                                                                        .withTitle("Add Targets");
                                                            })
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/general/cannibalism.png")
                                                                        .withText("Adds cannibalism onto mobs")
                                                                        .withTitle("Cannibalism");
                                                            });
                                                })
                                        .addEntry("tamed_friendlies",
                                                "Tamed Friendlies",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "general"),
                                                entry -> {
                                                    entry
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/general/tamed_friendlies.png")
                                                                        .withText("Some tamable animals from AM are passive when tamed")
                                                                        .withTitle("Tamed Friendlies");
                                                            });
                                                })
                                        .addEntry("egg_attack",
                                                "Egg Attack",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "general"),
                                                entry -> {
                                                    entry
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/general/egg_attack.png")
                                                                        .withText("Animals attack those seen holding their eggs")
                                                                        .withTitle("Egg Attack");
                                                            });
                                                })
                                        .addEntry("combust",
                                                "Combustion",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "general"),
                                                entry -> {
                                                    entry
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/general/combust.png")
                                                                        .withText("Combustion occurs to oiled mobs " +
                                                                                "Compatibility with Soul Fire")
                                                                        .withTitle("Combustion");
                                                            });
                                                })
                                        .addEntry("mob_transformation",
                                                "Mob Transformation",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "general"),
                                                entry -> {
                                                    entry
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMIItemRegistry.MUTATE_ITEMS),
                                                                    page -> page.withText(
                                                                            "Transform mobs into different mobs by splashing weakness and feeding their respective items \n" +
                                                                                    "Rainfrog/Frog + Warped Fungus = Warped Toad \n" +
                                                                                    "Fly + Blood Sac = Crimson Mosquito \n" +
                                                                                    "Crimson Mosquito + Warped Muscle = Warped Mosco").withTitle("Mob Transformation")
                                                            );
                                                });

                                lang.addCategory("mobs",
                                        "Mobs",
                                        "Specific Mob Additions",
                                        new ResourceLocation("alexsmobsinteraction:textures/gui/wiki/mobs/mobs.png"),
                                        category -> category.withSortnum(1))
                                        .addEntry("vanilla_mobs",
                                                "Vanilla Mobs",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(0)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/witch_additions.png")
                                                                        .withText("Witches imploy the use of poison resistance when poisoned")
                                                                        .withTitle("Poison Resistance");
                                                            });
                                                })
                                        .addEntry("alligator_snapping_turtle",
                                                "Alligator Snapping Turtle",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/snapping_dormancy.png")
                                                                        .withText("ASTs will go dormant during the day unless when raining or disturbed")
                                                                        .withTitle("Snapping Dormancy");
                                                            })
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/moss_propagation.png")
                                                                        .withText("ASTs propagate moss when in water or raining, " +
                                                                                "bone-meal can be applied to AST's to increase moss")
                                                                        .withTitle("Moss Propagation");
                                                            });
                                                })
                                        .addEntry("bald_eagle",
                                                "Bald Eagle",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/bird_bombing.png")
                                                                        .withText("Bald Eagles while controlled can pickup explosives, " +
                                                                                "allowing the user to drop it by pressing 'sneak' key")
                                                                        .withTitle("Bird Bombing");
                                                            });
                                                })
                                        .addEntry("banana_slug",
                                                "Banana Slug",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/banana_shear.png")
                                                                        .withText("Allows shearing of banana slugs for banana related items")
                                                                        .withTitle("Banana Shear");
                                                            });
                                                })
                                        .addEntry("bone_serpent",
                                                "Bone Serpent",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/body_shielding.png")
                                                                        .withText("Bone Serpent takes 1 damage while losing a body segment, " +
                                                                                "the head takes normal damage once enough are destroyed")
                                                                        .withTitle("Body Shielding");
                                                            });
                                                })
                                        .addEntry("bunfungus",
                                                "Bunfungus",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/crop_farming.png")
                                                                        .withText("Bunfungus during the day harvests nearby crops, healing a bit")
                                                                        .withTitle("Crop Farming");
                                                            });
                                                })
                                        .addEntry("capuchin_monkey",
                                                "Capuchin Monkey",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/dart_effects.png")
                                                                        .withText("Lace darts by monkeys with potion effects,by right clicking with a potion." +
                                                                                "Limited to the top effect of the given potion")
                                                                        .withTitle("Dart Effects");
                                                            });
                                                })
                                        .addEntry("catfish",
                                                "Catfish",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/cat_venom.png")
                                                                        .withText("Catfish expels poison when hurt")
                                                                        .withTitle("Cat Venom");
                                                            });
                                                })
                                        .addEntry("cave_centipede",
                                                "Cave Centipede",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/light_fear.png")
                                                                        .withText("Cave Centipedes flee from light held by players unless attacked (Compatible with Curios Lanterns)")
                                                                        .withTitle("Light Fear");
                                                            });
                                                })
                                        .addEntry("cockroach",
                                                "Cockroach",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/asmongold.png")
                                                                        .withText("Cockroaches can be given 'The Roach Crown', becoming the Roach King," +
                                                                                "nearby cockroaches also get turned into servants")
                                                                        .withTitle("Asmongold");
                                                            })
                                                            .addCraftingPage("asmon_crown", page -> {
                                                                page.withText("Cockroach servants convert other roaches and attack those that harm the king");
                                                            })
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/cockroach_mutation.png")
                                                                        .withText("[MUST HAVE ALEXS CAVES PRESENT] Cockroach mutate to Gammaroach when exposed to radiation")
                                                                        .withTitle("Cockroach Mutation");
                                                            });

                                                })
                                        .addEntry("cosmaw",
                                                "Cosmaw",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/cosmaw_weakness.png")
                                                                        .withText("Cosmaws gets weak when carrying heavily armored players, Cosmaws cannot carry players when weakened")
                                                                        .withTitle("Cosmaw Weakness");
                                                            })
                                                            .addTextPage("With the 'Lightweight' enchantment, found within the cities of the end. Cosmaws no longer get weak when carrying their owner", page ->
                                                                    {}
                                                            );

                                                })
                                        .addEntry("crimson_mosquito",
                                                "Crimson Mosquito",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/hemogenicism.png")
                                                                        .withText("'Blooded' is inflicted from blood projectiles to liquid blood from 'Biomes Of Plenty'" +
                                                                                "can be washed off with water")
                                                                        .withTitle("Hemogenicism");
                                                            })
                                                            .addTextPage("Blooded can be targeted by Crimson Mosquitoes twice as far." +
                                                                    "Blooded slows,reduces armor and damage for those inflicted.", page ->
                                                                    page.withTitle(" ")
                                                            );

                                                })
                                        .addEntry("elephant",
                                                "Elephant",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.ACACIA_BLOSSOM),
                                                                    page -> page
                                                                            .withText("Tusked elephants aggro towards players not holding Acacia Blossoms")
                                                                            .withTitle("Tusked Territorial")
                                                            )
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/elephant_trample.png")
                                                                        .withText("Ridden Elephants trample entities below 2 blocks")
                                                                        .withTitle("Elephant Trample");
                                                            });

                                                })
                                        .addEntry("emu",
                                                "Emu",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/ranged_aggro.png")
                                                                        .withText("Emu attack enemies holding ranged weapons")
                                                                        .withTitle("Ranged Aggro");
                                                            });

                                                })
                                        .addEntry("enderiophage",
                                                "Enderiophage",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/infect_interaction.png")
                                                                        .withText("Added infection interactions from Enderiophage")
                                                                        .withTitle("Infect Interraction");
                                                            })
                                                            .addTextPage("Enderiophage cannot infect mobs inflicted with Damage Resistance", page ->
                                                                    page.withTitle(" ")
                                                            )
                                                            .addMultiblockPage("Enderiophage Adaption", page -> page.withText("Enderiophage adapt to their dimension when created").withMultiblock(new PGenMultiblock(
                                                                            List.of(
                                                                                    List.of(" ", // Layer 3
                                                                                            "0",
                                                                                            " "),
                                                                                    List.of(" ", // Layer 2
                                                                                            "E",
                                                                                            " ")
                                                                            ),
                                                                            Map.of("0","alexsmobs:capsid","E","minecraft:end_rod")
                                                                    ))
                                                            );

                                                })

                                        .addEntry("endergrade",
                                                "Endergrade",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/unavoidable.png")
                                                                        .withText("Endergrade are immune to the void, including their riders")
                                                                        .withTitle("UnaVoidable");
                                                            })
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.CHORUS_ON_A_STICK),
                                                                    page -> page
                                                                            .withText("Endergrade can be boosted forward with a chorus fruit on a stick")
                                                                            .withTitle("Enderboosting")
                                                            );

                                                })
                                        .addEntry("devils_hole_pupfish",
                                                "Devils Hole Pupfish",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/devils_fishing_industry.png")
                                                                        .withText("Added interactions and trades, allowing to fish up DHPs within its chunk and trade with fishermen")
                                                                        .withTitle("Devils Fishing Industry");
                                                            });

                                                })
                                        .addEntry("farseer",
                                                "Farseer",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/farseer_altering.png")
                                                                        .withText("Farseer discombobulate targeted players, scrambling inventory and messing with the mind")
                                                                        .withTitle("Farseer Altering");
                                                            })
                                                            .addTextPage("With the 'Stabilizer' enchantment, found within the remains of a massive worm. Mind altering effects are nullified, from farseers,elder guardians and watchers (IF ALEXS CAVES IS ENABLED)", page ->
                                                                    {}
                                                            );

                                                })
                                        .addEntry("flutter",
                                                "Flutter",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/flutter_shear.png")
                                                                        .withText("Flutters can be sheared for azalea, moss and spore blossoms")
                                                                        .withTitle("Flutter Shear");
                                                            })
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/flowering_attraction.png")
                                                                        .withText("Attracts bees and hummingbirds")
                                                                        .withTitle("Flower Attraction");
                                                            });
                                                })
                                        .addEntry("fly",
                                                "Fly",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/scented_interaction.png")
                                                                        .withText("Flies get attracted to certain animals and get repelled by candles")
                                                                        .withTitle("Scented Interaction");
                                                            })
                                                            .addTextPage("Cow,Sheep,Pig,Llamas,Sniffer,Horses,Ravager,Elephant,Rhino,Seal,Bison", page ->
                                                                    {}
                                                            )
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/maggot_fishing.png")
                                                                        .withText("Casting a fishing rod with a maggot in hand increases fishing luck")
                                                                        .withTitle("Maggot Fishing");
                                                            });
                                                })

                                        .addEntry("flying_fish",
                                                "Flying Fish",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.FLYING_FISH_BOOTS),
                                                                    page -> page
                                                                            .withText("Flying Fish Boots's mechanics changed to be more fluid and based on speed and look angle")
                                                                            .withTitle("Weaving Waters")
                                                            );

                                                })

                                        .addEntry("frilled_shark",
                                                "Frilled Shark",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/bleeding_hunger.png")
                                                                        .withText("Frilled Shark attack players with exsanguination or low health")
                                                                        .withTitle("Bleeding Hunger");
                                                            });
                                                })
                                        .addEntry("guster",
                                                "Guster",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/gusting.png")
                                                                        .withText("Gusters ability to lift players is determined by their armor value")
                                                                        .withTitle("Gusting");
                                                            })
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.GUSTER_EYE),
                                                                    page -> page
                                                                            .withText("Introduces a new effect, 'Gusting'. It allows mobs to resist gusters and gusts lifting,increasing movement speed," +
                                                                                    " and players spawn gusts around them")
                                                                            .withTitle(" ")
                                                            );
                                                })
                                        .addEntry("grizzly_bear",
                                                "Grizzly Bear",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/honeyless_hunting.png")
                                                                        .withText("If they haven't had honey in a while, Grizzlies may break containers containing food")
                                                                        .withTitle("Honeyless Hunting");
                                                            })
                                                            .addRelationsPage(page -> {
                                                                page.addEntry("add_targets")
                                                                        .withText("If enabled for the grizzly bear, it will hunt for animals as well")
                                                                        .withTitle(" ");
                                                            })
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/grizzly_brushed.png")
                                                                        .withText("Grizzlies can be brushed for their hair")
                                                                        .withTitle("Grizzly Brushed");
                                                            });
                                                })
                                        .addEntry("hummingbird",
                                                "Hummingbird",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/day_pollination.png")
                                                                        .withText("Pollinates only during the day")
                                                                        .withTitle("Day Pollination");
                                                            });
                                                })
                                        .addEntry("kangaroo",
                                                "Kangaroo",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addSpotlightPage(
                                                                    itemIconGiver(new ResourceLocation("minecraft:totem_of_undying")),
                                                                    page -> page
                                                                            .withText("Allows kangaroos to be equipped with totems, also fixes non breakable weapons to break when equipped")
                                                                            .withTitle("Armaments")
                                                            );

                                                })
                                        .addEntry("laviathan",
                                                "Laviathan",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/obsidian_extract.png")
                                                                        .withText("Obsidian can be extracted from laviathans when solidified," +
                                                                                "causing them to take small damage and needing to reheat")
                                                                        .withTitle("Obsidian Extract");
                                                            });
                                                })
                                        .addEntry("leafcutter_ant",
                                                "Leafcutter Ants",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/cockroach_chamber.png")
                                                                        .withText("Leafcutter chambers with high levels of fungus may contain cockroaches when destroyed")
                                                                        .withTitle("Cockroach Chamber");
                                                            })
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.LEAFCUTTER_ANT_PUPA),
                                                                    page -> page
                                                                            .withText("Leafcutter pupi can be thrown as a projectile and spawns anthills when landing on appropriate terrain")
                                                                            .withTitle("Throwable Pupi")
                                                            )
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/ant_war.png")
                                                                        .withText("Ant variants appear, causing rivalry between ant colonies")
                                                                        .withTitle("Ant War");
                                                            });
                                                })
                                        .addEntry("mimicube",
                                                "Mimicube",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addTextPage("Mimicubes mimic their targeted players speech", page ->
                                                                    {page.withTitle("Mimickry");}
                                                            );
                                                })
                                        .addEntry("murmur",
                                                "Murmur",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/murmur_regrow.png")
                                                                        .withText("Murmur's body and head are seperate and its head will regrow until the body is dead")
                                                                        .withTitle("Murmur Regrow");
                                                            })
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.TENDON_WHIP),
                                                                    page -> page
                                                                            .withText("With the new enchantment 'Stretchy Accumulation' found within the abandoned railways" +
                                                                                    "Tendon Whips can grab items and xp for its user when launched")
                                                                            .withTitle("Tendon Grab")
                                                            )
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.UNSETTLING_KIMONO),
                                                                    page -> page
                                                                            .withText("Wearing a unsettling kimono will cause iron golems and snow golems to attack you")
                                                                            .withTitle("Unsettling Backfire")
                                                            );
                                                })
                                        .addEntry("orca",
                                                "Orca",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addTextPage("Orca's Might give neutrality towards skelewags", page ->
                                                                    {page.withTitle("Might Upgrade");}
                                                            );
                                                })
                                        .addEntry("potoo",
                                                "Potoo",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addTextPage("Potoo when held with falcon glove, " +
                                                                    "give night vision to its holder", page ->
                                                                    {page.withTitle("Visionary");}
                                                            );
                                                })
                                        .addEntry("rainfrog",
                                                "Rainfrog",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addTextPage("Rain frog burrow away when hurt", page ->
                                                                    {page.withTitle("Burrow Away");}
                                                            );
                                                })
                                        .addEntry("rocky_roller",
                                                "Rocky Roller",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.ROCKY_CHESTPLATE),
                                                                    page -> page
                                                                            .withText("With the enchantment 'Rolling Thunder', found within underground mineshafts. " +
                                                                                    "You're able to roll through liquids")
                                                                            .withTitle("Rolling Thunder")
                                                            );

                                                })
                                        .addEntry("seagull",
                                                "Seagull",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/snatch_interaction.png")
                                                                        .withText("Seagulls will avoid snatching when low on health, or when player has sombrero")
                                                                        .withTitle("Snatch Interaction");
                                                            })
                                                            .addTextPage("Seagulls will only snatch food from hands", page ->
                                                                    {page.withTitle("(OPTIONAL)");}
                                                            );
                                                })
                                        .addEntry("skreecher",
                                                "Skreecher",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/skreecher_ward.png")
                                                                        .withText("Skreecher Souls can be applied to sculk shriekers to allow them to spawn wardens")
                                                                        .withTitle("Skreecher Ward");
                                                            })
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.SKREECHER_SOUL),
                                                                    page -> page
                                                                            .withText("A new effect made from skreecher souls, that spawns a warden when the effect ends naturally")
                                                                            .withTitle("Skreech Your Last")
                                                            );
                                                })
                                        .addEntry("snow_leopard",
                                                "Snow Leopard",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/snow_luck.png")
                                                                        .withText("Snow Leopards causes slain mobs to drop their loot table 1-2 times")
                                                                        .withTitle("Snow Luck");
                                                            })
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.MOOSE_HEADGEAR),
                                                                    page -> page
                                                                            .withText("Snow Leopard will hunt weakened moose, and also attack players with moose headgear")
                                                                            .withTitle("Leopard Desires")
                                                            );
                                                })
                                        .addEntry("soul_vulture",
                                                "Soul Vulture",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/soul_steal.png")
                                                                        .withText("Soul Vultures no matter what, when attacking a mob will cause its soul level to go up")
                                                                        .withTitle("Soul Steal");
                                                            });
                                                })
                                        .addEntry("straddler",
                                                "Straddler",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/straddler_shots.png")
                                                                        .withText("Straddler can shoot a number of times before needing to reload, (default:8)")
                                                                        .withTitle("Straddler Shots");
                                                            })
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.STRADDLEBOARD),
                                                                    page -> page
                                                                            .withText("Straddleboards drop half of its material costs if it doesnt drop itself")
                                                                            .withTitle("Straddle Scavenge")
                                                            );
                                                })
                                        .addEntry("sunbird",
                                                "Sunbird",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/judgemental_returns.png")
                                                                        .withText("Provides additional effects, " +
                                                                                "blessings burn and weaken nearby undead," +
                                                                                "curses buffs nearby undead")
                                                                        .withTitle("Judgemental Returns");
                                                            });
                                                })
                                        .addEntry("terrapin",
                                                "Terrapin",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addEntityPage(new ResourceLocation("alexsmobs:terrapin"),null,page -> {
                                                                page.withText("Terrapin takes damage when stepped on")
                                                                        .withName("Terrapin Stomp");
                                                            });
                                                })
                                        .addEntry("tusklin",
                                                "Tusklin",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/zoglinned.png")
                                                                        .withText("Tusklin can be turned to zoglins with lightning")
                                                                        .withTitle("Zoglinned");
                                                            });
                                                })
                                        .addEntry("void_worm",
                                                "Void Worm",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/dimensional_lodestone.png")
                                                                        .withText("Dimensional Carver with a lodestone compass offhand," +
                                                                                "can carve a portal to the lodestone at the cost of the compass")
                                                                        .withTitle("Dimensional Lodestone");
                                                            })
                                                            .addSpotlightPage(
                                                                    itemIconGiver(AMItemRegistry.DIMENSIONAL_CARVER),
                                                                    page -> page
                                                                            .withText("Dimensional Carver can be sped up with haste")
                                                                            .withTitle("Hasty Carving")
                                                            );
                                                })
                                ;
                            });



                });

    }

    public Consumer<PGenSpotlightPageBuilder.ItemBuilder> itemIconGiver(RegistryObject itemIcon){
        return item -> {
            item.addItem(itemIcon.getKey() != null ? itemIcon.getKey().location() : new ResourceLocation("error"));
        };
    }
    public Consumer<PGenSpotlightPageBuilder.ItemBuilder> itemIconGiver(ResourceLocation resourceLocation){
        return item -> {
            item.addItem(resourceLocation != null ? resourceLocation : new ResourceLocation("error"));
        };
    }

    public static CompoundTag nbtInt(String name, int value){
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(name,value);
        return nbt;
    }




}
