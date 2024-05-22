package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.rainfrog;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMISkelewagInterface;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityRainFrog.class)
public class AMIRainfrog extends Mob implements AMITransform {

    int frogWarped;

    static {
        RAMINFROGSICK = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.BOOLEAN);
        MAGGOTFED = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.INT);
        MUNGUSFED = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.INT);
        WARPEDFED = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.INT);
    }

    private static final EntityDataAccessor<Boolean> RAMINFROGSICK;
    private static final EntityDataAccessor<Integer> MAGGOTFED;
    private static final EntityDataAccessor<Integer> MUNGUSFED;
    private static final EntityDataAccessor<Integer> WARPEDFED;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(RAMINFROGSICK, false);
        this.entityData.define(MAGGOTFED, 0);
        this.entityData.define(MUNGUSFED, 0);
        this.entityData.define(WARPEDFED, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("RainfrogSick", this.isTransforming());
        compound.putInt("MaggotFed", this.getMaggotFed());
        compound.putInt("MungusFed", this.getMungusFed());
        compound.putInt("WarpedFed", this.getWarpedFed());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setTransforming(compound.getBoolean("RainfrogSick"));
        this.setMaggotFed(compound.getInt("MaggotFed"));
        this.setMungusFed(compound.getInt("MungusFed"));
        this.setWarpedFed(compound.getInt("WarpedFed"));
    }

    public int getMaggotFed() {
        return (Integer)this.entityData.get(MAGGOTFED);
    }

    public void setMaggotFed(int maggot) {
        this.entityData.set(MAGGOTFED, maggot);
    }
    public int getMungusFed() {
        return (Integer)this.entityData.get(MUNGUSFED);
    }

    public void setMungusFed(int mungus) {
        this.entityData.set(MUNGUSFED, mungus);
    }
    public int getWarpedFed() {
        return (Integer)this.entityData.get(WARPEDFED);
    }

    public void setWarpedFed(int warped) {
        this.entityData.set(WARPEDFED, warped);
    }


    protected AMIRainfrog(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        EntityRainFrog frog = (EntityRainFrog)(Object)this;
        if (this.getMungusFed() >= 1 && this.getWarpedFed() >= 2 && this.getMaggotFed() >= 5) setTransforming(true);
        if (isTransforming()){
            frogWarped++;
            if (frogWarped > 160) {
                EntityWarpedToad warpedToad = AMEntityRegistry.WARPED_TOAD.get().create(level());
                warpedToad.copyPosition(this);
                if (!this.level().isClientSide) {
                    warpedToad.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }
                frog.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);

                if (!this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 79);
                    level().addFreshEntity(warpedToad);
                }
                frog.remove(RemovalReason.DISCARDED);

            }
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        {
            if (AMInteractionConfig.FROG_TRANSFORM_ENABLED){
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() == AMItemRegistry.MAGGOT.get() && !(getMaggotFed() >= 5)) {

                    gameEvent(GameEvent.ENTITY_INTERACT);
                    if (!player.isCreative()) stack.shrink(1);
                    this.playSound(SoundEvents.GENERIC_EAT);
                    this.setMaggotFed(this.getMaggotFed() + 1);

                }
                if (stack.getItem() == AMItemRegistry.MUNGAL_SPORES.get() && !(getMungusFed() >= 1)) {
                    gameEvent(GameEvent.ENTITY_INTERACT);
                    if (!player.isCreative()) stack.shrink(1);
                    this.playSound(SoundEvents.GENERIC_EAT);
                    this.setMungusFed(this.getMungusFed() + 1);

                }
                if (stack.getItem() == Items.WARPED_FUNGUS && !(this.getWarpedFed() >= 2)) {
                    gameEvent(GameEvent.ENTITY_INTERACT);
                    if (!player.isCreative()) stack.shrink(1);
                    this.playSound(SoundEvents.GENERIC_EAT);
                    this.setWarpedFed(this.getWarpedFed() + 1);

                }
            }
        }
    }

    @Inject(method = "changeWeather", at = @At("HEAD"),cancellable = true,remap = false)
    private void weatherChange(CallbackInfo ci) {
            ci.cancel();
            EntityRainFrog rainFrog = (EntityRainFrog) (Object) this;
            int time = 24000 + 1200 * this.random.nextInt(10);
            int type = 0;
            if (!this.level().isRaining()) {
                type = this.random.nextInt(1) + 1;
            }

            Level var4 = this.level();
            if (var4 instanceof ServerLevel serverLevel) {
                if (type == 0) {
                    serverLevel.setWeatherParameters(time, 0, false, false);
                    spawnGusters();
                } else {
                    serverLevel.setWeatherParameters(0, time, true, type == 2);
                    BlockPos pos = BlockPos.containing(this.getPosition(1));
                    spawnGusters();
                }
            }
            ReflectionUtil.setField(rainFrog, "weatherCooldown", time + 24000);
    }


        @Override
    public boolean isTransforming() {
        return this.entityData.get(RAMINFROGSICK);
    }

    @Override
    public void setTransforming(boolean transforming) {
        this.entityData.set(RAMINFROGSICK, transforming);
    }

    public void spawnGusters(){
        if (AMInteractionConfig.RAINFROG_SPAWNAGE_ENABLED && AMInteractionConfig.GOOFY_MODE_ENABLED) {
            for (int i = 0; i < 10; i++) {
                EntityGuster guster = AMEntityRegistry.GUSTER.get().create(level());
                guster.copyPosition(this);
                level().addFreshEntity(guster);
            }
        }
    }
}
