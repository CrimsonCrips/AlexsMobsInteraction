package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static com.crimsoncrips.alexsmobsinteraction.server.AMInteractionTagRegistry.INSECTS;


@Mixin(EntityCapuchinMonkey.class)
public abstract class AMICapuchinMonkey extends TamableAnimal {


    protected AMICapuchinMonkey(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityCapuchinMonkey capuchinMonkey = (EntityCapuchinMonkey)(Object)this;
        if (AMInteractionConfig.CAPUCHIN_HUNT_ENABLED) {
            capuchinMonkey.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(capuchinMonkey, LivingEntity.class, 400, true, true, AMEntityRegistry.buildPredicateFromTag(INSECTS)) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !capuchinMonkey.isTame();
                }
            });
        }
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
