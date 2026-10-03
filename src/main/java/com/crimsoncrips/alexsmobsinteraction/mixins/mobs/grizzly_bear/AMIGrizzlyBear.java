package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.grizzly_bear;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.misc.UrsaBossEvent;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;





@Mixin(EntityGrizzlyBear.class)
public abstract class AMIGrizzlyBear extends Animal {

    protected AMIGrizzlyBear(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    private final UrsaBossEvent bossEvent = new UrsaBossEvent(Component.nullToEmpty("Ulfsaar"), 0);

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.getData(AMIAttachments.URSA)) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if (this.getData(AMIAttachments.URSA)){
            this.bossEvent.addPlayer(serverPlayer);
        }
    }

    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        if (this.getData(AMIAttachments.URSA)) {
            this.bossEvent.removePlayer(serverPlayer);
        }
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityGrizzlyBear;doHurtTarget(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean alexsMobsInteraction$tick(EntityGrizzlyBear instance, Entity target) {
        return !(this.getData(AMIAttachments.URSA) && this.getData(AMIAttachments.URSA_FLURRY_TIME) > 0);
    }



//    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 18))
//    private boolean alexsMobsInteraction$registerGoals2(GoalSelector instance, int pPriority, Goal pGoal) {
//        return !AlexsMobsInteraction.COMMON_CONFIG.TAMED_FRIENDLIES_ENABLED.get();
//    }











}
