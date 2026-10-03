package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import com.crimsoncrips.alexsmobsinteraction.mixins.mobs.void_worm.AMIVoidWormAccessor;
import net.minecraft.core.particles.ParticleTypes;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormPart;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWormShot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AMIVoidWormBoss {

    public static final int MODE_IDLE = 0;
    public static final int MODE_PORTAL = 1;
    public static final int MODE_BARRAGE = 3;
    public static final int MODE_RECOMBINE = 4;
    public static final int MODE_GEYSER = 5;
    public static final int MODE_SURROUND = 6;

    public static final int SHOT_NORMAL = 0;
    public static final int SHOT_EASE = 1;
    public static final int SHOT_MISSILE = 3;
    public static final int SHOT_GEYSER = 4;
    public static final int SHOT_SURROUND = 5;

    public static final float SPLITTER_SCALE = 0.7F;
    private static final ResourceLocation SPLITTER_SCALE_ID = ResourceLocation.fromNamespaceAndPath(AlexsMobsInteraction.MODID, "splitter_scale");

    public static final int BARRAGE_MIN_SEGMENTS = 8;

    public static final int SEGMENT_MULTIPLIER = 3;
    public static final double HEALTH_MULTIPLIER = 2.0D;

    public static final float EASE_SPEED = 1.6F;
    public static final int EASE_TICKS = AMIUtils.seconds(1.25F);

    public static final int SHOT_LIFETIME = AMIUtils.seconds(6);

    public static final float GEYSER_SPEED = 0.8F;

    public static final double SURROUND_RADIUS = 20.0D;
    public static final int SURROUND_TICKS = AMIUtils.seconds(10);
    public static final float SURROUND_SPEED = (float) (SURROUND_RADIUS / SURROUND_TICKS);

    public static final float MISSILE_SPEED = 2.0F;
    public static final float MISSILE_START_SPEED = 0.15F;
    public static final float MISSILE_ACCELERATION = 0.08F;
    public static final float MISSILE_TURN = 0.06F;
    public static final float MISSILE_RISE_SPEED = 1.0F;
    public static final float MISSILE_RISE_DRAG = 0.85F;
    public static final int MISSILE_RISE_TICKS = AMIUtils.seconds(0.75F);
    public static final int MISSILE_RISE_VARIANCE = AMIUtils.seconds(0.6F);

    public static final int SPLIT_LIFETIME = AMIUtils.seconds(30);
    public static final int SPLIT_EXPIRE = AMIUtils.seconds(45);
    public static final int RECOMBINE_TIMEOUT = AMIUtils.seconds(15);
    public static final double REJOIN_DISTANCE = 3.0D;

    public static final float SHATTER_FIRST_DELAY = AMIUtils.seconds(0.5F);
    public static final float SHATTER_DELAY_RATIO = 0.93F;
    public static final int SHATTER_HEAD_LINGER = AMIUtils.seconds(3);

    public static final int LOOT_DEATH_TIME = AMIUtils.seconds(4);
    public static final int PORTAL_CLOSE_TICKS = AMIUtils.seconds(1);

    private static final int MAX_CHAIN = 1024;

    public static boolean enabled() {
        return AlexsMobsInteraction.COMMON_CONFIG.VOID_WORM_REWORK_ENABLED.get();
    }

    public static int getMode(EntityVoidWorm worm) {
        return worm.getData(AMIAttachments.VOID_WORM_MODE);
    }

    public static void setMode(EntityVoidWorm worm, int mode) {
        AMIAttachments.setIfChanged(worm, AMIAttachments.VOID_WORM_MODE, mode);
    }

    public static boolean isBarrageSplitter(EntityVoidWorm worm) {
        return worm.isSplitter() && worm.getData(AMIAttachments.BARRAGE_SPLITTER);
    }

    @Nullable
    public static EntityVoidWorm getMain(EntityVoidWorm splitter) {
        UUID mainId = splitter.getSplitFromUUID();
        if (mainId == null || !(splitter.level() instanceof ServerLevel serverLevel))
            return null;
        return serverLevel.getEntity(mainId) instanceof EntityVoidWorm main && main.isAlive() && !main.isSplitter() ? main : null;
    }

    @Nullable
    public static EntityVoidWorm getSplitterOf(Entity entity) {
        if (entity instanceof EntityVoidWorm worm)
            return worm.isSplitter() ? worm : null;
        if (entity instanceof EntityVoidWormPart part) {
            EntityVoidWorm worm = part.getWorm();
            return worm != null && worm.isSplitter() ? worm : null;
        }
        return null;
    }

    public static List<EntityVoidWormPart> getChain(EntityVoidWorm worm) {
        List<EntityVoidWormPart> chain = new ArrayList<>();
        Entity next = worm.getChild();
        while (next instanceof EntityVoidWormPart part && chain.size() < MAX_CHAIN && !chain.contains(part)) {
            chain.add(part);
            next = part.getChild();
        }
        return chain;
    }

    @Nullable
    public static EntityVoidWormPart getTail(EntityVoidWorm worm) {
        List<EntityVoidWormPart> chain = getChain(worm);
        return chain.isEmpty() ? null : chain.get(chain.size() - 1);
    }

    public static void rescale(EntityVoidWorm worm) {
        List<EntityVoidWormPart> chain = getChain(worm);
        int segments = chain.size();
        if (segments == 0)
            return;
        worm.setSegmentCount(segments);
        int tailStart = Math.min(3, segments);
        float size = worm.isSplitter() ? SPLITTER_SCALE : 1F;
        for (int i = 0; i < segments; i++) {
            EntityVoidWormPart part = chain.get(i);
            boolean tail = i >= segments - tailStart;
            float scale = (1F + (i / (float) segments) * 0.5F) * size;
            part.setBodyIndex(i);
            part.setTail(tail);
            part.setWormScale(tail ? scale * 0.85F : scale);
        }
    }

    @Nullable
    public static EntityVoidWorm splitAt(EntityVoidWorm main, EntityVoidWormPart cut, boolean barrage) {
        if (!(cut.getChild() instanceof EntityVoidWormPart child))
            return null;
        EntityVoidWorm head = AMEntityRegistry.VOID_WORM.get().create(main.level());
        if (head == null)
            return null;
        Entity previous = cut.getParent();
        head.copyPosition(cut);
        head.setNoAi(main.isNoAi());
        head.setChildId(child.getUUID());
        child.setParent(head);
        head.setSplitter(true);
        head.setSplitFromUuid(main.getUUID());
        head.setWormSpeed(Math.max(main.getWormSpeed(), 0.4F));
        head.setTarget(main.getTarget());
        head.setData(AMIAttachments.BARRAGE_SPLITTER, barrage);
        head.setSilent(true);
        if (barrage)
            setMode(head, MODE_BARRAGE);
        if (previous instanceof EntityVoidWormPart previousPart) {
            previousPart.setChildId(null);
        } else if (previous == main) {
            main.setChildId(null);
        }
        main.level().addFreshEntity(head);
        cut.discard();
        rescale(head);
        rescale(main);
        return head;
    }

    public static void shrinkSplitter(EntityVoidWorm splitter) {
        AttributeInstance scale = splitter.getAttribute(Attributes.SCALE);
        if (scale == null || scale.hasModifier(SPLITTER_SCALE_ID))
            return;
        scale.addPermanentModifier(new AttributeModifier(SPLITTER_SCALE_ID, SPLITTER_SCALE - 1.0F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        rescale(splitter);
    }

    public static void setGlowing(EntityVoidWorm worm, boolean glowing) {
        if (worm.hasGlowingTag() != glowing)
            worm.setGlowingTag(glowing);
        for (EntityVoidWormPart part : getChain(worm)) {
            if (part.hasGlowingTag() != glowing)
                part.setGlowingTag(glowing);
        }
    }

    public static void rejoin(EntityVoidWorm main, EntityVoidWorm splitter) {
        List<EntityVoidWormPart> splitterChain = getChain(splitter);
        if (!splitterChain.isEmpty()) {
            EntityVoidWormPart first = splitterChain.get(0);
            EntityVoidWormPart mainTail = getTail(main);
            if (mainTail != null) {
                mainTail.setChildId(first.getUUID());
                first.setParent(mainTail);
            } else {
                main.setChildId(first.getUUID());
                first.setParent(main);
            }
        }
        splitter.discard();
        rescale(main);
        main.playSound(SoundEvents.ENDERMAN_TELEPORT, 3.0F, 0.6F);
    }

    public static List<EntityVoidWorm> getBarrageSplitters(EntityVoidWorm main) {
        List<EntityVoidWorm> splitters = new ArrayList<>();
        if (main.level() instanceof ServerLevel serverLevel) {
            splitters.addAll(serverLevel.getEntities(AMEntityRegistry.VOID_WORM.get(), worm -> isBarrageSplitter(worm) && worm.isAlive() && worm.getData(AMIAttachments.SHATTER_TICKS) <= 0 && main.getUUID().equals(worm.getSplitFromUUID())));
        }
        return splitters;
    }

    public static void spit(EntityVoidWorm worm, Vec3 shotAt, float velocity, int shotMode) {
        spit(worm, shotAt, velocity, 3.0F, shotMode);
    }

    public static void spit(EntityVoidWorm worm, Vec3 shotAt, float velocity, float inaccuracy, int shotMode) {
        shotAt = shotAt.yRot(-worm.getYRot() * Mth.DEG_TO_RAD);
        EntityVoidWormShot shot = new EntityVoidWormShot(worm.level(), worm);
        float lift = Mth.sqrt((float) (shotAt.x * shotAt.x + shotAt.z * shotAt.z)) * 0.35F;
        shot.shoot(shotAt.x, shotAt.y + lift, shotAt.z, velocity, inaccuracy);
        shot.setData(AMIAttachments.VOID_SHOT_MODE, shotMode);
        if (!worm.isSilent()) {
            worm.gameEvent(GameEvent.PROJECTILE_SHOOT);
            worm.level().playSound(null, worm.getX(), worm.getY(), worm.getZ(), SoundEvents.DROWNED_SHOOT, worm.getSoundSource(), 1.0F, 1.0F + (worm.getRandom().nextFloat() - worm.getRandom().nextFloat()) * 0.2F);
        }
        worm.openMouth(5);
        worm.level().addFreshEntity(shot);
    }

    public static int getMissileRiseTicks(Entity shot) {
        return MISSILE_RISE_TICKS + Math.floorMod(shot.getId() * 7, MISSILE_RISE_VARIANCE);
    }

    public static void spawnStraightShot(EntityVoidWorm worm, Vec3 position, Vec3 velocity, int shotMode) {
        EntityVoidWormShot shot = AMEntityRegistry.VOID_WORM_SHOT.get().create(worm.level());
        if (shot == null)
            return;
        shot.setShooter(worm);
        shot.setPos(position.x, position.y, position.z);
        shot.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), 0.0F);
        shot.setData(AMIAttachments.VOID_SHOT_MODE, shotMode);
        worm.level().addFreshEntity(shot);
    }

    public static void flashHurt(EntityVoidWorm splitter, DamageSource source) {
        splitter.hurtDuration = 10;
        splitter.hurtTime = splitter.hurtDuration;
        splitter.level().broadcastDamageEvent(splitter, source);
    }

    public static boolean contactAttack(EntityVoidWorm worm) {
        boolean hit = false;
        for (LivingEntity entity : worm.level().getEntitiesOfClass(LivingEntity.class, worm.getBoundingBox().inflate(2.0D))) {
            if (entity.is(worm) || entity instanceof EntityVoidWormPart || entity instanceof EntityVoidWorm || entity.isAlliedTo(worm))
                continue;
            if (worm.isMouthOpen()) {
                launch(worm, entity, true);
                hit = true;
                wormAttack(entity, worm.damageSources().mobAttack(worm), 8.0F + worm.getRandom().nextFloat() * 8.0F);
            } else {
                worm.openMouth(15);
            }
        }
        return hit;
    }

    private static void wormAttack(Entity entity, DamageSource source, float damage) {
        damage *= (float) AMConfig.voidWormDamageModifier;
        entity.hurt(source, entity instanceof EnderDragon ? damage * 0.5F : damage);
    }

    private static void launch(EntityVoidWorm worm, Entity entity, boolean huge) {
        if (!entity.onGround())
            return;
        double dx = entity.getX() - worm.getX();
        double dz = entity.getZ() - worm.getZ();
        double distance = Math.max(dx * dx + dz * dz, 0.001D);
        float strength = huge ? 2F : 0.5F;
        entity.push(dx / distance * strength, huge ? 0.5D : 0.2F, dz / distance * strength);
    }

    public static void claimPortal(EntityVoidWorm worm) {
        EntityVoidPortal portal = worm.portalTarget;
        if (!enabled() || portal == null || AMIAttachments.getUUID(portal, AMIAttachments.PORTAL_OWNER) != null)
            return;
        AMIAttachments.setUUID(portal, AMIAttachments.PORTAL_OWNER, worm.getUUID());
        if (portal.getSister() instanceof EntityVoidPortal sister)
            AMIAttachments.setUUID(sister, AMIAttachments.PORTAL_OWNER, worm.getUUID());
    }

    public static void markPortalUsed(EntityVoidPortal portal) {
        if (AMIAttachments.getUUID(portal, AMIAttachments.PORTAL_OWNER) == null)
            return;
        portal.setData(AMIAttachments.PORTAL_USED, true);
        if (portal.getSister() instanceof EntityVoidPortal sister)
            sister.setData(AMIAttachments.PORTAL_USED, true);
    }

    public static void tickWormPortal(EntityVoidPortal portal) {
        UUID ownerId = AMIAttachments.getUUID(portal, AMIAttachments.PORTAL_OWNER);
        if (ownerId == null || portal.getLifespan() <= PORTAL_CLOSE_TICKS || !(portal.level() instanceof ServerLevel serverLevel))
            return;
        if (!(serverLevel.getEntity(ownerId) instanceof EntityVoidWorm worm) || !worm.isAlive()) {
            portal.setLifespan(PORTAL_CLOSE_TICKS);
            return;
        }
        if (worm.portalTarget == portal && worm.isOnPortalCooldown() && worm.getBoundingBox().intersects(portal.getBoundingBox().inflate(2.0D)))
            worm.setPortalCooldown(0);
        if (portal.getData(AMIAttachments.PORTAL_USED)) {
            if (worm.getPortalTicks() <= 0 && getChain(worm).stream().allMatch(part -> part.getPortalTicks() <= 0))
                portal.setLifespan(PORTAL_CLOSE_TICKS);
            return;
        }
        EntityVoidPortal target = worm.portalTarget;
        if (target == null || !(target.getUUID().equals(portal.getUUID()) || target.getUUID().equals(portal.getSisterId())))
            portal.setLifespan(PORTAL_CLOSE_TICKS);
    }

    public static boolean tickShatterDeath(EntityVoidWorm worm) {
        int shatterTicks = worm.getData(AMIAttachments.SHATTER_TICKS);
        if (shatterTicks < 0)
            return false;
        updateBossBar(worm, "shattering");
        List<EntityVoidWormPart> chain = getChain(worm);
        if (shatterTicks == 0)
            worm.setData(AMIAttachments.SHATTER_SEGMENTS, chain.size());
        shatterTicks++;
        worm.setData(AMIAttachments.SHATTER_TICKS, shatterTicks);
        worm.deathTime = Math.min(worm.deathTime + 1, 20);
        int segments = worm.getData(AMIAttachments.SHATTER_SEGMENTS);
        int shattered = Math.min(segments, shatteredBy(shatterTicks, segments));
        for (int i = segments - chain.size(); i < shattered && !chain.isEmpty(); i++) {
            EntityVoidWormPart part = chain.remove(chain.size() - 1);
            shatter(worm, part, i / (float) Math.max(segments, 1));
            if (chain.isEmpty()) {
                worm.setChildId(null);
            } else {
                chain.get(chain.size() - 1).setChildId(null);
            }
        }
        if (!chain.isEmpty())
            return true;
        if (!worm.isSplitter()) {
            int headStart = worm.getData(AMIAttachments.SHATTER_HEAD_START);
            if (headStart < 0) {
                worm.setData(AMIAttachments.SHATTER_HEAD_START, shatterTicks);
                return true;
            }
            if (shatterTicks - headStart < SHATTER_HEAD_LINGER)
                return true;
        }
        if (worm.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(AMParticleRegistry.WORM_PORTAL.get(), worm.getX(), worm.getY(0.5D), worm.getZ(), 120, 1.2D, 1.2D, 1.2D, 0.15D);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, worm.getX(), worm.getY(0.5D), worm.getZ(), 1, 0, 0, 0, 0);
        }
        worm.playSound(SoundEvents.GENERIC_EXPLODE.value(), 4.0F, 0.6F);
        worm.setData(AMIAttachments.SHATTER_TICKS, -1);
        worm.deathTime = 79;
        return false;
    }

    private static int shatteredBy(int ticks, int segments) {
        double remaining = 1.0D - ticks * (1.0D - SHATTER_DELAY_RATIO) / SHATTER_FIRST_DELAY;
        if (remaining <= 0.0D)
            return segments;
        return Mth.clamp(Mth.floor(Math.log(remaining) / Math.log(SHATTER_DELAY_RATIO)), 0, segments);
    }

    private static void shatter(EntityVoidWorm worm, EntityVoidWormPart part, float progress) {
        if (worm.level() instanceof ServerLevel serverLevel) {
            float size = part.getBbWidth() * 0.5F;
            serverLevel.sendParticles(AMParticleRegistry.WORM_PORTAL.get(), part.getX(), part.getY(0.5D), part.getZ(), 25, size, size, size, 0.08D);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, part.getX(), part.getY(0.5D), part.getZ(), 10, size, size, size, 0.05D);
        }
        part.playSound(SoundEvents.GLASS_BREAK, 2.0F, 0.5F + progress * 0.8F);
        part.discard();
    }

    public static void updateBossBar(EntityVoidWorm worm) {
        updateBossBar(worm, switch (getMode(worm)) {
            case MODE_PORTAL -> "portal";
            case MODE_GEYSER -> "geyser";
            case MODE_SURROUND -> "surround";
            case MODE_BARRAGE -> "barrage";
            case MODE_RECOMBINE -> "recombine";
            default -> "idle";
        });
    }

    private static void updateBossBar(EntityVoidWorm worm, String state) {
        ServerBossEvent bossBar = ((AMIVoidWormAccessor) worm).alexsMobsInteraction$getBossInfo();
        Component name = Component.translatable("boss.alexsmobsinteraction.void_worm.state", worm.getDisplayName(), Component.translatable("boss.alexsmobsinteraction.void_worm." + state));
        if (!bossBar.getName().equals(name)) {
            bossBar.setName(name);
        }
    }
}
