package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.mixins.mobs.farseer.AMIFarseerAccessor;
import com.crimsoncrips.alexsmobsinteraction.mixins.mobs.farseer.AMIFarseerModelAccessor;
import com.crimsoncrips.alexsmobsinteraction.server.entity.EntityFarseerPortal;
import com.github.alexthe666.alexsmobs.client.model.ModelFarseer;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderFarseerPortal extends EntityRenderer<EntityFarseerPortal> {

    private static final ResourceLocation FARSEER_TEXTURE = ResourceLocation.parse("alexsmobs:textures/entity/farseer/farseer.png");
    private static final ResourceLocation[] PORTAL_TEXTURES = {
            ResourceLocation.parse("alexsmobs:textures/entity/farseer/portal_0.png"),
            ResourceLocation.parse("alexsmobs:textures/entity/farseer/portal_1.png"),
            ResourceLocation.parse("alexsmobs:textures/entity/farseer/portal_2.png"),
            ResourceLocation.parse("alexsmobs:textures/entity/farseer/portal_3.png")
    };

    private static final int HANDS_FADE_TICKS = AMIUtils.seconds(0.5F);

    private final ModelFarseer hands = new ModelFarseer(0.0F);
    private EntityFarseer puppet;

    public RenderFarseerPortal(EntityRendererProvider.Context context) {
        super(context);
        AMIFarseerModelAccessor model = (AMIFarseerModelAccessor) this.hands;
        model.alexsMobsInteraction$getHead().showModel = false;
        model.alexsMobsInteraction$getBodyCube1().showModel = false;
        model.alexsMobsInteraction$getBodyCube2().showModel = false;
    }

    @Override
    public void render(EntityFarseerPortal portal, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float age = portal.getAge(partialTick);
        if (age < EntityFarseerPortal.OPEN_TICK + HANDS_FADE_TICKS) {
            this.renderHands(portal, age, poseStack, buffer, packedLight);
        }
        this.renderPortal(portal, partialTick, poseStack, buffer);
        super.render(portal, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderHands(EntityFarseerPortal portal, float age, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (this.puppet == null || this.puppet.level() != portal.level()) {
            this.puppet = AMEntityRegistry.FARSEER.get().create(portal.level());
            if (this.puppet == null)
                return;
        }
        this.puppet.setAnimation(EntityFarseer.ANIMATION_EMERGE);
        this.puppet.setAnimationTick((int) age);
        this.puppet.tickCount = (int) age;
        ((AMIFarseerAccessor) this.puppet).alexsMobsInteraction$setFaceCameraProgress(1.0F);
        ((AMIFarseerAccessor) this.puppet).alexsMobsInteraction$setPrevFaceCameraProgress(1.0F);
        this.hands.setupAnim(this.puppet, 0.0F, 0.0F, age, 0.0F, 0.0F);

        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        float alpha = Mth.clamp(1.0F - (age - EntityFarseerPortal.OPEN_TICK) / HANDS_FADE_TICKS, 0.0F, 1.0F);
        this.hands.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucent(FARSEER_TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F));
        poseStack.popPose();
    }

    private void renderPortal(EntityFarseerPortal portal, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        float alpha = portal.getPortalAlpha(partialTick);
        if (alpha <= 0.0F)
            return;
        poseStack.pushPose();
        poseStack.scale(3.0F, 3.0F, 3.0F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(PORTAL_TEXTURES[portal.getPortalFrame(partialTick)]));
        boolean photosensitive = AlexsMobsInteraction.CLIENT_CONFIG.PHOTOSENSITIVITY_ENABLED.get();
        float shade = photosensitive ? 0.0F : 1.0F;
        int color = FastColor.ARGB32.colorFromFloat(alpha, shade, shade, shade);
        vertex(consumer, pose, 0.0F, 0, 0, 1, color);
        vertex(consumer, pose, 1.0F, 0, 1, 1, color);
        vertex(consumer, pose, 1.0F, 1, 1, 0, color);
        vertex(consumer, pose, 0.0F, 1, 0, 0, color);
        if (alpha >= 1.0F && !photosensitive) {
            VertexConsumer staticLayer = buffer.getBuffer(AMRenderTypes.STATIC_PORTAL);
            staticVertex(staticLayer, pose, 0.0F, 0, 0, 1);
            staticVertex(staticLayer, pose, 1.0F, 0, 1, 1);
            staticVertex(staticLayer, pose, 1.0F, 1, 1, 0);
            staticVertex(staticLayer, pose, 0.0F, 1, 0, 0);
        }
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.25F, 0.0F);
        poseStack.scale(1.08F, 1.08F, 1.08F);
        poseStack.translate(0.0F, -0.25F, 0.01F);
        PoseStack.Pose borderPose = poseStack.last();
        VertexConsumer border = buffer.getBuffer(RenderType.entityTranslucent(PORTAL_TEXTURES[portal.getPortalFrame(partialTick)]));
        int black = FastColor.ARGB32.colorFromFloat(alpha, 0.0F, 0.0F, 0.0F);
        vertex(border, borderPose, 0.0F, 0, 0, 1, black);
        vertex(border, borderPose, 1.0F, 0, 1, 1, black);
        vertex(border, borderPose, 1.0F, 1, 1, 0, black);
        vertex(border, borderPose, 0.0F, 1, 0, 0, black);
        poseStack.popPose();
        poseStack.popPose();
    }

    private static void staticVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, int y, int u, int v) {
        consumer.addVertex(pose.pose(), x - 0.5F, y - 0.25F, 0.0F).setUv(u, v);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, int y, int u, int v, int color) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, x - 0.5F, y - 0.25F, 0.0F)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(240)
                .setNormal(pose, 0.0F, -1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityFarseerPortal portal) {
        return PORTAL_TEXTURES[3];
    }
}
