package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.terrapin;

import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBasicInterfaces;
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
public abstract class AMITerrapinMixin extends Mob implements AMIBasicInterfaces {

    protected AMITerrapinMixin(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityTerrapin;copySpinDelta(FLnet/minecraft/world/phys/Vec3;)V"))
    private void alexsMobsInteraction$tick2(CallbackInfo ci) {
        if (isBlueKoopa()){
            LivingEntity stomper = (LivingEntity) AMIReflectionUtil.getField(this, "lastLauncher");
            AMIUtils.awardAdvancement(stomper, "blue_shell", "blue_shell");
            this.level().explode(this, this.getX() + 1,this.getY() + 2,this.getZ() + 1,4, Level.ExplosionInteraction.NONE);
            discard();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void alexsMobsInteraction$tick3(CallbackInfo ci) {
        if (isBlueKoopa()){
            LivingEntity stomper = (LivingEntity) AMIReflectionUtil.getField(this, "lastLauncher");
            AMIUtils.awardAdvancement(stomper, "blue_shell", "blue_shell");
            this.level().explode(this, this.getX() + 1,this.getY() + 2,this.getZ() + 1,4, Level.ExplosionInteraction.NONE);
            discard();
        }
    }

    @Override
    public boolean isBlueKoopa() {
        String[] name = {"blue shell","blue koopa"};
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        for (String names : name) {
            return s != null && s.toLowerCase().contains(names) && AlexsMobsInteraction.COMMON_CONFIG.BLUE_SHELL_ENABLED.get();
        }
        return false;
    }

    @Override
    public boolean isMineTurtle() {
//        String[] name = {"mine turtle","mine","asdf"};
//        String s = ChatFormatting.stripFormatting(this.getName().getString());
//        for (String names : name) {
//            return s != null && s.toLowerCase().contains(names) && AlexsMobsInteraction.COMMON_CONFIG.MINE_TURTLE_ENABLED.get();
//        }
        return false;
    }
}
