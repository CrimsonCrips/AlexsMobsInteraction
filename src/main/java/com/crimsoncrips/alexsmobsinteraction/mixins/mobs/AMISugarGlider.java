package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntitySugarGlider.class)
public abstract class AMISugarGlider extends TamableAnimal {


    protected AMISugarGlider(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void registerGoals(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (AlexsMobsInteraction.COMMON_CONFIG.SUGAR_RUSH_ENABLED.get()) {
            if (itemStack.getItem() == Items.SUGAR || itemStack.getItem() == Items.SUGAR_CANE) {
                if (!player.isCreative()) itemStack.shrink(1);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.FOX_EAT, 1, this.getVoicePitch());
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 1));
                player.swing(hand,true);
            }
        }
    }

}
