package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.crocodile;


import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIVariant;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.WallyCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.github.alexthe666.alexsmobs.client.render.RenderCrocodile$CrownLayer")

public class AMICrocodileLayerRenderingMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILcom/github/alexthe666/alexsmobs/entity/EntityCrocodile;FFFFFF)V", at = @At(value = "TAIL"), remap = false)
    private void alexsMobsInteraction$render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityCrocodile entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

        if (!entitylivingbaseIn.isBaby() && ((WallyCrocodile) entitylivingbaseIn).isWally()) {
            ItemInHandRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            ItemStack haloStack = new ItemStack(AMItemRegistry.HALO.get());
            matrixStackIn.pushPose();
            float f = 0.1F * (float) Math.sin((entitylivingbaseIn.tickCount + partialTicks) * 0.1F) + (entitylivingbaseIn.isBaby() ? 0.2F : 0F);
            matrixStackIn.translate(0.0F, 0.2F - f, -2F);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(90F));
            matrixStackIn.scale(1.3F, 1.3F, 1.3F);
            renderer.renderItem(entitylivingbaseIn, haloStack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        }
    }

}
