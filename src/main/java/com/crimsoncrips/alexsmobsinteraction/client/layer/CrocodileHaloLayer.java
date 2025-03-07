package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.compat.ACCompat;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class CrocodileHaloLayer extends RenderLayer<EntityCrocodile, ModelCrocodile> {


    public CrocodileHaloLayer(RenderLayerParent<EntityCrocodile, ModelCrocodile> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, EntityCrocodile pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!pLivingEntity.isBaby() && ((AMIBaseInterfaces) pLivingEntity).isWally()) {
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            ItemStack haloStack = new ItemStack(AMItemRegistry.HALO.get());
            pPoseStack.pushPose();
            float f = 0.1F * (float) Math.sin((pLivingEntity.tickCount + pPartialTick) * 0.1F) + (pLivingEntity.isBaby() ? 0.2F : 0F);
            pPoseStack.translate(0.0F, 0.2F - f, -2F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90F));
            pPoseStack.scale(1.3F, 1.3F, 1.3F);
            renderer.renderItem(pLivingEntity, haloStack, ItemDisplayContext.GROUND, false, pPoseStack, pBuffer, pPackedLight);
            pPoseStack.popPose();
        }
    }
}
