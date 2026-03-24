package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.TransformingEntities;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Frog.class)
public class AMIFrog extends Mob implements TransformingEntities {

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
        if (AlexsMobsInteraction.COMMON_CONFIG.FROG_TRANSFORM_ENABLED.get()) {
            if (itemstack.getItem() == Items.WARPED_FUNGUS && this.hasEffect(MobEffects.WEAKNESS) ){
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                AMIUtils.awardAdvancement(player,"mutate_frog","mutate");
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
