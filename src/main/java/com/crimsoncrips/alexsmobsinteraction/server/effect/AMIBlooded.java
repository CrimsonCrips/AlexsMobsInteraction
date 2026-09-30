package com.crimsoncrips.alexsmobsinteraction.server.effect;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AMIBlooded extends MobEffect {

    public AMIBlooded() {
        super(MobEffectCategory.HARMFUL, 0Xff0000);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, AlexsMobsInteraction.prefix("effect.blooded.movement_speed"), -0.35000000596046448, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, AlexsMobsInteraction.prefix("effect.blooded.attack_damage"), -0.17000000596046448, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR, AlexsMobsInteraction.prefix("effect.blooded.armor"), -3, AttributeModifier.Operation.ADD_VALUE);

    }

    public String getDescriptionId() {
        if (AlexsMobsInteraction.COMMON_CONFIG.HEMOGENICISM_ENABLED.get()) {
            return "effect.alexsmobsinteraction.blooded.title";
        } else {
            return "misc.alexsmobsinteraction.feature_disabled";
        }
    }

    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (AlexsMobsInteraction.COMMON_CONFIG.HEMOGENICISM_ENABLED.get()){
            AMIUtils.awardAdvancement(entity, "blooded", "blood");
        }
        return true;
    }

    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

}