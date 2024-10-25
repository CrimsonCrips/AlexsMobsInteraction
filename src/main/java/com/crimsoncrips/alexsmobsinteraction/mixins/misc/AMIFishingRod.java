package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(FishingRodItem.class)
public abstract class AMIFishingRod extends Item implements Vanishable {


    public AMIFishingRod(Properties pProperties) {
        super(pProperties);
    }

    private Player player;

    @Inject(method = "use", at = @At("TAIL"))
    private void useMethod(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        player = pPlayer;
    }

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;<init>(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;II)V"), index = 2)
    private int adjustJ( int j) {
        if(AMInteractionConfig.MAGGOT_FISHING && player.getOffhandItem().is(AMItemRegistry.MAGGOT.get())){
            if (!player.isCreative()) {
                player.getOffhandItem().shrink(1);
            }
            return j + 30;
        } else return j;
    }


}
