package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import com.github.alexthe666.alexsmobs.entity.EntityOrca;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityOrca.class)
public abstract class AMIOrca extends TamableAnimal {


    protected AMIOrca(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityOrca orca = (EntityOrca)(Object)this;
        if(AMInteractionConfig.ORCA_HUNT_ENABLED){
            orca.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(orca, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ORCA_KILL)));
            orca.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(orca, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ORCA_CHANCE_KILL)));
        }
    }




}
