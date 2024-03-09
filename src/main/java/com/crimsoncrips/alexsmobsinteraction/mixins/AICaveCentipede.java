package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityCentipedeHead.class)
public class AICaveCentipede extends Mob {

    protected AICaveCentipede(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void CentipedeGoals(CallbackInfo ci){
        EntityCentipedeHead centipede = (EntityCentipedeHead)(Object)this;
        Predicate<LivingEntity> lightHold = (livingEntity) -> {
            return livingEntity.isHolding(Ingredient.of(AInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR));
        };
        Predicate<LivingEntity> lightNotHold = (livingEntity) -> {
            return !livingEntity.isHolding(Ingredient.of(AInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR));
        };
        if(AInteractionConfig.centipedelightfear) {
            this.goalSelector.addGoal(1, new AvoidEntityGoal(centipede, LivingEntity.class, 4.0F, 1.5, 2, lightHold));
        }
        this.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(this, LivingEntity.class, 55, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CAVE_CENTIPEDE_KILL)));
        if(AInteractionConfig.centipedelightfear) {
            this.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(this, Player.class, 1, true, false,lightNotHold));

        } else {
            this.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(this, Player.class, 1, true, false,null));

        }
        if(AInteractionConfig.centipedelightfear) {
            this.goalSelector.addGoal(3, new AvoidBlockGoal(centipede, 4,1,1.2,(pos) -> {
                BlockState state = level().getBlockState(pos);
                return state.is(AInteractionTagRegistry.CENTIPEDE_BLOCK_FEAR);
            }));
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.centipedelightfear) {
            LivingEntity livingEntity = getTarget();
            if (livingEntity != null && livingEntity.isHolding(Ingredient.of(AInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR))) {
                setTarget(null);
            }
        }
    }
}
