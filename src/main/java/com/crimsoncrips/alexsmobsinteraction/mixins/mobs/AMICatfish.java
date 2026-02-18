package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.AMIServerConfig;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMITargetFood;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.alexsmobs.entity.EntityEndergrade;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityCatfish.class)
public abstract class AMICatfish extends WaterAnimal {


    @Shadow protected abstract void registerGoals();

    @Shadow public abstract int getCatfishSize();

    @Shadow public SimpleContainer catfishInventory;

    @Shadow public abstract void setSpitTime(int time);

    protected AMICatfish(EntityType<? extends WaterAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "hurt", at = @At(value = "HEAD"))
    private void alexsmobsinteraction$hurt(DamageSource source, float f, CallbackInfoReturnable<Boolean> cir) {
        boolean prev = super.hurt(source, f);
        if(prev && source.getDirectEntity() instanceof LivingEntity living && AlexsMobsInteraction.COMMON_CONFIG.CATFISH_VENOM_ENABLED.get() && getRandom().nextDouble() < 0.4){
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 100 * getCatfishSize()));
            AMIUtils.awardAdvancement(living,"venomous_cat","venom");
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void alexsmobsinteraction$tick(CallbackInfo ci) {
        for (int i = 0; i < catfishInventory.getContainerSize(); i++) {
            if (catfishInventory.getItem(i).is(AMIItemTagGenerator.HOT) && AlexsMobsInteraction.COMMON_CONFIG.GOOFY_HOT_CAT_ENABLED.get()){
                setSpitTime(100);
                break;
            }
        }
    }

    @ModifyReturnValue(method = "isFood", at = @At("RETURN"),remap = false)
    private boolean alexsMobsInteraction$isFood(boolean original,@Local Entity entity) {
        if (AlexsMobsInteraction.TARGETS_CONFIG.CANNIBALISM_ENABLED.get()) {
            if (this.getCatfishSize() == 2) {
                return !entity.getType().is(AMTagRegistry.CATFISH_IGNORE_EATING) && entity instanceof Mob && !(entity instanceof EntityCatfish catfish && catfish.getCatfishSize() == 2 ) && entity.getBbHeight() <= 1.0F;
            } else {
                return entity instanceof ItemEntity && ((ItemEntity)entity).getAge() > 35;
            }
        }
        return original;
    }

}
