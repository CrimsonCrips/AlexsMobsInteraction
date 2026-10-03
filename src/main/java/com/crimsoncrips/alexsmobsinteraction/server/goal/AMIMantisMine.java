package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.EnumSet;

public class AMIMantisMine extends Goal {

    private static final int SEARCH_RADIUS = 8;
    private static final int COOLDOWN = AMIUtils.seconds(3);
    private static final int TIMEOUT = AMIUtils.seconds(10);
    private static final double REACH_SQR = 2.5D * 2.5D;

    private final EntityMantisShrimp mantisShrimp;
    private BlockPos target;
    private long nextSearch;
    private int timeout;

    public AMIMantisMine(EntityMantisShrimp mantisShrimp) {
        this.mantisShrimp = mantisShrimp;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mantisShrimp.level().getGameTime() < this.nextSearch || !this.canMine())
            return false;
        this.target = this.findTarget();
        if (this.target == null) {
            this.nextSearch = this.mantisShrimp.level().getGameTime() + COOLDOWN;
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.timeout < TIMEOUT && this.canMine() && this.matches(this.target);
    }

    @Override
    public void start() {
        this.timeout = 0;
    }

    @Override
    public void stop() {
        this.target = null;
        this.mantisShrimp.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.timeout++;
        Vec3 center = Vec3.atCenterOf(this.target);
        this.mantisShrimp.getLookControl().setLookAt(center);
        if (this.mantisShrimp.distanceToSqr(center) > REACH_SQR) {
            this.mantisShrimp.getNavigation().moveTo(center.x, center.y, center.z, 1.0D);
            return;
        }
        Level level = this.mantisShrimp.level();
        this.mantisShrimp.punch();
        if (CommonHooks.canEntityDestroy(level, this.target, this.mantisShrimp)) {
            level.destroyBlock(this.target, true, this.mantisShrimp);
        }
        this.nextSearch = level.getGameTime() + COOLDOWN;
        this.target = null;
    }

    public int getCooldown() {
        return (int) Math.max(0L, this.nextSearch - this.mantisShrimp.level().getGameTime());
    }

    public boolean isMining() {
        return this.target != null;
    }

    private boolean canMine() {
        return this.mantisShrimp.isTame() && !this.mantisShrimp.isBaby() && !this.mantisShrimp.isSitting()
                && (this.mantisShrimp.getTarget() == null || !this.mantisShrimp.getTarget().isAlive())
                && this.mantisShrimp.getMainHandItem().getItem() instanceof BlockItem;
    }

    private boolean matches(BlockPos pos) {
        Level level = this.mantisShrimp.level();
        BlockState state = level.getBlockState(pos);
        return this.mantisShrimp.getMainHandItem().getItem() instanceof BlockItem held && state.is(held.getBlock()) && state.getDestroySpeed(level, pos) >= 0.0F;
    }

    private BlockPos findTarget() {
        BlockPos origin = this.mantisShrimp.blockPosition();
        BlockPos closest = null;
        double closestDist = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS), origin.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
            if (!this.matches(pos))
                continue;
            double dist = pos.distSqr(origin);
            if (dist < closestDist) {
                closestDist = dist;
                closest = pos.immutable();
            }
        }
        return closest;
    }
}
