package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityVoidWorm.class)
public abstract class AMIVoidWorm extends Monster {

    float damageTaken;

    int damageRetain;

    int stunTicks;

    static {
        STUNNED = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.BOOLEAN);
    }

    private static final EntityDataAccessor<Boolean> STUNNED;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(STUNNED, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("Stunned", this.isStunned());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setStunned(compound.getBoolean("Stunned"));
    }

    public boolean isStunned() {
        return this.entityData.get(STUNNED);
    }

    public void setStunned(boolean stunned) {
        this.entityData.set(STUNNED, stunned);
    }

    protected AMIVoidWorm(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public boolean isNoGravity() {
        return !isStunned();
    }

    @Override
    protected boolean isImmobile() {
        return isStunned();
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
       if (!isStunned()) {
           damageTaken = damageTaken + amount;
       }
        damageRetain = 500;

        return prev;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if(AlexsMobsInteraction.COMMON_CONFIG.VOIDWORM_STUN_ENABLED.get()){
            EntityVoidWorm voidWorm = (EntityVoidWorm) (Object) this;

            if (damageRetain > 0) damageRetain--;

            if (damageRetain < 0) damageTaken = 0;

            if (this.getY() > -50) {
                if (damageTaken > 30) {
                    AMIUtils.awardAdvancement(getLastHurtByMob(), "voidworm_stun", "stun");
                    setStunned(true);
                    damageTaken = 0;
                }
            } else setStunned(false);

            if (isStunned() && !(stunTicks <= 0)) {
                AMIReflectionUtil.setField(voidWorm, "stillTicks", 0);
                stunTicks--;

            } else {
                stunTicks = 100;
                setStunned(false);
            }
        }

    }

}
