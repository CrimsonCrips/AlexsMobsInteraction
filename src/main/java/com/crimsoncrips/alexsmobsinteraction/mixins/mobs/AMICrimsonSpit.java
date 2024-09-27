package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityMosquitoSpit.class)
public class AMICrimsonSpit extends Entity {

    float damageAmount = 0F;


    public AMICrimsonSpit(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Shadow
    protected void defineSynchedData() {
    }
    @Shadow
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }
    @Shadow
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"),cancellable = true,remap = false)
    protected void Spit(EntityHitResult p_213868_1_, CallbackInfo ci){
        ci.cancel();
        EntityMosquitoSpit spit = (EntityMosquitoSpit)(Object)this;
        Entity spitOwner = spit.getOwner();
        LivingEntity hitEntity = (LivingEntity) p_213868_1_.getEntity();

        if(AMInteractionConfig.BLOOD_DAMAGE_DIFFERENCE_ENABLED){
            if (spitOwner instanceof EntityCrimsonMosquito || spitOwner instanceof EntityWarpedMosco) {
                damageAmount = 4.0F;
            } else {
                damageAmount = 2.0F;
            }
        } else damageAmount = 4.0F;

        if (AMInteractionConfig.BLOOD_PROTECTION_ENABLED){
            if (hitEntity instanceof EntityCrimsonMosquito mosquito && !this.level().isClientSide && mosquito.getRandom().nextDouble() < 0.2 && AMInteractionConfig.BLOODED_ENABLED) {
                mosquito.setBloodLevel(mosquito.getBloodLevel() + 1);
            }
            if (!(hitEntity instanceof EntityCrimsonMosquito || hitEntity instanceof EntityWarpedMosco)){
                if (AMInteractionConfig.FLY_CONVERT_ENABLED){
                    if (!(hitEntity instanceof EntityFly)) {
                        hurtEntity(hitEntity,spitOwner,damageAmount);
                    }
                } else {
                    hurtEntity(hitEntity,spitOwner,damageAmount);
                }
            }
        } else {
            if (AMInteractionConfig.FLY_CONVERT_ENABLED){
                if (!(hitEntity instanceof EntityFly)) {
                    hurtEntity(hitEntity,spitOwner,damageAmount);
                }
            } else {
                hurtEntity(hitEntity,spitOwner,damageAmount);
            }
        }

        if (!AMInteractionConfig.BLOODED_EFFECT_ENABLED)
            return;
        hitEntity.addEffect(new MobEffectInstance(AMIEffects.BLOODED.get(), 140, 0));

    }

    @Unique
    public void hurtEntity (LivingEntity entity, Entity spitOwner, float amount){
        entity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) spitOwner), amount);
    }
}
