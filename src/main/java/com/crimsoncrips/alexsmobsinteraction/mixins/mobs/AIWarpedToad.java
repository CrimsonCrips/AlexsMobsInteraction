package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityWarpedToad.class)
public class AIWarpedToad extends Mob {

    protected AIWarpedToad(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"),cancellable = true)
    private void WarpedMoscoGoals(CallbackInfo ci){
        ci.cancel();
        EntityWarpedToad warpedToad = (EntityWarpedToad)(Object)this;
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(warpedToad));
        this.goalSelector.addGoal(1, new EntityWarpedToad.TongueAttack(warpedToad));
        this.goalSelector.addGoal(2, new EntityWarpedToad.FollowOwner(warpedToad, 1.3D, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new AnimalAIFindWater(warpedToad));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(warpedToad));
        this.goalSelector.addGoal(3, new BreedGoal(warpedToad, 0.8D));
        this.goalSelector.addGoal(4, new TemptGoal(warpedToad, 1.0D, Ingredient.of(AMTagRegistry.INSECT_ITEMS), false));
        this.goalSelector.addGoal(5, new WarpedToadAIRandomSwimming(warpedToad, 1.0D, 7));
        this.goalSelector.addGoal(6, new AnimalAILeapRandomly(warpedToad, 50, 7){
            public boolean canUse(){
                return super.canUse() && !warpedToad.isOrderedToSit();
            }
        });
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(warpedToad, 60, 1.0D, 5, 4));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(warpedToad, false));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(warpedToad));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(warpedToad));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.WARPED_TOAD_TARGETS)));
        if(AInteractionConfig.warpedtarantula){
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityTarantulaHawk.class, 10, false, true,null){
                @Override
                public boolean canUse() {
                    return super.canUse() && !warpedToad.isTame();
                }
            });
        } else
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityTarantulaHawk.class, 10, false, true,null));

        if (!AInteractionConfig.moscoconsume) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityWarpedMosco.class, 10, false, true,null));
        }
        this.targetSelector.addGoal(5, new HurtByTargetGoal(warpedToad));
    }
}
