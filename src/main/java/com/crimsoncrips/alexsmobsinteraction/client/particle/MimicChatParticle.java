package com.crimsoncrips.alexsmobsinteraction.client.particle;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MimicChatParticle extends Particle {

    private static final int TEXT_COLOR = 0xE7B8FF;

    private static final int OUTLINE_COLOR = 0x3D1A52;

    private static final int MAX_LINE_WIDTH = 160;

    private static final float TEXT_SCALE = 0.025F;

    private static final int HOP_TICKS = AMIUtils.seconds(0.4F);

    private static final double HOP_GRAVITY = 0.052;

    private static final double HOVER_SPEED = 0.004;

    private static final int POP_IN_TICKS = AMIUtils.seconds(0.2F);

    private static final int SHRINK_TICKS = AMIUtils.seconds(0.5F);

    private final List<FormattedCharSequence> lines;

    private float scale;

    private float prevScale;

    public MimicChatParticle(ClientLevel level, double x, double y, double z, Component message) {
        super(level, x, y, z);
        this.lines = Minecraft.getInstance().font.split(message, MAX_LINE_WIDTH);
        this.lifetime = 50 + this.lines.size() * 10 + level.random.nextInt(5);
        this.yd = 0.25 + level.random.nextDouble() * 0.05;
        this.hasPhysics = false;
    }

    public static void spawnAbove(int entityId, Component message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;
        Entity entity = level.getEntity(entityId);
        if (entity == null)
            return;
        Minecraft.getInstance().particleEngine.add(new MimicChatParticle(level, entity.getX(), entity.getY() + entity.getBbHeight() + 0.3, entity.getZ(), message));
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.prevScale = this.scale;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        if (this.age < HOP_TICKS) {
            this.yd -= HOP_GRAVITY;
        } else {
            this.yd = Mth.lerp(0.3, this.yd, HOVER_SPEED);
        }
        this.move(0, this.yd, 0);
        if (this.age <= POP_IN_TICKS) {
            float progress = this.age / (float) POP_IN_TICKS;
            float overshoot = 1.70158F;
            float eased = progress - 1;
            this.scale = 1 + (overshoot + 1) * eased * eased * eased + overshoot * eased * eased;
        } else {
            this.scale = Mth.clamp((this.lifetime - this.age) / (float) SHRINK_TICKS, 0F, 1F);
        }
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
        float size = Mth.lerp(partialTicks, this.prevScale, this.scale) * TEXT_SCALE;
        if (size <= 0)
            return;
        Vec3 cameraPos = camera.getPosition();
        PoseStack poseStack = new PoseStack();
        poseStack.translate(Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x, Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y, Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(size, -size, size);
        Font font = Minecraft.getInstance().font;
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        int lineHeight = font.lineHeight + 1;
        float top = -this.lines.size() * lineHeight;
        for (int i = 0; i < this.lines.size(); i++) {
            FormattedCharSequence line = this.lines.get(i);
            font.drawInBatch8xOutline(line, -font.width(line) / 2F, top + i * lineHeight, TEXT_COLOR, OUTLINE_COLOR, poseStack.last().pose(), bufferSource, LightTexture.FULL_BRIGHT);
        }
        bufferSource.endBatch();
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        return AABB.INFINITE;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
}
