package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;


@Mixin(EntityGuster.class)
public class AIGuster extends Mob {

    protected AIGuster(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }
    EntityGuster guster = (EntityGuster)(Object)this;
    @Shadow private int maxLiftTime;

    @Shadow private int liftingTime;

    @Shadow
    private int shootingTicks;


    public void aiStep() {
        super.aiStep();
        Entity lifted = this.getLiftedEntity();
        if (lifted == null && !this.level().isClientSide && tickCount % 15 == 0) {
            List<ItemEntity> list = this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.8F));
            ItemEntity closestItem = null;
            for (int i = 0; i < list.size(); ++i) {
                ItemEntity entity = list.get(i);
                if (entity.onGround() && (closestItem == null || this.distanceTo(closestItem) > this.distanceTo(entity))) {
                    closestItem = entity;
                }
            }
            if (closestItem != null) {
                this.setLiftedEntity(closestItem.getId());
                maxLiftTime = 30 + random.nextInt(30);
            }
        }
        float f = (float) this.getY();
        if (this.isAlive()) {
            ParticleOptions type = this.getVariant() == 2 ? AMParticleRegistry.GUSTER_SAND_SPIN_SOUL.get() : this.getVariant() == 1 ? AMParticleRegistry.GUSTER_SAND_SPIN_RED.get() : AMParticleRegistry.GUSTER_SAND_SPIN.get();
            for (int j = 0; j < 4; ++j) {
                float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.95F;
                float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.95F;
                this.level().addParticle(type, this.getX() + (double) f1, f, this.getZ() + (double) f2, this.getX(), this.getY() + random.nextFloat() * this.getBbHeight() + 0.2F, this.getZ());
            }
        }
        if (lifted != null && liftingTime >= 0) {
            liftingTime++;
            float resist = 1F;
            if (lifted instanceof LivingEntity) {
                resist = (float) Mth.clamp((1.0D - ((LivingEntity) lifted).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
            }
            float radius = 1F + (liftingTime * 0.05F);
            if (lifted instanceof ItemEntity) {
                radius = 0.2F + (liftingTime * 0.025F);
            }
            float angle = liftingTime * -0.25F;
            double extraX = this.getX() + radius * Mth.sin(Mth.PI + angle);
            double extraZ = this.getZ() + radius * Mth.cos(angle);
            double d0 = (extraX - lifted.getX()) * resist;
            double d1 = (extraZ - lifted.getZ()) * resist;
            if (lifted instanceof Player && AInteractionConfig.gusterweighed) {
                int lift = ((Player) lifted).getArmorValue();
                float multiplier = 0.1f - lift / 3.0f;
                multiplier = Math.max(multiplier, 0.0f);
                lifted.setDeltaMovement(d0, multiplier * resist, d1);
            } else {
                lifted.setDeltaMovement(d0, 0.1 * resist, d1);
            }
            lifted.hasImpulse = true;
            if (liftingTime > maxLiftTime) {
                this.setLiftedEntity(0);
                liftingTime = -20;
                maxLiftTime = 30 + random.nextInt(30);
            }
        } else if (liftingTime < 0) {
            liftingTime++;
        } else if (this.getTarget() != null && this.distanceTo(this.getTarget()) < this.getBbWidth() + 1F && !(this.getTarget() instanceof EntityGuster)) {
            this.setLiftedEntity(this.getTarget().getId());
            maxLiftTime = 30 + random.nextInt(30);
        }
        if (!this.level().isClientSide && shootingTicks >= 0) {
            if (shootingTicks <= 0) {
                if (this.getTarget() != null && (lifted == null || lifted.getId() != this.getTarget().getId()) && this.isAlive()) {
                    this.spit(this.getTarget());
                }
                shootingTicks = 40 + random.nextInt(40);
            } else {
                shootingTicks--;
            }
        }
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround() && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
    }
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityGuster.class, EntityDataSerializers.INT);
    public int getVariant() {
        return this.entityData.get(VARIANT);
    }
    private void spit(LivingEntity target) {
        EntitySandShot sghot = new EntitySandShot(guster.level(), guster);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - sghot.getY();
        double d2 = target.getZ() - this.getZ();
        float f = Mth.sqrt((float)(d0 * d0 + d2 * d2)) * 0.35F;
        sghot.shoot(d0, d1 + (double) f, d2, 1F, 10.0F);
        sghot.setVariant(this.getVariant());
        if (!this.isSilent()) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SAND_BREAK, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }
        this.level().addFreshEntity(sghot);
    }
    public boolean hasLiftedEntity() {
        return this.entityData.get(LIFT_ENTITY) != 0;
    }

    public Entity getLiftedEntity() {
        if (!this.hasLiftedEntity()) {
            return null;
        } else {
            return this.level().getEntity(this.entityData.get(LIFT_ENTITY));
        }
    }
    private static final EntityDataAccessor<Integer> LIFT_ENTITY = SynchedEntityData.defineId(EntityGuster.class, EntityDataSerializers.INT);

    private void setLiftedEntity(int p_175463_1_) {
        this.entityData.set(LIFT_ENTITY, p_175463_1_);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return !AInteractionConfig.gusterprojectileprot;
    }

}
