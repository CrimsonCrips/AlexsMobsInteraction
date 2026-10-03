package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityGuster;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

public class AMIGusterRebound {

    private static final double CATCH_RANGE = 1.5D;
    private static final int SPIN_TICKS = AMIUtils.seconds(1.5F);
    private static final double SPIN_RADIUS = 1.3D;
    private static final float SPIN_SPEED = 0.55F;
    private static final double REBOUND_RANGE = 64.0D;
    private static final float REBOUND_VELOCITY = 1.8F;

    public static void tickGuster(EntityGuster guster) {
        if (guster.level().isClientSide || !AlexsMobsInteraction.COMMON_CONFIG.GUSTER_REBOUND_ENABLED.get())
            return;
        for (AbstractArrow arrow : guster.level().getEntitiesOfClass(AbstractArrow.class, guster.getBoundingBox().inflate(CATCH_RANGE))) {
            if (arrow.getOwner() == guster || arrow.getDeltaMovement().lengthSqr() < 0.01D || arrow.hasData(AMIAttachments.GUST_SPIN))
                continue;
            arrow.setData(AMIAttachments.GUST_SPIN, SPIN_TICKS);
            arrow.setData(AMIAttachments.GUST_HOLDER, guster.getId());
            arrow.setNoGravity(true);
            arrow.setDeltaMovement(Vec3.ZERO);
            guster.playSound(SoundEvents.BREEZE_DEFLECT, 1.0F, 0.8F + guster.getRandom().nextFloat() * 0.4F);
        }
    }

    public static void tickArrow(AbstractArrow arrow) {
        if (arrow.level().isClientSide || !arrow.hasData(AMIAttachments.GUST_SPIN))
            return;
        int spin = arrow.getData(AMIAttachments.GUST_SPIN);
        if (spin <= 0)
            return;
        Entity holder = arrow.level().getEntity(arrow.getData(AMIAttachments.GUST_HOLDER));
        if (!(holder instanceof EntityGuster guster) || !guster.isAlive()) {
            release(arrow);
            return;
        }
        spin--;
        arrow.setData(AMIAttachments.GUST_SPIN, spin);
        if (spin > 0) {
            float angle = (SPIN_TICKS - spin) * SPIN_SPEED;
            double rise = (SPIN_TICKS - spin) / (double) SPIN_TICKS * guster.getBbHeight() * 0.6D;
            arrow.setPos(guster.getX() + Mth.cos(angle) * SPIN_RADIUS, guster.getY() + 0.4D + rise, guster.getZ() + Mth.sin(angle) * SPIN_RADIUS);
            arrow.setDeltaMovement(Vec3.ZERO);
            arrow.setYRot(-angle * Mth.RAD_TO_DEG);
            return;
        }
        Entity shooter = arrow.getOwner();
        release(arrow);
        if (shooter != null && shooter.isAlive() && shooter.distanceToSqr(arrow) < REBOUND_RANGE * REBOUND_RANGE) {
            Vec3 toShooter = shooter.getEyePosition().subtract(arrow.position());
            arrow.setOwner(guster);
            arrow.shoot(toShooter.x, toShooter.y, toShooter.z, REBOUND_VELOCITY, 1.0F);
            guster.playSound(SoundEvents.BREEZE_SHOOT, 1.0F, 1.0F);
        }
    }

    private static void release(AbstractArrow arrow) {
        arrow.removeData(AMIAttachments.GUST_SPIN);
        arrow.removeData(AMIAttachments.GUST_HOLDER);
        arrow.setNoGravity(false);
    }
}
