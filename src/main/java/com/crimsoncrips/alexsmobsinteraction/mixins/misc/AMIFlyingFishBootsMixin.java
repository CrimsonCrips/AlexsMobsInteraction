package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.util.FlyingFishBootsUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(FlyingFishBootsUtil.class)
public abstract class AMIFlyingFishBootsMixin {

    private static final double LAUNCH_BASE = 0.35;

    private static final double LAUNCH_SPEED_SCALE = 2.0;

    private static final double LAUNCH_MAX = 1.6;

    private static final double LAUNCH_HORIZONTAL_SCALE = 1.2;

    private static final double MIN_ELEVATION = 0.2;

    private static final double MAX_ELEVATION = 0.85;

    private static final double AIR_STEER = 0.1;

    private static final double MIN_FALL_DRAG = 0.85;

    @Inject(method = "tickFlyingFishBoots", at = @At("HEAD"))
    private static void alexsMobsInteraction$tickFlyingFishBoots(LivingEntity wearer, CallbackInfo ci) {
        if (!AlexsMobsInteraction.COMMON_CONFIG.WEAVING_WATERS_ENABLED.get() || FlyingFishBootsUtil.getBoostTicks(wearer) <= 0 || wearer.onGround())
            return;
        Vec3 motion = wearer.getDeltaMovement();
        Vec3 look = wearer.getLookAngle();
        if (wearer.isInWaterOrBubble()) {
            if (motion.y < 0)
                wearer.setDeltaMovement(look.scale(motion.length()));
            return;
        }
        double horizontal = motion.horizontalDistance();
        Vec3 lookFlat = new Vec3(look.x, 0, look.z);
        if (horizontal > 1.0E-4 && lookFlat.lengthSqr() > 1.0E-4) {
            Vec3 steered = new Vec3(motion.x, 0, motion.z).normalize().lerp(lookFlat.normalize(), AIR_STEER).normalize().scale(horizontal);
            wearer.setDeltaMovement(steered.x, motion.y, steered.z);
        }
    }

    @WrapOperation(method = "tickFlyingFishBoots", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"))
    private static void alexsMobsInteraction$setDeltaMovement(LivingEntity wearer, double x, double y, double z, Operation<Void> original) {
        if (!AlexsMobsInteraction.COMMON_CONFIG.WEAVING_WATERS_ENABLED.get()) {
            original.call(wearer, x, y, z);
            return;
        }
        double power = Mth.clamp(LAUNCH_BASE + wearer.getDeltaMovement().length() * LAUNCH_SPEED_SCALE, LAUNCH_BASE, LAUNCH_MAX);
        double elevation = Mth.clamp(wearer.getLookAngle().y, MIN_ELEVATION, MAX_ELEVATION);
        double flat = Math.sqrt(1 - elevation * elevation) * LAUNCH_HORIZONTAL_SCALE;
        float yaw = wearer.getYHeadRot() * Mth.DEG_TO_RAD;
        original.call(wearer, -Mth.sin(yaw) * flat * power, elevation * power, Mth.cos(yaw) * flat * power);
    }

    @WrapOperation(method = "tickFlyingFishBoots", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private static void alexsMobsInteraction$setDeltaMovement1(LivingEntity wearer, Vec3 motion, Operation<Void> original) {
        if (!AlexsMobsInteraction.COMMON_CONFIG.WEAVING_WATERS_ENABLED.get()) {
            original.call(wearer, motion);
            return;
        }
        double drag = Mth.clampedLerp(1.0, MIN_FALL_DRAG, (wearer.getLookAngle().y + 1) / 2);
        original.call(wearer, wearer.getDeltaMovement().multiply(1, drag, 1));
    }
}
