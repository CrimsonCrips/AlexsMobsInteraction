package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityMantisShrimp.class)
public class AIMantisShrimp extends Mob {

    protected AIMantisShrimp(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void MantisShrimpGoals(CallbackInfo ci){
        EntityMantisShrimp mantisShrimp = (EntityMantisShrimp)(Object)this;
        Predicate<LivingEntity> weakEnough = (livingEntity) -> {
            return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
        };
        this.goalSelector.addGoal(0, new MantisShrimpAIFryRice(mantisShrimp));
        this.goalSelector.addGoal(0, new MantisShrimpAIBreakBlocks(mantisShrimp));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(mantisShrimp));
        this.goalSelector.addGoal(2, new EntityMantisShrimp.FollowOwner(mantisShrimp, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(mantisShrimp, 1.2F, false));
        this.goalSelector.addGoal(4, new AnimalAIFindWater(mantisShrimp));
        this.goalSelector.addGoal(4, new AnimalAILeaveWater(mantisShrimp));
        this.goalSelector.addGoal(5, new BreedGoal(mantisShrimp, 0.8D));
        this.goalSelector.addGoal(6, new TemptGoal(mantisShrimp, 1.0D, Ingredient.of(Items.TROPICAL_FISH, AMItemRegistry.LOBSTER_TAIL.get(), AMItemRegistry.COOKED_LOBSTER_TAIL.get()), false));
        this.goalSelector.addGoal(7, new SemiAquaticAIRandomSwimming(mantisShrimp, 1.0D, 30));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(mantisShrimp));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(mantisShrimp, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(mantisShrimp));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(mantisShrimp));

        if(AInteractionConfig.mantisaggresive) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 150, true, true, null) {
                public boolean canUse() {
                    if (AInteractionConfig.weakened) {
                        return !mantisShrimp.isTame() && !isBaby() && !(getHealth() <= 0.15F * getMaxHealth()) && !mantisShrimp.isInLove() && super.canUse();
                    } else {
                        return !mantisShrimp.isTame() && !isBaby() && !mantisShrimp.isInLove() && super.canUse();
                    }
                }
            });
        }
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, LivingEntity.class, 120, true, false, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.MANTIS_SHRIMP_TARGETS)) {
            public boolean canUse() {
                return !mantisShrimp.isTame() && super.canUse();
            }
        });
        this.targetSelector.addGoal(4, new HurtByTargetGoal(mantisShrimp));


        if(AInteractionConfig.mantiscannibalize) {
            this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, EntityMantisShrimp.class, 200, true, false, weakEnough) {
                public boolean canUse() {
                    return !mantisShrimp.isTame() && super.canUse();
                }
            });
        }
    }
}
