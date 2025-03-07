package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFollowNearestGoal;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
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

        if(AlexsMobsInteraction.COMMON_CONFIG.FLOWERING_ATTRACTION_ENABLED.get()) {
            bee.goalSelector.addGoal(4, new AMIFollowNearestGoal<>(bee, EntityFlutter.class, 10, 1) {
                public boolean canContinueToUse() {
                    return bee.level().isDay();
                }
            });
        }
    }

}
