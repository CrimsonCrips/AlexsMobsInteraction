package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBasicInterfaces;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;


@Mixin(EntityEndergrade.class)
public abstract class AMIEndergradeMixin extends Animal implements AMIBasicInterfaces {

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    int boostTime = 0;


    protected AMIEndergradeMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return super.isInvulnerableTo(pSource) || pSource.is(DamageTypes.FELL_OUT_OF_WORLD) && AlexsMobsInteraction.COMMON_CONFIG.VOIDED_ENDERGRADE_ENABLED.get();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (AlexsMobsInteraction.COMMON_CONFIG.VOIDED_ENDERGRADE_ENABLED.get() && this.getY() < (double)(this.level().getMinBuildHeight() - 64) && getFirstPassenger() instanceof LivingEntity living){
            AMIUtils.awardAdvancement(living,"void_dweller","void");
        }

        if (boostTime > 0){
            boostTime--;
        }

        if (boostTime == 70 && getControllingPassenger() instanceof Player player){
            this.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1, this.getVoicePitch());
            this.addDeltaMovement(player.getLookAngle().multiply(1.0F,0.5F, 1.0F).normalize().scale((5F) * this.getAttributeValue(Attributes.MOVEMENT_SPEED)));
        }
    }

    @Override
    public void boost() {
        boostTime = 100;
    }
}
