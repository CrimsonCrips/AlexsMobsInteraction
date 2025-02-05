package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs;

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
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Bee.class)
public abstract class AMIBeeMixin extends Animal {


    protected AMIBeeMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        Bee bee = (Bee)(Object)this;

        if(AMInteractionConfig.FLOWERING_ATTRACTION_ENABLED) {
            bee.goalSelector.addGoal(4, new AMIFollowNearestGoal<>(bee, EntityFlutter.class, 10, 1, EntityFlutter::isAlive) {
                public boolean canContinueToUse() {
                    return bee.level().isDay();
                }
            });
        }
    }

}
