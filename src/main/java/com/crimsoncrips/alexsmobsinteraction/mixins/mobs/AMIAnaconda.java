package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityAnaconda.class)
public abstract class AMIAnaconda extends Animal {



    protected AMIAnaconda(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }



    @Inject(method = "getBreedOffspring", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityAnaconda;setYellow(Z)V"))
    private void alexsMobsInteraction$getBreedOffspring(ServerLevel serverWorld, AgeableMob mob, CallbackInfoReturnable<AgeableMob> cir, @Local EntityAnaconda anaconda) {
        anaconda.setData(AMIAttachments.ORIGIN_ID, this.getId());
    }









}
