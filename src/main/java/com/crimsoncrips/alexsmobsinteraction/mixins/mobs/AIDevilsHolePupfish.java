package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityDevilsHolePupfish;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityDevilsHolePupfish.class)
public class AIDevilsHolePupfish extends Mob {

    protected AIDevilsHolePupfish(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BlobFishGoals(CallbackInfo ci){
       if(AInteractionConfig.preyfear) {
           this.goalSelector.addGoal(3, new AvoidEntityGoal<>((EntityDevilsHolePupfish) (Object) this, LivingEntity.class, 2.0F, 1.1, 1.3, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FISHFEAR)));
       }
    }
    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemstack = player.getItemInHand(hand);

        if (AInteractionConfig.mossfeed && (itemstack.getItem() == Items.MOSS_BLOCK)) {
            if (!player.isCreative()) itemstack.shrink(1);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.FOX_EAT, this.getSoundVolume(), this.getVoicePitch());
            this.
        }
    }
}
