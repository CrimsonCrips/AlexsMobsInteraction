package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.kangaroo;

import com.crimsoncrips.alexsmobsinteraction.client.layer.KangarooBoxingGloves;
import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.RenderKangaroo;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RenderKangaroo.class)
public abstract class AMIKangarooRenderMixin extends MobRenderer<EntityKangaroo, ModelKangaroo> {


    public AMIKangarooRenderMixin(EntityRendererProvider.Context pContext, ModelKangaroo pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$init(EntityRendererProvider.Context renderManagerIn, CallbackInfo ci) {
        this.addLayer(new KangarooBoxingGloves(this));
    }



}
