package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.compat.SoulFiredCompat;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockBananaPeel;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.util.RockyChestplateUtil;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import crystalspider.soulfired.api.FireManager;
import crystalspider.soulfired.api.type.FireTyped;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        BlockState feetBlockstate = getBlockStateOn();

        if (AMInteractionConfig.COMBUSTABLE_ENABLED && this.hasEffect(AMEffectRegistry.OILED.get())){
            if (feetBlockstate.is(Blocks.MAGMA_BLOCK) || feetBlockstate.is(Blocks.CAMPFIRE))
                this.setSecondsOnFire(20);

            if (feetBlockstate.is(Blocks.SOUL_CAMPFIRE)){
                if (ModList.get().isLoaded("soulfired")) {
                    SoulFiredCompat.setOnFire(this,20);
                } else this.setSecondsOnFire(20);
            }

        }

        if(AMInteractionConfig.GOOFY_BANANA_SLIP_ENABLED && AMInteractionConfig.GOOFY_MODE_ENABLED){
            if (feetBlockstate.is(AMBlockRegistry.BANANA_PEEL.get())){
                kill();
            }
        }

        if(AMInteractionConfig.SUNBIRD_UPGRADE_ENABLED){

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



        if (AMInteractionConfig.TIGER_STEALTH_ENABLED && hasEffect(AMEffectRegistry.TIGERS_BLESSING.get()) && isCrouching()){
            addEffect(new MobEffectInstance(MobEffects. INVISIBILITY, 20, 0));
        }

        double z = this.getLookAngle().z;
        double x = this.getLookAngle().x;


        if(AMInteractionConfig.ROLLING_THUNDER_ENABLED){
            if (!(rollingtime <= 0) && this.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(AMIEnchantmentRegistry.ROLLING_THUNDER.get()) > 0 && this.getItemBySlot(EquipmentSlot.CHEST).is((Item) AMItemRegistry.ROCKY_CHESTPLATE.get())) {
                if (isRolling()) {
                    if (!isInLiquid()) {
                        canRollInFluid = true;
                    }

                    if (feetBlockstate.is(Blocks.WATER)) {
                        rollingtime--;
                        double d1 = this.random.nextGaussian() * 0.02;
                        this.level().addParticle(ParticleTypes.SPLASH, this.getRandomX(0.1), this.getY() + 0.5, this.getRandomZ(0.1), x * -2 * random.nextInt(2), 0.1 + d1, z * -2 * random.nextInt(2));
                        this.level().addParticle(ParticleTypes.SPLASH, this.getRandomX(0.1), this.getY() + 0.5, this.getRandomZ(0.1), x * -2 * random.nextInt(2), 0.1 + d1, z * -2 * random.nextInt(2));
                        for (int i = 0; i < 5; i++) {
                            this.level().addParticle(ParticleTypes.BUBBLE, this.getRandomX(2), this.getY() + 0.5, this.getRandomZ(2), 0, 0, 0);
                        }
                    }
                    if (feetBlockstate.is(Blocks.LAVA)) {
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

    public  boolean isBlockFiery(Block block){
        if (block instanceof MagmaBlock || block instanceof CampfireBlock)
            return true;
        return false;
    }
}
