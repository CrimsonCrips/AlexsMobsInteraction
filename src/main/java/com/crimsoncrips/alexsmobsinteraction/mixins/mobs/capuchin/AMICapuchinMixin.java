package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AncientDartPotion;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityRhinoceros;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Objects;


@Mixin(EntityCapuchinMonkey.class)
public abstract class AMICapuchinMixin extends TamableAnimal implements AncientDartPotion {


    protected AMICapuchinMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract boolean hasDart();

    private static final EntityDataAccessor<Integer> DART_POTION_LEVEL = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DART_POTION = SynchedEntityData.defineId(EntityCapuchinMonkey.class, EntityDataSerializers.STRING);
    private static final Object2IntMap<String> potionToColor = new Object2IntOpenHashMap<>();




    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityCapuchinMonkey capuchinMonkey = (EntityCapuchinMonkey)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.ADD_TARGETS_ENABLED.get()) {
            capuchinMonkey.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(capuchinMonkey, LivingEntity.class, 400, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.INSECTS)) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !capuchinMonkey.isTame();
                }
            });
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityTossedItem;shoot(DDDFF)V"))
    private void alexsMobsInteraction$tick(CallbackInfo ci, @Local EntityTossedItem tossedItem) {
        if (!Objects.equals(getPotionId(), "")){
            ((AncientDartPotion) tossedItem).setPotionId(getPotionId());
        }
    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$onGetItem(ItemEntity e, CallbackInfo ci) {
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

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void alexsMobsInteraction$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof PotionItem && this.hasDart() && AlexsMobsInteraction.COMMON_CONFIG.FOOD_TARGET_EFFECTS_ENABLED.get()) {
            Potion contained = PotionUtils.getPotion(itemStack);
            if(applyPotion(contained)){
                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.BOTTLE_EMPTY);
                this.usePlayerItem(player, hand, itemStack);
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if(!player.addItem(bottle)){
                    player.drop(bottle, false);
                }
                player.swing(hand);
            }
        }
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityCapuchinMonkey;setDart(Z)V",ordinal = 1))
    private void alexsMobsInteraction$mobInteract2(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        resetPotion();
    }

    public void resetPotion() {
        this.setPotionId("");
        this.setPotionLevel(0);
    }

    public boolean applyPotion(Potion potion){
        if(potion == null || potion == Potions.WATER){
            resetPotion();
            return true;
        }else{
            if(!potion.getEffects().isEmpty()){
                MobEffectInstance fx = potion.getEffects().get(0);
                ResourceLocation potionId = ForgeRegistries.MOB_EFFECTS.getKey(fx.getEffect());
                if(potionId != null){
                    this.setPotionId(potionId.toString());
                    this.setPotionLevel(fx.getAmplifier());
                    return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void alexsMobsInteraction$defineSynchedData(CallbackInfo ci) {
        this.entityData.define(DART_POTION, "");
        this.entityData.define(DART_POTION_LEVEL, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putString("PotionName", this.getPotionId());
        compound.putInt("PotionLevel", this.getPotionLevel());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        this.setPotionId(compound.getString("PotionName"));
        this.setPotionLevel(compound.getInt("PotionLevel"));
    }




    @Override
    public String getPotionId() {
        return this.entityData.get(DART_POTION);
    }

    @Override
    public void setPotionId(String potionId) {
        this.entityData.set(DART_POTION, potionId);
    }

    public int getPotionLevel() {
        return this.entityData.get(DART_POTION_LEVEL);
    }

    @Override
    public void setPotionLevel(int time) {
        this.entityData.set(DART_POTION_LEVEL, time);
    }

    public MobEffect getPotionEffect() {
        return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(this.getPotionId()));
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

}
