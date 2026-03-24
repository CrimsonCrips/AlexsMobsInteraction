package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.skelewag;

import com.crimsoncrips.alexsmobsinteraction.client.layer.SkelewagGlow;
import com.github.alexthe666.alexsmobs.client.model.ModelSkelewag;
import com.github.alexthe666.alexsmobs.client.render.RenderSkelewag;
import com.github.alexthe666.alexsmobs.entity.EntitySkelewag;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RenderSkelewag.class)
public abstract class AMISkelewagRenderMixin extends MobRenderer<EntitySkelewag, ModelSkelewag> {

    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexsmobs:textures/entity/skelewag_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/skelewag_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexsmobsinteraction:textures/entity/skelewag/wither_skelewag_2.png");
    private static final ResourceLocation TEXTURE_3 = new ResourceLocation("alexsmobsinteraction:textures/entity/skelewag/wither_skelewag_3.png");


    public AMISkelewagRenderMixin(EntityRendererProvider.Context pContext, ModelSkelewag pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$getItem(EntityRendererProvider.Context renderManagerIn, CallbackInfo ci) {
        RenderSkelewag renderSkelewag = (RenderSkelewag)(Object)this;
        this.addLayer(new SkelewagGlow(renderSkelewag));
    }

    public ResourceLocation getTextureLocation(EntitySkelewag entity) {
        return entity.getVariant() == 3 ? TEXTURE_3 : entity.getVariant() == 2 ? TEXTURE_2 : entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_0;
    }
}
