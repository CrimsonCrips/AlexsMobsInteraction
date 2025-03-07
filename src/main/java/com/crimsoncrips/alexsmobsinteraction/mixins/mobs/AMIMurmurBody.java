package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import com.github.alexthe666.alexsmobs.entity.EntityOrca;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;


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

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityMurmur murmur = (EntityMurmur)(Object)this;
        if(AlexsMobsInteraction.COMMON_CONFIG.MURMUR_REGROW_ENABLED.get()){
            murmur.goalSelector.addGoal(2, new AvoidEntityGoal<>(murmur, LivingEntity.class, 10.0F, 1.8, 2){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && murmur.getHead() == null;
                }
            });
        }
    }





}
