package com.crimsoncrips.alexsmobsinteraction.mixins.misc.fishing;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
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


    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean alexsMobsInteraction$addFreshEntity(Level instance, Entity entity, Operation<Boolean> original, @Local Player player, @Local(ordinal = 0) int j, @Local(ordinal = 1) int k) {
        if (player.getOffhandItem().is(AMItemRegistry.MAGGOT.get()) && AlexsMobsInteraction.COMMON_CONFIG.MAGGOT_FISHING_ENABLED.get()){
            if (!player.isCreative()) {
                player.getOffhandItem().shrink(1);
            }
            AMIUtils.awardAdvancement(player,"maggot_fishing","fish");
            int luck = 30;

            return instance.addFreshEntity(new FishingHook(player, instance, j + luck, k));
        } else {
            return original.call(instance,entity);
        }
    }


}
