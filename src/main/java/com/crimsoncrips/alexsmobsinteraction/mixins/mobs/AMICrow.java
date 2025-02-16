package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;


@Mixin(EntityCrow.class)
public abstract class AMICrow extends Mob {

    protected AMICrow(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public boolean hurt(DamageSource source, float amount) {
        EntityCrow crow = (EntityCrow) (Object)this;
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            crow.setOrderedToSit(false);
            if (entity != null && crow.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
                amount = (amount + 1.0F) / 4.0F;
            }

            if (this.isPassenger()) {
                this.stopRiding();
            }

            boolean prev = super.hurt(source, amount);
            if (prev && AlexsMobsInteraction.COMMON_CONFIG.CROW_WARRIORS_ENABLED.get()){
                if(!this.isHolding(Ingredient.of(AMInteractionTagRegistry.CROW_WEAPON))){
                    this.spawnAtLocation(this.getMainHandItem().copy());
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
            } else if (prev && !this.getMainHandItem().isEmpty()) {
                this.spawnAtLocation(this.getMainHandItem().copy());
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }


            return prev;
        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityCrow crow = (EntityCrow)(Object)this;
        crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CROW_KILL)));
        if (AlexsMobsInteraction.COMMON_CONFIG.CROW_CANNIBALIZE_ENABLED.get()){
            crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, EntityCrow.class, 500, true, true, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.10F * livingEntity.getMaxHealth();
            }){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !crow.isTame() && !crow.isBaby();
                }
            });
        }
    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void getItem(ItemEntity e, CallbackInfo ci) {
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
}
