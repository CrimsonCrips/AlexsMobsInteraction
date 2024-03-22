package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityRattlesnake.class)
public class AIRattlesnake extends Mob {


    static{
        HUNGER = SynchedEntityData.defineId(EntityRattlesnake.class, EntityDataSerializers.INT);
    }
    private static final EntityDataAccessor<Integer> HUNGER;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(HUNGER, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Hunger", this.getHunger());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHunger(compound.getInt("Hunger"));
    }

    public int getHunger() {
        return (Integer)this.entityData.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.entityData.set(HUNGER, hunger);
    }

    protected AIRattlesnake(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void RattlesnakeGoals(CallbackInfo ci){
        Predicate<LivingEntity> rattlesnakecannibalism = (livingEntity) -> {
            return (livingEntity.getHealth() <= 0.70F * livingEntity.getMaxHealth() || livingEntity.isBaby()) && getHunger() >= 10;
        };
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.RATTLESNAKE_KILL)) {
            public void start(){
                super.start();
                setHunger(0);
            }
        });
        if (AInteractionConfig.rattlesnakecannibalize) {
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, EntityRattlesnake.class, 10, true, true, rattlesnakecannibalism) {
                public void start() {
                    super.start();
                    setHunger(200);
                }
            });
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.rattlesnakecannibalize) setHunger(getHunger() + 1);
    }


}
