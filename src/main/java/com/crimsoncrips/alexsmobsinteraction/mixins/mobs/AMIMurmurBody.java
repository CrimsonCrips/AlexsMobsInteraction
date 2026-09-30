package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(EntityMurmur.class)
public abstract class AMIMurmurBody extends Mob {

    @Shadow public abstract Entity getHead();

    protected AMIMurmurBody(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    int regrowTime = 501;


    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityMurmur;getHead()Lnet/minecraft/world/entity/Entity;"), cancellable = true)
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        if(AlexsMobsInteraction.COMMON_CONFIG.MURMUR_REGROW_ENABLED.get() && getHead() == null && regrowTime <= 200){
            ci.cancel();
            regrowTime++;
        } else {
            regrowTime = 0;
        }
    }





}
