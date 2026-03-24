package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.CuriosCompat;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIBloodedAttraction;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFungusBonemeal;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Mixin(EntityCrimsonMosquito.class)
public abstract class AMICrimsonMosquitoMixin extends Monster {


    @Shadow public abstract void setSick(boolean shrink);

    protected AMICrimsonMosquitoMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;

        if (AlexsMobsInteraction.COMMON_CONFIG.BLOODED_ENABLED.get()){
            this.targetSelector.addGoal(2, new AMIBloodedAttraction(this, Player.class, 10, true, false, (mob) -> !mob.hasEffect(AMEffectRegistry.MOSQUITO_REPELLENT.get())));
            this.targetSelector.addGoal(2, new AMIBloodedAttraction(this, LivingEntity.class, 30, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS)));
        }
    }

    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void alexsMobsInteraction$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (AlexsMobsInteraction.COMMON_CONFIG.CRIMSON_TRANSFORM_ENABLED.get()) {
            if (itemStack.getItem() == AMItemRegistry.WARPED_MUSCLE.get() && this.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.GENERIC_EAT, 1, this.getVoicePitch());
                this.setSick(true);
                player.swing(hand,true);
                AMIUtils.awardAdvancement(player, "mutate_mosquito", "mutate");
            }
        }
    }
}
