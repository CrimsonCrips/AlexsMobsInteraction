package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityEnderiophage.class)
public abstract class AMIEnderiophage extends Animal {


    protected AMIEnderiophage(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityEnderiophage enderiophage = (EntityEnderiophage)(Object)this;

        enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, EnderMan.class, 15, true, true, (livingEntity) -> {
            if (AMInteractionConfig.INFECT_IMMUNITY_ENABLED) {
                return !(livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE));
            } else return true;
        }) {
            public boolean canContinueToUse() {
                return enderiophage.isMissingEye() && super.canContinueToUse();
            }
        });

        enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, LivingEntity.class, 15, true, true, (livingEntity) -> {
            if (AMInteractionConfig.INFECT_WEAK_ENABLED){
                if (AMInteractionConfig.INFECT_IMMUNITY_ENABLED){
                    return !(livingEntity instanceof EntityEnderiophage) && (livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE) || livingEntity.getHealth() <= 0.30F * livingEntity.getMaxHealth()) && (livingEntity instanceof EntityEndergrade || livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get()));
                } else {
                    return !(livingEntity instanceof EntityEnderiophage) && livingEntity.getHealth() <= 0.30F * livingEntity.getMaxHealth() && (livingEntity instanceof EntityEndergrade || livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get()));
                }
            } else {
                if (AMInteractionConfig.INFECT_IMMUNITY_ENABLED){
                    return !(livingEntity instanceof EntityEnderiophage) && livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE) && (livingEntity instanceof EntityEndergrade || livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get()));
                } else {
                    return !(livingEntity instanceof EntityEnderiophage)  && (livingEntity instanceof EntityEndergrade || livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get()));
                }
            }
        }) {
            public boolean canUse() {
                return !enderiophage.isMissingEye() && (int) ReflectionUtil.getField(enderiophage, "fleeAfterStealTime") == 0 && super.canUse();
            }
        });
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 3))
    private boolean target(GoalSelector instance, int pPriority, Goal pGoal) {
        return false;
    }
    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 4))
    private boolean target2(GoalSelector instance, int pPriority, Goal pGoal) {
        return false;
    }

}
