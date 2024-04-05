package com.crimsoncrips.alexsmobsinteraction.goal;

import com.crimsoncrips.alexsmobsinteraction.enchantment.AIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AICosmawInterface;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AILavithanInterface;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class AICosmawOwner extends Goal {

    private LivingEntity owner;

    EntityCosmaw cosmaw;

    public AICosmawOwner(EntityCosmaw cosmaw) {
        this.cosmaw = cosmaw;
    }


    public boolean canUse() {
        AICosmawInterface myAccessor = (AICosmawInterface) cosmaw;
        int getweaktimer = myAccessor.getweakTimer();
        if (cosmaw.isTame() && cosmaw.getOwner() != null && !cosmaw.isSitting() && !cosmaw.getOwner().isPassenger() && !cosmaw.getOwner().onGround() && cosmaw.getOwner().fallDistance > 4.0F && getweaktimer <= 0) {
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
        }

    }

}