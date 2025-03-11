package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.client.model.ModelBaldEagle;
import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
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
        if (!pLivingEntity.isBaby() && pLivingEntity.flyProgress > 0.0F) {
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            ItemStack haloStack = pLivingEntity.getItemInHand(InteractionHand.MAIN_HAND);
            pPoseStack.pushPose();
            pPoseStack.translate(0.0F, 1.4F, 0F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90F));
            pPoseStack.scale(1.3F, 1.3F, 1.3F);
            renderer.renderItem(pLivingEntity, haloStack, ItemDisplayContext.GROUND, false, pPoseStack, pBuffer, pPackedLight);
            pPoseStack.popPose();
        }
    }
}
