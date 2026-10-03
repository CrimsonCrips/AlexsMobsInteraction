package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.portal;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.server.AMIVoidWormBoss;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityVoidPortal.class)
public abstract class AMIVoidPortalMixin extends Entity {

    protected AMIVoidPortalMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }






    @Inject(method = "tick", at = @At("HEAD"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (!this.level().isClientSide)
            AMIVoidWormBoss.tickWormPortal((EntityVoidPortal) (Object) this);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityVoidWorm;teleportTo(Lnet/minecraft/world/phys/Vec3;)V"), remap = false)
    private void alexsMobsInteraction$tickTeleport(CallbackInfo ci) {
        AMIVoidWormBoss.markPortalUsed((EntityVoidPortal) (Object) this);
    }

    @Inject(method = "createAndSetSister", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityVoidPortal;setShattered(Z)V"),remap = false)
    private void alexsMobsInteraction$createAndSetSister(Level world, Direction dir, CallbackInfo ci, @Local EntityVoidPortal sister) {
        if (!this.getData(AMIAttachments.PORTAL_DIMENSION).isEmpty()) {
            sister.setData(AMIAttachments.PORTAL_DIMENSION, sister.exitDimension.location().toString());
        }
    }


}
