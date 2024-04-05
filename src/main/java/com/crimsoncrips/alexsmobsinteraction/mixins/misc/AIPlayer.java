package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.block.BlockBananaPeel;
import com.github.alexthe666.alexsmobs.entity.EntityBlobfish;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Player.class)
public abstract class AIPlayer extends LivingEntity implements IForgePlayer {



    @Shadow public abstract InteractionResult interactOn(Entity pEntityToInteractOn, InteractionHand pHand);

    protected AIPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public EntityBlobfish blobfish;
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        Player player = (Player)(Object)this;
        Block block = player.getFeetBlockState().getBlock();

        if(AInteractionConfig.bananaslip && AInteractionConfig.aprilfools){
            if (block instanceof BlockBananaPeel){
                player.kill();
            }
        }

    }

    @Override
    @Shadow
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    @Shadow
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return null;
    }

    @Override
    @Shadow
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

    @Override
    @Shadow
    public HumanoidArm getMainArm() {
        return null;
    }


    @Override
    public boolean alwaysAccepts() {
        return super.alwaysAccepts();
    }

    @Override
    public LivingEntity self() {
        return super.self();
    }
}
