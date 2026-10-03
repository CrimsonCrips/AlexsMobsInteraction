package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(targets = "com.github.alexthe666.alexsmobs.entity.EntityCosmaw$AIPickupOwner")
public abstract class AMICosmaw extends Goal {


    @Shadow
    private LivingEntity owner;

    @Shadow
    @Final
    private EntityCosmaw this$0;

    @ModifyReturnValue(method = "canUse", at = @At("RETURN"))
    private boolean alexsMobsInteraction$canUse(boolean original){
        return original && !this$0.hasEffect(MobEffects.WEAKNESS);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {

        if (owner != null && (!owner.isFallFlying() || owner.getY() < -30.0)) {
            if (this$0.hasPassenger(owner) && owner.getArmorValue() > 8){
                if(!(AMIEnchantmentRegistry.getLevel(owner.level(), owner.getItemBySlot(EquipmentSlot.CHEST), AMIEnchantmentRegistry.LIGHTWEIGHT) > 0)){
                    AMIUtils.awardAdvancement(owner, "heavy_carriage", "heavy");
                    this$0.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, owner.getArmorValue() * AMIUtils.seconds(5), 0));
                } else {
                    AMIUtils.awardAdvancement(owner,"lightweight","lightweight");
                }
            }
        }

    }

}
