package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.grizzly_bear;

import com.crimsoncrips.alexsmobsinteraction.client.layer.UrsaArmorLayer;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.GrizzlyExtras;
import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.alexsmobs.client.render.RenderGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RenderGrizzlyBear.class)
public abstract class AMIGrizzlyBearRenderer extends MobRenderer<EntityGrizzlyBear, ModelGrizzlyBear> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/grizzly_bear.png");
    private static final ResourceLocation TEXTURE_FREDDY = new ResourceLocation("alexsmobs:textures/entity/grizzly_bear_freddy.png");
    private static final ResourceLocation TEXTURE_URSA = new ResourceLocation("alexsmobsinteraction:textures/entity/grizzly_bear/ursa.png");


    public AMIGrizzlyBearRenderer(EntityRendererProvider.Context pContext, ModelGrizzlyBear pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void alexsMobsInteraction$init(EntityRendererProvider.Context renderManagerIn, CallbackInfo ci) {
        RenderGrizzlyBear renderGrizzlyBear = (RenderGrizzlyBear)(Object)this;
        this.addLayer(new UrsaArmorLayer(renderGrizzlyBear));
    }

    public ResourceLocation getTextureLocation(EntityGrizzlyBear entity) {
        return ((GrizzlyExtras)entity).isUrsa() ?  TEXTURE_URSA : (entity.isFreddy() ? TEXTURE_FREDDY : TEXTURE);
    }
}
