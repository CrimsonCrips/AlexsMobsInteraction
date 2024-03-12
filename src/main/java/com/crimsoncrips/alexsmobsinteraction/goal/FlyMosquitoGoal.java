package com.crimsoncrips.alexsmobsinteraction.goal;

import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlyMosquitoGoal extends Goal {
    private final EntityCrimsonMosquito parentEntity;

    private BlockPos target = null;

    public FlyMosquitoGoal(EntityCrimsonMosquito mosquito) {
        this.parentEntity = mosquito;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        MoveControl movementcontroller = this.parentEntity.getMoveControl();
        if (this.parentEntity.isFlying() && this.parentEntity.getTarget() == null && !this.parentEntity.hasLuringLaviathan() && this.parentEntity.getFleeingEntityId() == -1) {
            if (movementcontroller.hasWanted() && this.target != null) {
                return false;
            } else {
                this.target = this.getBlockInViewMosquito();
                if (this.target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition((double)this.target.getX() + 0.5, (double)this.target.getY() + 0.5, (double)this.target.getZ() + 0.5, 1.0);
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        return this.target != null && this.parentEntity.isFlying() && this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) > 2.4 && this.parentEntity.getMoveControl().hasWanted() && !this.parentEntity.horizontalCollision;
    }

    public void stop() {
        this.target = null;
    }

    public void tick() {
        if (this.target == null) {
            this.target = this.getBlockInViewMosquito();
        }

        if (this.target != null) {
            this.parentEntity.getMoveControl().setWantedPosition((double)this.target.getX() + 0.5, (double)this.target.getY() + 0.5, (double)this.target.getZ() + 0.5, 1.0);
            if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) < 2.5) {
                this.target = null;
            }
        }

    }

    public BlockPos getBlockInViewMosquito() {
        float radius = (float)(1 + this.parentEntity.getRandom().nextInt(5));
        float neg = this.parentEntity.getRandom().nextBoolean() ? 1.0F : -1.0F;
        float renderYawOffset = this.parentEntity.yBodyRot;
        float angle = 0.017453292F * renderYawOffset + 3.15F + this.parentEntity.getRandom().nextFloat() * neg;
        double extraX = (double)(radius * Mth.sin(3.1415927F + angle));
        double extraZ = (double)(radius * Mth.cos(angle));
        BlockPos radialPos = AMBlockPos.fromCoords(this.parentEntity.getX() + extraX, this.parentEntity.getY() + 2.0, this.parentEntity.getZ() + extraZ);
        BlockPos ground = this.getGroundPosition(radialPos);
        int up = this.parentEntity.isSick() ? 2 : 6;
        BlockPos newPos = ground.above(1 + this.parentEntity.getRandom().nextInt(up));
        return !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.parentEntity.distanceToSqr(Vec3.atCenterOf(newPos)) > 6.0 ? newPos : null;
    }
    private BlockPos getGroundPosition(BlockPos radialPos) {
        while(radialPos.getY() > 1 && parentEntity.level().isEmptyBlock(radialPos)) {
            radialPos = radialPos.below();
        }

        return radialPos;
    }

}