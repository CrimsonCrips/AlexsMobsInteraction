package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.goal.AMICosmawOwner;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityHammerheadShark.class)
public class AMIHammerheadShark extends Mob {

    protected AMIHammerheadShark(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    private boolean stun;

    @Override
    protected boolean isImmobile() {
        return stun;
    }

    @Override
    public void tick() {
        super.tick();
        if (AMInteractionConfig.CHARGE_STUN_ENABLED) {
            stun = this.hasEffect(AMIEffects.DISABLED.get());

            LivingEntity target = getTarget();

            if (this.getTarget() instanceof Player player && (player.getItemBySlot(EquipmentSlot.OFFHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0 || player.getItemBySlot(EquipmentSlot.MAINHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0)) {
                if (this.distanceTo(this.getTarget()) < 2F && this.hasLineOfSight(this.getTarget()) && this.getTarget().isBlocking() && !stun) {
                    stun = true;
                    this.playSound(SoundEvents.SHIELD_BLOCK, 2F, 1F);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 150, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
                    this.addEffect(new MobEffectInstance(AMIEffects.DISABLED.get(), 500, 1));
                }
            }

        }

    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityHammerheadShark hammerheadShark = (EntityHammerheadShark)(Object)this;
        if (AMInteractionConfig.HAMMERHEAD_MANTIS_EAT_ENABLED){
            hammerheadShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(hammerheadShark, EntityMantisShrimp.class, 50, true, false, (mob) -> {
                return mob.getHealth() <= 0.2 * mob.getMaxHealth();
            }));
        }
        hammerheadShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(hammerheadShark, LivingEntity.class, 0, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.HAMMERHEAD_KILL)));

    }



}
