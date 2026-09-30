package com.crimsoncrips.alexsmobsinteraction.datagen;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.advancement.AMIAdvancementProvider;
import com.crimsoncrips.alexsmobsinteraction.datagen.language.AMILangGen;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMIGlobalLootModifierGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.recipe.AMIRecipeGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.sounds.AMISoundGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIBlockTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEnchantmentTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;



public class AMIDatagen {
    //Giga Props to Drull and TF for assistance (and code yoinking)//
    public static void generateData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        AMInRegistryDataGenerator registryData = generator.addProvider(event.includeServer(), new AMInRegistryDataGenerator(output, event.getLookupProvider()));
        CompletableFuture<HolderLookup.Provider> provider = registryData.getRegistryProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new AMISoundGenerator(output, helper));
        generator.addProvider(event.includeServer(), new AMIAdvancementProvider(output, provider, helper));
        generator.addProvider(event.includeServer(), new AMILootGenerator(output, provider));
        generator.addProvider(event.includeServer(), new AMIGlobalLootModifierGenerator(output, provider));
        generator.addProvider(event.includeServer(), new AMIRecipeGenerator(output, provider));
        //Lang
        generator.addProvider(event.includeClient(), new AMILangGen(output));



        generator.addProvider(event.includeServer(), new AMIEntityTagGenerator(output, provider, helper));
        AMIBlockTagGenerator blocktags = new AMIBlockTagGenerator(output, provider, helper);
        generator.addProvider(event.includeServer(), blocktags);
        generator.addProvider(event.includeServer(), new AMIItemTagGenerator(output, provider, blocktags.contentsGetter(), helper));
        generator.addProvider(event.includeServer(), new AMIEnchantmentTagGenerator(output, provider, helper));

    }

}
