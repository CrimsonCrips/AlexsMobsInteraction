package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRaccoon.class)
public abstract class AMIRaccoon extends TamableAnimal {


    protected AMIRaccoon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityRaccoon raccoon = (EntityRaccoon)(Object)this;
        if (AMInteractionConfig.PREY_FEAR_ENABLED) {
            raccoon.goalSelector.addGoal(3, new AvoidEntityGoal<>(raccoon, LivingEntity.class, 8.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RACCOON_FEAR)));
        }
        if (AMInteractionConfig.RACOON_HUNT_ENABLED) {
            raccoon.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(raccoon, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RACCOON_KILL)) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !raccoon.isTame() && raccoon.level().isNight();
                }

            });
        }
    }

}
