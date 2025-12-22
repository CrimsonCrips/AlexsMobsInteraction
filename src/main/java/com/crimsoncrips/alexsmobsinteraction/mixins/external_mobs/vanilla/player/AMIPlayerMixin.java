package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla.player;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.FarseerFx;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.crimsoncrips.alexsmobsinteraction.networking.AlterPacket;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.client.event.ClientEvents;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.alexthe666.alexsmobs.client.event.ClientEvents.renderStaticScreenFor;


@Mixin(Player.class)
public abstract class AMIPlayerMixin extends LivingEntity implements FarseerFx {

    protected AMIPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean canStandOnFluid(FluidState pFluidState) {
        ItemStack chestItem = this.getItemBySlot(EquipmentSlot.CHEST);
        if (AlexsMobsInteraction.COMMON_CONFIG.ROLLING_THUNDER_ENABLED.get() && chestItem.is(AMItemRegistry.ROCKY_CHESTPLATE.get()) && chestItem.getEnchantmentLevel(AMIEnchantmentRegistry.ROLLING_THUNDER.get()) > 0) {
            BlockState blockState = getBlockStateOn();
            double z = this.getLookAngle().z;
            double x = this.getLookAngle().x;
            if (RockyChestplateUtil.isRockyRolling(this) && this.level().getFluidState(getOnPos()).getFluidType() != null){
                double d1 = this.getRandom().nextGaussian() * 0.01;
                ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                if (random.nextDouble() < 0.1) this.level().addParticle(particle, this.getRandomX(0.1), this.getY() + 0.5, this.getRandomZ(0.1), x * -2 * this.getRandom().nextInt(2), 0.1 + d1, z * -2 * this.getRandom().nextInt(2));
                if (random.nextDouble() < 0.001) {
                    chestItem.hurtAndBreak(2, this, (p_233654_0_) -> {
                    });
                }
                AMIUtils.awardAdvancement(this,"rolling_thunder","roll");
                return true;
            } else return false;
        } else return false;
    }

    @ModifyReturnValue(method = "isInvulnerableTo", at = @At("RETURN"))
    private boolean alexsMobsInteraction$isInvulnerableTo(boolean original,@Local DamageSource pSource) {
        return original || pSource.is(DamageTypes.FELL_OUT_OF_WORLD) && this.getVehicle() instanceof EntityEndergrade && AlexsMobsInteraction.COMMON_CONFIG.VOIDED_ENDERGRADE_ENABLED.get();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        if (AlexsMobsInteraction.COMMON_CONFIG.FARSEER_ALTERING_ENABLED.get()) {
            if (!(getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AMIEnchantmentRegistry.STABILIZER.get()) > 0) && getStalkTime() >= 1.5 && !level().isClientSide && getAlterTime() <= 0) {
                setStalkDelay(-500);
                setAlterTime(100);
                AMIPacketHandler.FARSEER_ALTER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new AlterPacket());
                addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                AMIUtils.awardAdvancement(player,"alterred","alter");
            }

            if (getAlterTime() > 0){
                if (random.nextDouble() < 0.05 && !level().isClientSide) {
                    //Thanks ItemmStack for the help
                    for (int i = 0; i < 9; i++) {
                        Inventory inv = player.getInventory();
                        int j = this.getRandom().nextInt(0, 9);

                        ItemStack a = inv.getItem(i).copy();
                        ItemStack b = inv.getItem(j).copy();
                        inv.setItem(j, a);
                        inv.setItem(i, b);
                    }
                }
                setAlterTime(getAlterTime() - 1);
            }
        }


        if (getStalkDelay() == 0) {
            if (getStalkTime() > 0){
                setStalkTime(getStalkTime() - 0.01F);
            } else {
                setStalkTime(0);
            }
        } else {
            if (getStalkDelay() < 0){
                setStalkDelay(getStalkDelay() + 1);
                if (getStalkTime() > 0 && getAlterTime() <= 0) {
                    setStalkTime(getStalkTime() - 0.05F);
                } else if (getAlterTime() <= 0) {
                    setStalkTime(0);
                }
            } else {
                setStalkDelay(getStalkDelay() - 1);
            }
        }


        if (AlexsMobsInteraction.COMMON_CONFIG.SNAPPING_DORMANCY_ENABLED.get()){
            if (player.getUseItem().getItem() instanceof SpyglassItem) {
                Entity lookAt = AMIUtils.getClosestLookingAtEntityFor(player);
                if (lookAt instanceof EntityAlligatorSnappingTurtle snappingTurtle && ((AMIBaseInterfaces) snappingTurtle).isDaySleeping()) {
                    AMIUtils.awardAdvancement(player, "observe_dormancy", "observe");
                }
            }
        }
    }


    private static final EntityDataAccessor<Integer> FARSEER_ALTERRING_TIME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> FARSEER_STALKING_TIME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> FARSEER_STALKING_DELAY = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(FARSEER_ALTERRING_TIME, 0);
        this.entityData.define(FARSEER_STALKING_TIME, 0F);
        this.entityData.define(FARSEER_STALKING_DELAY, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("FarseerAlterringTime", this.getAlterTime());
        compound.putFloat("FarseerStalkingTime", this.getStalkTime());
        compound.putInt("FarseerStalkingDelay", this.getStalkDelay());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setAlterTime(compound.getInt("FarseerAlterringTime"));
        this.setStalkTime(compound.getInt("FarseerStalkingTime"));
        this.setStalkDelay(compound.getInt("FarseerStalkingDelay"));
    }

    @Override
    public int getStalkDelay() {
        return this.entityData.get(FARSEER_STALKING_DELAY);
    }

    @Override
    public void setStalkDelay(int time) {
        this.entityData.set(FARSEER_STALKING_DELAY, time);
    }

    @Override
    public float getStalkTime() {
        return this.entityData.get(FARSEER_STALKING_TIME);
    }

    @Override
    public void setStalkTime(float time) {
        this.entityData.set(FARSEER_STALKING_TIME, time);
    }

    @Override
    public int getAlterTime() {
        return this.entityData.get(FARSEER_ALTERRING_TIME);
    }

    @Override
    public void setAlterTime(int time) {
        this.entityData.set(FARSEER_ALTERRING_TIME, time);
    }
}