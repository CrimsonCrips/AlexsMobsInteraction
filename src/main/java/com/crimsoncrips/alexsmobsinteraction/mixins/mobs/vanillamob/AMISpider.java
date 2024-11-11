package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.vanillamob;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Spider.class)
public abstract class AMISpider extends Monster {


    protected AMISpider(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        if (AMInteractionConfig.SPIDER_EAT_ENABLED) {
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, EntityCockroach.class, 2, true, false, null));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Silverfish.class, 2, true, false, null));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Bee.class, 2, true, false, null));
        }
    }

}
