package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AncientDartPotion;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;


@Mixin(EntityCapuchinMonkey.class)
public abstract class AMICapuchinMixin extends TamableAnimal implements AncientDartPotion {


    protected AMICapuchinMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract boolean hasDart();
    private static final Object2IntMap<String> potionToColor = new Object2IntOpenHashMap<>();





    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityTossedItem;shoot(DDDFF)V"))
    private void alexsMobsInteraction$tick(CallbackInfo ci, @Local EntityTossedItem tossedItem) {
        if (!Objects.equals(this.getData(AMIAttachments.POTION_ID), "")){
            tossedItem.setData(AMIAttachments.POTION_ID, this.getData(AMIAttachments.POTION_ID));
        }
    }


    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void alexsMobsInteraction$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof PotionItem && this.hasDart() && AlexsMobsInteraction.COMMON_CONFIG.FOOD_FX_ENABLED.get()) {
            PotionContents contained = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if(applyPotion(contained)){
                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.BOTTLE_EMPTY);
                this.usePlayerItem(player, hand, itemStack);
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if(!player.addItem(bottle) && !player.isCreative()){
                    itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                    player.drop(bottle, false);
                }
                player.swing(hand);
                AMIUtils.awardAdvancement(player,"dart_effect","dart_effect");
            }
        }
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityCapuchinMonkey;setDart(Z)V",ordinal = 1))
    private void alexsMobsInteraction$mobInteract2(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        resetPotion();
    }

    public void resetPotion() {
        this.setData(AMIAttachments.POTION_ID, "");
        this.setData(AMIAttachments.POTION_LEVEL, 0);
    }

    public boolean applyPotion(PotionContents potion){
        if(potion.is(Potions.WATER)){
            resetPotion();
            return true;
        }else{
            if(potion.hasEffects()){
                MobEffectInstance fx = potion.getAllEffects().iterator().next();
                ResourceLocation potionId = BuiltInRegistries.MOB_EFFECT.getKey(fx.getEffect().value());
                if(potionId != null){
                    this.setData(AMIAttachments.POTION_ID, potionId.toString());
                    this.setData(AMIAttachments.POTION_LEVEL, fx.getAmplifier());
                    return true;
                }
            }
        }
        return false;
    }











    public MobEffect getPotionEffect() {
        return BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(this.getData(AMIAttachments.POTION_ID)));
    }

    @Override
    public int getPotionColor() {
        String id = this.getData(AMIAttachments.POTION_ID);
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
