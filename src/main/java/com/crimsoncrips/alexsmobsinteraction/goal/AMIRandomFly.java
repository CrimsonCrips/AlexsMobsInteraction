package com.crimsoncrips.alexsmobsinteraction.goal;

import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AMIRandomFly extends Goal {
    private final EntityCosmaw parentEntity;
    private BlockPos target = null;

    public AMIRandomFly(EntityCosmaw mosquito) {
        this.parentEntity = mosquito;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (this.parentEntity.getNavigation().isDone() && this.shouldWander() && this.parentEntity.getTarget() == null && this.parentEntity.getRandom().nextInt(4) == 0) {
            this.target = this.getBlockInViewCosmaw();
            if (this.target != null) {
                this.parentEntity.getMoveControl().setWantedPosition((double)this.target.getX() + 0.5, (double)this.target.getY() + 0.5, (double)this.target.getZ() + 0.5, 1.0);
                return true;
            }
        }

        return false;
    }

    public boolean canContinueToUse() {
        return this.target != null && this.shouldWander() && this.parentEntity.getTarget() == null;
    }

    public void stop() {
        this.target = null;
    }

    public void tick() {
        if (this.target != null) {
            this.parentEntity.getMoveControl().setWantedPosition((double)this.target.getX() + 0.5, (double)this.target.getY() + 0.5, (double)this.target.getZ() + 0.5, 1.0);
            if (this.parentEntity.distanceToSqr(Vec3.atCenterOf(this.target)) < 4.0 || this.parentEntity.horizontalCollision) {
                this.target = null;
            }
        }

    }

    public BlockPos getBlockInViewCosmaw() {
        RandomSource random = parentEntity.getRandom();
        float radius = (float)(5 + this.parentEntity.getRandom().nextInt(10));
        float neg = this.parentEntity.getRandom().nextBoolean() ? 1.0F : -1.0F;
        float renderYawOffset = this.parentEntity.getYRot();
        float angle = 0.017453292F * renderYawOffset + 3.15F * this.parentEntity.getRandom().nextFloat() * neg;
        double extraX = (double)(radius * Mth.sin(3.1415927F + angle));
        double extraZ = (double)(radius * Mth.cos(angle));
        BlockPos radialPos = AMBlockPos.fromCoords(this.parentEntity.getX() + extraX, this.parentEntity.getY(), this.parentEntity.getZ() + extraZ);
        BlockPos ground = this.getCosmawGround(radialPos);
        if (ground.getY() <= 1) {
            ground = ground.above(70 + random.nextInt(4));
        } else {
            ground = ground.above(2 + random.nextInt(2));
        }

        return !this.parentEntity.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? ground : null;
    }
    private BlockPos getCosmawGround(BlockPos in) {
        BlockPos position;
        for(position = new BlockPos(in.getX(), (int)parentEntity.getY(), in.getZ()); position.getY() < 256 && !parentEntity.level().getFluidState(position).isEmpty(); position = position.above()) {
        }

        while(position.getY() > 1 && parentEntity.level().isEmptyBlock(position)) {
            position = position.below();
        }

        return position;
    }
    private boolean shouldWander() {
        if (parentEntity.isVehicle()) {
            return false;
        } else if (parentEntity.isTame()) {
            int command = parentEntity.getCommand();
            if (command != 2 && !parentEntity.isSitting()) {
                if (command == 1 && parentEntity.getOwner() != null && parentEntity.distanceTo(parentEntity.getOwner()) < 10.0F) {
                    return true;
                } else {
                    return command == 0;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}