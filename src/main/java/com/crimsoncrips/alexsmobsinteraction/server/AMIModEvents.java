package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCreativeTabRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AMIModEvents {


    @SubscribeEvent
    public void addCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(AMCreativeTabRegistry.TAB.getKey())){
            event.accept(AMIItemRegistry.ASMON_CROWN);
        }
    }



}

