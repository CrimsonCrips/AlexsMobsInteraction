package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.compat.ACCompat;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.IFalconry;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.message.MessageSyncEntityPos;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.minecraft.world.entity.EntityType.ITEM;
import static net.minecraft.world.entity.EntityType.TNT_MINECART;


@Mixin(EntityBaldEagle.class)
public abstract class AMIBaldEagle  extends TamableAnimal {


    @Shadow @Final private static EntityDataAccessor<Boolean> SITTING;

    @Shadow public abstract void setTackling(boolean tackling);

    @Shadow public abstract boolean isLaunched();

    @Shadow private boolean controlledFlag;

    @Shadow private int launchTime;

    boolean shiftBombed = false;

    protected AMIBaldEagle(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }



    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityBaldEagle baldEagle = (EntityBaldEagle)(Object)this;
        if(AMInteractionConfig.EAGLE_CANNIBALIZE_ENABLED){
            baldEagle.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(baldEagle, EntityBaldEagle.class, 1000, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
            }) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !baldEagle.isTame();
                }
            });
        }
    }

    @Inject(method = "shouldHoodedReturn", at = @At("HEAD"),remap = false, cancellable = true)
    private void alexsMobsInteraction$shouldHoodedReturn(CallbackInfoReturnable<Boolean> cir) {
        boolean normal = !this.isAlive() || this.isInsidePortal || launchTime > 12000 || this.portalTime > 0 || this.isRemoved();
        boolean bombing = !AMInteractionConfig.BIRD_BOMBING_ENABLED || !shiftBombed;

        if (this.getOwner() != null) {
            normal = !this.getOwner().isAlive() || (this.getOwner().isShiftKeyDown() && bombing && this.getPassengers().isEmpty());
        }
        cir.setReturnValue(normal);
    }

    @Inject(method = "directFromPlayer", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$directFromPlayer(float rotationYaw, float rotationPitch, boolean loadChunk, Entity over, CallbackInfo ci) {
        if (AMInteractionConfig.BIRD_BOMBING_ENABLED){
            if (over instanceof ItemEntity itemEntity) {
                boolean nuclearBomb = ModList.get().isLoaded("alexscaves") && ACCompat.falconBomb(itemEntity.getItem());
                if ((itemEntity.getItem().is(Items.TNT) || nuclearBomb) && (double) this.distanceTo(over) <= (double) over.getBbWidth() + (double) 4.0F && this.getPassengers().isEmpty()) {
                    this.setTackling(true);
                    if ((double) this.distanceTo(over) <= (double) over.getBbWidth() + (double) 2.0F) {
                        ItemEntity bomb = new ItemEntity(ITEM, over.level());
                        bomb.setItem(nuclearBomb ? ACBlockRegistry.NUCLEAR_BOMB.get().asItem().getDefaultInstance() : Items.TNT.getDefaultInstance());
                        bomb.level().addFreshEntity(bomb);
                        bomb.setPickUpDelay(500);
                        bomb.setInvulnerable(true);
                        bomb.startRiding(this);
                        itemEntity.discard();
                    }
                }
            }

            if (this.getOwner() instanceof Player player && !this.getPassengers().isEmpty() && player.isShiftKeyDown() && !shiftBombed){
                for(Entity entity : this.getPassengers()){
                    if(entity instanceof ItemEntity itemEntity){
                        Entity bomb = null;
                        if (ModList.get().isLoaded("alexscaves") && ACCompat.falconBomb(itemEntity.getItem())) {
                            bomb = new NuclearBombEntity(ACEntityRegistry.NUCLEAR_BOMB.get(), this.level());
                            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 9));
                        } else if (itemEntity.getItem().is(Items.TNT)) {
                            bomb = new PrimedTnt(EntityType.TNT, this.level());
                        }

                        if (bomb != null){
                            shiftBombed = true;
                            bomb.level().addFreshEntity(bomb);
                            bomb.setPos(this.position().subtract(0,-1.5,0));
                            itemEntity.discard();
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (this.isLaunched() && !this.controlledFlag && this.isTame() && !this.isPassenger() && this.isVehicle() && (this.getTarget() == null || !this.getTarget().isAlive())){
            this.ejectPassengers();
        }
        if ((!controlledFlag || this.getOwner() == null || !this.getOwner().isShiftKeyDown()) && shiftBombed){
            shiftBombed = false;
        }
    }

}
