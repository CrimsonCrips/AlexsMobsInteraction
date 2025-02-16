package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.crocodile;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static com.crimsoncrips.alexsmobsinteraction.server.AMInteractionTagRegistry.CROCODILE_BABY_KILL;
import static com.github.alexthe666.alexsmobs.entity.EntityCrocodile.NOT_CREEPER;


@Mixin(EntityCrocodile.class)
public abstract class AMICrocodileMixin extends TamableAnimal implements AMIBaseInterfaces {

    protected AMICrocodileMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("HEAD"))
    private void registerGoals(CallbackInfo ci) {
        EntityCrocodile crocodile = (EntityCrocodile)(Object)this;
        crocodile.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(crocodile, LivingEntity.class, 3000, true, false, AMEntityRegistry.buildPredicateFromTag(CROCODILE_BABY_KILL)){
            @Override
            public boolean canUse() {
                return super.canUse() && !isWally();
            }
        });


        this.targetSelector.addGoal(4, new EntityAINearestTarget3D(this, Player.class, 80, false, true, (Predicate)null) {
            public boolean canUse() {
                return !crocodile.isBaby() && !crocodile.isTame() && crocodile.level().getDifficulty() != Difficulty.PEACEFUL && super.canUse() && !isWally();
            }
        });
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CROCODILE_TARGETS)) {
            public boolean canUse() {
                return !crocodile.isBaby() && !crocodile.isTame() && super.canUse() && !isWally();
            }
        });
        this.targetSelector.addGoal(6, new EntityAINearestTarget3D(this, Monster.class, 180, false, true, NOT_CREEPER) {
            public boolean canUse() {
                return !crocodile.isBaby() && crocodile.isTame() && super.canUse() && !isWally();
            }
        });
    
    }

    @Inject(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 13), cancellable = true)
    private void nearestTarget2(CallbackInfo ci) {
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
