package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntitySnowLeopard.class)
public abstract class AMISnowLeopard extends Animal {


    protected AMISnowLeopard(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntitySnowLeopard snowLeopard = (EntitySnowLeopard)(Object)this;
        if(AMInteractionConfig.LEOPARD_DESIRES_ENABLED){
            snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, EntityMoose.class, 100, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
            }));
            snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, Player.class, 100, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth() && livingEntity.getItemBySlot(EquipmentSlot.HEAD).is((Item) AMItemRegistry.MOOSE_HEADGEAR.get());
            }));
        }
    }


}
