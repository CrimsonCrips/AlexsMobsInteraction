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
                                        .addEntry("redo_spelunky",
                                                "Redoable Spelunky",
                                                new ResourceLocation("paper"),
                                                new ResourceLocation(AlexsMobsInteraction.MODID, "general"),
                                                entry -> {
                                                    entry
                                                            .addSpotlightPage(
                                                                    itemIconGiver(ACBlockRegistry.SPELUNKERY_TABLE),
                                                                    page -> page.withText(
                                                                            "Allows for you to back-out of the spelunkery table and not have the tablet break," +
                                                                                    "useful for when interrupted by any unwanted interruptions").withTitle("Redoable Spelunky")
                                                            );
                                                });
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
