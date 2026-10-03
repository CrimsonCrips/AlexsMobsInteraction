package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.void_worm;

import com.crimsoncrips.alexsmobsinteraction.server.AMIVoidWormBoss;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityVoidWormPart.class)
public abstract class AMIVoidWormPart extends LivingEntity {

    protected AMIVoidWormPart(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void alexsMobsInteraction$die(DamageSource cause, CallbackInfo ci) {
        if (!AMIVoidWormBoss.enabled())
            return;
        EntityVoidWorm worm = ((EntityVoidWormPart) (Object) this).getWorm();
        if (worm != null && worm.isSplitter()) {
            ci.cancel();
        }
    }
}
