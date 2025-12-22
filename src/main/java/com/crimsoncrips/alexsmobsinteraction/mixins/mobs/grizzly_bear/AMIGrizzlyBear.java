package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.grizzly_bear;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.UrsaBossEvent;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.GrizzlyExtras;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIGrizzlyScavenge;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;




@Mixin(EntityGrizzlyBear.class)
public abstract class AMIGrizzlyBear extends Animal implements GrizzlyExtras {


    @Shadow public abstract boolean isHoneyed();

    protected AMIGrizzlyBear(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private final UrsaBossEvent bossEvent = new UrsaBossEvent(Component.nullToEmpty("Ulfsaar"), 0);

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && isUrsa()) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if (isUrsa()){
            this.bossEvent.addPlayer(serverPlayer);
        }
    }

    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        if (isUrsa()) {
            this.bossEvent.removePlayer(serverPlayer);
        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityGrizzlyBear grizzlyBear = (EntityGrizzlyBear) (Object) this;

        if (AlexsMobsInteraction.COMMON_CONFIG.GRIZZLY_PACIFIED_ENABLED.get()) {
            this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::modifiedIsAngryAt));

        }
        if (AlexsMobsInteraction.COMMON_CONFIG.STORED_HUNGER_ENABLED.get()) {
            grizzlyBear.goalSelector.addGoal(6, new AMIGrizzlyScavenge(grizzlyBear,  1.2, 12));
        }
        if (AlexsMobsInteraction.COMMON_CONFIG.HONEYLESS_HUNTING_ENABLED.get()){
            grizzlyBear.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(grizzlyBear, LivingEntity.class, 500, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.GRIZZLY_BEAR_KILL)){
                @Override
                public boolean canUse() {
                    return super.canUse() && !grizzlyBear.isTame() && !grizzlyBear.isEating() && !grizzlyBear.isHoneyed() && ((GrizzlyExtras)grizzlyBear).getNoHoney() >= 10000;
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && canUse();
                }
            });
        }
    }

    boolean modifiedIsAngryAt(LivingEntity pTarget) {
        EntityGrizzlyBear grizzlyBear = (EntityGrizzlyBear) (Object) this;
        if (!this.canAttack(pTarget)) {
            return false;
        } else {
            return !grizzlyBear.isTame() && (pTarget.getType() == EntityType.PLAYER && grizzlyBear.isAngryAtAllPlayers(pTarget.level()) || pTarget.getUUID().equals(grizzlyBear.getPersistentAngerTarget()));
        }
    }


//    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 18))
//    private boolean alexsMobsInteraction$registerGoals2(GoalSelector instance, int pPriority, Goal pGoal) {
//        return !AlexsMobsInteraction.COMMON_CONFIG.GRIZZLY_PACIFIED_ENABLED.get();
//    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 19))
    private boolean alexsMobsInteraction$registerGoals3(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.GRIZZLY_PACIFIED_ENABLED.get();
    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$onGetItem(ItemEntity e, CallbackInfo ci) {
        if (e.getItem().isEdible() && AlexsMobsInteraction.COMMON_CONFIG.FOOD_TARGET_EFFECTS_ENABLED.get()) {
            this.heal(5);
            List<Pair<MobEffectInstance, Float>> test = Objects.requireNonNull(e.getItem().getFoodProperties(this)).getEffects();
            if (!test.isEmpty()){
                for (int i = 0; i < test.size(); i++){
                    this.addEffect(new MobEffectInstance(test.get(i).getFirst()));
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (this.isHoneyed()){
            setNoHoney(0);
        } else {
            setNoHoney(getNoHoney() + 1);
        }
    }

    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void alexsMobsInteraction$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (AlexsMobsInteraction.COMMON_CONFIG.BRUSHED_ENABLED.get() && itemStack.getItem() instanceof BrushItem && !this.level().isClientSide && this.isHoneyed() && !isUrsa()) {
            if (!player.isCreative()) {
                itemStack.hurtAndBreak(15, player, (p_233654_0_) -> {
                });
            }
            AMIUtils.spawnLoot(AMILootTables.GRIZZLY_BRUSH,this,player,0);
            this.playSound(SoundEvents.BRUSH_GENERIC, 1, this.getVoicePitch());
            AMIUtils.awardAdvancement(player,"brushed","brushed");
        }
    }

    private static final EntityDataAccessor<Integer> NO_HONEY = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> URSA = SynchedEntityData.defineId(EntityGrizzlyBear.class, EntityDataSerializers.BOOLEAN);

    @Override
    public int getNoHoney() {
        return this.entityData.get(NO_HONEY);
    }

    @Override
    public void setNoHoney(int time) {
        this.entityData.set(NO_HONEY, Integer.valueOf(time));
    }

    @Override
    public boolean isUrsa() {
        return this.entityData.get(URSA);
    }

    public void setUrsa(boolean ursa) {
        this.entityData.set(URSA, Boolean.valueOf(ursa));
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void define(CallbackInfo ci) {
        this.entityData.define(NO_HONEY, 0);
        this.entityData.define(URSA, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void add(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("NoHoney", getNoHoney());
        compound.putBoolean("Ursa", isUrsa());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$read(CompoundTag compound, CallbackInfo ci) {
        this.setNoHoney(compound.getInt("NoHoney"));
        this.setUrsa(compound.getBoolean("Ursa"));
    }

}
