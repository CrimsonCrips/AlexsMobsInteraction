package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.terrapin;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.ACCompat;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(EntityTerrapin.class)
public abstract class AMITerrapinMixin extends Mob implements AMIBaseInterfaces {

    protected AMITerrapinMixin(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        EntityTerrapin terrapin = (EntityTerrapin)(Object)this;
        if (!this.level().isClientSide && !isInWater() && !terrapin.hasRetreated() || !terrapin.isSpinning()) {
            List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(0, 0.15F, 0));
            for (Player player : list) {
                if ((player.jumping || !player.onGround()) && player.getY() > this.getEyeY()) {
                    if (AlexsMobsInteraction.COMMON_CONFIG.TERRAPIN_STOMP_ENABLED.get()) {
                        terrapin.hurt(terrapin.damageSources().generic(),2);
                    }
                    if (AlexsMobsInteraction.COMMON_CONFIG.MINE_TURTLE_ENABLED.get()) {
                        if (terrapin.getRandom().nextDouble() < 0.3 && ModList.get().isLoaded("alexscaves")) {
                            ACCompat.summonNuke(player);
                        } else {
                            terrapin.level().explode(this, terrapin.getX() + 1,terrapin.getY() + 2,terrapin.getZ() + 1,3, Level.ExplosionInteraction.BLOCK);
                        }
                        discard();
                    }
                }
            }
        }

    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityTerrapin;copySpinDelta(FLnet/minecraft/world/phys/Vec3;)V"))
    private void alexsMobsInteraction$tick2(CallbackInfo ci) {
        if (isBlueKoopa()){
            this.level().explode(this, this.getX() + 1,this.getY() + 2,this.getZ() + 1,4, Level.ExplosionInteraction.NONE);
            discard();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void alexsMobsInteraction$tick3(CallbackInfo ci) {
        if (isBlueKoopa()){
            this.level().explode(this, this.getX() + 1,this.getY() + 2,this.getZ() + 1,4, Level.ExplosionInteraction.NONE);
            discard();
        }
    }

    @Override
    public boolean isBlueKoopa() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && s.toLowerCase().contains("blue koopa");
    }
}
