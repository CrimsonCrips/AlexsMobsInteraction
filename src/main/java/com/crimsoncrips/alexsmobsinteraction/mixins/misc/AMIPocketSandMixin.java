package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntitySandShot;
import com.github.alexthe666.alexsmobs.item.ItemPocketSand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;


@Mixin(ItemPocketSand.class)
public class AMIPocketSandMixin extends Item {

    @Shadow @Final public static Predicate<ItemStack> IS_SAND;

    public AMIPocketSandMixin(Item.Properties properties) {
        super(properties);
    }

    public ItemStack findAmmo(Player entity) {
        if (entity.isCreative()) {
            return ItemStack.EMPTY;
        } else {
            for(int i = 0; i < entity.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack1 = entity.getInventory().getItem(i);
                if (this.IS_SAND.test(itemstack1)) {
                    return itemstack1;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player livingEntityIn, InteractionHand handIn) {
        ItemStack itemstack = livingEntityIn.getItemInHand(handIn);
        ItemStack ammo = this.findAmmo(livingEntityIn);
        if (livingEntityIn.isCreative()) {
            ammo = new ItemStack(Items.SAND);
        }

        if (!worldIn.isClientSide && (!ammo.isEmpty() || AMInteractionConfig.BOTTOMLESS_SAND_ENABLED)) {
            livingEntityIn.gameEvent(GameEvent.ITEM_INTERACT_START);
            worldIn.playSound((Player)null, livingEntityIn.getX(), livingEntityIn.getY(), livingEntityIn.getZ(), SoundEvents.SAND_BREAK, SoundSource.PLAYERS, 0.5F, 0.4F + livingEntityIn.getRandom().nextFloat() * 0.4F + 0.8F);
            boolean left = false;
            if (livingEntityIn.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntityIn.getMainArm() == HumanoidArm.RIGHT || livingEntityIn.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntityIn.getMainArm() == HumanoidArm.LEFT) {
                left = true;
            }

            EntitySandShot blood = new EntitySandShot(worldIn, livingEntityIn, !left);
            Vec3 vector3d = livingEntityIn.getViewVector(1.0F);
            blood.shoot(vector3d.x(), vector3d.y(), vector3d.z(), 1.2F, 11.0F);
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(blood);
            }

            livingEntityIn.getCooldowns().addCooldown(this, 2);
            if (!AMInteractionConfig.BOTTOMLESS_SAND_ENABLED) ammo.shrink(1);
            itemstack.hurtAndBreak(1, livingEntityIn, (player) -> {
                player.broadcastBreakEvent(livingEntityIn.getUsedItemHand());
            });
        }

        livingEntityIn.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
    }
}
