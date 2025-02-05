package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.lavithan;


import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.Relavaithan;
import com.github.alexthe666.alexsmobs.client.model.ModelLaviathan;
import com.github.alexthe666.alexsmobs.client.render.RenderLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderLaviathan.class)

public class AMILavithanRendering extends MobRenderer<EntityLaviathan, ModelLaviathan> {

    private static final ResourceLocation TEXTURE_RELAVA = new ResourceLocation("alexsmobsinteraction:textures/entity/laviathan_relava.png");

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/laviathan.png");
    private static final ResourceLocation TEXTURE_OBSIDIAN = new ResourceLocation("alexsmobs:textures/entity/laviathan_obsidian.png");


    public AMILavithanRendering(EntityRendererProvider.Context pContext, ModelLaviathan pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }
    public ResourceLocation getTextureLocation(EntityLaviathan entity) {
        Relavaithan myAccessor = (Relavaithan) entity;
        boolean isRelava = myAccessor.isRelava();
        return entity.isObsidian() ? TEXTURE_OBSIDIAN : (isRelava ? TEXTURE_RELAVA : TEXTURE);
    }


}
