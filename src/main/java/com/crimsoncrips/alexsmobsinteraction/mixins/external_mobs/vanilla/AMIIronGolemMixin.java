package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIUnsettlingKemonoAttack;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(IronGolem.class)
public abstract class AMIIronGolemMixin extends AbstractGolem {


    protected AMIIronGolemMixin(EntityType<? extends AbstractGolem> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        IronGolem ironGolem = (IronGolem)(Object)this;

        if(AlexsMobsInteraction.COMMON_CONFIG.UNSETTLING_BACKFIRE_ENABLED.get()) {
            ironGolem.targetSelector.addGoal(4, new AMIUnsettlingKemonoAttack<>(ironGolem, LivingEntity.class, true));
        }
    }

}
