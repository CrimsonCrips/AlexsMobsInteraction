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


    public AMISurroundAttacker(PathfinderMob mob, Class targetType, float areaSize, double speedModifier) {
        super(mob, targetType, areaSize, speedModifier);
    }
}