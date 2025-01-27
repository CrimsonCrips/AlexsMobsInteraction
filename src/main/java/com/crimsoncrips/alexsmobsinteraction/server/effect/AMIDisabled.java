package com.crimsoncrips.alexsmobsinteraction.server.effect;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityGust;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class AMIDisabled extends MobEffect {

    public AMIDisabled() {
        super(MobEffectCategory.HARMFUL, 0Xfaedc8);
    }


    public String getDescriptionId() {
        if (AMInteractionConfig.CHARGE_STUN_ENABLED) {
            return "alexsmobsinteraction.potion.disabled";
        } else {
            return "alexscavesexemplified.feature_disabled";
        }
    }

}