package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.block.BlockBananaPeel;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Player.class)
public abstract class AMIPlayer extends LivingEntity {

    public boolean canRollInFluid = false;

    int rollingtime = 0;

    protected AMIPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        Block block = getFeetBlockState().getBlock();

        if(AMInteractionConfig.bananaslip && AMInteractionConfig.goofymode){
            if (block instanceof BlockBananaPeel){
                kill();
            }
        }

        if(AMInteractionConfig.sunbirdupgrade){

            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(7, 4, 7))) {
                EntityType<?> entityType = entity.getType();
                if (entityType.is(AMInteractionTagRegistry.BURNABLE_DEAD) && !entity.isInWater()) {
                    if (hasEffect(AMEffectRegistry.SUNBIRD_BLESSING.get())) {
                        entity.setSecondsOnFire(3);
                    }
                    if (hasEffect(AMEffectRegistry.SUNBIRD_CURSE.get())) {
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 350, 2));
                        entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0));
                    }
                }
            }
        }



        if (AMInteractionConfig.tigersstealth && hasEffect(AMEffectRegistry.TIGERS_BLESSING.get()) && isCrouching()){
            addEffect(new MobEffectInstance(MobEffects. INVISIBILITY, 20, 0));
        }

        double z = this.getLookAngle().z;
        double x = this.getLookAngle().x;


        if(AMInteractionConfig.rollingthunder){
            if (!(rollingtime <= 0) && this.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(AMIEnchantmentRegistry.ROLLING_THUNDER.get()) > 0 && this.getItemBySlot(EquipmentSlot.CHEST).is((Item) AMItemRegistry.ROCKY_CHESTPLATE.get())) {
                this.setGlowingTag(true);
                if (isRolling()) {
                    if (!isInLiquid()) {
                        canRollInFluid = true;
                    }

                    if (this.getFeetBlockState().is(Blocks.WATER)) {
                        rollingtime--;
                        double d1 = this.random.nextGaussian() * 0.02;
                        this.level().addParticle(ParticleTypes.SPLASH, this.getRandomX(0.1), this.getY() + 0.5, this.getRandomZ(0.1), x * -2 * random.nextInt(2), 0.1 + d1, z * -2 * random.nextInt(2));
                        this.level().addParticle(ParticleTypes.SPLASH, this.getRandomX(0.1), this.getY() + 0.5, this.getRandomZ(0.1), x * -2 * random.nextInt(2), 0.1 + d1, z * -2 * random.nextInt(2));
                        for (int i = 0; i < 5; i++) {
                            this.level().addParticle(ParticleTypes.BUBBLE, this.getRandomX(2), this.getY() + 0.5, this.getRandomZ(2), 0, 0, 0);
                        }
                    }
                    if (this.getFeetBlockState().is(Blocks.LAVA)) {
                        rollingtime--;
                        this.setSecondsOnFire(3);
                        double d1 = this.random.nextGaussian() * 0.02;
                        double d2 = this.random.nextGaussian() * 0.02;
                        double d3 = this.random.nextGaussian() * 0.02;
                        this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY() + 1, this.getZ(), x * -2 + d1, 0.1 + d2, z * -2 + d3);

                    }
                }
                if (rollingtime <= 1 || !isRolling()) {
                    canRollInFluid = false;
                }

            } else {
                this.setGlowingTag(false);
            }
            if (!isInLiquid() && this.onGround()) {
                rollingtime = 100;
            }
        }





    }

    public boolean isInLiquid(){
        return isInWater() || isInLava();
    }

    @Override
    public boolean canStandOnFluid(FluidState pFluidState) {
        return canRollInFluid;
    }

    public boolean isRolling(){
        return RockyChestplateUtil.isRockyRolling(this);
    }
}
