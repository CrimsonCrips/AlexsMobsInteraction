package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(Player.class)
public abstract class AMIPlayerMixin extends LivingEntity {


    @Shadow protected abstract void hurtArmor(DamageSource pDamageSource, float pDamage);

    @Shadow public abstract Iterable<ItemStack> getArmorSlots();

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);

    protected AMIPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean canStandOnFluid(FluidState pFluidState) {
        if (AMInteractionConfig.ROLLING_THUNDER_ENABLED) {
            double z = this.getLookAngle().z;
            double x = this.getLookAngle().x;
            BlockState feetBlockstate = this.getBlockStateOn();
            if (RockyChestplateUtil.isRockyRolling(this)){
                if (feetBlockstate.is(Blocks.WATER)) {
                    double d1 = this.getRandom().nextGaussian() * 0.01;
                    if (random.nextDouble() < 0.1) this.level().addParticle(ParticleTypes.SPLASH, this.getRandomX(0.1), this.getY() + 0.5, this.getRandomZ(0.1), x * -2 * this.getRandom().nextInt(2), 0.1 + d1, z * -2 * this.getRandom().nextInt(2));
                    if (random.nextDouble() < 0.09) this.level().addParticle(ParticleTypes.BUBBLE, this.getRandomX(1.2), this.getY() + 0.5, this.getRandomZ(1.2), 0, 0, 0);
                }
                if (feetBlockstate.is(Blocks.LAVA)) {
                    this.setSecondsOnFire(5);
                    double d1 = this.getRandom().nextGaussian() * 0.01;
                    double d2 = this.getRandom().nextGaussian() * 0.01;
                    double d3 = this.getRandom().nextGaussian() * 0.01;
                    if (random.nextDouble() < 0.03) this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY() + 0.05, this.getZ(), x * -2 + d1, 0.1 + d2, z * -2 + d3);
                }
                if (random.nextDouble() < 0.001) this.getItemBySlot(EquipmentSlot.CHEST).hurtAndBreak(1, this, (p_233654_0_) -> {
                 });
            }
            return RockyChestplateUtil.isRockyRolling(this) && this.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(AMIEnchantmentRegistry.ROLLING_THUNDER.get()) > 0 && this.getItemBySlot(EquipmentSlot.CHEST).is((Item) AMItemRegistry.ROCKY_CHESTPLATE.get());
        } else return false;
    }
}