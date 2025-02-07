package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityMantisShrimp.class)
public abstract class AMIMantisShrimp extends TamableAnimal {


    protected AMIMantisShrimp(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityMantisShrimp mantisShrimp = (EntityMantisShrimp)(Object)this;
        if(AlexsMobsInteraction.COMMON_CONFIG.MANTIS_AGGRO_ENABLED.get()  && !mantisShrimp.isBaby()) {
            mantisShrimp.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mantisShrimp, Player.class, 150, true, true, null){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !mantisShrimp.isTame();
                }
            });
        }
        if(AlexsMobsInteraction.COMMON_CONFIG.MANTIS_CANNIBALIZE_ENABLED.get()) {
            mantisShrimp.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(mantisShrimp, EntityMantisShrimp.class, 200, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
            }){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !mantisShrimp.isTame();
                }
            });
        }
    }




}
