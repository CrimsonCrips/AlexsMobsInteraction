package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.BottomFeederAIWander;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityLaviathan.class)
public abstract class AILavithan extends Animal implements ISemiAquatic, IHerdPanic {

    @Shadow public abstract float getHeadHeight();

    private static final EntityDataAccessor<Boolean> RELAVA;

    public int relavaTime = 0;

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        EntityLaviathan laviathan = (EntityLaviathan)(Object)this;
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (itemstack.getItem() == Items.DIAMOND_PICKAXE && AInteractionConfig.lavaithanobsidianremove) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.BASALT_BREAK, this.getSoundVolume(), this.getVoicePitch());
            this.spawnAtLocation(Items.OBSIDIAN);
            this.spawnAtLocation(Items.OBSIDIAN);
            this.spawnAtLocation(Items.OBSIDIAN);
            this.spawnAtLocation(Items.OBSIDIAN);
            itemstack.hurtAndBreak(1, this, (p_233654_0_) -> {
            });
            this.setHealth(this.getHealth() - 3);
            return InteractionResult.SUCCESS;
        }
        if (item == Items.MAGMA_CREAM && this.getHealth() < this.getMaxHealth()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }

            this.heal(10.0F);
            return InteractionResult.SUCCESS;
        } else if (item == AMItemRegistry.STRADDLE_HELMET.get() && !laviathan.hasHeadGear() && !this.isBaby()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }

            laviathan.setHeadGear(true);
            return InteractionResult.SUCCESS;
        } else if (item == AMItemRegistry.STRADDLE_SADDLE.get() && !laviathan.hasBodyGear() && !this.isBaby()) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }

            laviathan.setBodyGear(true);
            return InteractionResult.SUCCESS;
        } else {
            InteractionResult type = super.mobInteract(player, hand);
            InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
            if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && !this.isFood(itemstack) && laviathan.hasBodyGear() && !this.isBaby()) {
                if (!player.isShiftKeyDown()) {
                    if (!this.level().isClientSide) {
                        player.startRiding(this);
                    }
                } else {
                    this.ejectPassengers();
                }

                return InteractionResult.SUCCESS;
            } else {
                return type;
            }
        }
    }

    protected AILavithan(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At("HEAD"),cancellable = true)
     private void SnappingTurtleGoals(CallbackInfo ci) {
            super.tick();
            EntityLaviathan laviathan = (EntityLaviathan)(Object)this;
            laviathan.prevSwimProgress = laviathan.swimProgress;
            laviathan.prevBiteProgress = laviathan.biteProgress;
            laviathan.prevHeadHeight = laviathan.getHeadHeight();
            laviathan.yBodyRot = laviathan.getYRot();
            if (laviathan.shouldSwim()) {
                if (laviathan.swimProgress < 5.0F) {
                    ++laviathan.swimProgress;
                }
            } else if (laviathan.swimProgress > 0.0F) {
                --laviathan.swimProgress;
            }

            if (laviathan.isObsidian()) {
                if (!laviathan.hasObsidianArmor) {
                    laviathan.hasObsidianArmor = true;
                    laviathan.getAttribute(Attributes.ARMOR).setBaseValue(30.0);
                }
            } else if (laviathan.hasObsidianArmor) {
                laviathan.hasObsidianArmor = false;
                laviathan.getAttribute(Attributes.ARMOR).setBaseValue(10.0);
            }

            if (!laviathan.level().isClientSide) {
                if (!laviathan.isObsidian() && laviathan.isInWaterOrBubble()) {
                    if (laviathan.conversionTime < 300) {
                        ++laviathan.conversionTime;
                    } else {
                        laviathan.setObsidian(true);
                    }
                }

                if (laviathan.shouldSwim()) {
                    laviathan.fallDistance = 0.0F;
                }
            }

            float neckBase = 0.8F;
            if (!laviathan.isNoAi()) {
                Vec3[] avector3d = new Vec3[laviathan.allParts.length];

                for(int j = 0; j < laviathan.allParts.length; ++j) {
                    laviathan.allParts[j].collideWithNearbyEntities();
                    avector3d[j] = new Vec3(laviathan.allParts[j].getX(), laviathan.allParts[j].getY(), laviathan.allParts[j].getZ());
                }

                float yaw = laviathan.getYRot() * 0.017453292F;
                float neckContraction = 2.0F * Math.abs(laviathan.getHeadHeight() / 3.0F) + 0.5F * Math.abs(laviathan.getHeadYaw(0.0F) / 50.0F);

                int l;
                for(l = 0; l < laviathan.theEntireNeck.length; ++l) {
                    float f = (float)l / (float)laviathan.theEntireNeck.length;
                    float f1 = -(2.2F + (float)l - f * neckContraction);
                    float f2 = Mth.sin(yaw + Maths.rad((double)(f * laviathan.getHeadYaw(0.0F)))) * (1.0F - Math.abs(laviathan.getXRot() / 90.0F));
                    float f3 = Mth.cos(yaw + Maths.rad((double)(f * laviathan.getHeadYaw(0.0F)))) * (1.0F - Math.abs(laviathan.getXRot() / 90.0F));
                    laviathan.setPartPosition(laviathan.theEntireNeck[l], (double)(f2 * f1), (double)neckBase + Math.sin((double)f * Math.PI * 0.5) * (double)(laviathan.getHeadHeight() * 1.1F), (double)(-f3 * f1));
                }

                laviathan.setPartPosition(laviathan.seat1, (double)(laviathan.getXForPart(yaw, 145.0F) * 0.75F), 2.0, (double)(laviathan.getZForPart(yaw, 145.0F) * 0.75F));
                laviathan.setPartPosition(laviathan.seat2, (double)(laviathan.getXForPart(yaw, -145.0F) * 0.75F), 2.0, (double)(laviathan.getZForPart(yaw, -145.0F) * 0.75F));
                laviathan.setPartPosition(laviathan.seat3, (double)(laviathan.getXForPart(yaw, 35.0F) * 0.95F), 2.0, (double)(laviathan.getZForPart(yaw, 35.0F) * 0.95F));
                laviathan.setPartPosition(laviathan.seat4, (double)(laviathan.getXForPart(yaw, -35.0F) * 0.95F), 2.0, (double)(laviathan.getZForPart(yaw, -35.0F) * 0.95F));
                if (laviathan.level().isClientSide && laviathan.isChilling()) {
                    if (!laviathan.isBaby()) {
                        laviathan.level().addParticle(ParticleTypes.SMOKE, laviathan.getX() + (double)(laviathan.getXForPart(yaw, 158.0F) * 1.75F), laviathan.getY(1.0), laviathan.getZ() + (double)(laviathan.getZForPart(yaw, 158.0F) * 1.75F), 0.0, laviathan.random.nextDouble() / 5.0, 0.0);
                        laviathan.level().addParticle(ParticleTypes.SMOKE, laviathan.getX() + (double)(laviathan.getXForPart(yaw, -166.0F) * 1.48F), laviathan.getY(1.0), laviathan.getZ() + (double)(laviathan.getZForPart(yaw, -166.0F) * 1.48F), 0.0, laviathan.random.nextDouble() / 5.0, 0.0);
                        laviathan.level().addParticle(ParticleTypes.SMOKE, laviathan.getX() + (double)(laviathan.getXForPart(yaw, 14.0F) * 1.78F), laviathan.getY(0.9), laviathan.getZ() + (double)(laviathan.getZForPart(yaw, 14.0F) * 1.78F), 0.0, laviathan.random.nextDouble() / 5.0, 0.0);
                        laviathan.level().addParticle(ParticleTypes.SMOKE, laviathan.getX() + (double)(laviathan.getXForPart(yaw, -14.0F) * 1.6F), laviathan.getY(1.1), laviathan.getZ() + (double)(laviathan.getZForPart(yaw, -14.0F) * 1.6F), 0.0, laviathan.random.nextDouble() / 5.0, 0.0);
                    }

                    laviathan.level().addParticle(ParticleTypes.SMOKE, laviathan.headPart.getRandomX(0.6), laviathan.headPart.getY(0.9), laviathan.headPart.getRandomZ(0.6), 0.0, laviathan.random.nextDouble() / 5.0, 0.0);
                }

                for(l = 0; l < laviathan.allParts.length; ++l) {
                    laviathan.allParts[l].xo = avector3d[l].x;
                    laviathan.allParts[l].yo = avector3d[l].y;
                    laviathan.allParts[l].zo = avector3d[l].z;
                    laviathan.allParts[l].xOld = avector3d[l].x;
                    laviathan.allParts[l].yOld = avector3d[l].y;
                    laviathan.allParts[l].zOld = avector3d[l].z;
                }
            }
    }


}
