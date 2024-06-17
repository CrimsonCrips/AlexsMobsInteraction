package com.crimsoncrips.alexsmobsinteraction.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AMISkreechingPotion extends MobEffect {

    public AMISkreechingPotion() {
        super(MobEffectCategory.HARMFUL, 0X142d40);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobsinteraction.potion.skreeching";
    }

}