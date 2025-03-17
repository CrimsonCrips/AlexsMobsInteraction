package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.crocodile;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.CuriosCompat;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

import static com.github.alexthe666.alexsmobs.entity.EntityCrocodile.NOT_CREEPER;


@Mixin(EntityCrocodile.class)
public abstract class AMICrocodileMixin extends TamableAnimal implements AMIBaseInterfaces {

    boolean panned = false;

    protected AMICrocodileMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void alexsMobsInteraction$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (isWally() && AlexsMobsInteraction.COMMON_CONFIG.EMOTIONAL_REMEMEMBRANCE_ENABLED.get()){
            AMIUtils.awardAdvancement(player, "wally", "wally");
        }
    }

    @Inject(method = "registerGoals", at = @At("HEAD"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityCrocodile crocodile = (EntityCrocodile)(Object)this;

        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, Player.class, 80, false, true, (Predicate)null) {
            public boolean canUse() {
                return !crocodile.isBaby() && !crocodile.isTame() && crocodile.level().getDifficulty() != Difficulty.PEACEFUL && super.canUse() && !isWally() && !panned;
            }
        });
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CROCODILE_TARGETS)) {
            public boolean canUse() {
                return !crocodile.isBaby() && !crocodile.isTame() && super.canUse() && !isWally() && !panned;
            }
        });
        this.targetSelector.addGoal(6, new EntityAINearestTarget3D(this, Monster.class, 180, false, true, NOT_CREEPER) {
            public boolean canUse() {
                return !crocodile.isBaby() && crocodile.isTame() && super.canUse() && !isWally() && !panned;
            }
        });
    
    }

    @Inject(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 13), cancellable = true)
    private void alexsMobsInteraction$registerGoals1(CallbackInfo ci) {
        if (AlexsMobsInteraction.COMMON_CONFIG.EMOTIONAL_REMEMEMBRANCE_ENABLED.get()){
            ci.cancel();
        }
    }

    @Override
    public boolean isWally() {
        String name = this.getName().getString().toLowerCase();
        return name.contains("wally") && this.isTame() && AlexsMobsInteraction.COMMON_CONFIG.EMOTIONAL_REMEMEMBRANCE_ENABLED.get();
    }

}
