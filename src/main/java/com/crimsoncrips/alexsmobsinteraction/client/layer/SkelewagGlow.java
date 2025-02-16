package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.github.alexmodguy.alexscaves.client.model.MineGuardianModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.client.model.ModelSkelewag;
import com.github.alexthe666.alexsmobs.client.render.RenderAnteater;
import com.github.alexthe666.alexsmobs.client.render.RenderSkelewag;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.EntitySkelewag;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SkelewagGlow extends RenderLayer<EntitySkelewag, ModelSkelewag> {

    public SkelewagGlow(RenderSkelewag render) {
        super(render);
    }

    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobsinteraction:textures/entity/skelewag/wither_skelewag_glow.png");


    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, EntitySkelewag pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.getVariant() == 2 || pLivingEntity.getVariant() == 3) {
            VertexConsumer magmaGlow = pBuffer.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            this.getParentModel().renderToBuffer(pPoseStack, magmaGlow, pPackedLight, LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0), 1.0F, 1.0F, 1.0F, 1.0F);


        }
    }
}