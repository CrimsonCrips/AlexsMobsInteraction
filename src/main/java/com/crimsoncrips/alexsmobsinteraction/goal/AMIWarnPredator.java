package com.crimsoncrips.alexsmobsinteraction.goal;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class AMIWarnPredator extends Goal {
    int executionChance = 20;
    LivingEntity target = null;

    EntityRattlesnake rattlesnake;

    private static final Predicate<LivingEntity> WARNABLE_PREDICATE = (mob) -> {
        return mob instanceof Player && !((Player) mob).isCreative() && !mob.isSpectator() || mob instanceof EntityRoadrunner || (mob instanceof EntityRattlesnake && AMInteractionConfig.RATTLESNAKE_TERRITORIAL_ENABLED) ;
    };

    public AMIWarnPredator(EntityRattlesnake rattlesnake) {
        this.rattlesnake = rattlesnake;
    }

    public boolean canUse() {
        if (rattlesnake.getRandom().nextInt(this.executionChance) == 0) {
            List<LivingEntity> list = rattlesnake.level().getEntitiesOfClass(LivingEntity.class, rattlesnake.getBoundingBox().inflate(5.0, 5.0, 5.0), WARNABLE_PREDICATE);
            double d0 = Double.MAX_VALUE;
            Entity possibleTarget = null;
            Iterator<LivingEntity> var7 = list.iterator();

            while (var7.hasNext()) {

                Entity entity = var7.next();
                double d1 = rattlesnake.distanceToSqr(entity);
                if (!(d1 > d0) && entity != rattlesnake) {
                    d0 = d1;
                    possibleTarget = entity;
                }

            }

            this.target = (LivingEntity) possibleTarget;
            return !list.isEmpty();
        } else return false;
    }

    public boolean canContinueToUse() {
        return this.target != null && (double)rattlesnake.distanceTo(this.target) < 5.0 && rattlesnake.getTarget() == null;
    }

    public void stop() {
        this.target = null;
        rattlesnake.setRattling(false);
    }

    public void tick() {
        rattlesnake.setRattling(true);
        rattlesnake.setCurled(true);
        ReflectionUtil.setField(rattlesnake, "curlTime", 0);
        if (target != null){
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
            rattlesnake.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
    }
}

