package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityMosquitoSpit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityMosquitoSpit.class)
public class AICrimsonSpit extends Entity {


    public AICrimsonSpit(EntityType<?> pEntityType, Level pLevel) {
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
        Entity entity = spit.getOwner();
        Entity hitEntity = p_213868_1_.getEntity();

        if (AInteractionConfig.crimsonbloodprot && AInteractionConfig.flyconvert && entity instanceof LivingEntity){/*flyconvert is on and crimsonbloodprot is on */
            if (!(hitEntity instanceof EntityFly) && !(hitEntity instanceof EntityCrimsonMosquito)) {
                hitEntity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) entity), 4.0F);
            }
            if (hitEntity instanceof EntityFly fly && fly.getHealth() == fly.getMaxHealth()){
                hitEntity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) entity), 1.0F);
            }
        } else {
            if (AInteractionConfig.flyconvert && entity instanceof LivingEntity) { //flyconvert is on and crimsonbloodprot is off
                if (!(hitEntity instanceof EntityFly)) {
                    hitEntity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) entity), 4.0F);
                }
                if (hitEntity instanceof EntityFly fly && fly.getHealth() == fly.getMaxHealth()){
                    hitEntity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) entity), 1.0F);
                }
            } else if (AInteractionConfig.crimsonbloodprot && entity instanceof LivingEntity) { //flyconvert is off and crimsonbloodprot is on
                if (!(hitEntity instanceof EntityCrimsonMosquito)) {
                    hitEntity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) entity), 4.0F);
                }
            }
        }
        if (!AInteractionConfig.crimsonbloodprot && !AInteractionConfig.flyconvert && entity instanceof LivingEntity) { //flyconvert is off and crimsonbloodprot is off
            if (entity instanceof LivingEntity) {
                hitEntity.hurt(this.damageSources().mobProjectile(this, (LivingEntity)entity), 4.0F);
            }
        }


        if (hitEntity instanceof EntityCrimsonMosquito mosquito && !this.level().isClientSide) {
            mosquito.setBloodLevel(mosquito.getBloodLevel() + 1);
        }



    }
}
