package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.cockroach;

import com.crimsoncrips.alexsmobsinteraction.client.layer.AsmonLayer;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.render.RenderCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RenderCockroach.class)
public abstract class AMICockroachRenderMixin extends MobRenderer<EntityCockroach, ModelCockroach> {


    public AMICockroachRenderMixin(EntityRendererProvider.Context pContext, ModelCockroach pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void alexsMobsInteraction$init(EntityRendererProvider.Context renderManagerIn, CallbackInfo ci) {
        RenderCockroach renderCockroach = (RenderCockroach)(Object)this;
        this.addLayer(new AsmonLayer(renderCockroach));
    }

    protected void scale(EntityCockroach entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float scale = ((AsmonRoach)entitylivingbaseIn).isGod() ? 1.5F : 0.85F;
        matrixStackIn.scale(scale, scale, scale);
    }
}
