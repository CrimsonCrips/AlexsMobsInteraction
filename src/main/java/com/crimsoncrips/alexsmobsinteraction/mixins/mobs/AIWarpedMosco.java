package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityWarpedMosco.class)
public class AIWarpedMosco extends Mob {


    protected AIWarpedMosco(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void WarpedMoscoGoals(CallbackInfo ci){
        Predicate<LivingEntity> weakenough = (livingEntity) -> {
            return livingEntity.getHealth() <= 0.05F * livingEntity.getMaxHealth();
        };
        if (AInteractionConfig.warpedcannibalism){
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityCrimsonMosquito.class, 1000, true, true, null));
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityWarpedMosco.class, 100, true, true, weakenough));
        }

    }
}
