package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityFlutter.class)
public class AIFlutter extends Mob {

    protected AIFlutter(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }
    EntityFlutter flutter = (EntityFlutter)(Object)this;
    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.WITHER_ROSE && AInteractionConfig.flutterwither && !flutter.isTame()) {
            stack.shrink(1);
            flutter.addEffect(new MobEffectInstance(MobEffects.WITHER, 900, 0));
        }
        if (stack.getItem() == Items.SHEARS && AInteractionConfig.fluttersheared) {
            this.playSound(SoundEvents.SHEEP_SHEAR, this.getSoundVolume(), this.getVoicePitch());
            this.spawnAtLocation(Items.SPORE_BLOSSOM);
            this.spawnAtLocation(Items.AZALEA);
            stack.hurtAndBreak(6, this, (p_233654_0_) -> {
            });
            this.discard();
        }
    }
}
