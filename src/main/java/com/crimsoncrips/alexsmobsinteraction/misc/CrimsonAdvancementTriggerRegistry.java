package com.crimsoncrips.alexsmobsinteraction.misc;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class CrimsonAdvancementTriggerRegistry {

    public static final CrimsonAdvancementTrigger AMI_BOOK = new CrimsonAdvancementTrigger(new ResourceLocation("alexsmobsinteraction:ami_book"));

    public static void init(){
        CriteriaTriggers.register(AMI_BOOK);

    }

}