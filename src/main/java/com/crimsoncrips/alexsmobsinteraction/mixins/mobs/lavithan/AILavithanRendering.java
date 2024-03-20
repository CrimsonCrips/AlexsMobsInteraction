package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.lavithan;


import com.crimsoncrips.alexsmobsinteraction.interfaces.AILavithanInterface;
import com.github.alexthe666.alexsmobs.client.model.ModelLaviathan;
import com.github.alexthe666.alexsmobs.client.render.RenderLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderLaviathan.class)

public class AILavithanRendering extends MobRenderer<EntityLaviathan, ModelLaviathan> {

    private static final ResourceLocation TEXTURE_RELAVA = new ResourceLocation("alexsmobsinteraction:textures/entity/laviathan_relava.png");

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/laviathan.png");
    private static final ResourceLocation TEXTURE_OBSIDIAN = new ResourceLocation("alexsmobs:textures/entity/laviathan_obsidian.png");


    public AILavithanRendering(EntityRendererProvider.Context pContext, ModelLaviathan pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }
    public ResourceLocation getTextureLocation(EntityLaviathan entity) {
        AILavithanInterface myAccessor = (AILavithanInterface) entity;
        boolean isRelava = myAccessor.isRelava();
        if (entity.isObsidian()){
            return TEXTURE_OBSIDIAN;
        } else if (isRelava){
            return TEXTURE_RELAVA;
        } else return TEXTURE;
    }


}
