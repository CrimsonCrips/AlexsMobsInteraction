package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityBlueJay.class)
public class AIBlueJay extends Mob {

    protected AIBlueJay(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BlueJayGoals(CallbackInfo ci){
        if(AInteractionConfig.bluejayhunt){
            this.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(this, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.BLUEJAYKILL)) {
                public boolean canUse() {
                    return super.canUse();
                }
            });
        }
    }
}
