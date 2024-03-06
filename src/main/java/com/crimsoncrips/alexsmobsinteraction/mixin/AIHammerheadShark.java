package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.github.alexthe666.alexsmobs.entity.EntityHammerheadShark;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityHammerheadShark.class)
public class AIHammerheadShark extends Mob {

    private static final Predicate<LivingEntity> MANTIS_EAT = (mob) -> {
        return mob.getHealth() <= 0.2 * mob.getMaxHealth();
    };

    protected AIHammerheadShark(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void HammerGoals(CallbackInfo ci){
        if (AInteractionConfig.hammerheadhuntmantisshrimp){
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityMantisShrimp.class, 50, true, false, MANTIS_EAT));
        }
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 0, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.HAMMERHEAD_KILL)));

    }

}
