package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class EndergradePlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {


    public EndergradePlayerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer abstractClientPlayer, float v, float v1, float v2, float v3, float v4, float v5) {
        if (abstractClientPlayer.getVehicle() instanceof EntityEndergrade && AlexsMobsInteraction.COMMON_CONFIG.VOIDED_ENDERGRADE_ENABLED.get()){
            PlayerModel<AbstractClientPlayer> entitymodel = this.getParentModel();
            entitymodel.prepareMobModel(abstractClientPlayer, v, v1, v2);
            this.getParentModel().copyPropertiesTo(entitymodel);

            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(getParentModel().renderType(ENDEGRADE_LAYER));
            entitymodel.setupAnim(abstractClientPlayer, v, v1, v3, v4, v5);
            entitymodel.renderToBuffer(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 0.4F);
        }
    }

    private static final ResourceLocation ENDEGRADE_LAYER = new ResourceLocation(AlexsMobsInteraction.MODID, "textures/entity/endergrade_player_layer.png");

}