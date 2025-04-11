package com.crimsoncrips.alexsmobsinteraction.client;

import com.crimsoncrips.alexsmobsinteraction.AMIClientProxy;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.TransformingEntities;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityRainFrog;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AMIClientEvents {


    private double vibrate;
    private static final ResourceLocation BOSS_BAR_HUD_OVERLAYS = new ResourceLocation(AlexsCaves.MODID, "textures/misc/boss_bar_hud_overlays.png");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderBossOverlay(CustomizeGuiOverlayEvent.BossEventProgress event) {
        if (AMIClientProxy.bossBarRenderTypes.containsKey(event.getBossEvent().getId())) {
            int renderTypeFor = AMIClientProxy.bossBarRenderTypes.get(event.getBossEvent().getId());
            int i = event.getGuiGraphics().guiWidth();
            int j = event.getY();
            Component component = event.getBossEvent().getName();
            if (renderTypeFor == 0) {
                event.setCanceled(true);
                event.getGuiGraphics().blit(BOSS_BAR_HUD_OVERLAYS, event.getX(), event.getY(), 0, 0, 182, 15);
                int progressScaled = (int) (event.getBossEvent().getProgress() * 183.0F);
                event.getGuiGraphics().blit(BOSS_BAR_HUD_OVERLAYS, event.getX(), event.getY(), 0, 15, progressScaled, 15);
                int l = Minecraft.getInstance().font.width(component);
                int i1 = i / 2 - l / 2;
                int j1 = j - 9;
                PoseStack poseStack = event.getGuiGraphics().pose();
                poseStack.pushPose();
                poseStack.translate(i1, j1, 0);
                Minecraft.getInstance().font.drawInBatch8xOutline(component.getVisualOrderText(), 0.0F, 0.0F, 0XFF5100, 0X361515, poseStack.last().pose(), event.getGuiGraphics().bufferSource(), 240);
                poseStack.popPose();
                event.setIncrement(event.getIncrement() + 7);
            }
        }
    }

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

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void logIn(ClientPlayerNetworkEvent.LoggingIn event) {
        if (AlexsMobsInteraction.CLIENT_CONFIG.EFFECTS_REMINDER_ENABLED.get() && AlexsMobsInteraction.CLIENT_CONFIG.FARSEER_EFFECTS_ENABLED.get()) {
            event.getPlayer().displayClientMessage(Component.nullToEmpty("BEWARE! BEWARE! BEWARE! BEWARE!"),false);
            event.getPlayer().displayClientMessage(Component.nullToEmpty("THERE IS PHOTOSENSITIVE EFFECTS THE FARSEER DOES WHEN TARGETTING A PLAYER,IF YOU ARE PHOTOSENSITIVE PLEASE EITHER DISABLE THE ENTIRE FEATURE IN THE COMMON CONFIG, OR DISABLE THE PHOTOSENSITIVE EFFECTS IN THE CLIENT CONFIG SPECIFICALLY"),false);
            event.getPlayer().displayClientMessage(Component.nullToEmpty("IF YOU DONT WISH TO SEE THIS MESSAGE ANYMORE DURING LOGIN, DISABLE IT IN CLIENT CONFIG"),false);

        }
    }



}
