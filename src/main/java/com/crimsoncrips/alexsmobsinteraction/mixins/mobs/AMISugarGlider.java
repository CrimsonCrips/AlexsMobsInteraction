package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntitySugarGlider.class)
public abstract class AMISugarGlider extends TamableAnimal {


    protected AMISugarGlider(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntitySugarGlider sugarGlider = (EntitySugarGlider)(Object)this;
    }

}
