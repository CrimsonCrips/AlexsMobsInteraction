package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.kangaroo;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityKangaroo.class)
public abstract class AMIKangaroo extends TamableAnimal {


    @Shadow @Final private static EntityDataAccessor<Integer> SWORD_INDEX;

    @Shadow public SimpleContainer kangarooInventory;

    @Shadow @Final private static EntityDataAccessor<Integer> HELMET_INDEX;

    @Shadow @Final private static EntityDataAccessor<Integer> CHEST_INDEX;

    @Shadow protected abstract void updateClientInventory();

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slotIn);

    private static final EntityDataAccessor<Integer> TOTEM_INDEX = SynchedEntityData.defineId(EntityKangaroo.class, EntityDataSerializers.INT);


    protected AMIKangaroo(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }



    @ModifyReturnValue(method = "getItemBySlot", at = @At("RETURN"))
    private ItemStack alexsMobsInteraction$getItemBySlot(ItemStack original,@Local EquipmentSlot slot) {
        if (AlexsMobsInteraction.COMMON_CONFIG.PROPER_ARMAMENTS_ENABLED.get()) {
            if (slot == EquipmentSlot.MAINHAND) {
                return getItemInHand(slot);
            } else if (slot == EquipmentSlot.OFFHAND) {
                return getItemInOffHand(slot);
            } else return getArmorInSlot(slot);
        }
        return original;
    }

    @Inject(method = "resetKangarooSlots", at = @At("TAIL"),remap = false)
    private void registerGoals(CallbackInfo ci) {
        if (!this.level().isClientSide && AlexsMobsInteraction.COMMON_CONFIG.PROPER_ARMAMENTS_ENABLED.get()) {
            int totemIndex = -1;
            for (int i = 0; i < this.kangarooInventory.getContainerSize(); ++i) {
                ItemStack stack = this.kangarooInventory.getItem(i);
                if (!stack.isEmpty()) {
                    if (stack.is(Items.TOTEM_OF_UNDYING)){
                        totemIndex = i;
                    }
                }
            }
            this.entityData.set(TOTEM_INDEX, totemIndex);
            updateClientInventory();
        }

    }


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void define(CallbackInfo ci) {
        this.entityData.define(TOTEM_INDEX, Integer.valueOf(-1));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void add(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("TotemIndex", this.entityData.get(TOTEM_INDEX));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compound, CallbackInfo ci) {
        this.entityData.set(TOTEM_INDEX, compound.getInt("TotemInvIndex"));
    }


    //Copies from AM
    private ItemStack getArmorInSlot(EquipmentSlot slot) {
        int helmIndex = entityData.get(HELMET_INDEX);
        int chestIndex = entityData.get(CHEST_INDEX);
        return slot == EquipmentSlot.HEAD && helmIndex >= 0 ? kangarooInventory.getItem(helmIndex) : slot == EquipmentSlot.CHEST && chestIndex >= 0 ? kangarooInventory.getItem(chestIndex) : ItemStack.EMPTY;
    }

    private ItemStack getItemInHand(EquipmentSlot slot) {
        int index = entityData.get(SWORD_INDEX);
        return slot == EquipmentSlot.MAINHAND && index >= 0 ? kangarooInventory.getItem(index) : ItemStack.EMPTY;
    }

    @Unique
    private ItemStack getItemInOffHand(EquipmentSlot slot) {
        int index = entityData.get(TOTEM_INDEX);
        return slot == EquipmentSlot.OFFHAND && index >= 0 ? kangarooInventory.getItem(index) : ItemStack.EMPTY;
    }


}
