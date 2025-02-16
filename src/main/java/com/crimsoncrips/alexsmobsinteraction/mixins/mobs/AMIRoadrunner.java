package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRoadrunner.class)
public abstract class AMIRoadrunner extends Animal {

    protected AMIRoadrunner(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityRoadrunner roadrunner = (EntityRoadrunner)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.ROADRUNNER_DAY_ENABLED.get()){
            roadrunner.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(roadrunner, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ROADRUNNER_KILL)) {
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && roadrunner.level().isDay();
                }
            });
        }

    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 9))
    private boolean nearestAttackable(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.ROADRUNNER_DAY_ENABLED.get();
    }

}
