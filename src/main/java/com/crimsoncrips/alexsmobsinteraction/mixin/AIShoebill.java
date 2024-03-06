package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityShoebill.class)
public class AIShoebill extends Mob {

    protected AIShoebill(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void ShoebillGoals(CallbackInfo ci){
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SHOEBILL_BABY_KILL)));
        this.targetSelector.addGoal(7, new EntityAINearestTarget3D(this, EntityTerrapin.class, 100, false, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SHOEBILL_KILL)));
    }
}
