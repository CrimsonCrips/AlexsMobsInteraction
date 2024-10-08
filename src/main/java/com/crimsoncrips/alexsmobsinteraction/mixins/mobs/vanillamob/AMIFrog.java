package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.vanillamob;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Frog.class)
public class AMIFrog extends Mob implements AMITransform {

    private static final EntityDataAccessor<Boolean> TRANFORMING = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.BOOLEAN);

    int frogWarped;


    protected AMIFrog(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(TRANFORMING, false);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
            if (isTransforming()){
                frogWarped++;
                if (frogWarped > 160) {
                    EntityWarpedToad warpedToad = AMEntityRegistry.WARPED_TOAD.get().create(level());
                    if(warpedToad != null) {
                        warpedToad.copyPosition(this);

                        if (!this.level().isClientSide) {
                            warpedToad.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                        }
                        this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);

                        if (!this.level().isClientSide) {
                            this.level().broadcastEntityEvent(this, (byte) 79);
                            level().addFreshEntity(warpedToad);
                        }
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

    @Override
    public boolean isTransforming() {
        return this.entityData.get(TRANFORMING);
    }

    @Override
    public void setTransforming(boolean transformingBoolean) {
        this.entityData.set(TRANFORMING, transformingBoolean);
    }


}
