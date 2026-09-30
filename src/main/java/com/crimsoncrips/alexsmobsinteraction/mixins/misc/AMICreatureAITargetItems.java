package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.ITargetsDroppedItems;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreatureAITargetItems.class)
public abstract class AMICreatureAITargetItems extends TargetGoal {

    protected AMICreatureAITargetItems(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/ITargetsDroppedItems;onGetItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
    private void alexsMobsInteraction$tick(ITargetsDroppedItems instance, ItemEntity itemEntity, Operation<Void> original) {
        original.call(instance, itemEntity);
        if (!AlexsMobsInteraction.COMMON_CONFIG.FOOD_FX_ENABLED.get())
            return;
        FoodProperties food = itemEntity.getItem().getFoodProperties(this.mob);
        if (food == null)
            return;
        this.mob.heal(5);
        for (FoodProperties.PossibleEffect effect : food.effects()) {
            this.mob.addEffect(new MobEffectInstance(effect.effect()));
        }
    }
}
