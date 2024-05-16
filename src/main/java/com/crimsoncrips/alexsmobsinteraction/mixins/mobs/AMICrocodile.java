package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry.CROCODILE_BABY_KILL;
import static com.github.alexthe666.alexsmobs.entity.EntityCrocodile.NOT_CREEPER;


@Mixin(EntityCrocodile.class)
public class AMICrocodile extends Mob {
    protected AMICrocodile(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public void awardKillScore(Entity entity, int score, DamageSource src) {
        if(entity instanceof LivingEntity living && AMInteractionConfig.nodropsforpredators){
            final CompoundTag emptyNbt = new CompoundTag();
            living.addAdditionalSaveData(emptyNbt);
            emptyNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
            living.readAdditionalSaveData(emptyNbt);

        }
        super.awardKillScore(entity, score, src);
    }
}
