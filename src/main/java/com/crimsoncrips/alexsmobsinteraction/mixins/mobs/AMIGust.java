package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.github.alexthe666.alexsmobs.entity.EntityGust;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(EntityGust.class)
public abstract class AMIGust extends Entity {

    protected AMIGust(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V",ordinal = 1))
    private boolean e(Entity instance, Vec3 pDeltaMovement) {
        if (instance instanceof LivingEntity livingEntity) {
            return !livingEntity.hasEffect(AMIEffects.GUSTING.get());
        } else return true;
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V",ordinal = 0))
    private boolean pushedEntity(Entity instance, Vec3 pDeltaMovement) {
        if (instance instanceof LivingEntity livingEntity) {
            return !livingEntity.hasEffect(AMIEffects.GUSTING.get());
        } else return true;
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V",ordinal = 2))
    private boolean ifE(Entity instance, Vec3 pDeltaMovement) {
        if (instance instanceof LivingEntity livingEntity) {
            return !livingEntity.hasEffect(AMIEffects.GUSTING.get());
        } else return true;
    }



}
