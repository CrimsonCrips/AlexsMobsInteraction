package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityStraddler.class)
public class AMIStraddlerMixin extends Mob {










    protected AMIStraddlerMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }


    @ModifyReturnValue(method = "shouldShoot", at = @At("RETURN"),remap = false)
    private boolean alexsMobsInteraction$shouldShoot(boolean original) {
        if (AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get() != 0) {
            return !(this.getData(AMIAttachments.SHOOT_SHOTS) <= 0);
        }else {
            return original;
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityStradpole;setXRot(F)V"))
    private void alexsMobsInteraction$tick1(CallbackInfo ci){
        if (AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get() != 0) {
            this.setData(AMIAttachments.SHOOT_SHOTS, this.getData(AMIAttachments.SHOOT_SHOTS) - 1);
        }
    }

}
