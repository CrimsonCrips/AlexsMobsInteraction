package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedMosco;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityWarpedMosco.class)
public abstract class AMIWarpedMosco extends Monster {


    protected AMIWarpedMosco(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityWarpedMosco warpedMosco = (EntityWarpedMosco)(Object)this;
        if (!AMInteractionConfig.MOSCO_CANNIBALISM_ENABLED)
            return;
        warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityCrimsonMosquito.class, 1000, true, true, null));
        warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityWarpedMosco.class, 100, true, true, (livingEntity) -> {
            return livingEntity.getHealth() <= 0.05F * livingEntity.getMaxHealth();
        }));
    }

}
