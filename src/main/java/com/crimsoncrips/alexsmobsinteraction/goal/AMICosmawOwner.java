package com.crimsoncrips.alexsmobsinteraction.goal;


import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class AMICosmawOwner extends Goal {

    private LivingEntity owner;

    EntityCosmaw cosmaw;

    public AMICosmawOwner(EntityCosmaw cosmaw) {
        this.cosmaw = cosmaw;
    }


    public boolean canUse() {
        if (cosmaw.isTame() && cosmaw.getOwner() != null && !cosmaw.isSitting() && !cosmaw.getOwner().isPassenger() && !cosmaw.getOwner().onGround() && cosmaw.getOwner().fallDistance > 4.0F && !cosmaw.hasEffect(MobEffects.WEAKNESS)) {
            this.owner = cosmaw.getOwner();
            return true;
        } else {
            return false;
        }
    }

    public void tick() {
        if (this.owner != null && (!this.owner.isFallFlying() || this.owner.getY() < -30.0)) {
            double dist = (double)cosmaw.distanceTo(this.owner);
            if (!(dist < 3.0) && !(this.owner.getY() <= -50.0)) {
                if (!(dist > 100.0) && !(this.owner.getY() <= -20.0)) {
                    cosmaw.getNavigation().moveTo(this.owner, 1.0 + Math.min(dist * 0.30000001192092896, 3.0));
                } else {
                    cosmaw.teleportTo(this.owner.getX(), this.owner.getY() - 1.0, this.owner.getZ());
                }
            } else {
                this.owner.fallDistance = 0.0F;
                this.owner.startRiding(cosmaw);
            }
            if (cosmaw.hasPassenger(owner) && owner.getArmorValue() > 5 && !(owner.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(AMIEnchantmentRegistry.LIGHTWEIGHT.get()) > 0)){
                cosmaw.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,owner.getArmorValue() * 100, 0));
            }
        }

    }

}