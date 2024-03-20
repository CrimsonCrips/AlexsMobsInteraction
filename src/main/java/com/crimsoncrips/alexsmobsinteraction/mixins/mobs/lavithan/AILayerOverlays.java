package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.lavithan;


import com.crimsoncrips.alexsmobsinteraction.AILavithanInterface;
import com.github.alexthe666.alexsmobs.client.model.ModelLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "com.github.alexthe666.alexsmobs.client.render.RenderLaviathan$LayerOverlays")

public class AILayerOverlays extends RenderLayer<EntityLaviathan, ModelLaviathan> {
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/laviathan_glow.png");
    private static final ResourceLocation TEXTURE_GEAR = new ResourceLocation("alexsmobs:textures/entity/laviathan_gear.png");
    private static final ResourceLocation TEXTURE_HELMET = new ResourceLocation("alexsmobs:textures/entity/laviathan_helmet.png");


    public AILayerOverlays(RenderLayerParent<EntityLaviathan, ModelLaviathan> pRenderer) {
        super(pRenderer);
    }


    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityLaviathan laviathan, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        AILavithanInterface myAccessor = (AILavithanInterface) laviathan;
        boolean isRelava = myAccessor.isRelava();
        if (!laviathan.isObsidian() && !isRelava) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE_GLOW));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (laviathan.hasBodyGear()) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_GEAR));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (laviathan.hasHeadGear()) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_HELMET));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

}
