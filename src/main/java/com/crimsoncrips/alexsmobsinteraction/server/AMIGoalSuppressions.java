package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class AMIGoalSuppressions {

    public static void apply(Mob mob) {
        AMIServerConfig common = AlexsMobsInteraction.COMMON_CONFIG;
        boolean tamedFriendlies = common.TAMED_FRIENDLIES_ENABLED.get();

        if (mob instanceof EntityCentipedeHead centipede && common.LIGHT_FEAR_ENABLED.get()) {
            remove(centipede.targetSelector, 2, NearestAttackableTargetGoal.class);
        }

        if (mob instanceof EntityCockroach cockroach && common.ASMONGOLD_ENABLED.get()) {
            remove(cockroach.goalSelector, 1, PanicGoal.class);
            remove(cockroach.goalSelector, 4, AvoidEntityGoal.class);
            remove(cockroach.goalSelector, 4, "com.github.alexthe666.alexsmobs.entity.EntityCockroach$1");
        }

        if (mob instanceof EntityEnderiophage enderiophage && common.INFECT_INTERACTION_ENABLED.get()) {
            remove(enderiophage.targetSelector, 1, "com.github.alexthe666.alexsmobs.entity.EntityEnderiophage$1");
            remove(enderiophage.targetSelector, 1, "com.github.alexthe666.alexsmobs.entity.EntityEnderiophage$2");
        }

        if (mob instanceof EntityGrizzlyBear grizzlyBear && tamedFriendlies) {
            remove(grizzlyBear.targetSelector, 6, NearestAttackableTargetGoal.class);
            grizzlyBear.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(grizzlyBear, Player.class, 10, true, false, target -> {
                if (!grizzlyBear.canAttack(target))
                    return false;
                return !grizzlyBear.isTame() && (target.getType() == EntityType.PLAYER && grizzlyBear.isAngryAtAllPlayers(target.level()) || target.getUUID().equals(grizzlyBear.getPersistentAngerTarget()));
            }));
        }

        if (mob instanceof EntityHummingbird hummingbird && common.DAY_POLINATION_ENABLED.get()) {
            remove(hummingbird.goalSelector, 4, HummingbirdAIPollinate.class);
        }

        if (mob instanceof EntityKomodoDragon komodoDragon && tamedFriendlies) {
            remove(komodoDragon.targetSelector, 6, NearestAttackableTargetGoal.class);
            remove(komodoDragon.targetSelector, 7, NearestAttackableTargetGoal.class);
            remove(komodoDragon.targetSelector, 8, EntityAINearestTarget3D.class);
        }

        if (mob instanceof EntityAlligatorSnappingTurtle snappingTurtle && common.SNAPPING_DORMANCY_ENABLED.get()) {
            remove(snappingTurtle.goalSelector, 5, RandomLookAroundGoal.class);
            remove(snappingTurtle.goalSelector, 6, LookAtPlayerGoal.class);
        }

        if (mob instanceof EntitySeagull seagull && common.SNATCH_INTERACTION_ENABLED.get()) {
            remove(seagull.targetSelector, 2, SeagullAIStealFromPlayers.class);
        }

        if (mob instanceof EntitySkelewag skelewag && common.MIGHT_UPGRADE_ENABLED.get()) {
            remove(skelewag.targetSelector, 2, EntityAINearestTarget3D.class);
            remove(skelewag.targetSelector, 3, EntityAINearestTarget3D.class);
            skelewag.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(skelewag, Player.class, 100, true, false, (LivingEntity livingEntity) -> {
                return !livingEntity.hasEffect(AMEffectRegistry.ORCAS_MIGHT);
            }));
        }

        if (mob instanceof EntityWarpedToad warpedToad && tamedFriendlies) {
            remove(warpedToad.targetSelector, 4, EntityAINearestTarget3D.class);
        }
    }

    static void remove(GoalSelector selector, int priority, Class<?> goalClass) {
        removeIf(selector, goal -> goal.getPriority() == priority && goal.getGoal().getClass() == goalClass);
    }

    private static void remove(GoalSelector selector, int priority, String goalClassName) {
        removeIf(selector, goal -> goal.getPriority() == priority && goal.getGoal().getClass().getName().equals(goalClassName));
    }

    private static void removeIf(GoalSelector selector, Predicate<WrappedGoal> filter) {
        selector.getAvailableGoals().stream().filter(filter).map(WrappedGoal::getGoal).toList().forEach(selector::removeGoal);
    }
}
