package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityPotoo;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPotoo.class)
public abstract class AMIPotoo extends Animal {


    protected AMIPotoo(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityPotoo potoo = (EntityPotoo)(Object)this;
        potoo.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(potoo, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.POTOO_KILL)));

    }




}
