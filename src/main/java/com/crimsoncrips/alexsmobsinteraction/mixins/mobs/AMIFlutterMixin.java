package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.misc.ManualLootUtil;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityFlutter.class)
public abstract class AMIFlutterMixin extends TamableAnimal {


    protected AMIFlutterMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void alexsMobsInteraction$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemStack = player.getItemInHand(hand);
        EntityFlutter flutter = (EntityFlutter)(Object)this;

        if (itemStack.getItem() == Items.WITHER_ROSE && AlexsMobsInteraction.COMMON_CONFIG.FLUTTER_WITHERED_ENABLED.get() && !flutter.isTame()) {
            if (!player.isCreative())
                itemStack.hurtAndBreak(1, flutter, (p_233654_0_) -> {});
            player.swing(hand,true);
            flutter.addEffect(new MobEffectInstance(MobEffects.WITHER, 900, 0));
        }

        if (itemStack.getItem() == Items.SHEARS && AlexsMobsInteraction.COMMON_CONFIG.FLUTTER_SHEAR_ENABLED.get() && !flutter.isTame() && flutter.level() instanceof ServerLevel serverLevel) {
            ManualLootUtil.spawnLoot(AMILootTables.FLUTTER_SHEAR,flutter,player,1);
            player.swing(hand,true);
            if (!player.isCreative()) itemStack.hurtAndBreak(3, flutter, (p_233654_0_) -> {});
            flutter.discard();
        }
    }

}
