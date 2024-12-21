package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.entity.EntityLeafcutterPupa;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMIVariant;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.IDancingMob;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemLeafcutterPupa;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
        if (AMInteractionConfig.LEAFCUTTER_THROWABLE_ENABLED){
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

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/UseOnContext;getLevel()Lnet/minecraft/world/level/Level;"),remap = false,locals = LocalCapture.CAPTURE_FAILHARD)
    private void variableAddition(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        variant = context.getLevel().random.nextBoolean() ? 1 : 2;
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityLeafcutterAnt;setQueen(Z)V"),remap = false,locals = LocalCapture.CAPTURE_FAILHARD)
    private void variable(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, Level world, BlockPos blockpos, BlockState blockstate, Player playerentity, BlockEntity tileentity, TileEntityLeafcutterAnthill beehivetileentity, int j, int k, EntityLeafcutterAnt beeentity){
        if (AMInteractionConfig.LEAFCUTTER_VARIANTS_ENABLED){
            ((AMIVariant) beeentity).setVariant(variant);
        } else {
            ((AMIVariant) beeentity).setVariant(1);
        }
    }

}