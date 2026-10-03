package com.crimsoncrips.alexsmobsinteraction.server.item;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIToastManager;
import com.crimsoncrips.alexsmobsinteraction.server.entity.EntityFarseerPortal;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemAscender extends Item {

    private static final double REACH = 2.5D;
    private static final int COOLDOWN = AMIUtils.seconds(3);

    public ItemAscender(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (AlexsMobsInteraction.COMMON_CONFIG.ASCENDER_ENABLED.get()) {
            if (!(level instanceof ServerLevel serverLevel))
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            if(!player.isCreative()){
                stack.shrink(1);
            }

            Vec3 look = player.getLookAngle();
            Vec3 origin = player.position().add(look.x * REACH, 0.75D, look.z * REACH);
            EntityFarseerPortal.openPair(serverLevel, origin);
            player.getCooldowns().addCooldown(this, COOLDOWN);
        } else {
            AMIToastManager.addToast(Component.translatable("misc.alexsmobsinteraction.ascender_disabled"), 5 * 1000L);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
