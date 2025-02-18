package com.crimsoncrips.alexsmobsinteraction.server.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

public class AMIFollowNearestGoal<T extends LivingEntity> extends Goal {
    protected final PathfinderMob mob;
    final Predicate<T> followPredicate;
    protected Class<T> targetType;
    private final double speedModifier;
    private final float areaSize;
    private final PathNavigation navigation;

    private int timeToRecalcPath;

    public AMIFollowNearestGoal(PathfinderMob mob, Class<T> targetType, float areaSize, double speedModifier) {
        this(mob, targetType, areaSize, speedModifier, Objects::isNull);
    }

    public AMIFollowNearestGoal(PathfinderMob mob, Class<T> targetType, float areaSize, double speedModifier, @Nullable Predicate<T> followPredicate) {
        this.mob = mob;
        this.targetType = targetType;
        this.followPredicate = followPredicate;
        this.speedModifier = speedModifier;
        this.areaSize = areaSize;
        this.navigation = mob.getNavigation();
    }


    @Override
    public boolean canUse() {
        return getTarget() != null;
    }

    public void tick() {
        var target = getTarget();
        if (target != null && !mob.isLeashed()) {
            mob.getLookControl().setLookAt(target, 10.0F, (float) this.mob.getMaxHeadXRot());
            if (--timeToRecalcPath <= 0) {
                timeToRecalcPath = adjustedTickDelay(10);
                navigation.moveTo(target, speedModifier);
            }
        } else {
            timeToRecalcPath = adjustedTickDelay(10);
            navigation.stop();
        }
    }

    @Nullable
    public LivingEntity getTarget() {
        var level = this.mob.level();
        var targetList = level.getEntitiesOfClass(targetType, mob.getBoundingBox().inflate(areaSize), followPredicate);
        if (targetList.isEmpty()) {
            return null;
        }
        return targetList.get(0);
    }
}