package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.compat.ACCompat;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;


public class AsmonLayer extends RenderLayer<EntityCockroach, ModelCockroach> {

    //Copy of AM crown layer
    private static final ResourceLocation TEXTURE_CROWN = ResourceLocation.parse("alexsmobsinteraction:textures/entity/asmon_crown.png");


    private static final float CROWN_SCALE = 0.45F;

    public AsmonLayer(RenderLayerParent<EntityCockroach, ModelCockroach> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, EntityCockroach pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.isAlive() && !pLivingEntity.hasMaracas() && pLivingEntity.getData(AMIAttachments.IS_GOD)){
            VertexConsumer crown = pBuffer.getBuffer(getParentModel().neck.getModel().renderType(TEXTURE_CROWN));
            pPoseStack.pushPose();
            pPoseStack.translate(0.080F, 1.5F, -2.2F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90F));
            pPoseStack.scale(1.3F, 1.3F, 1.3F);
            this.getParentModel().renderToBuffer(pPoseStack, crown, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.popPose();

            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            ModelCockroach model = this.getParentModel();
            pPoseStack.pushPose();
            model.root.translateRotate(pPoseStack);
            model.abdomen.translateRotate(pPoseStack);
            model.neck.translateRotate(pPoseStack);
            model.head.translateRotate(pPoseStack);
            pPoseStack.translate(0.0F, -1.0F / 16.0F, -1.0F / 16.0F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(180F));
            pPoseStack.scale(CROWN_SCALE, CROWN_SCALE, CROWN_SCALE);
            pPoseStack.translate(0.0F, 0.125F, 0.0F);
            renderer.renderItem(pLivingEntity, new ItemStack(AMIItemRegistry.ASMON_CROWN.get()), ItemDisplayContext.GROUND, false, pPoseStack, pBuffer, pPackedLight);
            pPoseStack.popPose();
        }

    }


}
