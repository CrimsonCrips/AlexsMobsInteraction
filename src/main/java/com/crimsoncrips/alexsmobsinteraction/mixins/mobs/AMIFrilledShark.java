package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFrilledShark;
import com.github.alexthe666.alexsmobs.entity.EntityGiantSquid;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityFrilledShark.class)
public abstract class AMIFrilledShark {

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityFrilledShark frilledShark = (EntityFrilledShark)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.BLEEDING_HUNGER_ENABLED.get()){
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, Player.class, 50, true, true, (mob) -> {
                return mob.hasEffect(AMEffectRegistry.EXSANGUINATION.get());
            }));
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, EntityGiantSquid.class, 300, false, true,(livingEntity) -> {
                return livingEntity.getHealth() <= 0.25F * livingEntity.getMaxHealth();
            }));
        }
        if (AlexsMobsInteraction.COMMON_CONFIG.ADD_TARGETS_ENABLED.get()) {
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, LivingEntity.class, 20, false, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.KILL_FISHES)));
        }
    }

}
