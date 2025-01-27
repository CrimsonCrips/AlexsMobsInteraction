package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.cachalot;

import com.crimsoncrips.alexsmobsinteraction.server.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityCachalotWhale.class)
public abstract class AMICachalotWhale extends Animal {




    boolean stun = false;

    protected AMICachalotWhale(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected boolean isImmobile() {
        return stun;
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (AMInteractionConfig.CHARGE_STUN_ENABLED) {
            stun = this.hasEffect(AMIEffects.DISABLED.get());

            LivingEntity target = getTarget();

            if (this.getTarget() instanceof Player player && (player.getItemBySlot(EquipmentSlot.OFFHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0 || player.getItemBySlot(EquipmentSlot.MAINHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0)) {
                if (this.distanceTo(this.getTarget()) < 6F && this.hasLineOfSight(this.getTarget()) && this.getTarget().isBlocking() && !stun) {
                    stun = true;
                    this.playSound(SoundEvents.SHIELD_BLOCK, 2F, 1F);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 2));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 550, 4));
                    this.addEffect(new MobEffectInstance(AMIEffects.DISABLED.get(), 500, 1));
                }
            }

        }
    }
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityCachalotWhale cachalotWhale = (EntityCachalotWhale)(Object)this;

        cachalotWhale.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(cachalotWhale, LivingEntity.class, 300, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CACHALOT_WHALE_KILL_CHANCE)));

    }

}
