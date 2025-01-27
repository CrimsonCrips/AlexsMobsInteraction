package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ThrownPotion.class)
public abstract class AMIThrownPotion extends ThrowableItemProjectile {


    public AMIThrownPotion(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "applyWater", at = @At("TAIL"))
    private void applyWater(CallbackInfo ci) {

        for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D))) {
            double d0 = this.distanceToSqr(livingentity);
            if (d0 < 16.0D) {

                if (AMInteractionConfig.BLOODED_EFFECT_ENABLED){
                    MobEffectInstance blooded = livingentity.getEffect(AMIEffects.BLOODED.get());
                    if (blooded != null){
                        livingentity.removeEffect(AMIEffects.BLOODED.get());
                        livingentity.addEffect(new MobEffectInstance(AMIEffects.BLOODED.get(), blooded.getDuration() - 300, blooded.getAmplifier()));
                    }
                }
            }
        }

    }


}
