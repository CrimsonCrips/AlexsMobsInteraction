package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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


    @Inject(method = "onEntityHit", at = @At("HEAD"),cancellable = true,remap = false)
    protected void Spit(EntityHitResult p_213868_1_, CallbackInfo ci) {
        ci.cancel();
        EntityMosquitoSpit spit = (EntityMosquitoSpit) (Object) this;
        Entity spitOwner = spit.getOwner();

        if (p_213868_1_.getEntity() instanceof LivingEntity livingHitEntity) {


        if (AMInteractionConfig.BLOOD_DAMAGE_DIFFERENCE_ENABLED) {
            if (spitOwner instanceof EntityCrimsonMosquito || spitOwner instanceof EntityWarpedMosco) {
                damageAmount = 4.0F;
            } else {
                damageAmount = 2.0F;
            }
        } else damageAmount = 4.0F;

        if (AMInteractionConfig.BLOOD_PROTECTION_ENABLED) {
            if (livingHitEntity instanceof EntityCrimsonMosquito mosquito && !this.level().isClientSide && mosquito.getRandom().nextDouble() < 0.2 && AMInteractionConfig.BLOODED_CHANCE > 0) {
                mosquito.setBloodLevel(mosquito.getBloodLevel() + 1);
            }
            if (!(livingHitEntity instanceof EntityCrimsonMosquito || livingHitEntity instanceof EntityWarpedMosco)) {
                if (AMInteractionConfig.FLY_TRANSFORM_ENABLED) {
                    if (!(livingHitEntity instanceof EntityFly)) {
                        hurtEntity(livingHitEntity, spitOwner, damageAmount);
                    }
                } else {
                    hurtEntity(livingHitEntity, spitOwner, damageAmount);
                }
            }
        } else {
            if (AMInteractionConfig.FLY_TRANSFORM_ENABLED) {
                if (!(livingHitEntity instanceof EntityFly)) {
                    hurtEntity(livingHitEntity, spitOwner, damageAmount);
                }
            } else {
                hurtEntity(livingHitEntity, spitOwner, damageAmount);
            }
        }

            if (!AMInteractionConfig.BLOODED_EFFECT_ENABLED)
                return;
            livingHitEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED.get(), 140, 0));
        }
    }

    @Unique
    public void hurtEntity (LivingEntity entity, Entity spitOwner, float amount){
        entity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) spitOwner), amount);
    }
}
