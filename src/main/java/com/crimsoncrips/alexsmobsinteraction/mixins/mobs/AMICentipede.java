package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.CuriosCompat;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIBlockTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIAvoidBlockGoal;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityCentipedeHead.class)
public class AMICentipede extends Monster {


    protected AMICentipede(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityCentipedeHead centipede = (EntityCentipedeHead)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.LIGHT_FEAR_ENABLED.get()) {
            centipede.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(centipede, Player.class, 50, true, false, livingEntity -> {
                return !CuriosCompat.hasLight(livingEntity) && centipede.getLastHurtByMob() != livingEntity ;
            }));

            centipede.goalSelector.addGoal(1, new AvoidEntityGoal<>(centipede, LivingEntity.class, 4.0F, 1.5, 2, (livingEntity) -> {
                return centipede.getLastAttacker() != livingEntity && CuriosCompat.hasLight(livingEntity);
            }));
            centipede.goalSelector.addGoal(1, new AMIAvoidBlockGoal(centipede, 4,1,1.2,(pos) -> {
                return centipede.level().getBlockState(pos).is(AMIBlockTagGenerator.LIGHT_FEAR);
            }));
        }
        if (AlexsMobsInteraction.COMMON_CONFIG.ADD_TARGETS_ENABLED.get()){
            centipede.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(centipede, LivingEntity.class, 55, true, false, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.CENTIPEDE_KILL)));
        }

    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 7))
    private boolean nearestAttack(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.LIGHT_FEAR_ENABLED.get();
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 8))
    private boolean nearestAttack2(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.LIGHT_FEAR_ENABLED.get();
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 9))
    private boolean nearestAttack3(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.LIGHT_FEAR_ENABLED.get();
    }



}
