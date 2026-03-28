package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityEnderiophage.class)
public abstract class AMIEnderiophage extends Animal {

    private static final Predicate<LivingEntity> ENDERGRADE_OR_INFECTED = (entity) -> !(entity.hasEffect(MobEffects.DAMAGE_RESISTANCE)) && (entity instanceof EntityEndergrade || entity.hasEffect((MobEffect)AMEffectRegistry.ENDER_FLU.get()));

    protected AMIEnderiophage(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityEnderiophage enderiophage = (EntityEnderiophage)(Object)this;

        if (AlexsMobsInteraction.COMMON_CONFIG.INFECT_INTERACTION_ENABLED.get()){
            this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, EnderMan.class, 15, true, true, (livingEntity) -> {
                return livingEntity instanceof LivingEntity entity && !entity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
            }) {
                public boolean canUse() {
                    return super.canContinueToUse();
                }

                public boolean canContinueToUse() {
                    return enderiophage.isMissingEye() && super.canContinueToUse();
                }
            });
            this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, LivingEntity.class, 15, true, true, ENDERGRADE_OR_INFECTED) {
                public boolean canUse() {
                    return !enderiophage.isMissingEye() && (int) AMIReflectionUtil.getField(this, "fleeAfterStealTime") == 0 && super.canUse();
                }

                public boolean canContinueToUse() {
                    return !enderiophage.isMissingEye() && super.canContinueToUse();
                }
            });
        }

    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 3))
    private boolean target(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.INFECT_INTERACTION_ENABLED.get();
    }
    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 4))
    private boolean target2(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.INFECT_INTERACTION_ENABLED.get();
    }

}
