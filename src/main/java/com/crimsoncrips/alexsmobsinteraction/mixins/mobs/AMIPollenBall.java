package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityMobProjectile;
import com.github.alexthe666.alexsmobs.entity.EntityPollenBall;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPollenBall.class)
public abstract class AMIPollenBall extends EntityMobProjectile {

    private static final float BONEMEAL_SPEED = 0.35F;

    private static final double BONEMEAL_REACH = 0.5;

    private static final int BONEMEAL_LIFETIME = AMIUtils.seconds(5);

    protected AMIPollenBall(EntityType<? extends EntityMobProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "doBehavior", at = @At("HEAD"), cancellable = true, remap = false)
    private void alexsMobsInteraction$doBehavior(CallbackInfo ci) {
        long packedTarget = this.getData(AMIAttachments.BONEMEAL_TARGET);
        if (packedTarget == AMIAttachments.NO_BLOCK)
            return;
        ci.cancel();
        BlockPos target = BlockPos.of(packedTarget);
        Vec3 toTarget = Vec3.atCenterOf(target).subtract(this.position());
        if (toTarget.length() < BONEMEAL_REACH) {
            if (!this.level().isClientSide && BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), this.level(), target)) {
                this.level().levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, target, 15);
            }
            this.discard();
            return;
        }
        if (this.tickCount > BONEMEAL_LIFETIME) {
            this.discard();
            return;
        }
        this.shoot(toTarget.x, toTarget.y, toTarget.z, BONEMEAL_SPEED, 0F);
        this.setYRot(-((float) Mth.atan2(toTarget.x, toTarget.z)) * Mth.RAD_TO_DEG);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return this.getData(AMIAttachments.BONEMEAL_TARGET) == AMIAttachments.NO_BLOCK && super.canHitEntity(entity);
    }
}
