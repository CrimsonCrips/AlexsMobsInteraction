package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMBlockItem;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
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


@Mixin(EntityCaiman.class)
public class AICaiman extends Mob {

    protected AICaiman(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void CaimanGoals(CallbackInfo ci){
        EntityCaiman caiman = (EntityCaiman)(Object)this;
        Predicate<LivingEntity> caimanHoldEgg = (livingEntity) -> {
            return livingEntity.isHolding( Ingredient.of(AMBlockRegistry.CAIMAN_EGG.get()));
        };
        if(AInteractionConfig.caimanaggresive) {
            this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Player.class, 150, true, true, null) {
                public boolean canUse() {
                    if (AInteractionConfig.weakened) {
                        return !caiman.isTame() && !isBaby() && !(getHealth() <= 0.15F * getMaxHealth()) && !caiman.isInLove() && super.canUse();
                    } else {
                        return !caiman.isTame() && !isBaby() && !caiman.isInLove() && super.canUse();
                    }
                }
            });
        }
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, LivingEntity.class, 100, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CAIMAN_KILL)) {
            public boolean canUse() {
                return !caiman.isTame() && super.canUse();
            }
            protected AABB getTargetSearchArea(double targetDistance) {
                return this.mob.getBoundingBox().inflate(15D, 1D, 15D);
            }
        });
        this.targetSelector.addGoal(5, new EntityAINearestTarget3D(this, EntityFly.class, 500, false, true,null));
        if (AInteractionConfig.caimaneggattack) {
            this.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(this, LivingEntity.class, 0, true, false, caimanHoldEgg) {
                public boolean canUse() {
                    return super.canUse() && !this.mob.isBaby();
                }
            });
        }
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
