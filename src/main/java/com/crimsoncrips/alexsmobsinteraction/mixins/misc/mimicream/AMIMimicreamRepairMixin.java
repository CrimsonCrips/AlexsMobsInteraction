package com.crimsoncrips.alexsmobsinteraction.mixins.misc.mimicream;

import com.crimsoncrips.alexsmobsinteraction.server.item.AMIDataComponents;
import com.github.alexthe666.alexsmobs.misc.RecipeMimicreamRepair;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeMimicreamRepair.class)
public abstract class AMIMimicreamRepairMixin {

    @ModifyReturnValue(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"))
    private boolean alexsMobsInteraction$matches(boolean original, CraftingInput input) {
        if (!original)
            return false;
        for (int i = 0; i < input.size(); i++) {
            if (input.getItem(i).has(AMIDataComponents.MIMICKED))
                return false;
        }
        return true;
    }

    @ModifyReturnValue(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private ItemStack alexsMobsInteraction$assemble(ItemStack original) {
        if (!original.isEmpty()) {
            original.set(AMIDataComponents.MIMICKED, Unit.INSTANCE);
            if (original.isDamageableItem()) {
                original.set(DataComponents.MAX_DAMAGE, Math.max(1, original.getMaxDamage() / 2));
                original.setDamageValue(0);
            }
        }
        return original;
    }
}
