package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityMurmurHead.class)
public abstract class AMIMurmur extends Mob {

    protected AMIMurmur(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }



    @ModifyReturnValue(method = "hurt", at = @At("RETURN"))
    private boolean alexsMobsInteraction$hurt(boolean original,@Local DamageSource source, @Local float damage) {
        if (AlexsMobsInteraction.COMMON_CONFIG.MURMUR_REGROW_ENABLED.get()) {
            return super.hurt(source,damage);
        }
        return original;
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (AlexsMobsInteraction.COMMON_CONFIG.MURMUR_REGROW_ENABLED.get()){
            AMIUtils.awardAdvancement(pDamageSource.getEntity(),"reheaded","reheaded");
        }
        super.die(pDamageSource);
    }
}
