package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.crimsoncrips.alexsmobsinteraction.server.entity.EntityLeafcutterPupa;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.ItemLeafcutterPupa;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(ItemLeafcutterPupa.class)
public abstract class AMILeafcutterPupaItemMixin  extends Item{


    public AMILeafcutterPupaItemMixin(Properties pProperties) {
        super(pProperties);
    }

    int variant;

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (AlexsMobsInteraction.COMMON_CONFIG.THROWABLE_PUPI_ENABLED.get()){
            playerIn.gameEvent(GameEvent.ITEM_INTERACT_START);
            worldIn.playSound((Player) null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (playerIn.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!worldIn.isClientSide) {
                ThrowableItemProjectile eggentity;
                eggentity = new EntityLeafcutterPupa(worldIn, playerIn);
                eggentity.setItem(itemstack);
                eggentity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F, 1.0F);
                worldIn.addFreshEntity(eggentity);
            }

            playerIn.awardStat(Stats.ITEM_USED.get(this));
            if (!playerIn.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/UseOnContext;getLevel()Lnet/minecraft/world/level/Level;"))
    private void variableAddition(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        variant = context.getLevel().random.nextBoolean() ? 1 : 2;
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityLeafcutterAnt;setQueen(Z)V"))
    private void variable(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local EntityLeafcutterAnt beeentity){
        if (AlexsMobsInteraction.COMMON_CONFIG.LEAFCUTTER_VARIANTS_ENABLED.get()){
            ((AMIBaseInterfaces) beeentity).setVariant(variant);
        } else {
            ((AMIBaseInterfaces) beeentity).setVariant(1);
        }
    }

}