package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBasicInterfaces;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AnacondaOrphanage;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;


@Mixin(EntityAnaconda.class)
public abstract class AMIAnaconda extends Animal implements AnacondaOrphanage {

    private static final EntityDataAccessor<Integer> ORIGIN_PARENT = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.INT);


    protected AMIAnaconda(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityAnaconda anaconda = (EntityAnaconda)(Object)this;
        if (AlexsMobsInteraction.TARGETS_CONFIG.CANNIBALISM_ENABLED.get()) {
            anaconda.targetSelector.addGoal(3, new HurtByTargetGoal(this, EntityAnaconda.class));
            anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 2500, true, false, (livingEntity) -> {
                return livingEntity instanceof EntityAnaconda entityAnaconda && (entityAnaconda.getHealth() <= 0.10F * entityAnaconda.getMaxHealth() || (entityAnaconda.isBaby() && ((AnacondaOrphanage)entityAnaconda).getOriginID() != this.getId()));
            }));
        }
        if (AlexsMobsInteraction.TARGETS_CONFIG.ANACONDA_ENABLED.get()){
            anaconda.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(anaconda, LivingEntity.class, 800, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.ANACONDA_KILL)));
        }
    }

    @Inject(method = "getBreedOffspring", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityAnaconda;setYellow(Z)V"))
    private void alexsMobsInteraction$getBreedOffspring(ServerLevel serverWorld, AgeableMob mob, CallbackInfoReturnable<AgeableMob> cir, @Local EntityAnaconda anaconda) {
        ((AnacondaOrphanage)anaconda).setOriginID(this.getId());
    }


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void alexsMobsInteraction$defineSynchedData(CallbackInfo ci) {
        this.entityData.define(ORIGIN_PARENT, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("OriginParent", this.getOriginParent());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        this.setOriginParent(compound.getInt("OriginParent"));
    }


    public int getOriginParent() {
        return this.entityData.get(ORIGIN_PARENT);
    }

    public void setOriginParent(int id){
        this.entityData.set(ORIGIN_PARENT,id);
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 13))
    private boolean hurtByTarget(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.TARGETS_CONFIG.CANNIBALISM_ENABLED.get();
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 6))
    private boolean followParent(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.ORPHANED_ANACONDAS_ENABLED.get();
    }

}
