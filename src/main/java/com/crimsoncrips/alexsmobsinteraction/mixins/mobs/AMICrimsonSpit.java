package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityMosquitoSpit.class)
public abstract class AMICrimsonSpit extends Entity {

    float damageAmount = 0F;


    public AMICrimsonSpit(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "onEntityHit", at = @At("HEAD"),remap = false)
    protected void alexsMobsInteraction$onEntityHit(EntityHitResult p_213868_1_, CallbackInfo ci) {
        EntityMosquitoSpit spit = (EntityMosquitoSpit) (Object) this;
        Entity spitOwner = spit.getOwner();


        if (AlexsMobsInteraction.COMMON_CONFIG.HEMOGENICISM_ENABLED.get()){
            if (p_213868_1_.getEntity() instanceof LivingEntity livingHitEntity){
                if (!(livingHitEntity instanceof EntityCrimsonMosquito || livingHitEntity instanceof EntityWarpedMosco)){
                    livingHitEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED.get(), 240, 0));
                }

                if (spitOwner instanceof EntityCrimsonMosquito || spitOwner instanceof EntityWarpedMosco) {
                    damageAmount = 4.0F;
                } else {
                    damageAmount = 2.0F;
                }

            }
        } else damageAmount = 4.0F;

    }

}
