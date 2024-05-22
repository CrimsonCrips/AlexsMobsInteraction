package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCapuchinItem;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(LayerCapuchinItem.class)
public abstract class AMILayerCapuchinItem extends RenderLayer<EntityCapuchinMonkey, ModelCapuchinMonkey> {


    public AMILayerCapuchinItem(RenderLayerParent<EntityCapuchinMonkey, ModelCapuchinMonkey> pRenderer) {
        super(pRenderer);
    }

    private static final ResourceLocation DART_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/ancient_dart.png");
    private static final ModelAncientDart DART_MODEL = new ModelAncientDart();

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityCapuchinMonkey entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.hasDart()) {
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.35F, 0.35F, 0.35F);
                matrixStackIn.translate(0.5, 2.6, 0.15);
                this.translateToHand(false, matrixStackIn);
                matrixStackIn.translate(-0.65, -0.75, -0.10000000149011612);
                matrixStackIn.scale(2.8F, 2.8F, 2.8F);
            } else {
                this.translateToHand(false, matrixStackIn);
            }

            float f = 0.0F;
            if (entitylivingbaseIn.getAnimation() == EntityCapuchinMonkey.ANIMATION_THROW) {
                if (entitylivingbaseIn.getAnimationTick() < 6) {
                    f = Math.min(3.0F, (float)entitylivingbaseIn.getAnimationTick() + partialTicks) * 60.0F;
                } else {
                    f = (12.0F - ((float)entitylivingbaseIn.getAnimationTick() + partialTicks)) * 30.0F;
                }
            }

            matrixStackIn.translate(0.0F, 0.5F, 0.0F);
            matrixStackIn.scale(1.2F, 1.2F, 1.2F);
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(f));
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(DART_MODEL.renderType(DART_TEXTURE));
            DART_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        } else if (entitylivingbaseIn.getAnimation() == EntityCapuchinMonkey.ANIMATION_THROW && entitylivingbaseIn.getAnimationTick() <= 5) {
            ItemStack itemstack;
            if (AMInteractionConfig.GOOFY_CAPUCHIN_BOMB_ENABLED && AMInteractionConfig.GOOFY_MODE_ENABLED) itemstack = new ItemStack(Items.TNT);
            else itemstack = new ItemStack(Items.COBBLESTONE);
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.35F, 0.35F, 0.35F);
                matrixStackIn.translate(0.5, 2.6, 0.15);
                this.translateToHand(false, matrixStackIn);
                matrixStackIn.translate(-0.4F, 0.75F, -0.0F);
                matrixStackIn.scale(2.8F, 2.8F, 2.8F);
            } else {
                this.translateToHand(false, matrixStackIn);
                matrixStackIn.translate(0.125F, 0.5F, 0.1F);
            }

            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F));
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            renderer.renderItem(entitylivingbaseIn, itemstack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }

    }

    protected void translateToHand(boolean left, PoseStack matrixStack) {
        ((ModelCapuchinMonkey)this.getParentModel()).root.translateAndRotate(matrixStack);
        ((ModelCapuchinMonkey)this.getParentModel()).body.translateAndRotate(matrixStack);
        ((ModelCapuchinMonkey)this.getParentModel()).arm_right.translateAndRotate(matrixStack);
    }
}
