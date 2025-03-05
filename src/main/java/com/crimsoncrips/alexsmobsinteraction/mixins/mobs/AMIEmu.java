package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
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

        if (AlexsMobsInteraction.COMMON_CONFIG.EMU_EGG_ATTACK_ENABLED.get()){
            emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 5, true, false, (livingEntity) -> {
                return livingEntity.isHolding( Ingredient.of(AMItemRegistry.EMU_EGG.get())) || livingEntity.isHolding( Ingredient.of(AMItemRegistry.BOILED_EMU_EGG.get()));
            }));
        }

        if (AlexsMobsInteraction.COMMON_CONFIG.RANGED_AGGRO_ENABLED.get()){
            emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 70, true, false, (livingEntity) -> {
                return livingEntity.isHolding(Ingredient.of(AMIItemTagGenerator.EMU_TRIGGER));
            }){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !emu.isBaby();
                }
            });
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
