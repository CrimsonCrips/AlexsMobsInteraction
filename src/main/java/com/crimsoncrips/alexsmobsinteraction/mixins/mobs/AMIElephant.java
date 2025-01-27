package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;


@Mixin(EntityElephant.class)
public abstract class AMIElephant extends Mob {


    @Shadow private boolean charging;

    @Shadow public abstract boolean isTusked();

    protected AMIElephant(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }


    private boolean stun = false;

    protected boolean isImmobile() {
        return stun;
    }


    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (AMInteractionConfig.CHARGE_STUN_ENABLED) {
            stun = this.hasEffect(AMIEffects.DISABLED.get());


            LivingEntity target = getTarget();

            if (this.getTarget() instanceof Player player && this.charging && this.isTusked() && (player.getItemBySlot(EquipmentSlot.OFFHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0 || player.getItemBySlot(EquipmentSlot.MAINHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0)) {
                if (this.distanceTo(this.getTarget()) < 3F && this.hasLineOfSight(this.getTarget()) && this.getTarget().isBlocking() && !stun) {
                    stun = true;
                    this.playSound(SoundEvents.SHIELD_BLOCK, 2F, 1F);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 350, 2));
                    this.addEffect(new MobEffectInstance(AMIEffects.DISABLED.get(), 500, 1));
                }
            }

        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityElephant elephant = (EntityElephant)(Object)this;
        if (AMInteractionConfig.ELEPHANT_TERRITORIAL_ENABLED){
            elephant.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(elephant, Player.class, 1000, true, true, (entity1 -> {
                return entity1.isHolding(Ingredient.of(AMItemRegistry.ACACIA_BLOSSOM.get()));
            })) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && elephant.isTusked() && !elephant.isTame();
                }
            });
        }

    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void getItem(ItemEntity e, CallbackInfo ci) {
        if (e.getItem().isEdible() && AMInteractionConfig.FOOD_TARGET_EFFECTS_ENABLED) {
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
