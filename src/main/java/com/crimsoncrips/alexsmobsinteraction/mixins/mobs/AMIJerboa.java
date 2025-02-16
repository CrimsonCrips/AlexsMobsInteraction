package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.github.alexthe666.alexsmobs.entity.EntityJerboa;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
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
    }

}
