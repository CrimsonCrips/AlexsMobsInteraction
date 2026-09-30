package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.kangaroo;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
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


@Mixin(EntityKangaroo.class)
public abstract class AMIKangaroo extends TamableAnimal {


    @Shadow @Final private static EntityDataAccessor<Integer> SWORD_INDEX;

    @Shadow public SimpleContainer kangarooInventory;

    @Shadow @Final private static EntityDataAccessor<Integer> HELMET_INDEX;

    @Shadow @Final private static EntityDataAccessor<Integer> CHEST_INDEX;

    @Shadow protected abstract void updateClientInventory();

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slotIn);


    protected AMIKangaroo(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }



    @ModifyReturnValue(method = "getItemBySlot", at = @At("RETURN"))
    private ItemStack alexsMobsInteraction$getItemBySlot(ItemStack original,@Local EquipmentSlot slot) {
        if (AlexsMobsInteraction.COMMON_CONFIG.ARMAMENTS_ENABLED.get()) {
            if (slot == EquipmentSlot.MAINHAND) {
                return getItemInHand(slot);
            } else if (slot == EquipmentSlot.OFFHAND) {
                return getItemInOffHand(slot);
            } else return getArmorInSlot(slot);
        }
        return original;
    }

    @Inject(method = "resetKangarooSlots", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$resetKangarooSlots(CallbackInfo ci) {
        if (!this.level().isClientSide && AlexsMobsInteraction.COMMON_CONFIG.ARMAMENTS_ENABLED.get()) {
            int totemIndex = -1;
            for (int i = 0; i < this.kangarooInventory.getContainerSize(); ++i) {
                ItemStack stack = this.kangarooInventory.getItem(i);
                if (!stack.isEmpty()) {
                    if (stack.is(Items.TOTEM_OF_UNDYING)){
                        totemIndex = i;
                    }
                }
            }
            this.setData(AMIAttachments.TOTEM_INDEX, totemIndex);
            updateClientInventory();
        }

    }





    @WrapWithCondition(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityKangaroo;damageItem(Lnet/minecraft/world/item/ItemStack;)V"))
    private boolean alexsMobsInteraction$doHurtTarget(EntityKangaroo instance, ItemStack stack) {
        return !AlexsMobsInteraction.COMMON_CONFIG.ARMAMENTS_ENABLED.get() || stack.isDamageableItem();
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
        int index = this.getData(AMIAttachments.TOTEM_INDEX);
        return slot == EquipmentSlot.OFFHAND && index >= 0 ? kangarooInventory.getItem(index) : ItemStack.EMPTY;
    }


}
