package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityMudskipper.class)
public abstract class AMIMudskipper extends TamableAnimal {


    protected AMIMudskipper(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityMudskipper mudskipper = (EntityMudskipper)(Object)this;
        if (AMInteractionConfig.MUDSKIPPER_HUNT_ENABLED) {
            mudskipper.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(mudskipper, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.MUDSKIPPER_KILL)));
        }
    }




}
