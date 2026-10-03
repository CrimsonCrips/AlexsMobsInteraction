package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityTossedItem.class)
public abstract class AMITossedItemMixin extends ThrowableItemProjectile {

    @Shadow public abstract boolean isDart();

    public AMITossedItemMixin(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void alexsMobsInteraction$onHitEntity(EntityHitResult p_213868_1_, CallbackInfo ci) {
        MobEffect potion = AMIUtils.getPotionEffect(this);

        if(potion != null && p_213868_1_.getEntity() instanceof LivingEntity livingEntity && AlexsMobsInteraction.COMMON_CONFIG.DART_EFFECTS_ENABLED.get()){
            MobEffectInstance instance = new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(potion), AMIUtils.seconds(5), this.getData(AMIAttachments.POTION_LEVEL));
            livingEntity.addEffect(instance);
        }
    }
}
