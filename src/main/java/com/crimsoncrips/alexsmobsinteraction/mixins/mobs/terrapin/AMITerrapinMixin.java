package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.terrapin;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(EntityTerrapin.class)
public abstract class AMITerrapinMixin extends Mob {

    protected AMITerrapinMixin(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityTerrapin;copySpinDelta(FLnet/minecraft/world/phys/Vec3;)V"))
    private void alexsMobsInteraction$tick2(CallbackInfo ci) {
        if (this.getData(AMIAttachments.BLUE_KOOPA)){
            LivingEntity stomper = (LivingEntity) AMIReflectionUtil.getField(this, "lastLauncher");
            AMIUtils.awardAdvancement(stomper, "blue_shell", "blue_shell");
            this.level().explode(this, this.getX() + 1,this.getY() + 2,this.getZ() + 1,4, Level.ExplosionInteraction.NONE);
            discard();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void alexsMobsInteraction$tick3(CallbackInfo ci) {
        if (this.getData(AMIAttachments.BLUE_KOOPA)){
            LivingEntity stomper = (LivingEntity) AMIReflectionUtil.getField(this, "lastLauncher");
            AMIUtils.awardAdvancement(stomper, "blue_shell", "blue_shell");
            this.level().explode(this, this.getX() + 1,this.getY() + 2,this.getZ() + 1,4, Level.ExplosionInteraction.NONE);
            discard();
        }
    }
}
