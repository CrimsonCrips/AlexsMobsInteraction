package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;


@Mixin(EntityVoidWorm.class)
public abstract class AMIVoidWorm extends Monster {

    @Shadow public abstract boolean isSplitter();

    static {
        STUNNED = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.BOOLEAN);
        DAMAGETAKEN = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.FLOAT);
        STUNTICKS = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.INT);
        DAMAGERETAMIN = SynchedEntityData.defineId(EntityVoidWorm.class, EntityDataSerializers.INT);
    }

    private static final EntityDataAccessor<Boolean> STUNNED;
    private static final EntityDataAccessor<Float> DAMAGETAKEN;
    private static final EntityDataAccessor<Integer> STUNTICKS;
    private static final EntityDataAccessor<Integer> DAMAGERETAMIN;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(STUNNED, false);
        this.entityData.define(DAMAGETAKEN, 0.0F);
        this.entityData.define(STUNTICKS, 0);
        this.entityData.define(DAMAGERETAMIN, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("Stunned", this.isStunned());
        compound.putDouble("DamageTaken", this.getDamageTaken());
        compound.putInt("StunTicks", this.getStunTicks());
        compound.putInt("DamageRetain", this.getDamageRetain());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setStunned(compound.getBoolean("Stunned"));
        this.setDamageTaken(compound.getInt("DamageTaken"));
        this.setStunTicks(compound.getInt("StunTicks"));
        this.setDamageRetain(compound.getInt("DamageRetain"));
    }

    public boolean isStunned() {
        return this.entityData.get(STUNNED);
    }

    public void setStunned(boolean stunned) {
        this.entityData.set(STUNNED, stunned);
    }
    public float getDamageTaken() {
        return this.entityData.get(DAMAGETAKEN);
    }

    public void setDamageTaken(float damageTaken) {
        this.entityData.set(DAMAGETAKEN, damageTaken);
    }
    public int getStunTicks() {
        return (Integer)this.entityData.get(STUNTICKS);
    }

    public void setStunTicks(int stunTick) {
        this.entityData.set(STUNTICKS, stunTick);
    }

    public int getDamageRetain() {
        return (Integer)this.entityData.get(DAMAGERETAMIN);
    }

    public void setDamageRetain(int damageRetain) {
        this.entityData.set(DAMAGERETAMIN, damageRetain);
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
           setDamageTaken(getDamageTaken() + amount);
       }
        setDamageRetain(500);

        return prev;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if(AMInteractionConfig.voidwormstun){
            EntityVoidWorm voidWorm = (EntityVoidWorm) (Object) this;

            if (getDamageRetain() > 0) setDamageRetain(getDamageRetain() - 1);
            else setDamageRetain(0);

            if (getDamageTaken() > 50) {
                setStunned(true);
                setDamageTaken(0);
            }
            if (isStunned() && !(getStunTicks() <= 0)) {
                ReflectionUtil.setField(voidWorm, "stillTicks", 0);
                setStunTicks(getStunTicks() - 1);

            } else {
                setStunTicks(100);
            }

            if (getStunTicks() <= 0) {
                setStunned(false);
            }
        }

    }
    @Inject(method = "spit", at = @At("HEAD"),cancellable = true,remap = false)
    private void spit(Vec3 shotAt, boolean portal, CallbackInfo ci) {
        ci.cancel();
        EntityVoidWorm voidWorm = (EntityVoidWorm)(Object)this;
        if (!isStunned()){
            shotAt = shotAt.yRot(-this.getYRot() * 0.017453292F);
            EntityVoidWormShot shot = new EntityVoidWormShot(this.level(), voidWorm);
            double d0 = shotAt.x;
            double d1 = shotAt.y;
            double d2 = shotAt.z;
            float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.35F;
            shot.shoot(d0, d1 + (double) f, d2, 0.5F, 3.0F);
            if (!this.isSilent()) {
                this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.DROWNED_SHOOT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            voidWorm.openMouth(5);
            this.level().addFreshEntity(shot);
        }
        

 
    }




}
