package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityTerrapin.class)
public abstract class AMITerrapin extends Mob {

    protected AMITerrapin(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityTerrapin;spinFor(I)V"))
    private void tickBison(CallbackInfo ci) {
        EntityTerrapin terrapin = (EntityTerrapin)(Object)this;
        if (!AMInteractionConfig.TERRAPIN_STOMP_ENABLED)
            return;
        terrapin.hurt(terrapin.damageSources().generic(),2);

    }

}
