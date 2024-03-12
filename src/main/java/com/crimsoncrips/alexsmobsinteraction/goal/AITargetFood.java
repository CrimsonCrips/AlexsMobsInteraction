package com.crimsoncrips.alexsmobsinteraction.goal;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class AITargetFood extends Goal {
    private int eatCooldown = 0;
    private final EntityCatfish catfish;
    private Entity food;
    private int executionCooldown = 50;


    Predicate<LivingEntity> bigCATFISHCANNOTEAT = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.BIGCATFISHCANNOTEAT);

    public AITargetFood(EntityCatfish catfish) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.catfish = catfish;
    }


    @Override
    public boolean canUse() {
        if (!catfish.isInWaterOrBubble() || this.eatCooldown > 0) {
            return false;
        }
        if (executionCooldown > 0) {
            executionCooldown--;
        } else {
            executionCooldown = 50;
            if (!this.catfish.isFull()) {
                final List<Entity> list = catfish.level().getEntitiesOfClass(Entity.class, catfish.getBoundingBox().inflate(8, 8, 8), EntitySelector.NO_SPECTATORS.and(entity -> entity != catfish && this.isFood(entity)));
                list.sort(Comparator.comparingDouble(catfish::distanceToSqr));
                if (!list.isEmpty()) {
                    food = list.get(0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return food != null && food.isAlive() && !this.catfish.isFull();
    }

    public void stop() {
        executionCooldown = 5;
    }

    @Override
    public void tick() {
        catfish.getNavigation().moveTo(food.getX(), food.getY(0.5F), food.getZ(), 1.0F);
        final float eatDist = catfish.getBbWidth() * 0.65F + food.getBbWidth();
        if (catfish.distanceTo(food) < eatDist + 3 && catfish.hasLineOfSight(food)) {
            final Vec3 delta = this.getMouthVec().subtract(food.position()).normalize().scale(0.1F);
            food.setDeltaMovement(food.getDeltaMovement().add(delta));
            if (catfish.distanceTo(food) < eatDist) {
                if (food instanceof Player) {
                    food.hurt(catfish.damageSources().mobAttack(catfish), 12000);
                } else if (catfish.swallowEntity(food)) {
                    catfish.gameEvent(GameEvent.EAT);
                    catfish.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), catfish.getVoicePitch());
                    food.discard();
                }
            }
        }
    }
    protected float getSoundVolume() {
        return 1.0F;
    }
    private Vec3 getMouthVec() {
        Vec3 vec3 = (new Vec3(0.0, (double)(catfish.getBbHeight() * 0.25F), (double)(catfish.getBbWidth() * 0.8F))).xRot(catfish.getXRot() * 0.017453292F).yRot(-catfish.getYRot() * 0.017453292F);
        return catfish.position().add(vec3);
    }
    private boolean isFood(Entity entity) {
        RandomSource random = catfish.getRandom();
        if (AInteractionConfig.catfishcannibalize) {
            if (AInteractionConfig.catfisheatstupid) {
                if (catfish.getCatfishSize() == 2) {
                    return (entity instanceof Mob && entity.getBbHeight() <= 1.0F && !catfish.isBaby()) || (entity instanceof EntityCatfish && entity.getBbHeight() <= 0.7F && random.nextDouble() < 0.002);
                } else {
                    return entity instanceof ItemEntity && ((ItemEntity) entity).getAge() > 35;
                }
            } else if (catfish.getCatfishSize() == 2) {
                return (entity instanceof Mob && !bigCATFISHCANNOTEAT.test((LivingEntity) entity) && entity.getBbHeight() <= 1.0F && !catfish.isBaby()) || (entity instanceof EntityCatfish && entity.getBbHeight() <= 0.7F && random.nextDouble() < 0.002);
            } else {
                return entity instanceof ItemEntity && ((ItemEntity) entity).getAge() > 35;
            }
        } else if (AInteractionConfig.catfisheatstupid) {
            if (catfish.getCatfishSize() == 2) {
                return (entity instanceof Mob && entity.getBbHeight() <= 1.0F) && !catfish.isBaby();
            } else {
                return entity instanceof ItemEntity && ((ItemEntity) entity).getAge() > 35;
            }
        } else {
            if (catfish.getCatfishSize() == 2) {
                return (entity instanceof Mob && !bigCATFISHCANNOTEAT.test((LivingEntity) entity) && entity.getBbHeight() <= 1.0F) && !catfish.isBaby();
            } else {
                return entity instanceof ItemEntity && ((ItemEntity) entity).getAge() > 35;
            }
        }
    }
}