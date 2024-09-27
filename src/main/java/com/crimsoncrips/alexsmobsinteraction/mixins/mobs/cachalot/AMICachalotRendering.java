package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.cachalot;


import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.github.alexthe666.alexsmobs.client.model.ModelCachalotWhale;
import com.github.alexthe666.alexsmobs.client.render.RenderCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderCachalotWhale.class)

public class AMICachalotRendering extends  MobRenderer<EntityCachalotWhale, ModelCachalotWhale> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_sleeping.png");
    private static final ResourceLocation TEXTURE_ALBINO = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_albino.png");
    private static final ResourceLocation TEXTURE_ALBINO_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/cachalot/cachalot_whale_albino_sleeping.png");
    private static final ResourceLocation TEXTURE_STUNNED = new ResourceLocation("alexsmobsinteraction:textures/entity/cachalot_whale_stunned.png");
    private static final ResourceLocation TEXTURE_ALBINO_STUNNED = new ResourceLocation("alexsmobsinteraction:textures/entity/cachalot_whale_albino_stunned.png");


    public AMICachalotRendering(EntityRendererProvider.Context pContext, ModelCachalotWhale pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }


    public ResourceLocation getTextureLocation(EntityCachalotWhale entity) {
        boolean isStunned = entity.hasEffect(AMIEffects.DISABLED.get());
        if (entity.isAlbino()){
            if (isStunned) return TEXTURE_ALBINO_STUNNED;
            else if (!entity.isSleeping() && !entity.isBeached()) return TEXTURE_ALBINO;
            else return TEXTURE_ALBINO_SLEEPING;
        } else {
            if (isStunned) return TEXTURE_STUNNED;
            else if (!entity.isSleeping() && !entity.isBeached()) return TEXTURE;
            else return TEXTURE_SLEEPING;
        }
    }

}
