package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFollowNearestGoal;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.github.alexthe666.alexsmobs.entity.EntityHummingbird;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityHummingbird.class)
public abstract class AMIHummingbird extends Animal {


    protected AMIHummingbird(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityHummingbird hummingbird = (EntityHummingbird)(Object)this;
        if(AMInteractionConfig.POLINATE_DAY_ENABLED) {

            hummingbird.goalSelector.addGoal(4, new HummingbirdAIPollinate(hummingbird){
                public boolean canUse() {
                    return super.canUse() && hummingbird.level().isDay();
                }
            });
        }
        if(AMInteractionConfig.HUMMING_FOLLOW_ENABLED) {
            hummingbird.goalSelector.addGoal(8, new AMIFollowNearestGoal<>(hummingbird, EntityFlutter.class, 10, 1.2, null) {
                public boolean canContinueToUse() {
                    return hummingbird.level().isDay();
                }
            });
        }
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 4))
    private boolean polinate(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.POLINATE_DAY_ENABLED;
    }

}
