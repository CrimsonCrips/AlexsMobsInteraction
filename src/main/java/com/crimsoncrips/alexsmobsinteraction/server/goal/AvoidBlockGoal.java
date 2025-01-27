package com.crimsoncrips.alexsmobsinteraction.server.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class AvoidBlockGoal extends Goal {
    protected final PathfinderMob mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    protected final float maxDist;
    @Nullable
    protected Path path;
    protected final PathNavigation pathNav;
    protected final Predicate<BlockPos> posFilter;

    @Nullable
    protected BlockPos toAvoid;

    /**
     * Goal that helps mobs avoid mobs of a specific class
     */
    public AvoidBlockGoal(PathfinderMob pMob, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier, Predicate<BlockPos> posFilter) {
        this.mob = pMob;
        this.maxDist = pMaxDistance;
        this.walkSpeedModifier = pWalkSpeedModifier;
        this.sprintSpeedModifier = pSprintSpeedModifier;
        this.posFilter = posFilter;
        this.pathNav = pMob.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        Optional<BlockPos> blockPos = BlockPos.findClosestMatch(this.mob.blockPosition(), 8, 4, posFilter);
        if (blockPos.isPresent()) {
            Vec3 posAway = DefaultRandomPos.getPosAway(mob, 16, 7, blockPos.get().getCenter());
            if (posAway != null && this.mob.distanceToSqr(posAway.x, posAway.y, posAway.z) > this.mob.distanceToSqr(blockPos.get().getCenter())) {
                this.path = this.pathNav.createPath(posAway.x, posAway.y, posAway.z, 0);
                if (this.path != null) {
                    this.toAvoid = blockPos.get();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether an in-progress EntityAMIBase should continue executing
     */
    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.toAvoid = null;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (this.toAvoid == null) {
            return;
        }
        if (this.mob.distanceToSqr(this.toAvoid.getCenter()) < 49.0D) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }
    }
}