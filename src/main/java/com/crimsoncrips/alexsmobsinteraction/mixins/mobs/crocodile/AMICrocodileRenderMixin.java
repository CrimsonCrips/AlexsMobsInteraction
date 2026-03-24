package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.crocodile;


import com.crimsoncrips.alexsmobsinteraction.client.layer.CrocodileHaloLayer;
import com.github.alexthe666.alexsmobs.client.model.ModelCrocodile;
import com.github.alexthe666.alexsmobs.client.render.RenderCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderCrocodile.class)

public abstract class AMICrocodileRenderMixin extends MobRenderer<EntityCrocodile, ModelCrocodile> {


    public AMICrocodileRenderMixin(EntityRendererProvider.Context pContext, ModelCrocodile pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void alexsMobsInteraction$init(EntityRendererProvider.Context renderManagerIn, CallbackInfo ci) {
        RenderCrocodile renderCrocodile = (RenderCrocodile)(Object)this;
        this.addLayer(new CrocodileHaloLayer(renderCrocodile));
    }
}
