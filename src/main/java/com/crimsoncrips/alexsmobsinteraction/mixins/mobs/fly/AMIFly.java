package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.fly;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.crimsoncrips.alexsmobsinteraction.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityFly.class)
public class AMIFly extends Mob implements AMITransform {
    private static final EntityDataAccessor<Boolean> BLOODED;
    private static final EntityDataAccessor<Boolean> MUNGUS;
    private static final EntityDataAccessor<Boolean> PACIFIED;
    private static final EntityDataAccessor<Boolean> FLYSICK;

    static {
        BLOODED = SynchedEntityData.defineId(AMIFly.class, EntityDataSerializers.BOOLEAN);
        MUNGUS = SynchedEntityData.defineId(AMIFly.class, EntityDataSerializers.BOOLEAN);
        PACIFIED = SynchedEntityData.defineId(AMIFly.class, EntityDataSerializers.BOOLEAN);
        FLYSICK = SynchedEntityData.defineId(AMIFly.class, EntityDataSerializers.BOOLEAN);
    }

    int flyConvert;
    double y2 = 0;
    private boolean noFollow = false;
    protected AMIFly(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }



    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(FLYSICK, false);
        this.entityData.define(BLOODED, false);
        this.entityData.define(MUNGUS, false);
        this.entityData.define(PACIFIED, false);
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("FlySick", this.isTransforming());
        compound.putBoolean("Blooded", this.isBlooded());
        compound.putBoolean("Mungused", this.isMungused());
        compound.putBoolean("Pacified", this.isPacified());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setTransforming(compound.getBoolean("FlySick"));
        this.setBlooded(compound.getBoolean("Blooded"));
        this.setMungused(compound.getBoolean("Mungused"));
        this.setPacified(compound.getBoolean("Pacified"));
    }

    public boolean isBlooded() {
        return this.entityData.get(BLOODED);
    }

    public void setBlooded(boolean relava) {
        this.entityData.set(BLOODED, relava);
    }
    public boolean isMungused() {
        return this.entityData.get(MUNGUS);
    }

    public void setMungused(boolean relava) {
        this.entityData.set(MUNGUS, relava);
    }
    public boolean isPacified() {
        return this.entityData.get(PACIFIED);
    }

    public void setPacified(boolean relava) {
        this.entityData.set(PACIFIED, relava);
    }


    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (source.getDirectEntity() instanceof EntityMosquitoSpit && AMInteractionConfig.flyconvert) {
            setBlooded(true);
        }

        return prev;
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemstack = player.getItemInHand(hand);
     if (AMInteractionConfig.flyconvert && (itemstack.getItem() == AMItemRegistry.MUNGAL_SPORES.get()) && !isMungused()) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            setMungused(true);
        }
        if (itemstack.getItem() == AMIItemRegistry.SWATTER.get() && AMInteractionConfig.flyconvert && !isPacified()) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            itemstack.hurtAndBreak(1, this, (p_233654_0_) -> {
            });
            setPacified(true);
            this.playSound(AMSoundRegistry.FLY_HURT.get(), 2F, 1F);

        }

    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void Test(CallbackInfo ci) {
        if (isPacified()){
                y2 = -0.05 + y2;
                this.setDeltaMovement(0, y2, 0);
           }
        if (isPacified() && isMungused() && isBlooded()) setTransforming(true);
        if(AMInteractionConfig.flypester){
            if (random.nextDouble() < 0.001 && !noFollow || level().isNight()) noFollow = true;
            if (random.nextDouble() < 0.05 && noFollow && level().isDay()) noFollow = false;
        }

        if (isTransforming()) {
            flyConvert++;

            if (flyConvert > 160) {
                EntityCrimsonMosquito crimsonMosquito = AMEntityRegistry.CRIMSON_MOSQUITO.get().create(level());
                crimsonMosquito.copyPosition(this);
                if (!this.level().isClientSide) {
                    crimsonMosquito.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }
                crimsonMosquito.onSpawnFromFly();

                if (!this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 79);
                    level().addFreshEntity(crimsonMosquito);
                }

                this.remove(RemovalReason.DISCARDED);

            }
        }

    }




    public boolean isTransforming() {
        return this.entityData.get(FLYSICK);
    }

    public void setTransforming(boolean transforming) {
        this.entityData.set(FLYSICK, transforming);
    }
}
