package com.crimsoncrips.alexsmobsinteraction.datagen.patchouli;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.reimnop.pgen.PGenBookProvider;
import com.reimnop.pgen.builder.PGenEntryBuilder;
import com.reimnop.pgen.builder.page.PGenSpotlightPageBuilder;
import com.reimnop.pgen.data.PGenMultiblock;
import com.reimnop.pgen.data.page.PGenSpotlightPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
                    book.withBookTexture("textures/gui/ami_gui_book.png").withModel("ami_book").withNameplateColor("e3cccc").withSubtitle("Bastion of Interaction").withCreativeTab(CreativeModeTabs.TOOLS_AND_UTILITIES.location())
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
                                                                    page.withTitle("Lightweight")
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
                                ;
                            });



                });

    }

    public Consumer<PGenSpotlightPageBuilder.ItemBuilder> itemIconGiver(RegistryObject itemIcon){
        return item -> {
            item.addItem(itemIcon.getKey() != null ? itemIcon.getKey().location() : new ResourceLocation("error"));
        };
    }

    public static CompoundTag nbtInt(String name, int value){
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(name,value);
        return nbt;
    }




}
