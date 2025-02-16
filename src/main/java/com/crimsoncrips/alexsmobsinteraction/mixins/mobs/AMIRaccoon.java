package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRaccoon;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;


@Mixin(EntityRaccoon.class)
public abstract class AMIRaccoon extends TamableAnimal {


    protected AMIRaccoon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void registerGoals();


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityRaccoon raccoon = (EntityRaccoon)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.RACOON_HUNT_ENABLED.get()) {
            raccoon.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(raccoon, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RACCOON_KILL)) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !raccoon.isTame() && raccoon.level().isNight();
                }

            });
        }
    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void getItem(ItemEntity e, CallbackInfo ci) {
        if (e.getItem().isEdible() && AlexsMobsInteraction.COMMON_CONFIG.FOOD_TARGET_EFFECTS_ENABLED.get()) {
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
