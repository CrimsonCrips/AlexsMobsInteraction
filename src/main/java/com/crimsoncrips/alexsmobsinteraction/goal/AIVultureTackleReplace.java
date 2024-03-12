package com.crimsoncrips.alexsmobsinteraction.goal;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntitySoulVulture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class AIVultureTackleReplace extends Goal {
    private final EntitySoulVulture vulture;


    public AIVultureTackleReplace(EntitySoulVulture vulture) {
        this.vulture = vulture;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }


    public boolean canUse() {
        if (this.vulture.getTarget() != null && this.vulture.shouldSwoop()) {
            this.vulture.setFlying(true);
            return true;
        } else {
            return false;
        }
    }


    public void stop() {
        this.vulture.setTackling(false);
    }

    public void tick() {




        if (this.vulture.isFlying()) {
            this.vulture.setTackling(true);
        } else {
            this.vulture.setTackling(false);
        }

        if (this.vulture.getTarget() != null) {

            RandomSource random = vulture.getRandom();
            this.vulture.getMoveControl().setWantedPosition(this.vulture.getTarget().getX(), this.vulture.getTarget().getY() + (double)this.vulture.getTarget().getEyeHeight(), this.vulture.getTarget().getZ(), 2.0);
            double d0 = this.vulture.getX() - this.vulture.getTarget().getX();
            double d2 = this.vulture.getZ() - this.vulture.getTarget().getZ();
            float f = (float)(Mth.atan2(d2, d0) * 57.2957763671875) - 90.0F;
            this.vulture.setYRot(f);
            this.vulture.yBodyRot = this.vulture.getYRot();
            if (this.vulture.getBoundingBox().inflate(0.30000001192092896, 0.30000001192092896, 0.30000001192092896).intersects(this.vulture.getTarget().getBoundingBox()) && (int) ReflectionUtil.getField(vulture, "tackleCooldown") == 0) {
                ReflectionUtil.setField(vulture, "tackleCooldown", 100 + random.nextInt(200));
                float dmg = (float)this.vulture.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
                if (AInteractionConfig.soulbuff) {
                    if (this.vulture.getTarget().hurt(this.vulture.damageSources().mobAttack(this.vulture), dmg) && this.vulture.getSoulLevel() < 5) {
                        this.vulture.setSoulLevel(this.vulture.getSoulLevel() + 1);
                        this.vulture.heal(dmg);
                        this.vulture.level().broadcastEntityEvent(this.vulture, (byte) 68);
                    }
                } else {
                    if (this.vulture.getTarget().hurt(this.vulture.damageSources().mobAttack(this.vulture), dmg) && this.vulture.getHealth() < this.vulture.getMaxHealth() - dmg && this.vulture.getSoulLevel() < 5) {
                        this.vulture.setSoulLevel(this.vulture.getSoulLevel() + 1);
                        this.vulture.heal(dmg);
                        this.vulture.level().broadcastEntityEvent(this.vulture, (byte) 68);
                    }
                }

                this.stop();
            }
        }

    }
}