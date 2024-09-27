package com.crimsoncrips.alexsmobsinteraction.effect;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class AMIBlooded extends MobEffect {

    public AMIBlooded() {
        super(MobEffectCategory.HARMFUL, 0Xff0000);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "44d3ee68-a7cb-4da4-b7dc-c17d1746f950", -0.35000000596046448, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "2a4b471b-402c-464e-b1e6-d1c2ca9e9095", -0.17000000596046448, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR, "403aaa63-5659-426d-aa33-7b98c171da91", -3, AttributeModifier.Operation.ADDITION);

    }

    public String getDescriptionId() {
        return "alexsmobsinteraction.potion.blooded";
    }

}