package com.crimsoncrips.alexsmobsinteraction.client.event;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.TransformingEntities;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityRainFrog;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AMIClientEvents {



    private double vibrate;



    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void preRender(RenderLivingEvent.Pre preEvent) {
        if (preEvent.getEntity() instanceof EntityFly fly) {
            TransformingEntities myAccessor = (TransformingEntities) fly;
            if (myAccessor.isTransforming()) {
                preEvent.getPoseStack().pushPose();
                vibrate = (fly.getRandom().nextFloat() - 0.5F) * (Math.sin((double) fly.tickCount / 50) * 0.5 + 0.5) * 0.1;
                preEvent.getPoseStack().translate(vibrate,  vibrate, vibrate);
            }
        }
        if (preEvent.getEntity() instanceof Frog frog) {
            TransformingEntities myAccessor = (TransformingEntities) frog;
            if (myAccessor.isTransforming()) {
                preEvent.getPoseStack().pushPose();
                vibrate = (frog.getRandom().nextFloat() - 0.5F) * (Math.sin((double) frog.tickCount / 50) * 0.5 + 0.5) * 0.1;
                preEvent.getPoseStack().translate(vibrate,  vibrate, vibrate);
            }
        }
        if (preEvent.getEntity() instanceof EntityRainFrog rainFrog) {
            TransformingEntities myAccessor = (TransformingEntities) rainFrog;
            if (myAccessor.isTransforming()) {
                preEvent.getPoseStack().pushPose();
                vibrate = (rainFrog.getRandom().nextFloat() - 0.5F) * (Math.sin((double) rainFrog.tickCount / 50) * 0.5 + 0.5) * 0.1;
                preEvent.getPoseStack().translate(vibrate,  vibrate, vibrate);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void postRender(RenderLivingEvent.Post postEvent) {

        if (postEvent.getEntity() instanceof EntityFly fly) {
            TransformingEntities myAccessor = (TransformingEntities) fly;
            if (myAccessor.isTransforming()) {
                postEvent.getPoseStack().popPose();
            }
        }
        if (postEvent.getEntity() instanceof Frog frog) {
            TransformingEntities myAccessor = (TransformingEntities) frog;
            if (myAccessor.isTransforming()) {
                postEvent.getPoseStack().popPose();
            }
        }
        if (postEvent.getEntity() instanceof EntityRainFrog rainFrog) {
            TransformingEntities myAccessor = (TransformingEntities) rainFrog;
            if (myAccessor.isTransforming()) {
                postEvent.getPoseStack().popPose();
            }
        }

    }



}
