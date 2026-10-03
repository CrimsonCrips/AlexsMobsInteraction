package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.void_worm;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.server.AMIVoidWormBoss;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormShot;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityVoidWormShot.class)
public abstract class AMIVoidWormShot extends Entity {

    @Shadow
    public abstract Entity getShooter();

    protected AMIVoidWormShot(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return AMIVoidWormBoss.enabled() ? this.isAlive() : super.canBeHitByProjectile();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (AMIVoidWormBoss.enabled() && (source.getDirectEntity() instanceof Projectile || source.is(DamageTypeTags.IS_PROJECTILE)) && this.isAlive()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(AMParticleRegistry.WORM_PORTAL.get(), this.getX(), this.getY(0.5D), this.getZ(), 12, 0.2D, 0.2D, 0.2D, 0.05D);
                this.playSound(SoundEvents.GLASS_BREAK, 0.6F, 1.6F);
                this.discard();
            }
            return true;
        }
        return super.hurt(source, amount);
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        int mode = this.getData(AMIAttachments.VOID_SHOT_MODE);
        Vec3 motion = this.getDeltaMovement();
        if (mode == AMIVoidWormBoss.SHOT_SURROUND) {
            if (this.tickCount > AMIVoidWormBoss.SURROUND_TICKS && !this.level().isClientSide) {
                this.discard();
            } else if (motion.lengthSqr() > 1.0E-8) {
                this.setDeltaMovement(motion.normalize().scale(AMIVoidWormBoss.SURROUND_SPEED));
            }
            return;
        }
        if (mode == AMIVoidWormBoss.SHOT_NORMAL || motion.lengthSqr() < 1.0E-8)
            return;
        Vec3 direction = motion.normalize();
        if (mode == AMIVoidWormBoss.SHOT_EASE) {
            if (this.tickCount <= AMIVoidWormBoss.EASE_TICKS) {
                double eased = Math.pow(2.0D, -10.0D * this.tickCount / AMIVoidWormBoss.EASE_TICKS);
                this.setDeltaMovement(direction.scale(AMIVoidWormBoss.EASE_SPEED * eased));
            }
        } else if (mode == AMIVoidWormBoss.SHOT_GEYSER) {
            if (this.tickCount > AMIVoidWormBoss.SHOT_LIFETIME && !this.level().isClientSide) {
                this.discard();
                return;
            }
            this.setDeltaMovement(direction.scale(AMIVoidWormBoss.GEYSER_SPEED));
        } else if (mode == AMIVoidWormBoss.SHOT_MISSILE) {
            int riseTicks = AMIVoidWormBoss.getMissileRiseTicks(this);
            if (this.tickCount <= riseTicks) {
                if (this.tickCount > 1)
                    this.setDeltaMovement(direction.scale(Math.max(motion.length() * AMIVoidWormBoss.MISSILE_RISE_DRAG, 0.05D)));
                return;
            }
            if (!this.getData(AMIAttachments.VOID_SHOT_LOST) && this.getShooter() instanceof Mob shooter && shooter.getTarget() instanceof LivingEntity target) {
                Vec3 toTarget = target.getEyePosition().subtract(this.position()).normalize();
                if (this.tickCount == riseTicks + 1) {
                    direction = toTarget;
                } else if (direction.dot(toTarget) <= 0.0D) {
                    this.setData(AMIAttachments.VOID_SHOT_LOST, true);
                } else {
                    direction = direction.lerp(toTarget, AMIVoidWormBoss.MISSILE_TURN).normalize();
                }
            }
            this.setDeltaMovement(direction.scale(Math.min(AMIVoidWormBoss.MISSILE_START_SPEED + AMIVoidWormBoss.MISSILE_ACCELERATION * (this.tickCount - riseTicks), AMIVoidWormBoss.MISSILE_SPEED)));
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 40), remap = false)
    private int alexsMobsInteraction$tick1(int homingDelay) {
        int mode = this.getData(AMIAttachments.VOID_SHOT_MODE);
        return mode == AMIVoidWormBoss.SHOT_GEYSER || mode == AMIVoidWormBoss.SHOT_MISSILE || mode == AMIVoidWormBoss.SHOT_SURROUND ? Integer.MAX_VALUE : homingDelay;
    }
}
