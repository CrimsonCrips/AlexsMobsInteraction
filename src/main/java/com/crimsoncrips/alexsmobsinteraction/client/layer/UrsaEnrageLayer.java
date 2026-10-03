package com.crimsoncrips.alexsmobsinteraction.client.layer;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.mixins.mobs.grizzly_bear.AMIGrizzlyBearModelAccessor;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

public class UrsaEnrageLayer extends RenderLayer<EntityGrizzlyBear, ModelGrizzlyBear> {

    private static final ResourceLocation TEXTURE_WHITE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    private static final float ALPHA = 0.45F;

    private static final float INFLATE = 0.05F;

    private static final float SNOUT_RAISE = 1.5F;

    private static final float FADE_TICKS = AMIUtils.seconds(1);

    public UrsaEnrageLayer(RenderLayerParent<EntityGrizzlyBear, ModelGrizzlyBear> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, EntityGrizzlyBear grizzlyBear, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        int enrageTime = grizzlyBear.getData(AMIAttachments.URSA_ENRAGE_TIME);
        if (!grizzlyBear.getData(AMIAttachments.URSA) || enrageTime <= 0)
            return;
        float strength = Mth.clamp((enrageTime - partialTick) / FADE_TICKS, 0.0F, 1.0F);
        int color = FastColor.ARGB32.colorFromFloat(ALPHA * strength, 1.0F, 0.0F, 0.0F);
        VertexConsumer consumer = new InflatingConsumer(bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE_WHITE)), color, INFLATE);
        ModelGrizzlyBear model = this.getParentModel();
        AMIGrizzlyBearModelAccessor accessor = (AMIGrizzlyBearModelAccessor) model;
        boolean hatShown = accessor.alexsMobsInteraction$getHat().showModel;
        boolean microphoneShown = accessor.alexsMobsInteraction$getMicrophone().showModel;
        accessor.alexsMobsInteraction$getHat().showModel = false;
        accessor.alexsMobsInteraction$getMicrophone().showModel = false;
        model.snout.rotationPointY -= SNOUT_RAISE;
        poseStack.pushPose();
        poseStack.scale(UrsaArmorLayer.SCALE_X, UrsaArmorLayer.SCALE_Y, UrsaArmorLayer.SCALE_Z);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        poseStack.popPose();
        model.snout.rotationPointY += SNOUT_RAISE;
        accessor.alexsMobsInteraction$getHat().showModel = hatShown;
        accessor.alexsMobsInteraction$getMicrophone().showModel = microphoneShown;
    }

    private static class InflatingConsumer extends VertexConsumerWrapper {
        private final int color;
        private final float inflate;
        private final Vector3f[] positions = {new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};
        private final float[] us = new float[4];
        private final float[] vs = new float[4];
        private final int[] overlays = new int[4];
        private final int[] lights = new int[4];
        private final Vector3f normal = new Vector3f();
        private int count;

        private InflatingConsumer(VertexConsumer parent, int color, float inflate) {
            super(parent);
            this.color = color;
            this.inflate = inflate;
        }

        @Override
        public void addVertex(float x, float y, float z, int color, float u, float v, int packedOverlay, int packedLight, float normalX, float normalY, float normalZ) {
            this.positions[this.count].set(x, y, z);
            this.us[this.count] = u;
            this.vs[this.count] = v;
            this.overlays[this.count] = packedOverlay;
            this.lights[this.count] = packedLight;
            this.normal.set(normalX, normalY, normalZ);
            if (++this.count < 4)
                return;
            this.count = 0;
            Vector3f center = new Vector3f(this.positions[0]).add(this.positions[1]).add(this.positions[2]).add(this.positions[3]).mul(0.25F);
            Vector3f edgeA = new Vector3f(this.positions[1]).sub(this.positions[0]);
            Vector3f edgeB = new Vector3f(this.positions[3]).sub(this.positions[0]);
            boolean hasEdgeA = edgeA.lengthSquared() > 1.0E-8F;
            boolean hasEdgeB = edgeB.lengthSquared() > 1.0E-8F;
            if (hasEdgeA)
                edgeA.normalize();
            if (hasEdgeB)
                edgeB.normalize();
            for (int i = 0; i < 4; i++) {
                Vector3f offset = new Vector3f(this.normal).mul(this.inflate);
                Vector3f fromCenter = new Vector3f(this.positions[i]).sub(center);
                if (hasEdgeA)
                    offset.add(new Vector3f(edgeA).mul(Math.signum(fromCenter.dot(edgeA)) * this.inflate));
                if (hasEdgeB)
                    offset.add(new Vector3f(edgeB).mul(Math.signum(fromCenter.dot(edgeB)) * this.inflate));
                Vector3f position = offset.add(this.positions[i]);
                super.addVertex(position.x, position.y, position.z, this.color, this.us[i], this.vs[i], this.overlays[i], this.lights[i], this.normal.x, this.normal.y, this.normal.z);
            }
        }
    }
}
