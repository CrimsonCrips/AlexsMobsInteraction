package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(EntityMurmurHead.class)
public abstract class AMIMurmur extends Mob {

    protected AMIMurmur(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        EntityMurmurHead murmurHead = (EntityMurmurHead)(Object)this;
        if (AMInteractionConfig.murmurbodykill) {
            boolean prev = super.hurt(source, damage);
            return prev;
        } else {
            Entity body = murmurHead.getBody();
            if (isInvulnerableTo(source)) {
                return false;
            }
            if (body != null && body.hurt(source, 0.5F * damage)) {
                return true;
            }
            return super.hurt(source, damage);
        }

    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_WALL);
    }

}
