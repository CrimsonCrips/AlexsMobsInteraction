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
        if(AMInteractionConfig.VOIDWORM_STUN_ENABLED){
            EntityVoidWorm voidWorm = (EntityVoidWorm) (Object) this;

            if (damageRetain > 0) damageRetain--;

            if (damageRetain < 0) damageTaken = 0;

            if (this.getY() > -50){
                if (damageTaken > 30) {
                    setStunned(true);
                    damageTaken = 0;
                }
            } else setStunned(false);

            if (damageTaken > 30) {
                if (this.getY() > -50) {
                    setStunned(true);
                }
                damageTaken = 0;
            }
            if (isStunned() && !(stunTicks <= 0)) {
                ReflectionUtil.setField(voidWorm, "stillTicks", 0);
                stunTicks--;

            } else {
                stunTicks = 100;
                setStunned(false);
            }
        }

    }

}
