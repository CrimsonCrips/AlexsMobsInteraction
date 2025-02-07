package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.leafcutter;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIVariant;
import com.github.alexthe666.alexsmobs.client.render.RenderLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(RenderLeafcutterAnt.class)
public abstract class AMILeafcutterAntRenderMixin extends MobRenderer<EntityLeafcutterAnt, AdvancedEntityModel<EntityLeafcutterAnt>> {

    private static final ResourceLocation BROWN_TEXTURE = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant.png");
    private static final ResourceLocation BROWN_TEXTURE_QUEEN = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_queen.png");
    private static final ResourceLocation BROWN_TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_angry.png");
    private static final ResourceLocation BROWN_TEXTURE_QUEEN_ANGRY = new ResourceLocation("alexsmobs:textures/entity/leafcutter_ant_queen_angry.png");

    private static final ResourceLocation BLACK_TEXTURE = new ResourceLocation("alexsmobsinteraction:textures/entity/leafcutter/black_leafcutter_ant.png");
    private static final ResourceLocation BLACK_TEXTURE_QUEEN = new ResourceLocation("alexsmobsinteraction:textures/entity/leafcutter/black_leafcutter_ant_queen.png");
    private static final ResourceLocation BLACK_TEXTURE_ANGRY = new ResourceLocation("alexsmobsinteraction:textures/entity/leafcutter/black_leafcutter_ant_angry.png");
    private static final ResourceLocation BLACK_TEXTURE_QUEEN_ANGRY = new ResourceLocation("alexsmobsinteraction:textures/entity/leafcutter/black_leafcutter_ant_queen_angry.png");


    public AMILeafcutterAntRenderMixin(EntityRendererProvider.Context pContext, AdvancedEntityModel<EntityLeafcutterAnt> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    public ResourceLocation getTextureLocation(EntityLeafcutterAnt entity) {
        if (AlexsMobsInteraction.COMMON_CONFIG.LEAFCUTTER_VARIANTS_ENABLED.get()){
            AMIVariant myAccessor = (AMIVariant) entity;
            if (myAccessor.getVariant() == 1){
                if(entity.getRemainingPersistentAngerTime() > 0){
                    return entity.isQueen() ? BROWN_TEXTURE_QUEEN_ANGRY : BROWN_TEXTURE_ANGRY;
                }else {
                    return entity.isQueen() ? BROWN_TEXTURE_QUEEN : BROWN_TEXTURE;
                }
            } else {
                if(entity.getRemainingPersistentAngerTime() > 0){
                    return entity.isQueen() ? BLACK_TEXTURE_QUEEN_ANGRY : BLACK_TEXTURE_ANGRY;
                }else {
                    return entity.isQueen() ? BLACK_TEXTURE_QUEEN : BLACK_TEXTURE;
                }
            }
        } else {
            if(entity.getRemainingPersistentAngerTime() > 0){
                return entity.isQueen() ? BROWN_TEXTURE_QUEEN_ANGRY : BROWN_TEXTURE_ANGRY;
            }else {
                return entity.isQueen() ? BROWN_TEXTURE_QUEEN : BROWN_TEXTURE;
            }
        }
    }



}
