package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla.player;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIFarseerEffects;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.crimsoncrips.alexsmobsinteraction.networking.FarseerPacket;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
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
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Player.class)
public abstract class AMIPlayerMixin extends LivingEntity implements AMIFarseerEffects {




    protected AMIPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean canStandOnFluid(FluidState pFluidState) {
        if (AlexsMobsInteraction.COMMON_CONFIG.ROLLING_THUNDER_ENABLED.get() && this.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.ROCKY_CHESTPLATE.get())) {
            BlockState blockState = getBlockStateOn();
            double z = this.getLookAngle().z;
            double x = this.getLookAngle().x;
            if (RockyChestplateUtil.isRockyRolling(this) && this.level().getFluidState(getOnPos()).getFluidType() != null){
                double d1 = this.getRandom().nextGaussian() * 0.01;
                ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                if (random.nextDouble() < 0.1) this.level().addParticle(particle, this.getRandomX(0.1), this.getY() + 0.5, this.getRandomZ(0.1), x * -2 * this.getRandom().nextInt(2), 0.1 + d1, z * -2 * this.getRandom().nextInt(2));
                if (random.nextDouble() < 0.001) {
                    this.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak(2, this, (p_233654_0_) -> {
                    });
                }
                AMIUtils.awardAdvancement(this,"rolling_thunder","roll");
                return this.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(AMIEnchantmentRegistry.ROLLING_THUNDER.get()) > 0;
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
        if (AlexsMobsInteraction.COMMON_CONFIG.FARSEER_ALTERING_ENABLED.get() && !(getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AMIEnchantmentRegistry.STABILIZER.get()) > 0) && getFarseerTime() > 0 && !player.level().isClientSide) {
            Inventory inv = player.getInventory();

            if (random.nextDouble() < 0.05){
                for (int i = 0; i < 9 - 1; i++) {
                    ItemStack current = inv.getItem(i);
                    int j = this.getRandom().nextInt(i + 1, 9);
                    ItemStack to = inv.getItem(j);
                    inv.setItem(j, current);
                    inv.setItem(i, to);
                }
            }

            if (getFarseerTime() == 99) {
                AMIPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new FarseerPacket());
                addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
            }
        } else if(getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AMIEnchantmentRegistry.STABILIZER.get()) > 0) {
            AMIUtils.awardAdvancement(player,"repel","repel");
        }
        if (AlexsMobsInteraction.COMMON_CONFIG.SNAPPING_DORMANCY_ENABLED.get()){
            Entity lookAt = AMIUtils.getClosestLookingAtEntityFor(player);
            if (lookAt instanceof EntityAlligatorSnappingTurtle snappingTurtle && ((AMIBaseInterfaces)snappingTurtle).isDaySleeping() && player.getUseItem().getItem() instanceof SpyglassItem){
                AMIUtils.awardAdvancement(player,"observe_dormancy","observe");
            }
        }

        if (getFarseerTime() > 0){
            setFarseerTime(getFarseerTime() - 1);
        }
    }


    private static final EntityDataAccessor<Integer> FARSEER_TIME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(FARSEER_TIME, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("FarseerTime", this.getFarseerTime());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setFarseerTime(compound.getInt("FarseerTime"));
    }

    @Override
    public int getFarseerTime() {
        return this.entityData.get(FARSEER_TIME);
    }

    @Override
    public void setFarseerTime(int time) {
        this.entityData.set(FARSEER_TIME, time);
    }
}