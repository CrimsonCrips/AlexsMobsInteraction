package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.w3c.dom.Entity;

public class AMISurroundEntity extends AMIFollowNearestGoal {



    public AMISurroundEntity(EntityCockroach mob) {
        super(mob, LivingEntity.class, 15, 1.2,living -> {
            return ((AsmonRoach)mob).getWorshiping() instanceof EntityCockroach asmon && (asmon.getLastHurtByMob() == living || (living instanceof EntityCockroach assimilateTarget && ((AsmonRoach)assimilateTarget).getWorshiping() == null && !(((AsmonRoach)assimilateTarget).isGod()) && asmon.getLastHurtByMob() == null));
        });
    }

    @Override
    public boolean canUse() {
        AsmonRoach myAccessor = (AsmonRoach) mob;
        return super.canUse() && !myAccessor.isGod() && myAccessor.getWorshiping() instanceof EntityCockroach;
    }

    @Override
    public void tick() {
        var target = getTarget();
        if (target != null && !mob.isLeashed()) {
            mob.getLookControl().setLookAt(target, 10.0F, (float) this.mob.getMaxHeadXRot());
            navigation.moveTo(target, speedModifier);
            if (mob.distanceTo(target) < 1){
                if(mob.getRandom().nextDouble() < 0.5 && !(target instanceof EntityCockroach)){
                    target.hurt(mob.damageSources().mobAttack(mob), 0.8F);
                }
                target.knockback(0.3F, target.getX() - mob.getX(), target.getZ() - mob.getZ());
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 0));
                if (target instanceof EntityCockroach possibleAssimilate && ((AsmonRoach)possibleAssimilate).getWorshiping() == null && !(((AsmonRoach)possibleAssimilate).isGod())){
                    ((AsmonRoach)possibleAssimilate).setWorshippingUUID(((AsmonRoach)mob).getWorshiping().getUUID());
                    possibleAssimilate.setCustomName(Component.nullToEmpty("Servant"));
                }
            }
        } else {
            navigation.stop();
        }
    }

}