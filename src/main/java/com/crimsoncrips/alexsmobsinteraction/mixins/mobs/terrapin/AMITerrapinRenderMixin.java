package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.terrapin;

import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.client.model.ModelAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.client.model.ModelTerrapin;
import com.github.alexthe666.alexsmobs.client.render.RenderAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.client.render.RenderTerrapin;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(RenderTerrapin.class)
public abstract class AMITerrapinRenderMixin extends MobRenderer<EntityTerrapin, ModelTerrapin> {

    public AMITerrapinRenderMixin(EntityRendererProvider.Context pContext, ModelTerrapin pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    private static final ResourceLocation BLUE_SHELL = new ResourceLocation("alexsmobsinteraction:textures/entity/blue_shell.png");

    public ResourceLocation getTextureLocation(EntityTerrapin entity) {
        return ((AMIBaseInterfaces)entity).isBlueKoopa() ? BLUE_SHELL : entity.isKoopa() ? TerrapinTypes.KOOPA.getTexture() : entity.getTurtleType().getTexture();
    }

}
