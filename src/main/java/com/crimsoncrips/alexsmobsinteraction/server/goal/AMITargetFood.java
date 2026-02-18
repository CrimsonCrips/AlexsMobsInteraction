package com.crimsoncrips.alexsmobsinteraction.server.goal;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class AMITargetFood extends Goal {
    private int eatCooldown = 0;
    private final EntityCatfish catfish;
    private Entity food;
    private int executionCooldown = 50;

    public AMITargetFood(EntityCatfish catfish) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.catfish = catfish;
    }

    public boolean canUse() {
        if (this.catfish.isInWaterOrBubble() && eatCooldown <= 0) {
            if (this.executionCooldown > 0) {
                --this.executionCooldown;
            } else {
                this.executionCooldown = 50 + catfish.getRandom().nextInt(50);
                if (!this.catfish.isFull()) {
                    List<Entity> list = this.catfish.level().getEntitiesOfClass(Entity.class, this.catfish.getBoundingBox().inflate(8.0, 8.0, 8.0), EntitySelector.NO_SPECTATORS.and((entity) -> {
                        return entity != this.catfish && isFood(entity);
                    }));
                    EntityCatfish var10001 = this.catfish;
                    Objects.requireNonNull(var10001);
                    list.sort(Comparator.comparingDouble(var10001::distanceToSqr));
                    if (!list.isEmpty()) {
                        this.food = (Entity)list.get(0);
                        return true;
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        return this.food != null && this.food.isAlive() && !this.catfish.isFull();
    }

    public void stop() {
        this.executionCooldown = 5;
    }

    public void tick() {
        this.catfish.getNavigation().moveTo(this.food.getX(), this.food.getY(0.5), this.food.getZ(), 1.0);
        float eatDist = this.catfish.getBbWidth() * 0.65F + this.food.getBbWidth();
        if (this.catfish.distanceTo(this.food) < eatDist + 3.0F && this.catfish.hasLineOfSight(this.food)) {
            Vec3 delta = getMouthVec().subtract(this.food.position()).normalize().scale(0.10000000149011612);
            this.food.setDeltaMovement(this.food.getDeltaMovement().add(delta));
            if (this.catfish.distanceTo(this.food) < eatDist) {
                if (this.food instanceof Player) {
                    this.food.hurt(this.catfish.damageSources().mobAttack(this.catfish), 12000.0F);
                } else if (this.catfish.swallowEntity(this.food)) {
                    this.catfish.gameEvent(GameEvent.EAT);
                    this.catfish.playSound(SoundEvents.GENERIC_EAT, 1, this.catfish.getVoicePitch());
                    this.food.discard();
                }
            }
        }

    }

    private Vec3 getMouthVec() {
        Vec3 vec3 = (new Vec3(0.0, (double)(catfish.getBbHeight() * 0.25F), (double)(catfish.getBbWidth() * 0.8F))).xRot(catfish.getXRot() * 0.017453292F).yRot(-catfish.getYRot() * 0.017453292F);
        return catfish.position().add(vec3);
    }

    private boolean isFood(Entity entity) {
        if (AlexsMobsInteraction.TARGETS_CONFIG.CANNIBALISM_ENABLED.get()) {
            if (catfish.getCatfishSize() == 2) {
                return !entity.getType().is(AMTagRegistry.CATFISH_IGNORE_EATING) && entity instanceof Mob && entity.getBbHeight() <= 1.0F;
            } else {
                return entity instanceof ItemEntity && ((ItemEntity) entity).getAge() > 35;
            }
        } else {
            if (catfish.getCatfishSize() == 2) {
                return !entity.getType().is(AMTagRegistry.CATFISH_IGNORE_EATING) && entity instanceof Mob && !(entity instanceof EntityCatfish) && entity.getBbHeight() <= 1.0F;
            } else {
                return entity instanceof ItemEntity && ((ItemEntity) entity).getAge() > 35;
            }
        }
    }
}