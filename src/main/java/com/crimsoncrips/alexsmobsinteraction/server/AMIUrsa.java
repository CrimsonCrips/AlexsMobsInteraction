package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;

public class AMIUrsa {

    private static final ResourceLocation HEALTH_BOOST = AlexsMobsInteraction.prefix("ursa_health");
    private static final ResourceLocation DAMAGE_BOOST = AlexsMobsInteraction.prefix("ursa_damage");
    private static final double HEALTH_MULTIPLIER = 2.0D;
    private static final double DAMAGE_MULTIPLIER = 0.75D;

    private static final int FLURRY_DURATION = AMIUtils.seconds(1);
    private static final int FLURRY_COOLDOWN = AMIUtils.seconds(10);
    private static final int FLURRY_HIT_INTERVAL = AMIUtils.seconds(0.2F);
    private static final int FLURRY_HITS = 4;

    private static final float ENRAGE_HEALTH = 0.3F;
    private static final int ENRAGE_DURATION = AMIUtils.seconds(15);
    private static final float ENRAGE_DAMAGE_TAKEN = 0.2F;

    private static final int SWIPE_DECAY_DELAY = AMIUtils.seconds(5);
    private static final int SWIPE_DECAY_INTERVAL = AMIUtils.seconds(1);

    public static boolean isUrsa(Entity entity) {
        return entity instanceof EntityGrizzlyBear grizzlyBear && grizzlyBear.getData(AMIAttachments.URSA);
    }

    public static boolean isEnraged(Entity entity) {
        return isUrsa(entity) && entity.getData(AMIAttachments.URSA_ENRAGE_TIME) > 0;
    }

    public static void tick(EntityGrizzlyBear grizzlyBear) {
        if (grizzlyBear.level().isClientSide)
            return;
        boolean ursa = grizzlyBear.getData(AMIAttachments.URSA);
        if (applyModifier(grizzlyBear.getAttribute(Attributes.MAX_HEALTH), HEALTH_BOOST, HEALTH_MULTIPLIER, ursa) && ursa) {
            grizzlyBear.setHealth(grizzlyBear.getMaxHealth());
        }
        applyModifier(grizzlyBear.getAttribute(Attributes.ATTACK_DAMAGE), DAMAGE_BOOST, DAMAGE_MULTIPLIER, ursa);
        if (!ursa)
            return;

        int enrageTime = grizzlyBear.getData(AMIAttachments.URSA_ENRAGE_TIME);
        if (enrageTime > 0) {
            grizzlyBear.setData(AMIAttachments.URSA_ENRAGE_TIME, enrageTime - 1);
        }

        tickFlurry(grizzlyBear);
    }

    private static void tickFlurry(EntityGrizzlyBear grizzlyBear) {
        LivingEntity target = grizzlyBear.getTarget();
        int flurryTime = grizzlyBear.getData(AMIAttachments.URSA_FLURRY_TIME);
        if (flurryTime > 0) {
            grizzlyBear.setData(AMIAttachments.URSA_FLURRY_TIME, flurryTime - 1);
            int animationTick = grizzlyBear.getAnimationTick();
            if (grizzlyBear.getAnimation() == EntityGrizzlyBear.ANIMATION_MAUL && target != null && inReach(grizzlyBear, target)
                    && animationTick > 0 && animationTick % FLURRY_HIT_INTERVAL == 0 && animationTick <= FLURRY_HIT_INTERVAL * FLURRY_HITS) {
                grizzlyBear.doHurtTarget(target);
            }
            return;
        }

        int cooldown = grizzlyBear.getData(AMIAttachments.URSA_FLURRY_COOLDOWN);
        if (cooldown > 0) {
            grizzlyBear.setData(AMIAttachments.URSA_FLURRY_COOLDOWN, cooldown - 1);
        } else if (target != null && target.isAlive() && grizzlyBear.getControllingPassenger() == null && inReach(grizzlyBear, target)
                && grizzlyBear.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            grizzlyBear.setAnimation(EntityGrizzlyBear.ANIMATION_MAUL);
            grizzlyBear.setData(AMIAttachments.URSA_FLURRY_TIME, FLURRY_DURATION);
            grizzlyBear.setData(AMIAttachments.URSA_FLURRY_COOLDOWN, FLURRY_COOLDOWN);
            grizzlyBear.playSound(SoundEvents.POLAR_BEAR_WARNING, 2.0F, 0.7F);
        }
    }

    private static boolean inReach(EntityGrizzlyBear grizzlyBear, LivingEntity target) {
        return grizzlyBear.distanceTo(target) < target.getBbWidth() + grizzlyBear.getBbWidth() + 2.5F;
    }

    private static boolean applyModifier(AttributeInstance attribute, ResourceLocation id, double amount, boolean present) {
        if (attribute == null)
            return false;
        if (present && !attribute.hasModifier(id)) {
            attribute.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            return true;
        }
        if (!present && attribute.hasModifier(id)) {
            attribute.removeModifier(id);
        }
        return false;
    }

    public static void onUrsaDamaged(EntityGrizzlyBear grizzlyBear) {
        if (!grizzlyBear.isAlive() || grizzlyBear.getData(AMIAttachments.URSA_ENRAGED) || grizzlyBear.getHealth() > grizzlyBear.getMaxHealth() * ENRAGE_HEALTH)
            return;
        grizzlyBear.setData(AMIAttachments.URSA_ENRAGED, true);
        grizzlyBear.setData(AMIAttachments.URSA_ENRAGE_TIME, ENRAGE_DURATION);
        for (MobEffectInstance effect : new ArrayList<>(grizzlyBear.getActiveEffects())) {
            if (isHarmful(effect)) {
                grizzlyBear.removeEffect(effect.getEffect());
            }
        }
        grizzlyBear.playSound(SoundEvents.RAVAGER_ROAR, 3.0F, 0.6F);
        if (grizzlyBear.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, grizzlyBear.getX(), grizzlyBear.getY(1.0D), grizzlyBear.getZ(), 12, 1.0D, 0.5D, 1.0D, 0);
        }
    }

    public static float enragedDamage(float amount) {
        return amount * ENRAGE_DAMAGE_TAKEN;
    }

    public static boolean isHarmful(MobEffectInstance effect) {
        return effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL;
    }

    public static void addSwipe(LivingEntity target) {
        target.setData(AMIAttachments.SWIPES, target.getData(AMIAttachments.SWIPES) + 1);
        target.setData(AMIAttachments.SWIPE_DELAY, SWIPE_DECAY_DELAY);
    }

    public static void tickSwipes(LivingEntity living) {
        if (living.level().isClientSide || !living.hasData(AMIAttachments.SWIPES))
            return;
        int swipes = living.getData(AMIAttachments.SWIPES);
        if (swipes <= 0)
            return;
        int delay = living.getData(AMIAttachments.SWIPE_DELAY);
        if (delay > 0) {
            living.setData(AMIAttachments.SWIPE_DELAY, delay - 1);
        } else {
            living.setData(AMIAttachments.SWIPES, swipes - 1);
            living.setData(AMIAttachments.SWIPE_DELAY, SWIPE_DECAY_INTERVAL);
        }
    }
}
