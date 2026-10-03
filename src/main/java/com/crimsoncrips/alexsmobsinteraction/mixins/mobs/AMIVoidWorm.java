package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerBossEvent;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Entity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.crimsoncrips.alexsmobsinteraction.server.AMIVoidWormBoss;
import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityVoidWorm.class)
public abstract class AMIVoidWorm extends Monster {


    protected AMIVoidWorm(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    //for later uses

    @Inject(method = "tickDeath", at = @At("HEAD"), cancellable = true)
    private void alexsMobsInteraction$tickDeath(CallbackInfo ci) {
        EntityVoidWorm worm = (EntityVoidWorm) (Object) this;
        if (!this.level().isClientSide && AMIVoidWormBoss.enabled() && !worm.isSplitter() && AMIVoidWormBoss.tickShatterDeath(worm)) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityVoidWorm;getChild()Lnet/minecraft/world/entity/Entity;"))
    private Entity alexsMobsInteraction$tick(Entity child) {
        return child == null && this.isDeadOrDying() && AMIVoidWormBoss.enabled() ? this : child;
    }

    @Inject(method = "createPortal(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction;)V", at = @At("TAIL"), remap = false)
    private void alexsMobsInteraction$createPortal(Vec3 from, Vec3 to, Direction outDir, CallbackInfo ci) {
        AMIVoidWormBoss.claimPortal((EntityVoidWorm) (Object) this);
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityVoidWorm;createPortalRandomDestination()V"), remap = false)
    private boolean alexsMobsInteraction$tickRandomPortal(EntityVoidWorm worm) {
        return !(AMIVoidWormBoss.enabled() && worm.getTarget() != null && worm.getTarget().isAlive());
    }

    @WrapWithCondition(method = "startSeenByPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;addPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private boolean alexsMobsInteraction$startSeenByPlayer(ServerBossEvent bossEvent, ServerPlayer player) {
        return !(AMIVoidWormBoss.enabled() && ((EntityVoidWorm) (Object) this).isSplitter());
    }
}
