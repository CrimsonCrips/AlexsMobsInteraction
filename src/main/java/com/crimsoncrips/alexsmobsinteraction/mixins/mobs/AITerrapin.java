package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityTerrapin.class)
public class AITerrapin extends Mob {


    boolean retreatStomp = false;

    boolean spinStomp = false;



    protected AITerrapin(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.terrapinstomp)
        {
            EntityTerrapin terrapin = (EntityTerrapin)(Object)this;
            if (terrapin.hasRetreated() && !retreatStomp){
                retreatStomp = true;
                hurt(damageSources().generic(), 2);
            }
            if (!terrapin.hasRetreated() && !terrapin.isSpinning()) {
               retreatStomp = false;
            }
        }
    }

}
