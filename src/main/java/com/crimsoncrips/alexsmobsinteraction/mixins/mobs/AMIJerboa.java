package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityJerboa.class)
public abstract class AMIJerboa extends Animal {


    protected AMIJerboa(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityJerboa jerboa = (EntityJerboa)(Object)this;
        if (AMInteractionConfig.PREY_FEAR_ENABLED) {
            jerboa.goalSelector.addGoal(3, new AvoidEntityGoal<>(jerboa, LivingEntity.class, 7.0F, 1.7D, 1.4,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.JERBOAFEAR)));
        }
    }

}
