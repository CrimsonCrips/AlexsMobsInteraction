package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
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

import java.util.function.Predicate;


@Mixin(EntityAnaconda.class)
public abstract class AMIAnaconda extends Animal {

    @Unique
    Predicate<LivingEntity> ANACONDA_BABY_TARGETS = AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ANACONDA_BABY_KILL);

    protected AMIAnaconda(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityAnaconda anaconda = (EntityAnaconda)(Object)this;
        anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, LivingEntity.class, 1000, true, false, (livingEntity) -> {
            return ANACONDA_BABY_TARGETS.test(livingEntity)  && livingEntity.isBaby();
        }));
        if (AlexsMobsInteraction.COMMON_CONFIG.ANACONDA_CANNIBALIZE_ENABLED.get()) {
            anaconda.targetSelector.addGoal(3, new HurtByTargetGoal(this, EntityAnaconda.class));
            anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 2500, true, false, (livingEntity) -> {
                return (livingEntity.getHealth() <= 0.10F * livingEntity.getMaxHealth() || livingEntity.isBaby());
            }));
        }
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 13))
    private boolean hurtByTarget(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.ANACONDA_CANNIBALIZE_ENABLED.get();
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 6))
    private boolean followParent(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.ORPHANED_ANACONDAS_ENABLED.get();
    }

}
