package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla.player;

import net.minecraft.world.entity.EquipmentSlot;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(Player.class)
public abstract class AMIPlayerMixin extends LivingEntity {

    protected AMIPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean canStandOnFluid(FluidState pFluidState) {
        ItemStack chestItem = this.getItemBySlot(EquipmentSlot.CHEST);
        if (AlexsMobsInteraction.COMMON_CONFIG.ROLLING_THUNDER_ENABLED.get() && chestItem.is(AMItemRegistry.ROCKY_CHESTPLATE.get()) && AMIEnchantmentRegistry.getLevel(level(), chestItem, AMIEnchantmentRegistry.ROLLING_THUNDER) > 0) {
            BlockState blockState = getBlockStateOn();
            double z = this.getLookAngle().z;
            double x = this.getLookAngle().x;
            if (RockyChestplateUtil.isRockyRolling(this) && this.level().getFluidState(getOnPos()).getFluidType() != null){
                double d1 = this.getRandom().nextGaussian() * 0.01;
                ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                if (random.nextDouble() < 0.1) this.level().addParticle(particle, this.getRandomX(0.1), this.getY() + 0.5, this.getRandomZ(0.1), x * -2 * this.getRandom().nextInt(2), 0.1 + d1, z * -2 * this.getRandom().nextInt(2));
                if (random.nextDouble() < 0.001) {
                    chestItem.hurtAndBreak(2, this, EquipmentSlot.CHEST);
                }
                AMIUtils.awardAdvancement(this,"rolling_thunder","roll");
                return true;
            } else return false;
        } else return false;
    }

    @ModifyReturnValue(method = "isInvulnerableTo", at = @At("RETURN"))
    private boolean alexsMobsInteraction$isInvulnerableTo(boolean original,@Local DamageSource pSource) {
        return original || pSource.is(DamageTypes.FELL_OUT_OF_WORLD) && this.getVehicle() instanceof EntityEndergrade && AlexsMobsInteraction.COMMON_CONFIG.UNAVOIDABLE_ENABLED.get();
    }
}
