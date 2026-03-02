package com.crimsoncrips.alexsmobsinteraction.datagen.patchouli;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.reimnop.pgen.PGenBookProvider;
import com.reimnop.pgen.builder.page.PGenSpotlightPageBuilder;
import com.reimnop.pgen.data.PGenMultiblock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
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
                                                                        .withText("Combustion occurs to oiled mobs \n" +
                                                                                "Compatibility with Soul Fire")
                                                                        .withTitle("Combustion");
                                                            });
                                                });

                                lang.addCategory("Mobs",
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
                                                                        .withText("ASTs will fall into a dormant sleep during the day and only awaken when raining or disturbed ")
                                                                        .withTitle("Snapping Dormancy");
                                                            })
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/moss_propogation.png")
                                                                        .withText("ASTs propogate moss within their shells when its within water or raining, \n" +
                                                                                "bonemeal can be applied to the AST to propagate its moss accumulation")
                                                                        .withTitle("Moss Propogation");
                                                            });
                                                })
                                        .addEntry("anaconda",
                                                "Anaconda",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "mobs"),
                                                entry -> {
                                                    entry.withSortnum(1)
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/snapping_dormancy.png")
                                                                        .withText("ASTs will fall into a dormant sleep during the day and only awaken when raining or disturbed ")
                                                                        .withTitle("Snapping Dormancy");
                                                            })
                                                            .addImagePage(page -> {
                                                                page.addImage("textures/gui/wiki/mobs/moss_propogation.png")
                                                                        .withText("ASTs propogate moss within their shells when its within water or raining, \n" +
                                                                                "bonemeal can be applied to the AST to propagate its moss accumulation")
                                                                        .withTitle("Moss Propogation");
                                                            });
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
