package com.crimsoncrips.alexsmobsinteraction.mixins.misc.mimicream;

import com.crimsoncrips.alexsmobsinteraction.client.glint.AMIGlintRenderTypes;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIDataComponents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class AMIItemRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void alexsMobsInteraction$render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        AMIGlintRenderTypes.renderingMimicked = itemStack.has(AMIDataComponents.MIMICKED);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void alexsMobsInteraction$render1(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        AMIGlintRenderTypes.renderingMimicked = false;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z"))
    private boolean alexsMobsInteraction$render2(boolean original) {
        return original || AMIGlintRenderTypes.renderingMimicked;
    }

    @ModifyExpressionValue(method = {"getCompassFoilBuffer", "getFoilBuffer", "getFoilBufferDirect"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glint()Lnet/minecraft/client/renderer/RenderType;"), require = 0)
    private static RenderType alexsMobsInteraction$getCompassFoilBuffer(RenderType original) {
        return AMIGlintRenderTypes.swap(original);
    }

    @ModifyExpressionValue(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glintTranslucent()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType alexsMobsInteraction$getFoilBuffer(RenderType original) {
        return AMIGlintRenderTypes.swap(original);
    }

    @ModifyExpressionValue(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityGlint()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType alexsMobsInteraction$getFoilBuffer1(RenderType original) {
        return AMIGlintRenderTypes.swap(original);
    }

    @ModifyExpressionValue(method = "getFoilBufferDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityGlintDirect()Lnet/minecraft/client/renderer/RenderType;"))
    private static RenderType alexsMobsInteraction$getFoilBufferDirect(RenderType original) {
        return AMIGlintRenderTypes.swap(original);
    }
}
