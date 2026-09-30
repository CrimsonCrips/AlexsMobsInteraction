package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.GrizzlyExtras;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntity.class)
public abstract class AMILivingEntity extends Entity implements GrizzlyExtras {



    private static final EntityDataAccessor<Integer> SWIPES = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SWIPES_DELAY = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);

    public AMILivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void alexsMobsInteraction$defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci){
        builder.define(SWIPES, 0);
        builder.define(SWIPES_DELAY, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci){
        compound.putInt("Swipes", this.getSwipes());
        compound.putInt("SwipeDelay", this.getSwipeDelay());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci){
        this.setSwipes(compound.getInt("Swipes"));
        this.setSwipeDelay(compound.getInt("SwipeDelay"));
    }

    @ModifyExpressionValue(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;onLivingDamagePre(Lnet/minecraft/world/entity/LivingEntity;Lnet/neoforged/neoforge/common/damagesource/DamageContainer;)F"))
    private float alexsMobsInteraction$actuallyHurt(float original, @Local(argsOnly = true) DamageSource source){
        if (source.getEntity() instanceof EntityGrizzlyBear grizzlyBear && ((GrizzlyExtras)grizzlyBear).isUrsa()){
            return original * (1.10F * getSwipes());
        }
        return original;
    }

    @Override
    public int getSwipes() {
        return this.entityData.get(SWIPES);
    }

    @Override
    public void setSwipes(int num) {
        this.entityData.set(SWIPES, num);
    }

    @Override
    public int getSwipeDelay() {
        return this.entityData.get(SWIPES_DELAY);
    }

    @Override
    public void setSwipeDelay(int num) {
        this.entityData.set(SWIPES_DELAY, num);
    }
}