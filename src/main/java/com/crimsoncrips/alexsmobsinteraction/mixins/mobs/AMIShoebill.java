package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;


@Mixin(EntityShoebill.class)
public abstract class AMIShoebill extends Animal {


    protected AMIShoebill(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityShoebill shoebill = (EntityShoebill)(Object)this;
        shoebill.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(shoebill, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SHOEBILL_BABY_KILL)));
        shoebill.targetSelector.addGoal(7, new EntityAINearestTarget3D<>(shoebill, EntityTerrapin.class, 100, false, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SHOEBILL_KILL)));
    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void getItem(ItemEntity e, CallbackInfo ci) {
        if (e.getItem().isEdible() && AMInteractionConfig.FOOD_TARGET_EFFECTS_ENABLED) {
            this.heal(5);
            List<Pair<MobEffectInstance, Float>> test = Objects.requireNonNull(e.getItem().getFoodProperties(this)).getEffects();
            if (!test.isEmpty()){
                for (int i = 0; i < test.size(); i++){
                    this.addEffect(new MobEffectInstance(test.get(i).getFirst()));
                }
            }
        }
    }


}
