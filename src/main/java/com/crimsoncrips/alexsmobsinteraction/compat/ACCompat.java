package com.crimsoncrips.alexsmobsinteraction.compat;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.world.item.ItemStack;

public class ACCompat {

    public static boolean falconBomb(ItemStack itemStack){
        return itemStack.is(ACBlockRegistry.NUCLEAR_BOMB.get().asItem().getDefaultInstance().getItem());
    }
}
