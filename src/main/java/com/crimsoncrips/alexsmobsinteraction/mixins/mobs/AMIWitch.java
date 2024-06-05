package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Witch.class)
public abstract class AMIWitch extends Mob {

    protected AMIWitch(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "STORE"), ordinal = 0)
    private Potion modifyPotion(Potion original) {
        if (this.hasEffect(MobEffects.POISON) && !this.hasEffect(AMEffectRegistry.POISON_RESISTANCE.get()) && AMInteractionConfig.WITCH_ADDITIONS_ENABLED)
            return AMEffectRegistry.POISON_RESISTANCE_POTION.get();
        return original;
    }
}