package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.leafcutter;

import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMIVariant;
import com.github.alexthe666.alexsmobs.client.render.RenderLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


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
        if (true){
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
