package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
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


@Mixin(EntityCapuchinMonkey.class)
public class AICapuchinMonkey extends Mob {


    protected AICapuchinMonkey(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void CapuchinMonkeyGoals(CallbackInfo ci){
        EntityCapuchinMonkey capuchinMonkey = (EntityCapuchinMonkey)(Object)this;
        Predicate<LivingEntity> CAPUCHIN_TARGETS = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CAPUCHIN_KILL);
        Predicate<LivingEntity> capuchinHunt = (livingEntity) -> {
            return CAPUCHIN_TARGETS.test(livingEntity) && !capuchinMonkey.isTame();
        };
        if(AInteractionConfig.capuchinhunt) {
            this.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(this, LivingEntity.class, 400, true, true, capuchinHunt));
        }
    }

}
