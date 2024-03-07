package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntitySkelewag.class)
public class AISkelewag extends Mob {

    protected AISkelewag(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void ShoebillGoals(CallbackInfo ci){
        ci.cancel();
        EntitySkelewag skelewag = (EntitySkelewag) (Object) this;
        Predicate<LivingEntity> NOT_TARGET = (livingEntity) -> {
            return !(livingEntity instanceof Monster) && !(livingEntity instanceof Player);
        };
        this.goalSelector.addGoal(1, new TryFindWaterGoal(skelewag));
        Object aiAttackGoal = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntitySkelewag$AttackGoal",
                new Class[] {EntitySkelewag.class,EntitySkelewag.class},
                new Object[]    {skelewag,this}
        );
        this.goalSelector.addGoal(2,(Goal)aiAttackGoal);
        this.goalSelector.addGoal(3, new AnimalAIRandomSwimming(skelewag, 1F, 12, 5));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(skelewag, Drowned.class, EntitySkelewag.class));
        if (AInteractionConfig.scourgingseas){
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, LivingEntity.class, 100, true, false, NOT_TARGET));
        } else {
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true));
            this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, Dolphin.class, true));
        }

        this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, Player.class, 1, true, false,null));

    }
}
