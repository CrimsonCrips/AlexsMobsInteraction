package com.crimsoncrips.alexsmobsinteraction;

import com.github.alexthe666.alexsmobs.entity.EntitySkelewag;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

  public class AISkelewagCircleGoal extends Goal {
    EntitySkelewag skelewag;
    float speed;
    float circlingTime = 0.0F;
    float circleDistance = 5.0F;
    float maxCirclingTime = 80.0F;
    boolean clockwise = false;





    public AISkelewagCircleGoal(EntitySkelewag skelewag, float speed) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.skelewag = skelewag;
        this.speed = speed;
    }

    public boolean canUse() {
        return skelewag.getTarget() != null;
    }

    public boolean canContinueToUse() {
        return skelewag.getTarget() != null;
    }


    public void start() {
        RandomSource random = skelewag.getRandom();
        this.circlingTime = 0.0F;
        this.maxCirclingTime = (float)(20 + random.nextInt(80));
        this.circleDistance = 5.0F + random.nextFloat() * 5.0F;
        this.clockwise = random.nextBoolean();
    }

    public void stop() {
        RandomSource random = skelewag.getRandom();
        this.circlingTime = 0.0F;
        this.maxCirclingTime = (float)(20 + random.nextInt(80));
        this.circleDistance = 5.0F + random.nextFloat() * 5.0F;
        this.clockwise = random.nextBoolean();
    }

    public void tick() {
        LivingEntity prey = skelewag.getTarget();
        if (prey != null) {
            double dist = (double)skelewag.distanceTo(prey);
            if (circlingTime >= maxCirclingTime) {
                skelewag.lookAt(prey, 30.0F, 30.0F);
                skelewag.getNavigation().moveTo(prey, 1.5);
                if (dist < 2.0) {
                    skelewag.doHurtTarget(prey);

                    this.stop();
                }
            } else if (dist <= 25.0) {
                ++this.circlingTime;
                BlockPos circlePos = this.getskelewagCirclePos(prey);
                if (circlePos != null) {
                    skelewag.getNavigation().moveTo((double)circlePos.getX() + 0.5, (double)circlePos.getY() + 0.5, (double)circlePos.getZ() + 0.5, 0.6);
                }
            } else {
                skelewag.lookAt(prey, 30.0F, 30.0F);
                skelewag.getNavigation().moveTo(prey, 0.8);
            }
        }

    }

    public BlockPos getskelewagCirclePos(LivingEntity target) {
        float angle = 0.017453292F * (this.clockwise ? -this.circlingTime : this.circlingTime);
        double extraX = (double)(this.circleDistance * Mth.sin(angle));
        double extraZ = (double)(this.circleDistance * Mth.cos(angle));
        BlockPos ground = AMBlockPos.fromCoords(target.getX() + 0.5 + extraX, this.skelewag.getY(), target.getZ() + 0.5 + extraZ);
        return this.skelewag.level().getFluidState(ground).is(FluidTags.WATER) ? ground : null;
    }
}