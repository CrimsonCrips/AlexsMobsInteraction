package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityCachalotWhale.class)
public abstract class AMICachalotWhale extends Animal {

    protected AMICachalotWhale(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityCachalotWhale cachalotWhale = (EntityCachalotWhale)(Object)this;
        if (AlexsMobsInteraction.TARGETS_CONFIG.CACHALOT_ENABLED.get()){
            cachalotWhale.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(cachalotWhale, LivingEntity.class, 300, true, false, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.KILL_FISHES)));
        }
    }

}
