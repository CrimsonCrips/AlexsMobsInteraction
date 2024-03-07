package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRaccoon.class)
public class AIRaccoon extends Mob {

    protected AIRaccoon(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BlobFishGoals(CallbackInfo ci) {
        EntityRaccoon raccoon = (EntityRaccoon)(Object)this;
        if (AInteractionConfig.raccoonhunt) {
            this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.RACCOON_KILL)) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !raccoon.isTame() && level().isNight();
                }

                protected AABB getTargetSearchArea(double targetDistance) {
                    return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
                }
            });
        }
        if (AInteractionConfig.preyfear)
            this.goalSelector.addGoal(3, new AvoidEntityGoal(raccoon, LivingEntity.class, 8.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.RACCOON_FEAR)));

    }

}
