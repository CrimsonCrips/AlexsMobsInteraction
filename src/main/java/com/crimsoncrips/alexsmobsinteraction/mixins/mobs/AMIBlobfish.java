package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityBlobfish.class)
public abstract class AMIBlobfish extends WaterAnimal {


    protected AMIBlobfish(EntityType<? extends WaterAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityBlobfish blobfish = (EntityBlobfish)(Object)this;
        if (AMInteractionConfig.PREY_FEAR_ENABLED) {
            blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, Player.class, 3.0F, 0.8, 1.2));
            blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, EntityGiantSquid.class, 5.0F, 1, 1.4));
            blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, EntityFrilledShark.class, 2.0F, 0.8, 1));
        }
    }

}
