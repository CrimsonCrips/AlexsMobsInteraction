package com.crimsoncrips.alexsmobsinteraction.client;

import net.minecraft.util.Mth;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.mojang.math.Axis;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIDataComponents;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.AMIClientProxy;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityRainFrog;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Frog;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AMIClientEvents {


    private double vibrate;
    private static final ResourceLocation BOSS_BAR_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/boss_bar_hud_overlays.png");

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
    public void itemTooltip(ItemTooltipEvent tooltipEvent) {
        ItemStack stack = tooltipEvent.getItemStack();
        if (stack.has(AMIDataComponents.MIMICKED)) {
            tooltipEvent.getToolTip().add(Component.translatable("misc.alexsmobsinteraction.mimicked").withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        if (stack.has(AMIDataComponents.MAGGOT_BAITED)) {
            tooltipEvent.getToolTip().add(Component.translatable("misc.alexsmobsinteraction.maggot_baited", stack.get(AMIDataComponents.MAGGOT_BAITED)).withStyle(ChatFormatting.GRAY));
        }
        if (stack.is(AMItemRegistry.LEAFCUTTER_ANT_PUPA.get())) {
            int variant = AMIUtils.getPupaVariant(stack);
            if (variant == 1 || variant == 2) {
                tooltipEvent.getToolTip().add(Component.translatable("misc.alexsmobsinteraction.pupa_variant_" + (variant == 1 ? "red" : "black")).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static final float SEAL_SPIN_SPEED = 2.0F;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void preRender(RenderLivingEvent.Pre preEvent) {
        if (preEvent.getEntity() instanceof EntitySeal seal && seal.getData(AMIAttachments.SPINNING_SEAL)) {
            PoseStack poseStack = preEvent.getPoseStack();
            float bodyRot = Mth.rotLerp(preEvent.getPartialTick(), seal.yBodyRotO, seal.yBodyRot);
            poseStack.pushPose();
            poseStack.translate(0.0F, seal.getBbWidth() / 2.0F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees((seal.tickCount + preEvent.getPartialTick()) * SEAL_SPIN_SPEED - bodyRot));
            poseStack.translate(0.0F, 0.0F, seal.getBbHeight() / 2.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(bodyRot));
        }
        if (preEvent.getEntity() instanceof EntityFly fly) {
            if (AMIUtils.isTransforming(fly)) {
                preEvent.getPoseStack().pushPose();
                vibrate = (fly.getRandom().nextFloat() - 0.5F) * (Math.sin((double) fly.tickCount / 50) * 0.5 + 0.5) * 0.1;
                preEvent.getPoseStack().translate(vibrate,  vibrate, vibrate);
            }
        }
        if (preEvent.getEntity() instanceof Frog frog) {
            if (AMIUtils.isTransforming(frog)) {
                preEvent.getPoseStack().pushPose();
                vibrate = (frog.getRandom().nextFloat() - 0.5F) * (Math.sin((double) frog.tickCount / 50) * 0.5 + 0.5) * 0.1;
                preEvent.getPoseStack().translate(vibrate,  vibrate, vibrate);
            }
        }
        if (preEvent.getEntity() instanceof EntityRainFrog rainFrog) {
            if (AMIUtils.isTransforming(rainFrog)) {
                preEvent.getPoseStack().pushPose();
                vibrate = (rainFrog.getRandom().nextFloat() - 0.5F) * (Math.sin((double) rainFrog.tickCount / 50) * 0.5 + 0.5) * 0.1;
                preEvent.getPoseStack().translate(vibrate,  vibrate, vibrate);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void postRender(RenderLivingEvent.Post postEvent) {
        if (postEvent.getEntity() instanceof EntitySeal seal && seal.getData(AMIAttachments.SPINNING_SEAL)) {
            postEvent.getPoseStack().popPose();
        }

        if (postEvent.getEntity() instanceof EntityFly fly) {
            if (AMIUtils.isTransforming(fly)) {
                postEvent.getPoseStack().popPose();
            }
        }
        if (postEvent.getEntity() instanceof Frog frog) {
            if (AMIUtils.isTransforming(frog)) {
                postEvent.getPoseStack().popPose();
            }
        }
        if (postEvent.getEntity() instanceof EntityRainFrog rainFrog) {
            if (AMIUtils.isTransforming(rainFrog)) {
                postEvent.getPoseStack().popPose();
            }
        }

    }



}
