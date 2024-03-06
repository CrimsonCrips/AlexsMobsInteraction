package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AISeagullAIStealFromPlayers;
import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIRevealTreasure;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntitySeagull.class)
public class AISeagull extends Mob {

    protected AISeagull(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void RoadrunnerGoals(CallbackInfo ci) {
        EntitySeagull seagull = (EntitySeagull) (Object) this;
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new SeagullAIRevealTreasure(seagull));
        this.targetSelector.addGoal(2, new AISeagullAIStealFromPlayers(seagull){
            @Override
            public boolean canUse() {
                if (AInteractionConfig.seagullnotsnatch) {
                    return super.canUse() && !(getHealth() <= 0.40F * getMaxHealth());
                } else {
                    return super.canUse();
                }
            }
        });
        this.goalSelector.addGoal(3, new BreedGoal(seagull, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(seagull, 1.0D, Ingredient.of(Items.COD, AMItemRegistry.LOBSTER_TAIL.get(), AMItemRegistry.COOKED_LOBSTER_TAIL.get()), false){
            public boolean canUse(){
                return !seagull.aiItemFlag && super.canUse();
            }
        });
        Object aiWanderIdle = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntitySeagull$AIWanderIdle",
                new Class[] {EntitySeagull.class},
                new Object[]    {seagull}
        );
        this.goalSelector.addGoal(2,(Goal)aiWanderIdle);
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        Object aiScatter = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntitySeagull$AIScatter",
                new Class[] {EntitySeagull.class},
                new Object[]    {seagull}
        );
        this.goalSelector.addGoal(2,(Goal)aiScatter);
        Object aiTargetItems = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntitySeagull$AITargetItems",
                new Class[] {PathfinderMob.class,boolean.class,boolean.class,int.class,int.class},
                new Object[]    {seagull, false, false, 15, 16}
        );
        this.goalSelector.addGoal(2,(Goal)aiTargetItems);

    }

}
