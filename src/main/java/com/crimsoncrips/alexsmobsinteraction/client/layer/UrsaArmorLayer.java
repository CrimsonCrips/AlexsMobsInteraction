package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.GrizzlyExtras;
import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class UrsaArmorLayer extends RenderLayer<EntityGrizzlyBear, ModelGrizzlyBear> {


    public UrsaArmorLayer(RenderLayerParent<EntityGrizzlyBear, ModelGrizzlyBear> pRenderer) {
        super(pRenderer);
    }

    private static final ResourceLocation TEXTURE_URSA_ARMOR = new ResourceLocation("alexsmobsinteraction:textures/entity/grizzly_bear/ursa_armor.png");

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, EntityGrizzlyBear entityGrizzlyBear, float v, float v1, float v2, float v3, float v4, float v5) {
        GrizzlyExtras beatInterface = (((GrizzlyExtras)entityGrizzlyBear));
        if (beatInterface.isUrsa()){
            ModelGrizzlyBear entitymodel = this.getParentModel();
            entitymodel.prepareMobModel(entityGrizzlyBear, v, v1, v2);
            this.getParentModel().copyPropertiesTo(entitymodel);

            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(getParentModel().renderType(TEXTURE_URSA_ARMOR));
            entitymodel.setupAnim(entityGrizzlyBear, v, v1, v3, v4, v5);
            poseStack.scale(1.1F,1.2F,1.02F);
            entitymodel.renderToBuffer(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        }
    }
}