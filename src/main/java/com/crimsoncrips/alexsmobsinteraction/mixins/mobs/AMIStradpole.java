package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;


@Mixin(EntityStradpole.class)
public abstract class AMIStradpole extends Mob {

    @Shadow @Final private static EntityDataAccessor<Boolean> LAUNCHED;
    private int despawnTimer = 0;

    @Shadow public abstract boolean isDespawnSoon();

    static{
        HOPUPTICK = SynchedEntityData.defineId(EntityStradpole.class, EntityDataSerializers.INT);
    }
    private static final EntityDataAccessor<Integer> HOPUPTICK;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(HOPUPTICK, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("HopUpTick", this.getHopUpTick());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setHopUpTick(compound.getInt("HopUpTick"));

    }

    public int getHopUpTick() {
        return (Integer)this.entityData.get(HOPUPTICK);
    }

    public void setHopUpTick(int hopUpTick) {
        this.entityData.set(HOPUPTICK, hopUpTick);
    }

    protected AMIStradpole(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    double y2;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (AMInteractionConfig.stradpolebobup) {
            setHopUpTick(getHopUpTick() + 1);
            if (getHopUpTick() >= 200 + random.nextInt(600) && this.isInLava()){
                y2 = 0.05 + y2;
                this.setDeltaMovement(0, y2, 0);
            }
            if (!this.isInLava()) {
                setHopUpTick(0);
                y2 = 0;
            }
        }
        if (AMInteractionConfig.straddlerexplosivespread && AMInteractionConfig.goofymode && isDespawnSoon()){
            int x = this.getBlockX();
            int y = this.getBlockY();
            int z = this.getBlockZ();
                ++this.despawnTimer;
                if (this.despawnTimer > 80) {
                    this.despawnTimer = 0;
                    this.spawnAnim();
                    this.level().explode(this, x + 1,y + 2,z + 1,3, Level.ExplosionInteraction.NONE);
                    this.remove(RemovalReason.DISCARDED);
                }
        }
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.is(DamageTypes.FALL);
    }


    @Inject(method = "onEntityHit", at = @At("HEAD"),cancellable = true,remap = false)
    private void entityhit(EntityHitResult raytraceresult, CallbackInfo ci) {
        ci.cancel();
        EntityStradpole stradpole = (EntityStradpole)(Object)this;
        Entity entity = stradpole.getParent();
        if (entity instanceof LivingEntity && !this.level().isClientSide) {
            Entity var4 = raytraceresult.getEntity();
            if (var4 instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)var4;
                if (random.nextDouble() < 0.2)target.setSecondsOnFire(2);
                if (!target.isBlocking()) {
                    target.hurt(this.damageSources().mobProjectile(this, (LivingEntity)entity), 3.0F);
                    target.knockback(0.699999988079071, entity.getX() - this.getX(), entity.getZ() - this.getZ());
                } else if (this.getTarget() instanceof Player) {
                    this.damageShieldFor((Player)this.getTarget(), 3.0F);
                }

                this.entityData.set(LAUNCHED, false);
            }
        }


    }

    protected void damageShieldFor(Player holder, float damage) {
        if (holder.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
            if (!this.level().isClientSide) {
                holder.awardStat(Stats.ITEM_USED.get(holder.getUseItem().getItem()));
            }

            if (damage >= 3.0F) {
                int i = 1 + Mth.floor(damage);
                InteractionHand hand = holder.getUsedItemHand();
                holder.getUseItem().hurtAndBreak(i, holder, (p_213833_1_) -> {
                    p_213833_1_.broadcastBreakEvent(hand);
                    ForgeEventFactory.onPlayerDestroyItem(holder, holder.getUseItem(), hand);
                });
                if (holder.getUseItem().isEmpty()) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        holder.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        holder.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    holder.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                }
            }
        }

    }




}
