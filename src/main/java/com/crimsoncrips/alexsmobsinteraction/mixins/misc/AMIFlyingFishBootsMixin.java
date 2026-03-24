package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;


@Mixin(FlyingFishBootsUtil.class)
public abstract class AMIFlyingFishBootsMixin {


    @ModifyArg(method = "tickFlyingFishBoots", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"), index = 1)
    private static double adjustY(double y,@Local(ordinal = 0, argsOnly = true) LivingEntity wearer) {
        if(AlexsMobsInteraction.COMMON_CONFIG.WEAVING_WATERS_ENABLED.get()){
            double speed = 0;
            double lookAngle = 0;
            if (wearer.getDeltaMovement().horizontalDistance() != 0) {
                speed = wearer.getDeltaMovement().horizontalDistance() * 0.3;
            }

            if (!(wearer.getLookAngle().y <= 0)) {
                lookAngle = wearer.getLookAngle().y;
            }
            return speed + lookAngle * speed + 0.3;
        }
        return y;
    }

    @ModifyArg(method = "tickFlyingFishBoots", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"), index = 0)
    private static double adjustX(double x,@Local(ordinal = 0, argsOnly = true) LivingEntity wearer) {
        if(AlexsMobsInteraction.COMMON_CONFIG.WEAVING_WATERS_ENABLED.get()){
            double speed = 0;
            if (wearer.getDeltaMovement().horizontalDistance() != 0) {
                speed = wearer.getDeltaMovement().horizontalDistance() * 0.3;
            }
            return x * (speed + 0.4);
        }
        return x;
    }

    
}
