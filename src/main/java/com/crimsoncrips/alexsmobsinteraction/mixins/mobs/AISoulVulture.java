package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.goal.AIVultureTackleReplace;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntitySoulVulture.class)
public class AISoulVulture extends Mob {

    protected AISoulVulture(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void SoulVultureGoals(CallbackInfo ci){
        ci.cancel();
        EntitySoulVulture soulVulture = (EntitySoulVulture)(Object)this;
        Object aiCirclePerch = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntitySoulVulture$AICirclePerch",
                new Class[]{EntitySoulVulture.class,EntitySoulVulture.class},
                new Object[]{soulVulture, this}
        );
        this.goalSelector.addGoal(2,(Goal)aiCirclePerch);
        Object aiFlyRandom = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntitySoulVulture$AIFlyRandom",
                new Class[]{EntitySoulVulture.class,EntitySoulVulture.class},
                new Object[]{soulVulture, this}
        );
        this.goalSelector.addGoal(2,(Goal)aiFlyRandom);
        if(AInteractionConfig.soulbuff){
            this.goalSelector.addGoal(3, new AIVultureTackleReplace(soulVulture));
        } else {
            Object aiTackleMelee = ReflectionUtil.createInstance(
                    "com.github.alexthe666.alexsmobs.entity.EntitySoulVulture$AITackleMelee",
                    new Class[]{EntitySoulVulture.class,EntitySoulVulture.class},
                    new Object[]{soulVulture, this}
            );
            this.goalSelector.addGoal(3,(Goal)aiTackleMelee);
        }
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(soulVulture, new Class[]{EntitySoulVulture.class}));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, AbstractPiglin.class, true));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Hoglin.class, true));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityDropBear.class, true));
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, EntityBoneSerpent.class, 0, true, false, (entity) -> {
           return !entity.isInLava();
        }));

}

    }
