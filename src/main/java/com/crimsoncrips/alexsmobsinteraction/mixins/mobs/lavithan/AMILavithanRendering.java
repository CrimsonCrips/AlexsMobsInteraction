package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.lavithan;


import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMILavithanInterface;
import com.github.alexthe666.alexsmobs.client.model.ModelLaviathan;
import com.github.alexthe666.alexsmobs.client.render.RenderLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLaviathan.class)

public class AMILavithanRendering extends MobRenderer<EntityLaviathan, ModelLaviathan> {

    private static final ResourceLocation TEXTURE_RELAVA = new ResourceLocation("alexsmobsinteraction:textures/entity/laviathan_relava.png");

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/laviathan.png");
    private static final ResourceLocation TEXTURE_OBSIDIAN = new ResourceLocation("alexsmobs:textures/entity/laviathan_obsidian.png");


    public AMILavithanRendering(EntityRendererProvider.Context pContext, ModelLaviathan pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }
    public ResourceLocation getTextureLocation(EntityLaviathan entity) {
        AMILavithanInterface myAccessor = (AMILavithanInterface) entity;
        boolean isRelava = myAccessor.isRelava();
        return entity.isObsidian() ? TEXTURE_OBSIDIAN : (isRelava ? TEXTURE_RELAVA : TEXTURE);
    }


}
