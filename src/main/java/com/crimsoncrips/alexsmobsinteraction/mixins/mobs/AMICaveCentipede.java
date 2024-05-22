package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.goal.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityCentipedeHead.class)
public class AMICaveCentipede extends Mob {

    protected AMICaveCentipede(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickCaveCentipede(CallbackInfo ci) {
        if (AMInteractionConfig.LIGHT_FEAR_ENABLED) {
            LivingEntity livingEntity = getTarget();
            if (livingEntity != null && livingEntity.isHolding(Ingredient.of(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR)) && this.getLastHurtByMob() != livingEntity) {
                setTarget(null);
            }
        }
    }
}
