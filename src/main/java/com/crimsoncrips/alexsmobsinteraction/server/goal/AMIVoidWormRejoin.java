package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.server.AMIVoidWormBoss;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AMIVoidWormRejoin extends Goal {

    private static final float REJOIN_SPEED = 3.0F;

    private final EntityVoidWorm worm;
    private EntityVoidWorm main;

    public AMIVoidWormRejoin(EntityVoidWorm worm) {
        this.worm = worm;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!AMIVoidWormBoss.enabled() || !worm.isSplitter() || worm.portalTarget != null)
            return false;
        main = AMIVoidWormBoss.getMain(worm);
        if (main == null)
            return false;
        if (AMIVoidWormBoss.isBarrageSplitter(worm))
            return AMIVoidWormBoss.getMode(main) == AMIVoidWormBoss.MODE_RECOMBINE;
        return worm.getData(AMIAttachments.SPLIT_TICKS) >= AMIVoidWormBoss.SPLIT_LIFETIME
                && AMIVoidWormBoss.getMode(main) == AMIVoidWormBoss.MODE_IDLE
                && AMIVoidWormBoss.getMode(worm) == AMIVoidWormBoss.MODE_IDLE;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return main != null && main.isAlive() && worm.isAlive();
    }

    @Override
    public void start() {
        AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_RECOMBINE);
    }

    @Override
    public void tick() {
        EntityVoidWormPart tail = AMIVoidWormBoss.getTail(main);
        Entity anchor = tail != null ? tail : main;
        Vec3 anchorPos = anchor.position();
        worm.openMouth(10);
        worm.getMoveControl().setWantedPosition(anchorPos.x, anchorPos.y, anchorPos.z, REJOIN_SPEED);
        if (worm.distanceToSqr(anchorPos) < AMIVoidWormBoss.REJOIN_DISTANCE * AMIVoidWormBoss.REJOIN_DISTANCE) {
            AMIVoidWormBoss.rejoin(main, worm);
        }
    }
}
