package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.crimsoncrips.alexsmobsinteraction.server.AMInteractionTagRegistry.CROCODILE_BABY_KILL;


@Mixin(EntityCrocodile.class)
public abstract class AMICrocodile extends TamableAnimal {

    protected AMICrocodile(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityCrocodile crocodile = (EntityCrocodile)(Object)this;
        crocodile.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(crocodile, LivingEntity.class, 5000, true, false, AMEntityRegistry.buildPredicateFromTag(CROCODILE_BABY_KILL)));
    }
}
