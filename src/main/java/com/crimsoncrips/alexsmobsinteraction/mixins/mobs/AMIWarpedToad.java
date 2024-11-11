package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityWarpedToad.class)
public abstract class AMIWarpedToad extends Animal {


    protected AMIWarpedToad(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityWarpedToad warpedToad = (EntityWarpedToad)(Object)this;
        if(!AMInteractionConfig.WARPED_FRIENDLY_ENABLED)
            return;
        warpedToad.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(warpedToad, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.WARPED_TOAD_TARGETS)){
            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !warpedToad.isTame();
            }
        });
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 15))
    private boolean nearestTarget(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.WARPED_FRIENDLY_ENABLED;
    }

}
