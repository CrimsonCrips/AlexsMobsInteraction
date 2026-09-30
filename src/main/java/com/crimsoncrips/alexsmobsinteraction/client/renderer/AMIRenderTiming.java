package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.FarseerFx;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering.ALTER_PROGRESS;
import static com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering.STALK_PROGRESS;

@EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class AMIRenderTiming {
    private static long LAST_TIME = 0;

    @SubscribeEvent
    public static void onRenderTick(RenderFrameEvent.Pre event) {
        {
            final long time = Util.getNanos();
            final double deltaTime = (time - LAST_TIME) / 1.0E9D;
            LAST_TIME = time;
            if (ALTER_PROGRESS > 0) {
                ALTER_PROGRESS = Math.min(ALTER_PROGRESS - (float) deltaTime / 4, 1.0F);
            }

            if (Minecraft.getInstance().player != null){
                STALK_PROGRESS = ((FarseerFx)Minecraft.getInstance().player).getStalkTime();
            }
        }

    }
}
