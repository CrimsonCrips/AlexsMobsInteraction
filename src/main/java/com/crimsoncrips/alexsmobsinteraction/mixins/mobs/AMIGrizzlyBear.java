package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry.GRIZZLY_KILL;
import static com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry.GRIZZLY_TERRITORIAL;


@Mixin(EntityGrizzlyBear.class)
public abstract class AMIGrizzlyBear extends Mob {

    @Shadow public abstract boolean isHoneyed();

    static{
        HUNGER = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.INT);
    }
    private static final EntityDataAccessor<Integer> HUNGER;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(HUNGER, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("Hunger", this.getHunger());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setHunger(compound.getInt("Hunger"));

    }

    public int getHunger() {
        return (Integer)this.entityData.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.entityData.set(HUNGER, hunger);
    }

    protected AMIGrizzlyBear(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void GrizzlyGoals(CallbackInfo ci){
        ci.cancel();
        EntityGrizzlyBear grizzlyBear = (EntityGrizzlyBear)(Object)this;
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
        this.goalSelector.addGoal(5, new TameableAITempt(grizzlyBear, 1.1D, Ingredient.of(AMInteractionTagRegistry.GRIZZLY_ENTICE), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(grizzlyBear, 1.25D));
        this.goalSelector.addGoal(5, new GrizzlyBearAIBeehive(grizzlyBear));
        this.goalSelector.addGoal(6, new GrizzlyBearAIFleeBees(grizzlyBear, 14, 1D, 1D));
        this.goalSelector.addGoal(6, new BreedGoal(grizzlyBear, 1.0D));
        this.goalSelector.addGoal(7, new RandomStrollGoal(grizzlyBear, 0.75D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(grizzlyBear));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(grizzlyBear));
        Object aiHurtByTargetGoal = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear$HurtByTargetGoal",
                new Class[] {EntityGrizzlyBear.class},
                new Object[]    {grizzlyBear}
        );
        this.targetSelector.addGoal(3,(Goal)aiHurtByTargetGoal);
        this.targetSelector.addGoal(4, new CreatureAITargetItems(grizzlyBear, false));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 100, true, false,AMEntityRegistry.buildPredicateFromTag(GRIZZLY_TERRITORIAL)));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, Player.class, 300, true, false,null) {

            public boolean canUse() {
                if (AMInteractionConfig.grizzlyattackfriendly) {
                    if (AMInteractionConfig.weakened) {
                        return super.canUse() && !(getHealth() <= 0.20F * getMaxHealth() && !grizzlyBear.isTame()) && !isHoneyed();
                    } else {
                        return super.canUse() && !grizzlyBear.isTame() && !isHoneyed();
                    }
                } else if (AMInteractionConfig.weakened) {
                    return super.canUse() && !(getHealth() <= 0.20F * getMaxHealth() && !isHoneyed());
                } else {
                    return super.canUse() && !isHoneyed();
                }
            }
        });

        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 300, true, false, AMEntityRegistry.buildPredicateFromTag(GRIZZLY_KILL)){
            public boolean canUse() {
                return  super.canUse() && getHunger() >= 5000 || !grizzlyBear.isTame() && level().isDay();
            }
        });
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(grizzlyBear, false));
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
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        EntityGrizzlyBear grizzlyBear = (EntityGrizzlyBear)(Object)this;
        if (grizzlyBear.isHoneyed()){
            this.setTarget(null);
        }
        if(AMInteractionConfig.grizzlyfreddy){
            String freddy = "Freddy Fazbear";
            if (this.getName().getString().equals(freddy)) {
                grizzlyBear.setAprilFoolsFlag(2);
                if(!AMInteractionConfig.goofymode){
                    grizzlyBear.setTame(false);
                    grizzlyBear.setOwnerUUID(null);
                }
            }
        }
        if(isHoneyed()) {
            this.setHunger(0);
        }
        else {
            this.setHunger(this.getHunger() + 1);
        }
    }




}
