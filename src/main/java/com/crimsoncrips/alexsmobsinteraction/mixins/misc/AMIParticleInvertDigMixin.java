package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.client.particle.ParticleInvertDig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ParticleInvertDig.class)
public abstract class AMIParticleInvertDigMixin extends SimpleAnimatedParticle {


    @Shadow @Final private Entity creator;

    protected AMIParticleInvertDigMixin(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites, float pGravity) {
        super(pLevel, pX, pY, pZ, pSprites, pGravity);
    }


    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I"))
    private int bypassExpensiveCalculationIfNecessary(int pValue, int pMin, int pMax, Operation<Integer> original) {
        if (AlexsMobsInteraction.COMMON_CONFIG.HASTY_CARVING_ENABLED.get() && creator instanceof Player player){
            MobEffectInstance haste = player.getEffect(MobEffects.DIG_SPEED);
            return original.call(pValue,pMin,pMax) * (haste != null ? (haste.getAmplifier() == 0 ? 2 : 4) : 1);
        }
        return original.call(pValue,pMin,pMax);
    }




}
