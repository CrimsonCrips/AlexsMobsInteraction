package com.crimsoncrips.alexsmobsinteraction.server.effect;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AMIFarseerIcon extends MobEffect {

    public AMIFarseerIcon() {
        super(MobEffectCategory.NEUTRAL, 0Xfae6af);
    }

    public String getDescriptionId() {
        if (AlexsMobsInteraction.COMMON_CONFIG.FARSEER_ALTERING_ENABLED.get()) {
            return "effect.alexsmobsinteraction.farseer_icon.title";
        } else {
            return "misc.alexsmobsinteraction.feature_disabled";
        }
    }



}