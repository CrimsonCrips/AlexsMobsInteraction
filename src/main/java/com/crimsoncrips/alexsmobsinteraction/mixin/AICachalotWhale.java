package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityCachalotWhale.class)
public class AICachalotWhale extends Mob {

    protected AICachalotWhale(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void CachalotWhaleGoals(CallbackInfo ci){
        EntityCachalotWhale cachalotWhale = (EntityCachalotWhale)(Object)this;
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CACHALOT_WHALE_KILL)) {
            public boolean canUse() {
                return !isSleeping() && !cachalotWhale.isBeached() && super.canUse();
            }
        });
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 300, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CACHALOT_WHALE_KILL_CHANCE)) {
            public boolean canUse() {
                return !isSleeping() && !cachalotWhale.isBeached() && super.canUse();
            }
        });
    }
    public void awardKillScore(Entity entity, int score, DamageSource src) {
        if(entity instanceof LivingEntity living && AInteractionConfig.nodropsforpredators){
            final CompoundTag emptyNbt = new CompoundTag();
            living.addAdditionalSaveData(emptyNbt);
            emptyNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
            living.readAdditionalSaveData(emptyNbt);
        }
        super.awardKillScore(entity, score, src);
    }
}
