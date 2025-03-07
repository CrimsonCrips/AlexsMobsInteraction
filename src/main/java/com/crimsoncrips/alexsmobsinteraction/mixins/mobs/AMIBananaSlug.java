package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(EntityBananaSlug.class)
public abstract class AMIBananaSlug extends Animal {


    protected AMIBananaSlug(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (AlexsMobsInteraction.COMMON_CONFIG.BANANA_SHEAR_ENABLED.get() && itemStack.getItem() instanceof ShearsItem && !pPlayer.level().isClientSide) {
            if (!pPlayer.isCreative()) {
                itemStack.hurtAndBreak(1, pPlayer, (p_233654_0_) -> {
                });
            }
            pPlayer.swing(pHand,true);
            AMIUtils.spawnLoot(AMILootTables.BANANA_SHEAR,this,pPlayer,0);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1, this.getVoicePitch());
            this.discard();
            AMIUtils.awardAdvancement(pPlayer,"banana_shear","banana");
        }

        return super.mobInteract(pPlayer, pHand);

    }
}
