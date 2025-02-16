package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.laviathan;


import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.github.alexthe666.alexsmobs.client.render.RenderLaviathan$LayerOverlays")

public class AMILaviathanLayerRenderingMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILcom/github/alexthe666/alexsmobs/entity/EntityLaviathan;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityLaviathan;isObsidian()Z"), cancellable = true,remap = false)
    private void alter1(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityLaviathan laviathan, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {;
        if (AlexsMobsInteraction.COMMON_CONFIG.OBSIDIAN_EXTRACT_ENABLED.get() && ((AMIBaseInterfaces) laviathan).isRelava()){
            ci.cancel();
        }
    }

}
