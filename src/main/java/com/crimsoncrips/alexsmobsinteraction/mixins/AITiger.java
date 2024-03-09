package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
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


@Mixin(EntityTiger.class)
public class AITiger extends Mob {


    protected AITiger(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void TigerGoals(CallbackInfo ci){
        ci.cancel();
        EntityTiger tiger = (EntityTiger)(Object)this;
        Predicate<LivingEntity> NOBLESSING = (livingEntity) -> {
                return !livingEntity.hasEffect(AMEffectRegistry.TIGERS_BLESSING.get());
        };
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIPanicBaby(tiger, 1.25D));
        Object aiMeleeAttackGoal = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityTiger$AIMelee",
                new Class[] {EntityTiger.class},
                new Object[]    {tiger}
        );
        this.goalSelector.addGoal(2,(Goal)aiMeleeAttackGoal);
        this.goalSelector.addGoal(5, new BreedGoal(tiger, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(tiger, 1.1D));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(tiger, 60, 1.0D, 14, 7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 25F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(tiger, false, 10));
        this.targetSelector.addGoal(1, new HurtByTargetGoal((EntityTiger)(Object)this) {
            public void start() {
                super.start();
                if (tiger.isBaby()) {
                    this.alertOthers();
                    this.stop();
                }

            }
            protected void alertOther(Mob mobIn, LivingEntity targetIn) {
                if (mobIn instanceof EntityTiger && !isBaby()) {
                    super.alertOther(mobIn, targetIn);
                }
            }

        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Player.class, 500, true, false,NOBLESSING) {
            public boolean canUse() {
                if (AInteractionConfig.weakened) {
                    return !isBaby() && !(getHealth() <= 0.20F * getMaxHealth()) && !tiger.isInLove() && super.canUse();
                } else {
                    return !isBaby() && !tiger.isInLove() && super.canUse();
                }


            }
            protected double getFollowDistance() {
                return 4.0;
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, LivingEntity.class, 220, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.TIGER_KILL)) {

        });
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(tiger, true));

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
                int i = 30 + Mth.floor(damage);
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
