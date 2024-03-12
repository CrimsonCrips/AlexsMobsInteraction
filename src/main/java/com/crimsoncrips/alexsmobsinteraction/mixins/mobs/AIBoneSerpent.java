package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

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


@Mixin(EntityBoneSerpent.class)
public class AIBoneSerpent extends Mob {

    protected AIBoneSerpent(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BoneSerpentGoals(CallbackInfo ci){
        targetSelector.addGoal(3, new EntityAINearestTarget3D<>(this, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.BONESERPENT_KILL)));
        if(AInteractionConfig.boneserpentfear) {
            goalSelector.addGoal(7, new AvoidEntityGoal((EntityBoneSerpent) (Object) this, EntityLaviathan.class, 10.0F, 1.6, 2));
        }
    }
}
