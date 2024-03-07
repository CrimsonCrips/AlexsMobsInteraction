package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.alexsmobs.entity.EntityRoadrunner;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRoadrunner.class)
public class AIRoadrunner extends Mob {

    protected AIRoadrunner(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void RoadrunnerGoals(CallbackInfo ci) {
        ci.cancel();
        EntityRoadrunner roadrunner = (EntityRoadrunner) (Object) this;
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(roadrunner, 1.1D));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(roadrunner, 1.0D, false));
        this.goalSelector.addGoal(2, new BreedGoal(roadrunner, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(roadrunner, 1.1D));
        this.goalSelector.addGoal(4, new TemptGoal(roadrunner, 1.1D, Ingredient.of(AMTagRegistry.INSECT_ITEMS), false));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(roadrunner, 50, 1.0D, 25, 7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(roadrunner, EntityRattlesnake.class, Player.class)).setAlertOthers());
        if (AInteractionConfig.roadrunnerday) {
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ROADRUNNER_KILL)) {
                @Override
                public boolean canUse() {
                    return super.canUse() && level().isDay();
                }
            });
        } else {
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ROADRUNNER_KILL)) {
                @Override
                public boolean canUse() {
                    return super.canUse() && level().isDay();
                }
            });
        }
    }

}
