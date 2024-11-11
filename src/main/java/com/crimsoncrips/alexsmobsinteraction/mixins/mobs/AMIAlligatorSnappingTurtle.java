package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.BottomFeederAIWander;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityAlligatorSnappingTurtle.class)
public abstract class AMIAlligatorSnappingTurtle extends Animal {

    protected AMIAlligatorSnappingTurtle(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityAlligatorSnappingTurtle snappingturtle = (EntityAlligatorSnappingTurtle)(Object)this;
        if (AMInteractionConfig.SNAPPING_DORMANCY_ENABLED){

            snappingturtle.goalSelector.addGoal(2, new AnimalAIFindWater(snappingturtle){
                @Override
                public boolean canContinueToUse() {
                    Level level = snappingturtle.level();
                    return super.canContinueToUse() && (level.isNight() || level.isRaining() || level.isThundering());
                }
            });
            snappingturtle.goalSelector.addGoal(2, new AnimalAILeaveWater(snappingturtle){
                @Override
                public boolean canContinueToUse() {
                    Level level = snappingturtle.level();
                    return super.canContinueToUse() && (level.isNight() || level.isRaining() || level.isThundering());
                }
            });
            snappingturtle.goalSelector.addGoal(3, new BottomFeederAIWander(snappingturtle, 1.0, 120, 150, 10){
                @Override
                public boolean canContinueToUse() {
                    Level level = snappingturtle.level();
                    return super.canContinueToUse() && (level.isNight() || level.isRaining() || level.isThundering());
                }
            });
            snappingturtle.goalSelector.addGoal(5, new RandomLookAroundGoal(snappingturtle){
                @Override
                public boolean canContinueToUse() {
                    Level level = snappingturtle.level();
                    return super.canContinueToUse() && (level.isNight() || level.isRaining() || level.isThundering());
                }
            });
            snappingturtle.goalSelector.addGoal(6, new LookAtPlayerGoal(snappingturtle, Player.class, 6.0F){
                @Override
                public boolean canContinueToUse() {
                    Level level = snappingturtle.level();
                    return super.canContinueToUse() && (level.isNight() || level.isRaining() || level.isThundering());
                }
            });
            snappingturtle.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(snappingturtle, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SNAPPING_TURTLE_KILL)){
                public boolean canUse() {
                    return snappingturtle.chaseTime >= 0 && super.canUse() && snappingturtle.level().isNight();
                }
                protected AABB getTargetSearchArea(double targetDistance) {
                    return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
                }});
        } else
            snappingturtle.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(snappingturtle, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SNAPPING_TURTLE_KILL)){
                public boolean canUse() {
                    return snappingturtle.chaseTime >= 0 && super.canUse();
                }
                protected AABB getTargetSearchArea(double targetDistance) {
                    return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
                }});
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 1))
    private boolean findWater(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.SNAPPING_DORMANCY_ENABLED;
    }
    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 2))
    private boolean leaveWater(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.SNAPPING_DORMANCY_ENABLED;
    }
    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 3))
    private boolean wander(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.SNAPPING_DORMANCY_ENABLED;
    }
    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 5))
    private boolean lookAround(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.SNAPPING_DORMANCY_ENABLED;
    }
    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 6))
    private boolean lookPlayer(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.SNAPPING_DORMANCY_ENABLED;
    }

}
