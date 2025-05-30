package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityRhinoceros;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRhinoceros.class)
public abstract class AMIRhino extends Animal {


    protected AMIRhino(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityRhinoceros rhinoceros = (EntityRhinoceros)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.ACCIDENTAL_BETRAYAL_ENABLED.get()){

            rhinoceros.targetSelector.addGoal(1, new HurtByTargetGoal(rhinoceros, new Class[]{EntityRhinoceros.class}));

            rhinoceros.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(rhinoceros, EntityRhinoceros.class, 200, true, true, (mob) -> {
                return mob.isBaby() && mob.getDeltaMovement().horizontalDistance() >= 0.08 && mob.getLastAttacker() != rhinoceros;
            }) {
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !rhinoceros.isBaby() && rhinoceros.level().isDay() && !rhinoceros.isLeashed() && !rhinoceros.isInLove();
                }

                protected double getFollowDistance() {
                    return 3.0;
                }
            });
        }
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 13))
    private boolean targetFood(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.ACCIDENTAL_BETRAYAL_ENABLED.get();
    }




}
