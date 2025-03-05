package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityStraddler.class)
public class AMIStraddlerMixin extends Mob {


    static{
        SHOOTCOOLDOWN = SynchedEntityData.defineId(EntityStraddler.class, EntityDataSerializers.INT);
        SHOOTSHOTS = SynchedEntityData.defineId(EntityStraddler.class, EntityDataSerializers.INT);
    }
    private static final EntityDataAccessor<Integer> SHOOTCOOLDOWN;

    private static final EntityDataAccessor<Integer> SHOOTSHOTS;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(SHOOTCOOLDOWN, 0);
        this.entityData.define(SHOOTSHOTS, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("ShootCooldown", this.getShootCooldown());
        compound.putInt("ShootShots", this.getShootShots());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setShootCooldown(compound.getInt("ShootCooldown"));
        this.setShootShots(compound.getInt("ShootShots"));
    }

    public int getShootCooldown() {
        return (Integer)this.entityData.get(SHOOTCOOLDOWN);
    }

    public void setShootCooldown(int shootcooldown) {
        this.entityData.set(SHOOTCOOLDOWN, shootcooldown);
    }

    public int getShootShots() {
        return (Integer)this.entityData.get(SHOOTSHOTS);
    }

    public void setShootShots(int shootshots) {
        this.entityData.set(SHOOTSHOTS, shootshots);
    }


    protected AMIStraddlerMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "shouldShoot", at = @At("HEAD"), cancellable = true,remap = false)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(doSomething4());
    }


    private boolean doSomething4() {
        if (AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get() != 0) {
            return !(getShootShots() <= 0);
        }else {
            return true;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci){
        if (AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get() != 0) {
            if (getShootShots() <= 0) {
                setShootCooldown(getShootCooldown() - 1);
            }
            if (getShootCooldown() <= 0 && getShootShots() <= 0) {
                setShootShots(AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get());
                setShootCooldown(100);
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityStradpole;setXRot(F)V"))
    private void addShots(CallbackInfo ci){
        if (AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get() != 0) {
            setShootShots(getShootShots() - 1);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void addExplosiveShots(CallbackInfo ci){
        if (AlexsMobsInteraction.COMMON_CONFIG.GOOFY_STRADDLER_SHOTGUN_ENABLED.get()) {
            for (int i = 0; i < 15; i++) {
                int spread = random.nextInt(10);
                EntityStradpole pole = AMEntityRegistry.STRADPOLE.get().create(level());
                pole.setParentId(this.getUUID());
                pole.setPos(this.getX(), this.getEyeY(), this.getZ());
                final double d0 = this.getTarget().getEyeY() - (double) 1.1F;
                final double d1 = this.getTarget().getX() - this.getX();
                final double d2 = d0 - pole.getY();
                final double d3 = this.getTarget().getZ() - this.getZ();
                final float f3 = Mth.sqrt((float) (d1 * d1 + d2 * d2 + d3 * d3)) * 0.2F;
                this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                this.playSound(SoundEvents.CROSSBOW_LOADING_END, 2F, 1F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                pole.shoot(d1, d2 + (double) f3, d3, 2F, 0F + spread);
                pole.setYRot(this.getYRot() % 360.0F);
                pole.setXRot(Mth.clamp(this.getYRot(), -90.0F, 90.0F) % 360.0F);
                if (!this.level().isClientSide) {
                    this.level().addFreshEntity(pole);
                }
                if (AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get() != 0) {
                    setShootShots(getShootShots() - 1);
                }
            }
        }
    }



    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityStraddler straddler = (EntityStraddler)(Object)this;
        if(AlexsMobsInteraction.COMMON_CONFIG.ADD_TARGETS_ENABLED.get()) {
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityBoneSerpent.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityCrimsonMosquito.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityWarpedMosco.class, true));
        }
    }

}
