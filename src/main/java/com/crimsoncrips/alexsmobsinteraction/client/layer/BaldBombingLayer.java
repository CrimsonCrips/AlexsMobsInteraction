package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.mixins.mobs.bald_eagle.AMIBaldEagleModelAccessor;
import com.github.alexthe666.alexsmobs.client.model.ModelBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BaldBombingLayer extends RenderLayer<EntityBaldEagle, ModelBaldEagle> {


    public BaldBombingLayer(RenderLayerParent<EntityBaldEagle, ModelBaldEagle> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, EntityBaldEagle pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        ItemStack bombStack = pLivingEntity.getItemInHand(InteractionHand.MAIN_HAND);
        if (!pLivingEntity.isBaby() && !bombStack.isEmpty()) {
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            AMIBaldEagleModelAccessor model = (AMIBaldEagleModelAccessor) this.getParentModel();
            pPoseStack.pushPose();
            model.alexsMobsInteraction$getRoot().translateAndRotate(pPoseStack);
            model.alexsMobsInteraction$getBody().translateAndRotate(pPoseStack);
            pPoseStack.translate(0.0F, 3.5F / 16.0F, 5.6F / 16.0F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90F));
            pPoseStack.scale(0.3F, 0.3F, 0.3F);
            renderer.renderItem(pLivingEntity, bombStack, ItemDisplayContext.NONE, false, pPoseStack, pBuffer, pPackedLight);
            pPoseStack.popPose();
        }
    }
}
