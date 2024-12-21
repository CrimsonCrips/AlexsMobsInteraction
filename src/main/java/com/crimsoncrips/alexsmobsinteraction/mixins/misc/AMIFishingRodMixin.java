package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(FishingRodItem.class)
public abstract class AMIFishingRodMixin  {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void adjustJ(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack itemstack, int k, int j) {
        if(AMInteractionConfig.MAGGOT_FISHING_ENABLED && pPlayer.getOffhandItem().is(AMItemRegistry.MAGGOT.get())){
            if (!pPlayer.isCreative()) {
                pPlayer.getOffhandItem().shrink(1);
            }
            j = j + 30;
        }
        pLevel.addFreshEntity(new FishingHook(pPlayer, pLevel, j, k));
        cir.cancel();
    }


}