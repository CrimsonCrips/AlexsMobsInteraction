package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.goal.AICosmawOwner;
import com.crimsoncrips.alexsmobsinteraction.goal.AIRandomFly;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAITemptDistance;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAIFollowOwner;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.google.common.base.Predicates;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityCosmaw.class)
public class AICosmaw extends Mob {

    static{
        WEAKTIMER = SynchedEntityData.defineId(EntityCosmaw.class, EntityDataSerializers.INT);
    }
    private static final EntityDataAccessor<Integer> WEAKTIMER;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(WEAKTIMER, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("WeakTimer", this.getweakTimer());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setweakTimer(compound.getInt("WeakTimer"));

    }

    public int getweakTimer() {
        return (Integer)this.entityData.get(WEAKTIMER);
    }

    public void setweakTimer(int weaktimer) {
        this.entityData.set(WEAKTIMER, weaktimer);
    }



    protected AICosmaw(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    int armor = 0;

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true,remap = false)
    private void test(CallbackInfo ci) {
        ci.cancel();
        EntityCosmaw cosmaw = (EntityCosmaw)(Object)this;
        this.goalSelector.addGoal(0, new FloatGoal(this));
        Object aiAIAttack = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityCosmaw$AIAttack",
                new Class[]{EntityCosmaw.class},
                new Object[]{cosmaw}
        );
        this.goalSelector.addGoal(1, (Goal) aiAIAttack);

        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(cosmaw));
        this.goalSelector.addGoal(3, new FlyingAIFollowOwner(cosmaw, 1.3, 8.0F, 4.0F, false));
        if (AInteractionConfig.cosmawweakness) {
            this.goalSelector.addGoal(4, new AICosmawOwner(cosmaw){

               /* @Override
                public void stop() {
                    if (armor > 10) {
                        setweakTimer(getweakTimer() + armor * 100);
                        cosmaw.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, armor * 100, 0));
                    }
                } */

                @Override
                public boolean canUse() {
                    return getweakTimer() <= 0 || cosmaw.getOwner().getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AIEnchantmentRegistry.LIGHTWEIGHT.get()) > 0;
                }

                @Override
                public boolean canContinueToUse() {
                    return canUse();
                }
            });
        } else {
            Object aiAIPickupOwner = ReflectionUtil.createInstance(
                    "com.github.alexthe666.alexsmobs.entity.EntityCosmaw$AIPickupOwner",
                    new Class[]{EntityCosmaw.class},
                    new Object[]{cosmaw}
            );
            this.goalSelector.addGoal(4, (Goal) aiAIPickupOwner);
        }
        this.goalSelector.addGoal(5, new BreedGoal(cosmaw, 1.2));
        this.goalSelector.addGoal(6, new AnimalAITemptDistance(cosmaw, 1.1, Ingredient.of(new ItemLike[]{Items.CHORUS_FRUIT, (ItemLike) AMItemRegistry.COSMIC_COD.get()}), false, 25.0) {
            public boolean canUse() {
                return super.canUse() && cosmaw.getMainHandItem().isEmpty();
            }

            public boolean canContinueToUse() {
                return super.canContinueToUse() && cosmaw.getMainHandItem().isEmpty();
            }
        });
        this.goalSelector.addGoal(7, new AIRandomFly(cosmaw));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems<>(cosmaw, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(cosmaw, new Class[0]) {
            public boolean canUse() {
                LivingEntity livingentity = this.mob.getLastHurtByMob();
                return livingentity != null && cosmaw.isOwnedBy(livingentity) ? false : super.canUse();
            }
        });
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, EntityCosmicCod.class, 80, true, false, Predicates.alwaysTrue()));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        EntityCosmaw cosmaw = (EntityCosmaw)(Object)this;
        if(cosmaw.getOwner() != null){
            armor = cosmaw.getOwner().getArmorValue();
        }
       }

}
