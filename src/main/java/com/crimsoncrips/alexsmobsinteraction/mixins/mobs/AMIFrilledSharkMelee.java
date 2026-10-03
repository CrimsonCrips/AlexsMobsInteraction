package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityFrilledShark;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.github.alexthe666.alexsmobs.entity.EntityFrilledShark$AIMelee")
public abstract class AMIFrilledSharkMelee extends Goal {

    private static final float CHARGE_RANGE = 7.0F;

    private static final float CHARGE_MIN_RANGE = 2.5F;

    private static final double CHARGE_STRENGTH = 0.12;

    private static final int CHARGE_COOLDOWN = AMIUtils.seconds(3);

    @Shadow
    @Final
    EntityFrilledShark this$0;

    @Unique
    private int alexsMobsInteraction$chargeCooldown;

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (alexsMobsInteraction$chargeCooldown > 0) {
            alexsMobsInteraction$chargeCooldown--;
            return;
        }
        LivingEntity target = this$0.getTarget();
        if (!AlexsMobsInteraction.COMMON_CONFIG.BLEEDING_HUNGER_ENABLED.get() || target == null || !this$0.isInWaterOrBubble() || !this$0.hasLineOfSight(target))
            return;
        float distance = this$0.distanceTo(target);
        if (distance > CHARGE_RANGE || distance < CHARGE_MIN_RANGE)
            return;
        Vec3 direction = target.getEyePosition().subtract(this$0.position()).normalize();
        this$0.setDeltaMovement(this$0.getDeltaMovement().add(direction.scale(distance * CHARGE_STRENGTH)));
        this$0.lookAt(target, 180.0F, 180.0F);
        alexsMobsInteraction$chargeCooldown = CHARGE_COOLDOWN;
    }
}
