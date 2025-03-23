package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.FarseerFx;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering.ALTER_PROGRESS;
import static com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering.STALK_PROGRESS;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AMIRenderTiming {
    private static long LAST_TIME = 0;

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
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
