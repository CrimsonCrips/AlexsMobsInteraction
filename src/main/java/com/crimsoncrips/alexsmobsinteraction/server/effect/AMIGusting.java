package com.crimsoncrips.alexsmobsinteraction.server.effect;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AMIGusting extends MobEffect {

    private int timer;

    public AMIGusting() {
        super(MobEffectCategory.BENEFICIAL, 0Xfae6af);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "9015e1e9-ce4f-4bfa-ad17-b64d01056aa6", 0.35000000596046448, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public void removeAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
        super.removeAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    public void addAttributeModifiers(LivingEntity entityLivingBaseIn, AttributeMap attributeMapIn, int amplifier) {
        super.addAttributeModifiers(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (!AlexsMobsInteraction.COMMON_CONFIG.GUSTING_EFFECT_ENABLED.get())
            return;
        if (entity.getRandom().nextDouble() < 0.5){
            for (int j = 0; j < 4; ++j) {
                float f1 = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * entity.getBbWidth() * 0.95F;
                float f2 = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * entity.getBbWidth() * 0.95F;
                level.addParticle(AMParticleRegistry.GUSTER_SAND_SPIN.get(), entity.getX() + (double) f1, entity.getY(), entity.getZ() + (double) f2, entity.getX(), entity.getY() + entity.getRandom().nextFloat() * entity.getBbHeight() + 0.2F, entity.getZ());
            }
        }
        if (timer < 0){
            timer = 40;
        }
        timer--;
        if (level.isClientSide)
            return;
        if(timer == 39 && entity instanceof Player){
            int z = 1;
            for (int x = -2; x != 2; x ++) {
                if (x > -2) {
                    summonGust(entity, x, z);
                    summonGust(entity, x, -z);
                    if (z <= 1) {
                        z++;
                    } else {
                        z--;
                    }
                }
            }
            if (entity instanceof Player) {
                summonGust(entity, 2, 0);
                summonGust(entity, -2, 0);
            }
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        if (AlexsMobsInteraction.COMMON_CONFIG.GUSTING_EFFECT_ENABLED.get()) {
            return "effect.alexsmobsinteraction.gusting.title";
        } else {
            return "misc.alexsmobsinteraction.feature_disabled";
        }
    }

    private void summonGust(LivingEntity entity,double x, double z){
        Entity entityToSpawn = (AMEntityRegistry.GUST.get()).spawn((ServerLevel) entity.level(), BlockPos.containing(entity.getX() + x, entity.getY() + 0.2, entity.getZ() + z), MobSpawnType.MOB_SUMMONED);
        if (entityToSpawn != null) {
            entityToSpawn.tickCount = 250;
        }
    }

}