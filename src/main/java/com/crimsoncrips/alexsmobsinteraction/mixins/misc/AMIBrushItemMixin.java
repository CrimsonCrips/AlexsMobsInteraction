package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
public abstract class AMIBrushItemMixin {

    private static final int GRIZZLY_BRUSH_TIME = AMIUtils.seconds(2);

    @Shadow
    private HitResult calculateHitResult(Player player) {
        throw new AssertionError();
    }

    @Shadow
    public abstract int getUseDuration(ItemStack stack, LivingEntity entity);

    @Inject(method = "onUseTick", at = @At("HEAD"), cancellable = true)
    private void alexsMobsInteraction$onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration, CallbackInfo ci) {
        if (remainingUseDuration < 0 || !(livingEntity instanceof Player player))
            return;
        if (!(calculateHitResult(player) instanceof EntityHitResult entityHit) || !(entityHit.getEntity() instanceof EntityGrizzlyBear grizzlyBear))
            return;
        ci.cancel();
        if (!AMIUtils.canBrushGrizzly(grizzlyBear, player)) {
            player.releaseUsingItem();
            return;
        }
        int elapsed = getUseDuration(stack, player) - remainingUseDuration + 1;
        if (elapsed % 10 == 5) {
            level.playSound(player, grizzlyBear.blockPosition(), SoundEvents.BRUSH_GENERIC, SoundSource.PLAYERS);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(AMItemRegistry.BEAR_FUR.get())), entityHit.getLocation().x, entityHit.getLocation().y, entityHit.getLocation().z, 4, 0.1, 0.1, 0.1, 0.05);
            }
        }
        if (elapsed < GRIZZLY_BRUSH_TIME)
            return;
        if (!level.isClientSide) {
            if (!player.isCreative()) {
                stack.hurtAndBreak(15, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
            }
            AMIUtils.spawnLoot(AMILootTables.GRIZZLY_BRUSH, grizzlyBear, player, 0);
            grizzlyBear.playSound(SoundEvents.BRUSH_GENERIC, 1, grizzlyBear.getVoicePitch());
            grizzlyBear.gameEvent(GameEvent.SHEAR, player);
            AMIUtils.awardAdvancement(player, "brushed", "brushed");
        }
        player.releaseUsingItem();
    }
}
