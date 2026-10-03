package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import net.minecraft.sounds.SoundEvents;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.AMIVoidWormBoss;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AMIVoidWormAttack extends Goal {

    private static final int IDLE_MIN_TICKS = AMIUtils.seconds(6);
    private static final int IDLE_RANDOM_TICKS = AMIUtils.seconds(20);
    private static final int IDLE_SPIT_INTERVAL = AMIUtils.seconds(13.3F);
    private static final int IDLE_SPIT_INTERVAL_HURT = AMIUtils.seconds(5);
    private static final float IDLE_SPIT_VELOCITY = 0.5F;

    private static final double PORTAL_PLAYER_DISTANCE = 20.0D;
    private static final int PORTAL_TIMEOUT = AMIUtils.seconds(10);
    private static final int CHARGE_TICKS = AMIUtils.seconds(7);
    private static final double CHARGE_DISTANCE = 48.0D;
    private static final float CHARGE_SPEED = 2.5F;
    private static final double CHARGE_VELOCITY = 1.6D;
    private static final int CHARGE_COUNT = 5;
    private static final int PORTAL_COOLDOWN = AMIUtils.seconds(30);

    private static final int GEYSER_PORTALS = 20;
    private static final int GEYSER_PER_WAVE = 4;
    private static final int GEYSER_SPAWN_INTERVAL = AMIUtils.seconds(3);
    private static final int GEYSER_OPEN_TICKS = AMIUtils.seconds(0.75F);
    private static final int GEYSER_FIRE_TICKS = AMIUtils.seconds(2);
    private static final int GEYSER_FIRE_INTERVAL = AMIUtils.seconds(0.4F);
    private static final int GEYSER_CLOSE_TICKS = AMIUtils.seconds(1);
    private static final int GEYSER_MIN_RADIUS = 0;
    private static final int GEYSER_MAX_RADIUS = 15;

    private static final int SURROUND_SHOTS = 200;
    private static final double GOLDEN_ANGLE = Math.PI * (3.0D - Math.sqrt(5.0D));

    private static final int BARRAGE_TICKS = AMIUtils.seconds(4);
    private static final int BARRAGE_VOLLEY_INTERVAL = AMIUtils.seconds(1);
    private static final int BARRAGE_SHOTS = 20;
    private static final int BARRAGE_COOLDOWN = AMIUtils.seconds(30);

    private final EntityVoidWorm worm;
    private int modeTicks;
    private int idleTicks;
    private int barrageCooldown;
    private int portalCooldown;
    private Vec3 moveTo;
    private Vec3 portalExit;
    private boolean portalEntered;
    private Vec3 chargeStart;
    private Vec3 chargeTarget;
    private Vec3 chargeDirection;
    private int chargesDone;
    private int geysersSpawned;
    private final List<EntityVoidPortal> geysers = new ArrayList<>();

    public AMIVoidWormAttack(EntityVoidWorm worm) {
        this.worm = worm;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return AMIVoidWormBoss.enabled() && worm.getTarget() != null && worm.getTarget().isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        if (AMIVoidWormBoss.getMode(worm) == AMIVoidWormBoss.MODE_IDLE)
            startIdle();
    }

    @Override
    public void stop() {
        if (worm.getTarget() != null && worm.getTarget().isAlive())
            return;
        int mode = AMIVoidWormBoss.getMode(worm);
        if (mode == AMIVoidWormBoss.MODE_BARRAGE && !worm.isSplitter()) {
            AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_RECOMBINE);
        } else if (mode != AMIVoidWormBoss.MODE_RECOMBINE && mode != AMIVoidWormBoss.MODE_BARRAGE) {
            AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_IDLE);
        }
        moveTo = null;
    }

    @Override
    public void tick() {
        LivingEntity target = worm.getTarget();
        if (target == null)
            return;
        if (barrageCooldown > 0)
            barrageCooldown--;
        if (portalCooldown > 0)
            portalCooldown--;
        int forcedAttack = worm.getData(AMIAttachments.VOID_WORM_FORCED_ATTACK);
        if (forcedAttack >= 0) {
            worm.setData(AMIAttachments.VOID_WORM_FORCED_ATTACK, -1);
            if (worm.portalTarget != null)
                worm.resetPortalLogic();
            modeTicks = 0;
            moveTo = null;
            startAttack(forcedAttack, target);
        }
        boolean hit = AMIVoidWormBoss.contactAttack(worm);
        float speed = 1.0F;
        modeTicks++;
        switch (AMIVoidWormBoss.getMode(worm)) {
            case AMIVoidWormBoss.MODE_PORTAL -> speed = tickPortal(target, hit);
            case AMIVoidWormBoss.MODE_GEYSER -> tickGeyser(target);
            case AMIVoidWormBoss.MODE_SURROUND -> tickSurround(target);
            case AMIVoidWormBoss.MODE_BARRAGE -> tickBarrage(target);
            case AMIVoidWormBoss.MODE_RECOMBINE -> circle(target);
            default -> tickIdle(target);
        }
        if (moveTo != null && worm.portalTarget == null) {
            worm.getMoveControl().setWantedPosition(moveTo.x, moveTo.y, moveTo.z, speed);
        }
    }

    private void startIdle() {
        if (AMIVoidWormBoss.getMode(worm) == AMIVoidWormBoss.MODE_PORTAL)
            portalCooldown = PORTAL_COOLDOWN;
        AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_IDLE);
        modeTicks = 0;
        idleTicks = IDLE_MIN_TICKS + worm.getRandom().nextInt(IDLE_RANDOM_TICKS);
        moveTo = null;
    }

    private void circle(LivingEntity target) {
        if (moveTo == null || worm.distanceToSqr(moveTo) < 16 || worm.horizontalCollision) {
            moveTo = worm.getBlockInViewAway(target.position(), 0.4F + worm.getRandom().nextFloat() * 0.2F);
        }
    }

    private void tickIdle(LivingEntity target) {
        circle(target);
        int interval = worm.getHealth() < worm.getMaxHealth() && !worm.isSplitter() ? IDLE_SPIT_INTERVAL_HURT : IDLE_SPIT_INTERVAL;
        if (modeTicks % interval == 0) {
            AMIVoidWormBoss.spit(worm, new Vec3(3, 3, 0), IDLE_SPIT_VELOCITY, AMIVoidWormBoss.SHOT_EASE);
            AMIVoidWormBoss.spit(worm, new Vec3(-3, 3, 0), IDLE_SPIT_VELOCITY, AMIVoidWormBoss.SHOT_EASE);
            AMIVoidWormBoss.spit(worm, new Vec3(3, -3, 0), IDLE_SPIT_VELOCITY, AMIVoidWormBoss.SHOT_EASE);
            AMIVoidWormBoss.spit(worm, new Vec3(-3, -3, 0), IDLE_SPIT_VELOCITY, AMIVoidWormBoss.SHOT_EASE);
        }
        if (modeTicks > idleTicks) {
            chooseNextAttack(target);
        }
    }

    private void chooseNextAttack(LivingEntity target) {
        modeTicks = 0;
        moveTo = null;
        if (!worm.isSplitter() && barrageCooldown <= 0 && worm.getHealth() <= worm.getMaxHealth() * 0.5F
                && AMIVoidWormBoss.getChain(worm).size() >= AMIVoidWormBoss.BARRAGE_MIN_SEGMENTS && worm.getRandom().nextBoolean()) {
            startBarrage();
            return;
        }
        if (worm.isSplitter()) {
            startIdle();
            return;
        }
        int attack = worm.getRandom().nextInt(portalCooldown > 0 ? 2 : 3);
        startAttack(attack == 0 ? AMIVoidWormBoss.MODE_SURROUND : attack == 1 ? AMIVoidWormBoss.MODE_GEYSER : AMIVoidWormBoss.MODE_PORTAL, target);
    }

    private void startAttack(int mode, LivingEntity target) {
        switch (mode) {
            case AMIVoidWormBoss.MODE_SURROUND -> AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_SURROUND);
            case AMIVoidWormBoss.MODE_GEYSER -> {
                geysersSpawned = 0;
                geysers.clear();
                AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_GEYSER);
            }
            case AMIVoidWormBoss.MODE_BARRAGE -> {
                if (AMIVoidWormBoss.getChain(worm).size() >= AMIVoidWormBoss.BARRAGE_MIN_SEGMENTS) {
                    startBarrage();
                } else {
                    startIdle();
                }
            }
            default -> {
                chargesDone = 0;
                startPortal(target);
            }
        }
    }

    private void startPortal(LivingEntity target) {
        portalExit = findPortalExit(target);
        portalEntered = false;
        chargeStart = null;
        chargeTarget = null;
        chargeDirection = null;
        if (portalExit == null || worm.portalTarget != null) {
            startIdle();
            return;
        }
        Vec3 toPlayer = target.getEyePosition().subtract(portalExit);
        Direction exitFacing = Direction.getNearest(toPlayer.x, toPlayer.y, toPlayer.z);
        worm.createPortal(worm.position().add(worm.getLookAngle().scale(10)), portalExit, exitFacing);
        if (worm.portalTarget == null) {
            startIdle();
            return;
        }
        AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_PORTAL);
    }

    private Vec3 findPortalExit(LivingEntity target) {
        for (int attempt = 0; attempt < 16; attempt++) {
            float angle = worm.getRandom().nextFloat() * Mth.TWO_PI;
            Vec3 candidate = target.position().add(Mth.cos(angle) * PORTAL_PLAYER_DISTANCE, 2 + worm.getRandom().nextInt(5), Mth.sin(angle) * PORTAL_PLAYER_DISTANCE);
            BlockPos pos = BlockPos.containing(candidate);
            if (worm.level().isEmptyBlock(pos) && worm.level().isEmptyBlock(pos.above()) && worm.level().isEmptyBlock(pos.below())) {
                return Vec3.atCenterOf(pos);
            }
        }
        return null;
    }

    private float tickPortal(LivingEntity target, boolean hit) {
        if (!portalEntered) {
            moveTo = null;
            if (worm.portalTarget == null) {
                portalEntered = true;
                modeTicks = 0;
            } else if (modeTicks > PORTAL_TIMEOUT) {
                worm.resetPortalLogic();
                startIdle();
            }
            return 1.0F;
        }
        if (chargeStart == null) {
            if (worm.getPortalTicks() > 0)
                return 1.0F;
            chargeStart = worm.position();
            chargeDirection = target.getEyePosition().subtract(chargeStart).normalize();
            chargeTarget = chargeStart.add(chargeDirection.scale(CHARGE_DISTANCE));
            modeTicks = 0;
        }
        worm.openMouth(10);
        moveTo = chargeTarget;
        worm.setDeltaMovement(chargeDirection.scale(CHARGE_VELOCITY));
        if (modeTicks > CHARGE_TICKS || worm.distanceToSqr(chargeStart) > CHARGE_DISTANCE * CHARGE_DISTANCE || worm.distanceToSqr(chargeTarget) < 9 || worm.horizontalCollision || hit) {
            chargesDone++;
            if (chargesDone < CHARGE_COUNT) {
                modeTicks = 0;
                startPortal(target);
            } else {
                startIdle();
            }
        }
        return CHARGE_SPEED;
    }

    private void tickGeyser(LivingEntity target) {
        circle(target);
        if (geysersSpawned < GEYSER_PORTALS && modeTicks % GEYSER_SPAWN_INTERVAL == 1) {
            for (int i = 0; i < GEYSER_PER_WAVE && geysersSpawned < GEYSER_PORTALS; i++) {
                EntityVoidPortal portal = spawnGeyser(target);
                if (portal != null)
                    geysers.add(portal);
                geysersSpawned++;
            }
        }
        geysers.removeIf(portal -> !portal.isAlive());
        for (EntityVoidPortal portal : geysers) {
            int age = portal.tickCount - GEYSER_OPEN_TICKS;
            if (age >= 0 && age < GEYSER_FIRE_TICKS && age % GEYSER_FIRE_INTERVAL == 0) {
                AMIVoidWormBoss.spawnStraightShot(worm, portal.position().add(0, 0.3D, 0), new Vec3(0, AMIVoidWormBoss.GEYSER_SPEED, 0), AMIVoidWormBoss.SHOT_GEYSER);
                portal.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 0.8F + worm.getRandom().nextFloat() * 0.4F);
            }
        }
        if (geysersSpawned >= GEYSER_PORTALS && geysers.isEmpty()) {
            startIdle();
        }
    }

    private EntityVoidPortal spawnGeyser(LivingEntity target) {
        for (int attempt = 0; attempt < 8; attempt++) {
            float angle = worm.getRandom().nextFloat() * Mth.TWO_PI;
            float radius = GEYSER_MIN_RADIUS + worm.getRandom().nextFloat() * (GEYSER_MAX_RADIUS - GEYSER_MIN_RADIUS);
            BlockPos column = BlockPos.containing(target.getX() + Mth.cos(angle) * radius, target.getY() + 3, target.getZ() + Mth.sin(angle) * radius);
            for (int down = 0; down < 10; down++) {
                BlockPos pos = column.below(down);
                if (worm.level().isEmptyBlock(pos) && worm.level().getBlockState(pos.below()).isFaceSturdy(worm.level(), pos.below(), Direction.UP)) {
                    EntityVoidPortal portal = AMEntityRegistry.VOID_PORTAL.get().create(worm.level());
                    if (portal == null)
                        return null;
                    portal.setPos(pos.getX() + 0.5D, pos.getY() + 0.05D, pos.getZ() + 0.5D);
                    portal.setAttachmentFacing(Direction.UP);
                    portal.setLifespan(GEYSER_OPEN_TICKS + GEYSER_FIRE_TICKS + GEYSER_CLOSE_TICKS);
                    worm.level().addFreshEntity(portal);
                    return portal;
                }
            }
        }
        return null;
    }

    private void tickSurround(LivingEntity target) {
        circle(target);
        if (modeTicks == 1) {
            Vec3 center = target.getEyePosition();
            for (int i = 0; i < SURROUND_SHOTS; i++) {
                double y = 1.0D - 2.0D * (i + 0.5D) / SURROUND_SHOTS;
                double ring = Math.sqrt(1.0D - y * y);
                double angle = i * GOLDEN_ANGLE;
                Vec3 position = center.add(new Vec3(Math.cos(angle) * ring, y, Math.sin(angle) * ring).scale(AMIVoidWormBoss.SURROUND_RADIUS));
                if (worm.level().isEmptyBlock(BlockPos.containing(position)))
                    AMIVoidWormBoss.spawnStraightShot(worm, position, center.subtract(position).normalize().scale(AMIVoidWormBoss.SURROUND_SPEED), AMIVoidWormBoss.SHOT_SURROUND);
            }
            worm.openMouth(15);
            worm.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.6F);
        }
        if (modeTicks >= AMIVoidWormBoss.SURROUND_TICKS) {
            startIdle();
        }
    }

    private void startBarrage() {
        List<EntityVoidWormPart> chain = AMIVoidWormBoss.getChain(worm);
        int segments = chain.size();
        List<EntityVoidWormPart> cuts = new ArrayList<>();
        cuts.add(chain.get(segments * 3 / 4));
        cuts.add(chain.get(segments / 2));
        cuts.add(chain.get(segments / 4));
        for (EntityVoidWormPart cut : cuts) {
            AMIVoidWormBoss.splitAt(worm, cut, true);
        }
        modeTicks = 0;
        AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_BARRAGE);
    }

    private void tickBarrage(LivingEntity target) {
        circle(target);
        if (worm.isSplitter()) {
            EntityVoidWorm main = AMIVoidWormBoss.getMain(worm);
            if (main == null || AMIVoidWormBoss.getMode(main) != AMIVoidWormBoss.MODE_BARRAGE) {
                AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_RECOMBINE);
                return;
            }
        } else if (modeTicks >= BARRAGE_TICKS) {
            AMIVoidWormBoss.setMode(worm, AMIVoidWormBoss.MODE_RECOMBINE);
            barrageCooldown = BARRAGE_COOLDOWN;
            moveTo = null;
            return;
        }
        if (modeTicks % BARRAGE_VOLLEY_INTERVAL == 0) {
            for (int i = 0; i < BARRAGE_SHOTS; i++) {
                float angle = (i + worm.getRandom().nextFloat()) / BARRAGE_SHOTS * Mth.TWO_PI;
                float spread = 1.0F + worm.getRandom().nextFloat() * 5.0F;
                float velocity = AMIVoidWormBoss.MISSILE_RISE_SPEED * (0.6F + worm.getRandom().nextFloat());
                AMIVoidWormBoss.spit(worm, new Vec3(Mth.cos(angle) * spread, 4, Mth.sin(angle) * spread), velocity, 12.0F, AMIVoidWormBoss.SHOT_MISSILE);
            }
        }
    }
}
