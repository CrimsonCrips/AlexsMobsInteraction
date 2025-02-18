package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.misc.ManualLootUtil;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIGrizzlyScavenge;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;




@Mixin(EntityGrizzlyBear.class)
public abstract class AMIGrizzlyBear extends Animal {

    protected AMIGrizzlyBear(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityGrizzlyBear grizzlyBear = (EntityGrizzlyBear) (Object) this;
        if (AlexsMobsInteraction.COMMON_CONFIG.GRIZZLY_PACIFIED_ENABLED.get()) {
            grizzlyBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(grizzlyBear, Player.class, 10, true, true, null) {
                public boolean canUse() {
                    return super.canUse() && !grizzlyBear.isTame();
                }
            });
        }
        if (AlexsMobsInteraction.COMMON_CONFIG.STORED_HUNGER_ENABLED.get()) {
            grizzlyBear.goalSelector.addGoal(6, new AMIGrizzlyScavenge(grizzlyBear,  1.2, 12));
        }
    }


    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 18))
    private boolean alexsMobsInteraction$registerGoals2(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.GRIZZLY_PACIFIED_ENABLED.get();
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 19))
    private boolean alexsMobsInteraction$registerGoals3(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.GRIZZLY_PACIFIED_ENABLED.get();
    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$onGetItem(ItemEntity e, CallbackInfo ci) {
        if (e.getItem().isEdible() && AlexsMobsInteraction.COMMON_CONFIG.FOOD_TARGET_EFFECTS_ENABLED.get()) {
            this.heal(5);
            List<Pair<MobEffectInstance, Float>> test = Objects.requireNonNull(e.getItem().getFoodProperties(this)).getEffects();
            if (!test.isEmpty()){
                for (int i = 0; i < test.size(); i++){
                    this.addEffect(new MobEffectInstance(test.get(i).getFirst()));
                }
            }
        }
    }

    @Inject(method = "mobInteract", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (AlexsMobsInteraction.COMMON_CONFIG.BRUSHED_ENABLED.get() && itemStack.getItem() instanceof BrushItem && !this.level().isClientSide) {
            if (!player.isCreative()) {
                itemStack.hurtAndBreak(16, player, (p_233654_0_) -> {
                });
            }
            ManualLootUtil.spawnLoot(AMILootTables.GRIZZLY_BRUSH,this,player,0);
        }
    }

}
