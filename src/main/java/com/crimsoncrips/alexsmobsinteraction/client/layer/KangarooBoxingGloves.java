package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class KangarooBoxingGloves extends RenderLayer<EntityKangaroo, ModelKangaroo> {


    public KangarooBoxingGloves(RenderLayerParent<EntityKangaroo, ModelKangaroo> pRenderer) {
        super(pRenderer);
    }

    private static final ResourceLocation LAYER = new ResourceLocation(AlexsMobsInteraction.MODID, "textures/entity/boxing_gloves.png");

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, EntityKangaroo entityKangaroo, float v, float v1, float v2, float v3, float v4, float v5) {
        if (AlexsMobsInteraction.COMMON_CONFIG.BOXING_GLOVES_ENABLED.get() && entityKangaroo.getOffhandItem().is(ItemStack.EMPTY.getItem()) && entityKangaroo.getMainHandItem().is(ItemStack.EMPTY.getItem())) {
            ModelKangaroo entitymodel = this.getParentModel();
            entitymodel.prepareMobModel(entityKangaroo, v, v1, v2);
            this.getParentModel().copyPropertiesTo(entitymodel);

            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(getParentModel().renderType(LAYER));
            entitymodel.setupAnim(entityKangaroo, v, v1, v3, v4, v5);
            entitymodel.renderToBuffer(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        }
    }


}