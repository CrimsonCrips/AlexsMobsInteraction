package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIWarnPredator;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
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


@Mixin(EntityRattlesnake.class)
public abstract class AMIRattlesnake extends Animal {

    protected AMIRattlesnake(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityRattlesnake rattlesnake = (EntityRattlesnake)(Object)this;
        rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, LivingEntity.class, 300, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RATTLESNAKE_KILL)) {
            public void start(){
                super.start();
            }
        });
        if (AMInteractionConfig.RATTLESNAKE_CANNIBALIZE_ENABLED) {
            rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, EntityRattlesnake.class, 1500, true, true, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.60F * livingEntity.getMaxHealth() || livingEntity.isBaby();
            }));
        }

        if (AMInteractionConfig.RATTLESNAKE_TERRITORIAL_ENABLED) {
            rattlesnake.goalSelector.addGoal(2, new AMIWarnPredator(rattlesnake));

        }
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 2))
    private boolean warnPredator(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.RATTLESNAKE_TERRITORIAL_ENABLED;
    }

}
