package com.crimsoncrips.alexsmobsinteraction.datagen;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.advancement.AMIAdvancementProvider;
import com.crimsoncrips.alexsmobsinteraction.datagen.language.locale.AMIEnglishGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.sounds.AMISoundGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIBlockTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;



@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMIDatagen {
    //Giga Props to Drull and TF for assistance (and code yoinking)//
    public static void generateData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new AMISoundGenerator(output, helper));
        generator.addProvider(event.includeServer(), new AMIAdvancementProvider(output, provider, helper));
        generator.addProvider(event.includeServer(), new AMILootGenerator(output));
        generator.addProvider(event.includeClient(), new AMIEnglishGenerator(output));
        generator.addProvider(event.includeServer(), new AMIEntityTagGenerator(output, provider, helper));
        AMIBlockTagGenerator blocktags = new AMIBlockTagGenerator(output, provider, helper);
        generator.addProvider(event.includeServer(), blocktags);
        generator.addProvider(event.includeServer(), new AMIItemTagGenerator(output, provider, blocktags.contentsGetter(), helper));

    }

}
