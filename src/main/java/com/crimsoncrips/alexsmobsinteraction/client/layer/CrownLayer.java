package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.compat.ACCompat;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderCockroach;
import com.github.alexthe666.alexsmobs.client.render.RenderCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class CrownLayer extends RenderLayer<EntityCockroach, ModelCockroach> {

    //Copy of AM crown layer
    private static final ResourceLocation TEXTURE_CROWN = new ResourceLocation("alexsmobsinteraction:textures/entity/asmon_crown.png");


    public CrownLayer(RenderLayerParent<EntityCockroach, ModelCockroach> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, EntityCockroach pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.isAlive() && !pLivingEntity.isDancing() && !pLivingEntity.hasMaracas() && ((AsmonRoach)pLivingEntity).isGod()){
            VertexConsumer crown = pBuffer.getBuffer(getParentModel().neck.getModel().renderType(TEXTURE_CROWN));
            pPoseStack.pushPose();
            pPoseStack.translate(0.115F, 1.5F, -2.2F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90F));
            pPoseStack.scale(1.3F, 1.3F, 1.3F);
            this.getParentModel().renderToBuffer(pPoseStack, crown, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            pPoseStack.popPose();

            float cameraY = Minecraft.getInstance().getEntityRenderDispatcher().camera.getYRot();

            if (ModList.get().isLoaded("alexscaves")) {
                ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();

                ItemStack haloStack = new ItemStack(ACCompat.gameController().getItem());
                pPoseStack.pushPose();
                float f = 0.1F * (float) Math.sin((pLivingEntity.tickCount + pPartialTick) * 0.1F) + (pLivingEntity.isBaby() ? 0.2F : 0F);
                pPoseStack.translate(0.0F, 1F - f, 0F);
                pPoseStack.mulPose(Axis.XP.rotationDegrees(180F));
                //Thanks drull with the math
                pPoseStack.mulPose(Axis.YP.rotationDegrees(180 - cameraY + Mth.rotLerp(pPartialTick, pLivingEntity.yBodyRotO, pLivingEntity.yBodyRot)));
                pPoseStack.scale(0.6F, 0.6F, 0.6F);
                renderer.renderItem(pLivingEntity, haloStack, ItemDisplayContext.GROUND, false, pPoseStack, pBuffer, pPackedLight);
                pPoseStack.popPose();
            }
        }

    }


}
