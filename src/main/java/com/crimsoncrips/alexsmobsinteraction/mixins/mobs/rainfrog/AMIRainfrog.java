package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.rainfrog;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityRainFrog.class)
public class AMIRainfrog extends Mob implements AMITransform {

    private static final EntityDataAccessor<Boolean> TRANFORMING = SynchedEntityData.defineId(EntityRainFrog.class, EntityDataSerializers.BOOLEAN);

    int frogWarped;


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(TRANFORMING, false);
    }


    protected AMIRainfrog(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (isTransforming()){
            frogWarped++;
            if (frogWarped > 160 && !this.level().isClientSide) {
                LivingEntity entityToSpawn;
                entityToSpawn = AMEntityRegistry.WARPED_TOAD.get().spawn((ServerLevel) this.level(), BlockPos.containing(this.getPosition(1)), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);
                    this.remove(RemovalReason.DISCARDED);
                }

            }
        }
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (AMInteractionConfig.FROG_TRANSFORM_ENABLED) {
            if (itemstack.getItem() == Items.WARPED_FUNGUS && this.hasEffect(MobEffects.WEAKNESS) ){
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                gameEvent(GameEvent.ENTITY_INTERACT);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                setTransforming(true);
                return InteractionResult.SUCCESS;
            }
        } return type;
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
        return this.entityData.get(TRANFORMING);
    }

    @Override
    public void setTransforming(boolean transformingBoolean) {
        this.entityData.set(TRANFORMING, transformingBoolean);
    }


    public void spawnGusters(){
        if (AMInteractionConfig.GOOFY_RAINFROG_SPAWNAGE_ENABLED && AMInteractionConfig.GOOFY_MODE_ENABLED) {
            for (int i = 0; i < 10; i++) {
                LivingEntity entityToSpawn;
                entityToSpawn = AMEntityRegistry.GUSTER.get().spawn((ServerLevel) this.level(), BlockPos.containing(this.getPosition(1)), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null && !this.level().isClientSide) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
    }
}
