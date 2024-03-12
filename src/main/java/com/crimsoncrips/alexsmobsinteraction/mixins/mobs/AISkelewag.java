package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.goal.AISkelewagCircleGoal;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntitySkelewag.class)
public class AISkelewag extends Mob {

    protected AISkelewag(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void ShoebillGoals(CallbackInfo ci){
        ci.cancel();
        EntitySkelewag skelewag = (EntitySkelewag) (Object) this;
        Predicate<LivingEntity> NOT_TARGET = (livingEntity) -> {
            return !(livingEntity instanceof Monster) && !(livingEntity instanceof Player);
        };
        this.goalSelector.addGoal(1, new TryFindWaterGoal(skelewag));
        if (AInteractionConfig.skelewagcircle){
            this.goalSelector.addGoal(1, new AISkelewagCircleGoal(skelewag,1F));
        } else {
            Object aiAttackGoal = ReflectionUtil.createInstance(
                    "com.github.alexthe666.alexsmobs.entity.EntitySkelewag$AttackGoal",
                    new Class[]{EntitySkelewag.class, EntitySkelewag.class},
                    new Object[]{skelewag, this}
            );
            this.goalSelector.addGoal(2,(Goal)aiAttackGoal);
        }
        this.goalSelector.addGoal(3, new AnimalAIRandomSwimming(skelewag, 1F, 12, 5));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(skelewag, Drowned.class, EntitySkelewag.class));
        if (AInteractionConfig.scourgingseas){
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, LivingEntity.class, 500, true, false, NOT_TARGET));
        } else {
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, true));
            this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, Dolphin.class, true));
        }

        this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, Player.class, 1, true, false,null));

    }

    int stunned;

    boolean stun = false;

    protected boolean isImmobile() {
        return stun;
    }


    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (AInteractionConfig.stunnablecharge) {
            stunned--;
            LivingEntity target = getTarget();
            if (stunned > 0 && target != null) {
                setTarget(null);
            }
            if (stunned < 1) {
                stun = false;
                setTarget(target);
            }

            if (this.getTarget() instanceof Player) {
                if (this.distanceTo(this.getTarget()) < 3F && this.hasLineOfSight(this.getTarget()) && this.getTarget().isBlocking() && !stun) {
                    stunned = 100;
                    stun = true;
                    if (target instanceof Player) {
                        Player player = (Player)target;
                        this.damageShieldFor(player, (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                        this.playSound(SoundEvents.SHIELD_BLOCK, 2F, 1F);
                    }
                }
            }

        }
    }
    protected void damageShieldFor(Player holder, float damage) {
        if (holder.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
            if (!this.level().isClientSide) {
                holder.awardStat(Stats.ITEM_USED.get(holder.getUseItem().getItem()));
            }

            if (damage >= 3.0F) {
                int i = 4 + Mth.floor(damage);
                InteractionHand hand = holder.getUsedItemHand();
                holder.getUseItem().hurtAndBreak(i, holder, (p_213833_1_) -> {
                    p_213833_1_.broadcastBreakEvent(hand);
                    ForgeEventFactory.onPlayerDestroyItem(holder, holder.getUseItem(), hand);
                });
                if (holder.getUseItem().isEmpty()) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        holder.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        holder.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    holder.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                }
            }
        }

    }
}
