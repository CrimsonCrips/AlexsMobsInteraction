package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
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


@Mixin(EntityGiantSquid.class)
public class AIGiantSquid extends Mob {

    protected AIGiantSquid(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void GiantSquidGoals(CallbackInfo ci){
        EntityGiantSquid giantSquid = (EntityGiantSquid)(Object)this;
        Predicate<LivingEntity> weakEnough = (livingEntity) -> {
            return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
        };
        if(AInteractionConfig.giantsquidcannibalize) {
            this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, EntityGiantSquid.class, 200, true, false, weakEnough) {
                public boolean canUse() {
                    return giantSquid.isInWaterOrBubble() && !giantSquid.isCaptured() && super.canUse();
                }
            });
        }

    }


}
