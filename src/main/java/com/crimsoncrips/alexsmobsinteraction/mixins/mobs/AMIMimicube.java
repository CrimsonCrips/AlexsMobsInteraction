package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.github.alexthe666.alexsmobs.entity.EntityMimicube;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityMimicube.class)
public abstract class AMIMimicube extends Mob {


    protected AMIMimicube(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    public boolean hurt(DamageSource source, float amount) {
        Entity trueSource = source.getEntity();
        if (trueSource instanceof LivingEntity attacker && attacker.getItemBySlot(EquipmentSlot.HEAD).getEnchantmentLevel(AMIEnchantmentRegistry.MIMICLESS.get()) > 0) {
            if (!attacker.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                this.setItemSlot(EquipmentSlot.HEAD, this.mimicStack(attacker.getItemBySlot(EquipmentSlot.HEAD)));
            }

            if (!attacker.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
                this.setItemSlot(EquipmentSlot.OFFHAND, this.mimicStack(attacker.getItemBySlot(EquipmentSlot.OFFHAND)));
            }

            if (!attacker.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, this.mimicStack(attacker.getItemBySlot(EquipmentSlot.MAINHAND)));
            }
        }

        return super.hurt(source, amount);
    }

    private ItemStack mimicStack(ItemStack stack) {
        ItemStack copy = stack.copy();
        if (copy.isDamageableItem()) {
            copy.setDamageValue(copy.getMaxDamage());
        }

        return copy;
    }



}
