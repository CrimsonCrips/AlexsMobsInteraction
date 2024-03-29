package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFrilledShark;
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


@Mixin(EntityFrilledShark.class)
public class AIFrilledShark extends Mob {

    protected AIFrilledShark(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void FrilledGoals(CallbackInfo ci){
        Predicate<LivingEntity> weakEnough = (livingEntity) -> {
            return livingEntity.getHealth() <= 0.25F * livingEntity.getMaxHealth();
        };
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 1, false, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FRILLED_KILL)));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, EntityGiantSquid.class, 1, false, true,weakEnough));
    }
}
