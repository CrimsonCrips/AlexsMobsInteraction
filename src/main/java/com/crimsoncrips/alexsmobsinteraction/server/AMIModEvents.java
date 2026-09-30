package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCreativeTabRegistry;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class AMIModEvents {

    public static void addCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(AMCreativeTabRegistry.TAB.getKey())) {
            event.accept(AMIItemRegistry.ASMON_CROWN.get());
        }
    }
}
