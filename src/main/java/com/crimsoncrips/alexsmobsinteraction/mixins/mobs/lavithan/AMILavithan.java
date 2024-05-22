package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.lavithan;

import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMILavithanInterface;
import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityLaviathan.class)
public abstract class AMILavithan extends Animal implements ISemiAquatic, IHerdPanic , AMILavithanInterface {


    static{
        RELAVA = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
        RELAVATICK = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.INT);
    }

    private static final EntityDataAccessor<Boolean> RELAVA;
    private static final EntityDataAccessor<Integer> RELAVATICK;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(RELAVA, false);
        this.entityData.define(RELAVATICK, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("Relava", this.isRelava());
        compound.putInt("RelavaTicks", this.getRelavaTicks());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setRelava(compound.getBoolean("Relava"));
        this.setRelavaTicks(compound.getInt("RelavaTicks"));

    }

    public int getRelavaTicks() {
        return (Integer)this.entityData.get(RELAVATICK);
    }

    public void setRelavaTicks(int relavaTime) {
        this.entityData.set(RELAVATICK, relavaTime);
    }


    private boolean hasObsidianArmor;

    private int conversionTime = 0;

    public final EntityLaviathanPart[] allParts;



    protected AMILavithan(EntityType<? extends Animal> pEntityType, Level pLevel, EntityLaviathanPart[] allParts, EntityLaviathanPart[] allParts1) {
        super(pEntityType, pLevel);
        this.allParts = allParts1;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        EntityLaviathan laviathan = (EntityLaviathan)(Object)this;
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (itemstack.is(AMInteractionTagRegistry.LAVITHAN_PICKAXES) && laviathan.isObsidian() && !this.isRelava() && AMInteractionConfig.OBSIDIAN_EXTRACT_ENABLED){
            this.setRelava(true);
            laviathan.setObsidian(false);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.BASALT_BREAK, this.getSoundVolume(), this.getVoicePitch());

            if(!this.isBaby()) {
                for (int i = 0; i < 6; i++) this.spawnAtLocation(Items.OBSIDIAN);
                if (random.nextDouble() < 0.1) this.spawnAtLocation(Items.OBSIDIAN);
            } else {
                for (int i = 0; i < 2; i++) this.spawnAtLocation(Items.OBSIDIAN);
                if (random.nextDouble() < 0.05) this.spawnAtLocation(Items.OBSIDIAN);
            }

            itemstack.hurtAndBreak(1, this, (p_233654_0_) -> {
            });
            this.setHealth(this.getHealth() - 3);
            itemstack.hurtAndBreak(10, this, (p_233654_0_) -> {
            });
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

    public boolean isRelava() {
        return this.entityData.get(RELAVA);
    }

    public void setRelava(boolean relava) {
        this.entityData.set(RELAVA, relava);
    }

    public void tick() {
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
            if (!this.hasObsidianArmor) {
                this.hasObsidianArmor = true;
                laviathan.getAttribute(Attributes.ARMOR).setBaseValue(30.0);
            }
        } else if (this.hasObsidianArmor) {
            this.hasObsidianArmor = false;
            laviathan.getAttribute(Attributes.ARMOR).setBaseValue(10.0);
        }

        if (!this.level().isClientSide) {
            if (!laviathan.isObsidian() && this.isInWaterOrBubble() && (!this.isRelava() || !AMInteractionConfig.OBSIDIAN_EXTRACT_ENABLED)) {
                if (conversionTime < 300) {
                    conversionTime++;
                } else {
                    laviathan.setObsidian(true);
                    conversionTime = 0;
                }

            }
            if (!laviathan.isObsidian() && !this.isInWaterOrBubble() && this.isRelava() && AMInteractionConfig.OBSIDIAN_EXTRACT_ENABLED) {
                if (getRelavaTicks() < 5000) {
                    this.setRelavaTicks(getRelavaTicks() + 1);
                } else {
                    this.setRelava(false);
                    this.setRelavaTicks(0);
                }


            }
            if (laviathan.shouldSwim()) {
                fallDistance = 0.0F;
            }
        }

        float neckBase = 0.8F;
        if (!laviathan.isNoAi()) {
            Vec3[] avector3d = new Vec3[laviathan.allParts.length];


            ReflectionUtil.callMethod(
                    collideWithNearbyEntities(),
                    "collideWithNearbyEntities",
                    new Class[] { Integer.class, Float.class },
                    new Object[] { 42, 64.0F });

            for (int j = 0; j < laviathan.allParts.length; ++j) {
                ReflectionUtil.callMethod(laviathan.allParts[j], "collideWithNearbyEntities", new Class[0], new Object[0]);
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
                this.setPartPosition(laviathan.theEntireNeck[l], (double)(f2 * f1), (double)neckBase + Math.sin((double)f * Math.PI * 0.5) * (double)(laviathan.getHeadHeight() * 1.1F), (double)(-f3 * f1));
            }

            this.setPartPosition(laviathan.seat1, (double)(this.getXForPart(yaw, 145.0F) * 0.75F), 2.0, (double)(this.getZForPart(yaw, 145.0F) * 0.75F));
            this.setPartPosition(laviathan.seat2, (double)(this.getXForPart(yaw, -145.0F) * 0.75F), 2.0, (double)(this.getZForPart(yaw, -145.0F) * 0.75F));
            this.setPartPosition(laviathan.seat3, (double)(this.getXForPart(yaw, 35.0F) * 0.95F), 2.0, (double)(this.getZForPart(yaw, 35.0F) * 0.95F));
            this.setPartPosition(laviathan.seat4, (double)(this.getXForPart(yaw, -35.0F) * 0.95F), 2.0, (double)(this.getZForPart(yaw, -35.0F) * 0.95F));
            if (this.level().isClientSide && laviathan.isChilling()) {
                if (!this.isBaby()) {
                    this.level().addParticle(ParticleTypes.SMOKE, this.getX() + getXForPart(yaw, 158) * 1.75F, this.getY(1), this.getZ() + getZForPart(yaw, 158) * 1.75F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level().addParticle(ParticleTypes.SMOKE, this.getX() + getXForPart(yaw, -166) * 1.48F, this.getY(1), this.getZ() + getZForPart(yaw, -166) * 1.48F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level().addParticle(ParticleTypes.SMOKE, this.getX() + getXForPart(yaw, 14) * 1.78F, this.getY(0.9), this.getZ() + getZForPart(yaw, 14) * 1.78F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level().addParticle(ParticleTypes.SMOKE, this.getX() + getXForPart(yaw, -14) * 1.6F, this.getY(1.1), this.getZ() + getZForPart(yaw, -14) * 1.6F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                }
                this.level().addParticle(ParticleTypes.SMOKE, laviathan.headPart.getRandomX(0.6D), laviathan.headPart.getY(0.9), laviathan.headPart.getRandomZ(0.6D), 0.0D, this.random.nextDouble() / 5.0D, 0.0D);

            }
            if (this.level().isClientSide && this.isRelava() && AMInteractionConfig.OBSIDIAN_EXTRACT_ENABLED) {

                if (!this.isBaby() ) {
                    if (random.nextDouble() < 0.1) {
                        this.level().addParticle(ParticleTypes.LAVA, this.getX() + getXForPart(yaw, 158) * 1.75F, this.getY(1), this.getZ() + getZForPart(yaw, 158) * 1.75F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                        this.level().addParticle(ParticleTypes.LAVA, this.getX() + getXForPart(yaw, -166) * 1.48F, this.getY(1), this.getZ() + getZForPart(yaw, -166) * 1.48F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                        this.level().addParticle(ParticleTypes.LAVA, this.getX() + getXForPart(yaw, 14) * 1.78F, this.getY(0.9), this.getZ() + getZForPart(yaw, 14) * 1.78F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                        this.level().addParticle(ParticleTypes.LAVA, this.getX() + getXForPart(yaw, -14) * 1.6F, this.getY(1.1), this.getZ() + getZForPart(yaw, -14) * 1.6F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    }
                }

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
    private void setPartPosition(EntityLaviathanPart part, double offsetX, double offsetY, double offsetZ) {
        part.setPos(this.getX() + offsetX * (double)part.scale, this.getY() + offsetY * (double)part.scale, this.getZ() + offsetZ * (double)part.scale);
    }
    private float getXForPart(float yaw, float degree) {
        return Mth.sin((float)((double)yaw + Math.toRadians((double)degree)));
    }
    private float getZForPart(float yaw, float degree) {
        return -Mth.cos((float)((double)yaw + Math.toRadians((double)degree)));
    }

    protected Object collideWithNearbyEntities() {
        return null;
    }


}
