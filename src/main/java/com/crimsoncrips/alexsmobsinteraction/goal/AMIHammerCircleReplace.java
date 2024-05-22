package com.crimsoncrips.alexsmobsinteraction.goal;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityHammerheadShark;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.EnumSet;

public class AMIHammerCircleReplace extends Goal {
  EntityHammerheadShark hammerhead;
  float speed;
  float circlingTime = 0.0F;
  float circleDistance = 5.0F;
  float maxCirclingTime = 80.0F;
  boolean clockwise = false;


    int stunned;

   public boolean stun = false;



  public AMIHammerCircleReplace(EntityHammerheadShark hammerhead, float speed) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.hammerhead = hammerhead;
      this.speed = speed;
  }

  public boolean canUse() {
      return hammerhead.getTarget() != null;
  }

  public boolean canContinueToUse() {
      return hammerhead.getTarget() != null;
  }


  public void start() {
      RandomSource random = hammerhead.getRandom();
      this.circlingTime = 0.0F;
      this.maxCirclingTime = (float)(100 + random.nextInt(80));
      this.circleDistance = 5.0F + random.nextFloat() * 5.0F;
      this.clockwise = random.nextBoolean();
  }

  public void stop() {
      RandomSource random = hammerhead.getRandom();
      this.circlingTime = 0.0F;
      this.maxCirclingTime = (float)(100 + random.nextInt(80));
      this.circleDistance = 5.0F + random.nextFloat() * 5.0F;
      this.clockwise = random.nextBoolean();
  }

  public void tick() {
      LivingEntity prey = hammerhead.getTarget();
      if (prey != null) {
          double dist = (double)hammerhead.distanceTo(prey);
          if (circlingTime >= maxCirclingTime) {
              hammerhead.lookAt(prey, 30.0F, 30.0F);
              hammerhead.getNavigation().moveTo(prey, 1.5);
              if (dist < 2.0) {
                  hammerhead.doHurtTarget(prey);

                  this.stop();
              }
          } else if (dist <= 25.0) {
              ++this.circlingTime;
              BlockPos circlePos = this.gethammerheadCirclePos(prey);
              if (circlePos != null) {
                  hammerhead.getNavigation().moveTo((double)circlePos.getX() + 0.5, (double)circlePos.getY() + 0.5, (double)circlePos.getZ() + 0.5, 0.6);
              }
          } else {
              hammerhead.lookAt(prey, 30.0F, 30.0F);
              hammerhead.getNavigation().moveTo(prey, 0.8);
          }
      }
      if (AMInteractionConfig.CHARGE_STUN_ENABLED) {
          stunned--;
          LivingEntity target = hammerhead.getTarget();


          if (hammerhead.getTarget() instanceof Player) {
              if (hammerhead.distanceTo(hammerhead.getTarget()) < 3F && hammerhead.hasLineOfSight(hammerhead.getTarget()) && hammerhead.getTarget().isBlocking() && !stun && circlingTime >= maxCirclingTime) {
                  stunned = 100;
                  stun = true;
                  if (target instanceof Player) {
                      Player player = (Player)target;
                      this.damageShieldFor(player, (float)hammerhead.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                      hammerhead.playSound(SoundEvents.SHIELD_BLOCK, 2F, 1F);
                  }
              }
          }

      }
      

  }
    protected void damageShieldFor(Player holder, float damage) {
        if (holder.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
            if (!hammerhead.level().isClientSide) {
                holder.awardStat(Stats.ITEM_USED.get(holder.getUseItem().getItem()));
            }

            if (damage >= 3.0F) {
                int i = 10 + Mth.floor(damage);
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

                    holder.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + hammerhead.level().random.nextFloat() * 0.4F);
                }
            }
        }

    }

  public BlockPos gethammerheadCirclePos(LivingEntity target) {
      float angle = 0.017453292F * (this.clockwise ? -this.circlingTime : this.circlingTime);
      double extraX = (double)(this.circleDistance * Mth.sin(angle));
      double extraZ = (double)(this.circleDistance * Mth.cos(angle));
      BlockPos ground = AMBlockPos.fromCoords(target.getX() + 0.5 + extraX, this.hammerhead.getY(), target.getZ() + 0.5 + extraZ);
      return this.hammerhead.level().getFluidState(ground).is(FluidTags.WATER) ? ground : null;
  }
}