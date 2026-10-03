package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(LivingEntity.class)
public abstract class AMILivingEntity extends Entity {




    public AMILivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }




    @ModifyExpressionValue(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;onLivingDamagePre(Lnet/minecraft/world/entity/LivingEntity;Lnet/neoforged/neoforge/common/damagesource/DamageContainer;)F"))
    private float alexsMobsInteraction$actuallyHurt(float original, @Local(argsOnly = true) DamageSource source){
        if (source.getEntity() instanceof EntityGrizzlyBear grizzlyBear && grizzlyBear.getData(AMIAttachments.URSA)){
            return original * (1.0F + 0.10F * this.getData(AMIAttachments.SWIPES));
        }
        return original;
    }




}