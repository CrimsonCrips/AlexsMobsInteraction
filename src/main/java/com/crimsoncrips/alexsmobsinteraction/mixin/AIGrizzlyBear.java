package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry.GRIZZLY_KILL;
import static com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry.GRIZZLY_TERRITORIAL;


@Mixin(EntityGrizzlyBear.class)
public abstract class AIGrizzlyBear extends Mob {

    @Shadow public abstract boolean isHoneyed();

    int noHoney = 0;
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(Items.SALMON, Items.HONEYCOMB, Items.HONEY_BOTTLE);

    protected AIGrizzlyBear(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void GrizzlyGoals(CallbackInfo ci){
        EntityGrizzlyBear grizzlyBear = (EntityGrizzlyBear)(Object)this;
        Predicate<LivingEntity> GRIZZLY_TARGET = AMEntityRegistry.buildPredicateFromTag(GRIZZLY_KILL);
        Predicate<LivingEntity> TERRITORIAL = AMEntityRegistry.buildPredicateFromTag(GRIZZLY_TERRITORIAL);
        Predicate<LivingEntity> GRIZZLY_HUNT = (livingEntity) -> {
            return GRIZZLY_TARGET.test(livingEntity) && level().isDay();
        };
        Predicate<LivingEntity> territorial = (livingEntity) -> {
            return TERRITORIAL.test(livingEntity) || livingEntity instanceof Player;
        };
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(grizzlyBear));
        this.goalSelector.addGoal(2, new TameableAIFollowOwner(grizzlyBear, 1.2D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new GrizzlyBearAIAprilFools(grizzlyBear));
        Object aiMeleeAttackGoal = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear$MeleeAttackGoal",
                new Class[] {EntityGrizzlyBear.class},
                new Object[]    {grizzlyBear}
        );
        this.goalSelector.addGoal(2,(Goal)aiMeleeAttackGoal);
        Object aiPanicGoal = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear$PanicGoal",
                new Class[] {EntityGrizzlyBear.class},
                new Object[]    {grizzlyBear}
        );
        this.goalSelector.addGoal(2,(Goal)aiPanicGoal);
        this.goalSelector.addGoal(2, new TemptGoal(grizzlyBear, 1.25, Ingredient.of(AInteractionTagRegistry.GRIZZLY_ENTICE), false));
        this.goalSelector.addGoal(5, new TameableAITempt(grizzlyBear, 1.1D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(grizzlyBear, 1.25D));
        this.goalSelector.addGoal(5, new GrizzlyBearAIBeehive(grizzlyBear));
        this.goalSelector.addGoal(6, new GrizzlyBearAIFleeBees(grizzlyBear, 14, 1D, 1D));
        this.goalSelector.addGoal(6, new BreedGoal(grizzlyBear, 1.0D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(grizzlyBear, 0.75D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(grizzlyBear));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(grizzlyBear));
        this.targetSelector.addGoal(2,(Goal)aiMeleeAttackGoal);
        Object aiHurtByTargetGoal = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear$HurtByTargetGoal",
                new Class[] {EntityGrizzlyBear.class},
                new Object[]    {grizzlyBear}
        );
        this.targetSelector.addGoal(2,(Goal)aiHurtByTargetGoal);
        this.targetSelector.addGoal(4, new CreatureAITargetItems(grizzlyBear, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, LivingEntity.class, 200, true, true,territorial) {
            protected AABB getTargetSearchArea(double targetDistance) {
                return this.mob.getBoundingBox().inflate(2, 2.0, 2);
            }
            public boolean canUse() {
                if (AInteractionConfig.grizzlyattackfriendly) {
                    if (AInteractionConfig.weakened) {
                        return super.canUse() && !(getHealth() <= 0.20F * getMaxHealth() && !grizzlyBear.isTame()) && !grizzlyBear.isHoneyed();
                    } else {
                        return super.canUse() && !grizzlyBear.isTame() && !grizzlyBear.isHoneyed();
                    }
                } else if (AInteractionConfig.weakened) {
                    return super.canUse() && !(getHealth() <= 0.20F * getMaxHealth() && !grizzlyBear.isHoneyed());
                } else {
                    return super.canUse() && !grizzlyBear.isHoneyed();
                }
            }
        });

        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 300, true, false, GRIZZLY_HUNT){
            public boolean canUse() {
                return  super.canUse() && (noHoney >= 5000) || !grizzlyBear.isTame();
            }
        });
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(grizzlyBear, false));

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
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if(this.isHoneyed()) {
            noHoney = 0;
        }
        else {
            noHoney++;
        }
    }



}
