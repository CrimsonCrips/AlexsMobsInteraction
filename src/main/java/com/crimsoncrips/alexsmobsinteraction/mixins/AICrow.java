package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityCrow.class)
public class AICrow extends Mob {

    protected AICrow(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void CrowGoals(CallbackInfo ci){
        EntityCrow crow = (EntityCrow)(Object)this;
        java.util.function.Predicate<LivingEntity> weakEnough = (livingEntity) -> {
            return livingEntity.getHealth() <= 0.10F * livingEntity.getMaxHealth();
        };
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(this, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CROW_KILL)){
            public boolean canUse() {
                return super.canUse() && !crow.isTame();
            }});

        if (AInteractionConfig.crowcannibalize)
        {
            this.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(this, EntityCrow.class, 500, true, true, weakEnough) {
                public boolean canUse() {
                    return super.canUse()  && !crow.isTame();
                }
            });
        }
    }

}
