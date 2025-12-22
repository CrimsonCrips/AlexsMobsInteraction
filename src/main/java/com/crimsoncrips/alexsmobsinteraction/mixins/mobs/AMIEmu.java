package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIEggHeldAttack;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIEmuRangedTrigger;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEmu;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityEmu.class)
public abstract class AMIEmu extends Animal {


    protected AMIEmu(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityEmu emu = (EntityEmu)(Object)this;

        if (AlexsMobsInteraction.COMMON_CONFIG.EGG_ATTACK_ENABLED.get()){
            emu.targetSelector.addGoal(6, new AMIEggHeldAttack<>(emu, LivingEntity.class, true));
        }

        if (AlexsMobsInteraction.COMMON_CONFIG.RANGED_AGGRO_ENABLED.get()){
            emu.targetSelector.addGoal(7, new AMIEmuRangedTrigger(emu, LivingEntity.class,  true));
        }
        if (AlexsMobsInteraction.COMMON_CONFIG.ADD_TARGETS_ENABLED.get()) {
            emu.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.INSECTS)) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !emu.isBaby();
                }
            });
        }
    }

}
