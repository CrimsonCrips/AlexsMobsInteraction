package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;




@Mixin(EntityAnaconda.class)
public class AIAnaconda extends Mob {

    private int ate = 0;
    protected AIAnaconda(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"),cancellable = true)
    private void AnacondaGoals(CallbackInfo ci){
        ci.cancel();
        EntityAnaconda anaconda = (EntityAnaconda)(Object)this;
        Predicate<LivingEntity> ANACONDA_BABY_TARGETS = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ANACONDA_BABY_KILL);
        Predicate<LivingEntity> anacondaBaby = (livingEntity) -> {
            return ANACONDA_BABY_TARGETS.test(livingEntity) && level().isNight() && livingEntity.isBaby();
        };
        Predicate<LivingEntity> ancaondacannibalism = (livingEntity) -> {
            return (livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth() || livingEntity.isBaby()) && ate >= 5000;
        };

        this.goalSelector.addGoal(1, new AnimalAIPanicBaby(anaconda, 1.25D));
        Object aiMelee = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityAnaconda$AIMelee",
                new Class[] {EntityAnaconda.class},
                new Object[]    {anaconda}
        );
        this.goalSelector.addGoal(2,(Goal)aiMelee);
        this.goalSelector.addGoal(3, new AnimalAIFindWater(anaconda));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(anaconda));
        this.goalSelector.addGoal(4, new TemptGoal(anaconda, 1.25D, Ingredient.of(Items.CHICKEN, Items.COOKED_CHICKEN), false));
        this.goalSelector.addGoal(5, new BreedGoal(anaconda, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(anaconda, 1.1D));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(anaconda, 60, 1.0D, 14, 7));
        this.goalSelector.addGoal(8, new SemiAquaticAIRandomSwimming(anaconda, 1.5D, 7));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(anaconda, Player.class, 25F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(anaconda));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(anaconda, Player.class, 110, false, true, null) {
            public boolean canUse() {
                if (AInteractionConfig.weakened) {
                    return !isBaby() && (int) ReflectionUtil.getField(anaconda, "passiveFor") == 0 && !(getHealth() <= 0.10F * getMaxHealth()) && !anaconda.isInLove() && super.canUse();
                } else
                {
                    return !isBaby() && (int) ReflectionUtil.getField(anaconda, "passiveFor") == 0 && !anaconda.isInLove() && super.canUse();
                }
            }
        });
        this.targetSelector.addGoal(3, new HurtByTargetGoal(anaconda, new Class[]{EntityAnaconda.class}));
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, LivingEntity.class, 50, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ANACONDA_KILL)){
            public void start() {super.start(); ate = 0;}
            protected AABB getTargetSearchArea(double targetDistance) {
                return anaconda.getBoundingBox().inflate(25D, 1D, 25D);
            }
        });
        if (AInteractionConfig.anacondaattackbabies){
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, LivingEntity.class, 4000, true, false, anacondaBaby) {
                public void start() {
                    super.start();
                    ate = 0;
                }
                protected AABB getTargetSearchArea(double targetDistance) {
                    return anaconda.getBoundingBox().inflate(25D, 1D, 25D);
                }
            });
        }


        if (AInteractionConfig.anacondacanibalize) {
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 200, true, false, ancaondacannibalism) {
                public void start() {super.start(); ate = 0;}
            });
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.anacondacanibalize) ate++;
    }


}
