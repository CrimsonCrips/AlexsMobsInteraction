package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntitySoulVulture.class)
public abstract class AMISoulVulture extends Monster {


    protected AMISoulVulture(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntitySoulVulture soulVulture = (EntitySoulVulture)(Object)this;
        soulVulture.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(soulVulture, Hoglin.class, true));
        soulVulture.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(soulVulture, EntityDropBear.class, true));
        soulVulture.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(soulVulture, EntityBoneSerpent.class, 0, true, false, (mob) -> {
            return !mob.isInLava();
        }));

    }

}
