package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFollowNearestGoal;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.TransformingEntities;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;


@Mixin(EntityFly.class)
public class AMIFly extends Mob implements TransformingEntities {

    private static final EntityDataAccessor<Boolean> TRANFORMING = SynchedEntityData.defineId(EntityFly.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(TRANFORMING, false);
    }

    int flyConvert;

    protected AMIFly(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemstack = player.getItemInHand(hand);
        if (AlexsMobsInteraction.COMMON_CONFIG.FLY_TRANSFORM_ENABLED.get()) {
            if (itemstack.getItem() == AMItemRegistry.BLOOD_SAC.get() && this.hasEffect(AMIEffects.BLOODED.get())){
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                gameEvent(GameEvent.ENTITY_INTERACT);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                setTransforming(true);
            }
        }

    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void Test(CallbackInfo ci) {
        if (isTransforming()) {
            flyConvert++;

            if (flyConvert > 160 && !this.level().isClientSide) {
                LivingEntity entityToSpawn;
                entityToSpawn = AMEntityRegistry.CRIMSON_MOSQUITO.get().spawn((ServerLevel) this.level(), BlockPos.containing(this.getPosition(1)), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof EntityCrimsonMosquito crimsonMosquito) {
                    crimsonMosquito.onSpawnFromFly();
                    this.remove(RemovalReason.DISCARDED);
                }

            }
        }

    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityFly fly = (EntityFly)(Object)this;

        Predicate<LivingEntity> PESTERTARGET = AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.PESTER_ENTITY);
        if(AlexsMobsInteraction.COMMON_CONFIG.CANDLE_REPEL_ENABLED.get()){
            fly.goalSelector.addGoal(3, new AvoidBlockGoal(fly, 4, 1.8, 2.3, (pos) -> {
                BlockState state = fly.level().getBlockState(pos);
                if (state.is(BlockTags.CANDLES)){
                    return state.getValue(CandleBlock.LIT);
                } else return false;
            }));
        }
        if(AlexsMobsInteraction.COMMON_CONFIG.FLY_PESTER_ENABLED.get()) {
            fly.goalSelector.addGoal(8, new AMIFollowNearestGoal<>(fly, LivingEntity.class, 1, 0.8, PESTERTARGET));
        }
        fly.goalSelector.addGoal(2, new MoveToBlockGoal(fly,2,15) {
            @Override
            protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
                BlockState blockState = levelReader.getBlockState(blockPos);
                return blockState.is(ACBlockRegistry.GUANO_LAYER.get()) || blockState.is(ACBlockRegistry.GUANO_BLOCK.get());
            }
        });
    }




    public boolean isTransforming() {
        return this.entityData.get(TRANFORMING);
    }

    public void setTransforming(boolean transformingBoolean) {
        this.entityData.set(TRANFORMING, transformingBoolean);
    }


}
