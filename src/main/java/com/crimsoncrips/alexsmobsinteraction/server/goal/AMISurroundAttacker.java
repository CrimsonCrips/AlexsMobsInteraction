package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

public class AMISurroundAttacker extends AMIFollowNearestGoal {


    public AMISurroundAttacker(PathfinderMob mob) {
        super(mob, LivingEntity.class, 15, 1.2,living -> {
            return mob.level().getEntity(((AsmonRoach)mob).getWorshiping()) instanceof EntityCockroach entityCockroach && entityCockroach.getLastHurtByMob() == living;
        });
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !((AsmonRoach)mob).isGod();
    }

    @Override
    public void tick() {
        var target = getTarget();
        if (target != null && !mob.isLeashed()) {
            mob.getLookControl().setLookAt(target, 10.0F, (float) this.mob.getMaxHeadXRot());
            navigation.moveTo(target, speedModifier);
        } else {
            navigation.stop();
        }
    }
}