package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.bald_eagle;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.ACCompat;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.entity.EntityType.ITEM;


@Mixin(EntityBaldEagle.class)
public abstract class AMIBaldEagle  extends TamableAnimal {

    

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
        if(AlexsMobsInteraction.COMMON_CONFIG.CANNIBALIZATION_ENABLED.get()){
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


    @ModifyReturnValue(method = "shouldHoodedReturn", at = @At("RETURN"),remap = false)
    private boolean alexsMobsInteraction$shouldShoot(boolean original) {
        if (AlexsMobsInteraction.COMMON_CONFIG.BIRD_BOMBING_ENABLED.get()){
            boolean normal = !this.isAlive() || this.isInsidePortal || launchTime > 12000 || this.portalTime > 0 || this.isRemoved();
            ItemStack itemStack = getItemInHand(InteractionHand.MAIN_HAND);
            boolean nuclearBomb = ModList.get().isLoaded("alexscaves") && ACCompat.falconBomb(itemStack);

            if (this.getOwner() != null) {
                normal = this.isDeadOrDying() || this.getOwner().isDeadOrDying() || (this.getOwner().isShiftKeyDown() && !shiftBombed && !itemStack.is(Items.TNT) && !nuclearBomb);
            }
            return normal;
        }
        return original;
    }

    @Inject(method = "directFromPlayer", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$directFromPlayer(float rotationYaw, float rotationPitch, boolean loadChunk, Entity over, CallbackInfo ci) {
        if (AlexsMobsInteraction.COMMON_CONFIG.BIRD_BOMBING_ENABLED.get()){
            if (over instanceof ItemEntity itemEntity) {
                boolean nuclearBomb = ModList.get().isLoaded("alexscaves") && ACCompat.falconBomb(itemEntity.getItem());
                if ((itemEntity.getItem().is(Items.TNT) || nuclearBomb) && (double) this.distanceTo(over) <= (double) over.getBbWidth() + (double) 4.0F && this.getPassengers().isEmpty()) {
                    this.setTackling(true);
                    if ((double) this.distanceTo(over) <= (double) over.getBbWidth() + (double) 2.0F) {
                        this.setItemInHand(InteractionHand.MAIN_HAND,itemEntity.getItem());
                        setTackling(false);
                        itemEntity.discard();
                    }
                }
            }

            if (this.getOwner() instanceof Player player && player.isShiftKeyDown() && !shiftBombed){
                ItemStack itemStack = getItemInHand(InteractionHand.MAIN_HAND);
                boolean nuclearBomb = ModList.get().isLoaded("alexscaves") && ACCompat.falconBomb(itemStack);

                Entity bomb = null;
                if (nuclearBomb && this.level() instanceof ServerLevel serverLevel) {
                    bomb = ACCompat.nuke(serverLevel);
                } else if (itemStack.is(Items.TNT)) {
                    bomb = new PrimedTnt(EntityType.TNT, this.level());
                }

                if (bomb != null){
                    shiftBombed = true;
                    bomb.level().addFreshEntity(bomb);
                    bomb.setPos(this.position().subtract(0,-1.5,0));
                    setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
                    AMIUtils.awardAdvancement(player,"bird_bomb","bomb");
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if (this.isLaunched() && !this.controlledFlag && this.isTame() && !this.isPassenger() && this.isVehicle() && (this.getTarget() == null || !this.getTarget().isAlive())){
            this.ejectPassengers();
        }
        if ((!controlledFlag || this.getOwner() == null || !this.getOwner().isShiftKeyDown()) && shiftBombed){
            shiftBombed = false;
        }
    }

}
