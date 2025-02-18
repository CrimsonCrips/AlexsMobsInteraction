package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.cockroach;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.ACCompat;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AsmonRoach;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFollowNearestGoal;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMISurroundAttacker;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.MineGuardianEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Mixin(EntityCockroach.class)
public abstract class AMICockroach extends Mob implements AsmonRoach {

    private int conversionTime;
    private static final EntityDataAccessor<Integer> WORSHIPING = SynchedEntityData.defineId(EntityCockroach.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_GOD = SynchedEntityData.defineId(EntityCockroach.class, EntityDataSerializers.BOOLEAN);

    @Shadow public abstract boolean isDancing();

    @Shadow public abstract boolean hasMaracas();

    @Shadow @Final protected static EntityDimensions STAND_SIZE;

    protected AMICockroach(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$getItem(ItemEntity e, CallbackInfo ci) {
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
        EntityCockroach cockroach = (EntityCockroach)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.COCKROACH_MUTATION_ENABLED.get() && ModList.get().isLoaded("alexscaves")) {
            if (ACCompat.toxicCaves(cockroach)){
                ++conversionTime;
            }
            if (conversionTime > 160 && !this.level().isClientSide) {
                ACEntityRegistry.GAMMAROACH.get().spawn((ServerLevel) this.level(), BlockPos.containing(this.getPosition(1)), MobSpawnType.MOB_SUMMONED);
                this.remove(RemovalReason.DISCARDED);
            }
        }

        if (isGod() && cockroach.getLastHurtByMob() != null){
           System.out.println(cockroach.getLastHurtByMob().getType());
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(WORSHIPING, -1);
        this.entityData.define(IS_GOD, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("Worshiping", this.getWorshiping());
        compound.putBoolean("RoachGod", this.isGod());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setWorshiping(compound.getInt("Worshiping"));
        this.setGod(compound.getBoolean("RoachGod"));
    }
    @Override
    public int getWorshiping() {
        return this.entityData.get(WORSHIPING);
    }

    @Override
    public boolean isGod() {
        return this.entityData.get(IS_GOD);
    }

    @Override
    public void setWorshiping(int god) {
        this.entityData.set(WORSHIPING, god);
    }

    public void setGod(boolean bool) {
        this.entityData.set(IS_GOD, bool);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityCockroach cockroach = (EntityCockroach)(Object)this;
        this.goalSelector.addGoal(1, new PanicGoal(cockroach, 1.1){
            public boolean canUse() {
                return super.canUse() && !isGod() && getWorshiping() == 1;
            }
        });
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(cockroach, EntityCentipedeHead.class, 16.0F, 1.3, (double)1.0F){
            public boolean canUse() {
                return super.canUse() && !isGod() && getWorshiping() == 1;
            }
        });
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(cockroach, Player.class, 8.0F, 1.3, (double)1.0F) {
            public boolean canUse() {
                return !cockroach.isBreaded() && super.canUse() && !isGod() && getWorshiping() == 1;
            }
        });
        cockroach.goalSelector.addGoal(8, new AMISurroundAttacker(cockroach));


    }

    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void alexsMobsInteraction$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        EntityCockroach cockroach = (EntityCockroach)(Object)this;

        if (ModList.get().isLoaded("alexscaves") && itemStack.is(ACCompat.gameController().getItem()) && !this.isDancing() && !this.hasMaracas() && !isGod() && getWorshiping() == -1){
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            this.setGod(true);
            cockroach.setCustomName(Component.nullToEmpty("Asmongold"));
            cockroach.getAttribute(Attributes.ARMOR).setBaseValue(10F);
            cockroach.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100F);
            cockroach.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.10F);
            cockroach.setHealth(100);
            cockroach.setPersistenceRequired();
            cockroach.refreshDimensions();

            for (EntityCockroach nearRoaches : cockroach.level().getEntitiesOfClass(EntityCockroach.class, this.getBoundingBox().inflate(10))) {
                if (nearRoaches != cockroach && !((AsmonRoach)nearRoaches).isGod() && ((AsmonRoach)nearRoaches).getWorshiping() == -1) {
                    ((AsmonRoach)nearRoaches).setWorshiping(cockroach.getId());
                    nearRoaches.setCustomName(Component.nullToEmpty("Servant"));
                }
            }
        }
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"))
    private boolean alexsMobsInteraction$registerGoals(GoalSelector instance, int pPriority, Goal pGoal) {
        if (pGoal instanceof PanicGoal || pGoal instanceof AvoidEntityGoal<?>) {
            return !AlexsMobsInteraction.COMMON_CONFIG.ORPHANED_ANACONDAS_ENABLED.get();
        } else {
            return true;
        }
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        float asmon = isGod() ? 1.15F : 1F;
        return this.isDancing() ? STAND_SIZE.scale(asmon) : super.getDimensions(poseIn).scale(asmon);
    }




}
