package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.bald_eagle;

import com.crimsoncrips.alexsmobsinteraction.client.layer.AsmonLayer;
import com.crimsoncrips.alexsmobsinteraction.client.layer.BaldBombingLayer;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.github.alexthe666.alexsmobs.client.model.ModelBaldEagle;
import com.github.alexthe666.alexsmobs.client.model.ModelCockroach;
import com.github.alexthe666.alexsmobs.client.render.RenderBaldEagle;
import com.github.alexthe666.alexsmobs.client.render.RenderCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RenderBaldEagle.class)
public abstract class AMIBaldEagleRenderMixin extends MobRenderer<EntityBaldEagle, ModelBaldEagle> {


    public AMIBaldEagleRenderMixin(EntityRendererProvider.Context pContext, ModelBaldEagle pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void alexsMobsInteraction$init(EntityRendererProvider.Context renderManagerIn, CallbackInfo ci) {
        RenderBaldEagle renderBaldEagle = (RenderBaldEagle)(Object)this;
        this.addLayer(new BaldBombingLayer(renderBaldEagle));
    }
}
