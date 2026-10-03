package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityFroststalker;
import com.mojang.datafixers.DataFixUtils;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;



public class AMIFollowAsmon extends Goal {


    private final EntityCockroach mob;
    private int timeToRecalcPath;
    private int nextStartTick;

    public AMIFollowAsmon(EntityCockroach cockroach) {
        this.mob = cockroach;
        this.nextStartTick = this.nextStartTick(cockroach);
    }

    protected int nextStartTick(EntityCockroach cockroach) {
        return 100 + cockroach.getRandom().nextInt(200) % 40;
    }

    public boolean canUse() {
        if (AMIUtils.getWorshiping(mob) instanceof EntityCockroach entityCockroach && !mob.getData(AMIAttachments.IS_GOD) && entityCockroach.getLastHurtByMob() == null){
            if (this.nextStartTick > 0) {
                --this.nextStartTick;
                return false;
            } else {
                this.nextStartTick = this.nextStartTick(this.mob);
                return true;
            }
        } else return false;
    }

    public boolean canContinueToUse() {
        return AMIUtils.getWorshiping(mob) != null && !mob.getData(AMIAttachments.IS_GOD);
    }

    public void start() {
        this.timeToRecalcPath = 0;
    }

    public void tick() {
        Entity asmon = AMIUtils.getWorshiping(mob);
        if (--this.timeToRecalcPath <= 0 && mob.distanceTo(asmon) > 6) {
            this.timeToRecalcPath = 10;
            mob.getNavigation().moveTo(asmon,1.1);
        }
    }
}