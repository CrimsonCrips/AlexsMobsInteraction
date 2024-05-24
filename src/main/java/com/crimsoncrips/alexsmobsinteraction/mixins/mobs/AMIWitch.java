package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.BottomFeederAIWander;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;


@Mixin(Witch.class)
public abstract class AMIWitch extends Mob {

    protected AMIWitch(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");

    private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
    private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

    public void setUsingItem(boolean pUsingItem) {
        this.getEntityData().set(DATA_USING_ITEM, pUsingItem);
    }

    public boolean isDrinkingPotion() {
        return (Boolean)this.getEntityData().get(DATA_USING_ITEM);
    }

    private int usingTime;

    private static final AttributeModifier SPEED_MODIFIER_DRINKING;

    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM;

    public void aiStep() {
        if (!this.level().isClientSide && this.isAlive()) {
            this.healRaidersGoal.decrementCooldown();
            if (this.healRaidersGoal.getCooldown() <= 0) {
                this.attackPlayersGoal.setCanAttack(true);
            } else {
                this.attackPlayersGoal.setCanAttack(false);
            }

            if (this.isDrinkingPotion()) {
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false);
                    ItemStack $$0 = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if ($$0.is(Items.POTION)) {
                        List<MobEffectInstance> $$1 = PotionUtils.getMobEffects($$0);
                        if ($$1 != null) {
                            Iterator var3 = $$1.iterator();

                            while(var3.hasNext()) {
                                MobEffectInstance $$2 = (MobEffectInstance)var3.next();
                                this.addEffect(new MobEffectInstance($$2));
                            }
                        }
                    }

                    this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
                }
            } else {
                Potion $$3 = null;
                if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING))
                    $$3 = Potions.WATER_BREATHING;
                if (this.random.nextFloat() < 0.15F && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE)) && !this.hasEffect(MobEffects.FIRE_RESISTANCE))
                    $$3 = Potions.FIRE_RESISTANCE;
                if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth())
                    $$3 = Potions.HEALING;
                if (this.random.nextFloat() < 0.5F && this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0)
                    $$3 = Potions.SWIFTNESS;
                if (this.hasEffect(MobEffects.POISON) && !this.hasEffect(AMEffectRegistry.POISON_RESISTANCE.get()))
                    $$3 = AMEffectRegistry.POISON_RESISTANCE_POTION.get();


                if ($$3 != null) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), $$3));
                    this.usingTime = this.getMainHandItem().getUseDuration();
                    this.setUsingItem(true);
                    if (!this.isSilent()) {
                        this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                    }

                    AttributeInstance $$4 = this.getAttribute(Attributes.MOVEMENT_SPEED);
                    $$4.removeModifier(SPEED_MODIFIER_DRINKING);
                    $$4.addTransientModifier(SPEED_MODIFIER_DRINKING);
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.level().broadcastEntityEvent(this, (byte)15);
            }
        }

        super.aiStep();
    }

    static {
        SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25, AttributeModifier.Operation.ADDITION);
        DATA_USING_ITEM = SynchedEntityData.defineId(Witch.class, EntityDataSerializers.BOOLEAN);
    }
}
