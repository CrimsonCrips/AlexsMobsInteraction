package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityBaldEagle.class)
public class AIBaldEagle extends Mob {

    protected AIBaldEagle(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BaldEagleGoals(CallbackInfo ci){
        Predicate<LivingEntity> weakEnough = (livingEntity) -> {
            return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
        };
        EntityBaldEagle baldEagle = (EntityBaldEagle)(Object)this;
        if (AInteractionConfig.baldeaglecannibalize) {
            this.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(this, EntityBaldEagle.class, 1000, true, false, weakEnough) {
                public boolean canUse() {
                    return super.canUse() && !baldEagle.isTame();
                }
            });
        }
    }
}
