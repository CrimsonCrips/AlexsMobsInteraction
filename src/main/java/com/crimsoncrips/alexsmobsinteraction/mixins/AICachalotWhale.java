package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCachalotWhale;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityCachalotWhale.class)
public abstract class AICachalotWhale extends Mob {

    @Shadow public abstract boolean isCharging();

    protected AICachalotWhale(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void CachalotWhaleGoals(CallbackInfo ci){
        EntityCachalotWhale cachalotWhale = (EntityCachalotWhale)(Object)this;
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CACHALOT_WHALE_KILL)) {
            public boolean canUse() {
                return !isSleeping() && !cachalotWhale.isBeached() && super.canUse();
            }
        });
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 300, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CACHALOT_WHALE_KILL_CHANCE)) {
            public boolean canUse() {
                return !isSleeping() && !cachalotWhale.isBeached() && super.canUse();
            }
        });
    }
    public void awardKillScore(Entity entity, int score, DamageSource src) {
        if(entity instanceof LivingEntity living && AInteractionConfig.nodropsforpredators){
            final CompoundTag emptyNbt = new CompoundTag();
            living.addAdditionalSaveData(emptyNbt);
            emptyNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
            living.readAdditionalSaveData(emptyNbt);
        }
        super.awardKillScore(entity, score, src);
    }
    int stunned;

    boolean stun = false;

    protected boolean isImmobile() {
        return stun;
    }
    @Inject(method = "baseTick", at = @At("TAIL"))
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

            if (this.getTarget() instanceof Player && isCharging()) {
                if (this.distanceTo(this.getTarget()) < 6F && this.hasLineOfSight(this.getTarget()) && this.getTarget().isBlocking() && !stun) {
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
                int i = 100 + Mth.floor(damage);
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
