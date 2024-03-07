package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityOrca;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityOrca.class)
public class AIOrca extends Mob {

    protected AIOrca(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BlobFishGoals(CallbackInfo ci) {
        if(AInteractionConfig.orcahunt){
            this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ORCA_KILL)));
            this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ORCA_CHANCE_KILL)));
        }
    }

}
