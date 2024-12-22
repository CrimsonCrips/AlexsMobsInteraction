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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nonnull;


@Mixin(EntityStradpole.class)
public abstract class AMIStradpole extends Mob {

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
        if (AMInteractionConfig.STRADPOLE_BOB_UP_ENABLED) {
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
        if (AMInteractionConfig.GOOFY_STRADDLER_SHOTGUN_ENABLED  && isDespawnSoon()){
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


    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),remap = false,locals = LocalCapture.CAPTURE_FAILSOFT)
    private void entityhit(EntityHitResult raytraceresult, CallbackInfo ci, Entity entity, LivingEntity target, Entity var4) {
        if(AMInteractionConfig.STRADPOLE_FLAME_ENABLED && random.nextDouble() < 0.2){
            target.setSecondsOnFire(2);
        }
    }




}
