package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.goal.AMISeagullSteal;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntitySeagull.class)
public class AMISeagull extends Mob {

    protected AMISeagull(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AMInteractionConfig.SEAGULL_BUFFED_ENABLED){
            if (this.getMainHandItem().is(Items.ENCHANTED_GOLDEN_APPLE)) {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1000, 0));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3000, 0));
            }
            if (this.getMainHandItem().is(Items.GOLDEN_APPLE)) {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1));
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1000, 2));
            }
        }
    }


}
