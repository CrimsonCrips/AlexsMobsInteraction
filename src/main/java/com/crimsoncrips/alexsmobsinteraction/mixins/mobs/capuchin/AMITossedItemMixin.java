package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AncientDartPotion;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityTossedItem.class)
public abstract class AMITossedItemMixin extends ThrowableItemProjectile implements AncientDartPotion {

    @Shadow public abstract boolean isDart();

    public AMITossedItemMixin(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private static final EntityDataAccessor<Integer> POTION_LEVEL = SynchedEntityData.defineId(EntityTossedItem.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> APPLIED_POTION = SynchedEntityData.defineId(EntityTossedItem.class, EntityDataSerializers.STRING);
    private static final Object2IntMap<String> potionToColor = new Object2IntOpenHashMap<>();

    protected Item getDefaultItem() {
        return isDart() ? AMItemRegistry.ANCIENT_DART.get() : AlexsMobsInteraction.COMMON_CONFIG.GOOFY_CAPUCHIN_BOMB_ENABLED.get() ? Items.TNT : Items.COBBLESTONE;
    }

    @Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
    private void alexsMobsInteraction$defineSynchedData(CallbackInfo ci) {
        this.entityData.define(APPLIED_POTION, "");
        this.entityData.define(POTION_LEVEL, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    private void alexsMobsInteraction$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putString("PotionName", this.getPotionId());
        compound.putInt("PotionLevel", this.getPotionLevel());
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    private void alexsMobsInteraction$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        this.setPotionId(compound.getString("PotionName"));
        this.setPotionLevel(compound.getInt("PotionLevel"));
    }


    @Override
    public String getPotionId() {
        return this.entityData.get(APPLIED_POTION);
    }

    @Override
    public void setPotionId(String potionId) {
        this.entityData.set(APPLIED_POTION, potionId);
    }

    public int getPotionLevel() {
        return this.entityData.get(POTION_LEVEL);
    }

    @Override
    public void setPotionLevel(int time) {
        this.entityData.set(POTION_LEVEL, time);
    }

    @Override
    public int getPotionColor() {
        String id = getPotionId();
        if (id.isEmpty()) {
            return -1;
        } else {
            if (!potionToColor.containsKey(id)) {
                MobEffect effect = getPotionEffect();
                if (effect != null) {
                    int color = effect.getColor();
                    potionToColor.put(id, color);
                    return color;
                }
                return -1;
            } else {
                return potionToColor.getInt(id);
            }
        }
    }


    public MobEffect getPotionEffect() {
        if (getPotionId() != null) {
            return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(this.getPotionId()));
        } else return null;
    }

    @Inject(method = "onHit", at = @At(value = "TAIL"))
    private void alexsMobsInteraction$onHit(HitResult result, CallbackInfo ci) {
        int x = this.getBlockX();
        int y = this.getBlockY();
        int z = this.getBlockZ();
        if (!isDart() && AlexsMobsInteraction.COMMON_CONFIG.GOOFY_CAPUCHIN_BOMB_ENABLED.get()) {
            this.level().explode(this, x + 1,y + 2,z + 1,2, Level.ExplosionInteraction.MOB);
        }
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void alexsMobsInteraction$onHitEntity(EntityHitResult p_213868_1_, CallbackInfo ci) {
        MobEffect potion = getPotionEffect();

        if(potion != null && p_213868_1_.getEntity() instanceof LivingEntity livingEntity && AlexsMobsInteraction.COMMON_CONFIG.DART_EFFECTS_ENABLED.get()){
            MobEffectInstance instance = new MobEffectInstance(potion, 100, getPotionLevel());
            livingEntity.addEffect(instance);
        }
    }
}
