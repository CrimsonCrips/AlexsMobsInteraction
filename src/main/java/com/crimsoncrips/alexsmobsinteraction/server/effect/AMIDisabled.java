package com.crimsoncrips.alexsmobsinteraction.server.effect;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class AMIDisabled extends MobEffect {

    public AMIDisabled() {
        super(MobEffectCategory.HARMFUL, 0Xfaedc8);
    }


    public String getDescriptionId() {
        if (AlexsMobsInteraction.COMMON_CONFIG.CHARGE_STUN_ENABLED.get()) {
            return "alexsmobsinteraction.potion.disabled";
        } else {
            return "alexscavesexemplified.feature_disabled";
        }
    }

}