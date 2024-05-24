package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AMIRenderTiming {
    private static long LAST_TIME = 0;

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            final long time = Util.getNanos();
            final double deltaTime = (time - LAST_TIME) / 1.0E9D;
            LAST_TIME = time;

            AMIRendering.ALPHA_PROGRESS = Math.min(AMIRendering.ALPHA_PROGRESS + (float)deltaTime * 2.0F, 1.0F);
        }
    }
}
