package com.crimsoncrips.alexsmobsinteraction.server.entity;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityFarseerPortal extends Entity {

    public static final int OPEN_TICK = AMIUtils.seconds(1.5F);
    public static final int CLOSE_TICKS = AMIUtils.seconds(0.5F);
    private static final int OPEN_DURATION = AMIUtils.seconds(20);
    public static final int LIFETIME = OPEN_TICK + OPEN_DURATION + CLOSE_TICKS;
    private static final int TELEPORT_COOLDOWN = AMIUtils.seconds(2);
    private static final String COOLDOWN_TAG = "AMIFarseerPortalCooldown";

    private static final EntityDataAccessor<Long> BIRTH_TIME = SynchedEntityData.defineId(EntityFarseerPortal.class, EntityDataSerializers.LONG);

    @Nullable
    private UUID linkedPortal;

    public EntityFarseerPortal(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static void openPair(ServerLevel level, Vec3 origin) {
        EntityFarseerPortal lower = AMIEntityRegistry.FARSEER_PORTAL.get().create(level);
        EntityFarseerPortal upper = AMIEntityRegistry.FARSEER_PORTAL.get().create(level);
        if (lower == null || upper == null)
            return;
        BlockPos column = BlockPos.containing(origin);
        double skyY = Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING, column.getX(), column.getZ()) + 0.75D, origin.y + 3.0D);
        lower.setPos(origin);
        upper.setPos(origin.x, skyY + 2, origin.z);
        lower.linkedPortal = upper.getUUID();
        upper.linkedPortal = lower.getUUID();
        long now = level.getGameTime();
        lower.entityData.set(BIRTH_TIME, now);
        upper.entityData.set(BIRTH_TIME, now);
        level.addFreshEntity(lower);
        level.addFreshEntity(upper);
        lower.playSound(AMSoundRegistry.FARSEER_EMERGE.get(), 1.0F, 1.0F);
        upper.playSound(AMSoundRegistry.FARSEER_EMERGE.get(), 1.0F, 1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BIRTH_TIME, 0L);
    }

    public float getAge(float partialTick) {
        return this.level().getGameTime() - this.entityData.get(BIRTH_TIME) + partialTick;
    }

    public boolean isOpen() {
        float age = this.getAge(0.0F);
        return age >= OPEN_TICK && age < LIFETIME - CLOSE_TICKS;
    }

    public int getPortalFrame(float partialTick) {
        float age = this.getAge(partialTick);
        if (age < 10.0F)
            return 0;
        if (age < 20.0F)
            return 1;
        if (age < OPEN_TICK)
            return 2;
        float left = LIFETIME - age;
        if (left < CLOSE_TICKS)
            return left < 3.0F ? 0 : (left < 6.0F ? 1 : 2);
        return 3;
    }

    public float getPortalAlpha(float partialTick) {
        float age = this.getAge(partialTick);
        float left = LIFETIME - age;
        return Math.max(0.0F, Math.min(1.0F, Math.min(age / 5.0F, left / 3.0F)));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide)
            return;
        if (this.getAge(0.0F) >= LIFETIME) {
            this.discard();
            return;
        }
        if (!this.isOpen() || this.linkedPortal == null)
            return;
        if (!(((ServerLevel) this.level()).getEntity(this.linkedPortal) instanceof EntityFarseerPortal exit)) {
            return;
        }
        long now = this.level().getGameTime();
        for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(0.5D), entity -> !(entity instanceof EntityFarseerPortal) && entity.canUsePortal(false))) {
            if (entity.isPassenger() || now - entity.getPersistentData().getLong(COOLDOWN_TAG) < TELEPORT_COOLDOWN)
                continue;
            entity.getPersistentData().putLong(COOLDOWN_TAG, now);
            exit.getPersistentData().putLong(COOLDOWN_TAG, now);
            entity.teleportTo(exit.getX(), exit.getY() + 0.5D, exit.getZ());
            entity.resetFallDistance();
            this.level().playSound(null, exit.getX(), exit.getY(), exit.getZ(), SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 0.6F);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(BIRTH_TIME, tag.getLong("BirthTime"));
        if (tag.hasUUID("LinkedPortal")) {
            this.linkedPortal = tag.getUUID("LinkedPortal");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("BirthTime", this.entityData.get(BIRTH_TIME));
        if (this.linkedPortal != null) {
            tag.putUUID("LinkedPortal", this.linkedPortal);
        }
    }
}
