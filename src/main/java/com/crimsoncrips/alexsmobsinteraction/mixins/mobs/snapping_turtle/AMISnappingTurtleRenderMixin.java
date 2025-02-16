package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.snapping_turtle;

import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.client.model.ModelAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.client.render.RenderAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(RenderAlligatorSnappingTurtle.class)
public abstract class AMISnappingTurtleRenderMixin extends MobRenderer<EntityAlligatorSnappingTurtle, ModelAlligatorSnappingTurtle> {


    public AMISnappingTurtleRenderMixin(EntityRendererProvider.Context pContext, ModelAlligatorSnappingTurtle pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/alligator_snapping_turtle.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobsinteraction:textures/entity/alligator_sleeping_turtle.png");

    @Override
    public ResourceLocation getTextureLocation(EntityAlligatorSnappingTurtle entity) {
        return ((AMIBaseInterfaces)entity).isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }
}
