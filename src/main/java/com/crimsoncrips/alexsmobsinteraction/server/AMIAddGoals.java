package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.server.entity.EntityVoidWormDummy;
import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIVoidWormRejoin;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIVoidWormAttack;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIMantisMine;
import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIBloodedAttraction;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIEmuRangedTrigger;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMISeagullSteal;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import java.util.function.Predicate;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.CuriosCompat;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIAvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIEggHeldAttack;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFollowAsmon;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFollowNearestGoal;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIGrizzlyScavenge;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIHarvestCrop;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIPanicBurrow;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMISurroundEntity;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;


public class AMIAddGoals {

    private static final Set<Mob> APPLIED = Collections.newSetFromMap(new WeakHashMap<>());

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob) || !APPLIED.add(mob))
            return;

        AMIGoalSuppressions.apply(mob);

        AMIServerConfig common = AlexsMobsInteraction.COMMON_CONFIG;
        Entity entity = event.getEntity();

        if(entity instanceof EntityBunfungus bunfungus){
            if (common.UNSETTLING_BACKFIRE_ENABLED.get()) {
                bunfungus.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(bunfungus, LivingEntity.class, 2, true, false, livingEntity -> {
                    return livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO.get()) && !livingEntity.isAlliedTo(livingEntity);
                }));
            }
            if (common.CROP_FARMING_ENABLED.get()) {
                bunfungus.goalSelector.addGoal(2, new AMIHarvestCrop(bunfungus));
            }
        }


        if(entity instanceof EntityCaiman caiman){
            if (common.EGG_ATTACK_ENABLED.get()) {
                caiman.targetSelector.addGoal(8, new AMIEggHeldAttack<>(caiman, LivingEntity.class,true));
            }
        }

        if(entity instanceof EntityCentipedeHead centipede){
            if (common.LIGHT_FEAR_ENABLED.get()) {
                centipede.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(centipede, Player.class, 50, true, false, livingEntity -> {
                    return !CuriosCompat.hasLight(livingEntity) && centipede.getLastHurtByMob() != livingEntity ;
                }));

                centipede.goalSelector.addGoal(1, new AvoidEntityGoal<>(centipede, LivingEntity.class, 4.0F, 1.5, 2, (livingEntity) -> {
                    return centipede.getLastAttacker() != livingEntity && CuriosCompat.hasLight(livingEntity);
                }));
            }
        }

        if (entity instanceof Bee bee && common.FLOWERING_ATTRACTION_ENABLED.get()) {
            bee.goalSelector.addGoal(4, new AMIFollowNearestGoal<>(bee, EntityFlutter.class, 10, 1) {
                public boolean canContinueToUse() {
                    return bee.level().isDay();
                }
            });
        }

        if (entity instanceof EntityHummingbird hummingbird) {
            if (common.DAY_POLINATION_ENABLED.get()) {
                hummingbird.goalSelector.addGoal(4, new HummingbirdAIPollinate(hummingbird){
                    public boolean canUse() {
                        return super.canUse() && hummingbird.level().isDay();
                    }
                });
            }
            if (common.FLOWERING_ATTRACTION_ENABLED.get()) {
                hummingbird.goalSelector.addGoal(8, new AMIFollowNearestGoal<>(hummingbird, EntityFlutter.class, 10, 1.2) {
                    public boolean canContinueToUse() {
                        return hummingbird.level().isDay();
                    }
                });
            }
        }

        if (entity instanceof EntityRainFrog rainFrog && common.BURROW_AWAY_ENABLED.get()) {
            rainFrog.goalSelector.addGoal(1, new AMIPanicBurrow(rainFrog, 1.25D));
        }

        if (entity instanceof EntityFly fly && common.SCENTED_INTERACTION_ENABLED.get()) {
            fly.goalSelector.addGoal(3, new AMIAvoidBlockGoal(fly, 4, 1.8, 2.3, (pos) -> {
                BlockState state = fly.level().getBlockState(pos);
                if (state.is(BlockTags.CANDLES) && state.getBlock() instanceof CandleBlock){
                    return state.getValue(CandleBlock.LIT);
                } else return false;
            }));
            fly.goalSelector.addGoal(8, new AMIFollowNearestGoal<>(fly, LivingEntity.class, 1, 0.8, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.FLY_PESTER)));
        }

        if (entity instanceof EntityVoidWorm voidWorm) {
            voidWorm.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(voidWorm, EntityVoidWormDummy.class, 10, false, true, null));
        }

        if (entity instanceof EntityVoidWorm voidWorm && common.VOID_WORM_REWORK_ENABLED.get()) {
            voidWorm.goalSelector.addGoal(0, new AMIVoidWormRejoin(voidWorm));
            voidWorm.goalSelector.addGoal(2, new AMIVoidWormAttack(voidWorm));
            if (voidWorm.getWormSpeed() <= 0.0F) {
                voidWorm.setWormSpeed(1.0F);
            }
            if (voidWorm.getChildId() == null && !voidWorm.getData(AMIAttachments.BOSS_SCALED)) {
                voidWorm.setSegmentCount(voidWorm.getSegmentCount() * AMIVoidWormBoss.SEGMENT_MULTIPLIER);
                voidWorm.setBaseMaxHealth(voidWorm.getBaseMaxHealth() * AMIVoidWormBoss.HEALTH_MULTIPLIER, true);
                voidWorm.setData(AMIAttachments.BOSS_SCALED, true);
            }
        }

        if (entity instanceof EntityMurmur murmur && common.MURMUR_REGROW_ENABLED.get()) {
            murmur.goalSelector.addGoal(2, new AvoidEntityGoal<>(murmur, LivingEntity.class, 10.0F, 1.8, 2){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && murmur.getHead() == null;
                }
            });
        }

        if (entity instanceof EntityGrizzlyBear grizzlyBear && common.HONEYLESS_HUNTING_ENABLED.get()) {
            grizzlyBear.goalSelector.addGoal(6, new AMIGrizzlyScavenge(grizzlyBear,  1.2, 12));
        }

        if (entity instanceof EntityAlligatorSnappingTurtle snappingTurtle && common.SNAPPING_DORMANCY_ENABLED.get()) {
            snappingTurtle.goalSelector.addGoal(5, new RandomLookAroundGoal(snappingTurtle){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !snappingTurtle.getData(AMIAttachments.DAY_SLEEPING);
                }
            });
            snappingTurtle.goalSelector.addGoal(6, new LookAtPlayerGoal(snappingTurtle, Player.class, 6.0F){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !snappingTurtle.getData(AMIAttachments.DAY_SLEEPING);
                }
            });
        }

        if (entity instanceof EntityMantisShrimp mantisShrimp && common.MANTIS_MINING_ENABLED.get()) {
            mantisShrimp.goalSelector.addGoal(0, new AMIMantisMine(mantisShrimp));
        }

        if (entity instanceof EntityCockroach cockroach) {
            cockroach.goalSelector.addGoal(1, new PanicGoal(cockroach, 1.1){
                public boolean canUse() {
                    return super.canUse() && !cockroach.getData(AMIAttachments.IS_GOD) && AMIUtils.getWorshiping(cockroach) == null;
                }
            });
            cockroach.goalSelector.addGoal(4, new AvoidEntityGoal<>(cockroach, EntityCentipedeHead.class, 16.0F, 1.3, 1.0F){
                public boolean canUse() {
                    return super.canUse() && !cockroach.getData(AMIAttachments.IS_GOD) && AMIUtils.getWorshiping(cockroach) == null;
                }
            });
            cockroach.goalSelector.addGoal(4, new AvoidEntityGoal<>(cockroach, Player.class, 8.0F, 1.3, 1.0F) {
                public boolean canUse() {
                    return !cockroach.isBreaded() && super.canUse() && !cockroach.getData(AMIAttachments.IS_GOD) && AMIUtils.getWorshiping(cockroach) == null;
                }
            });
            cockroach.goalSelector.addGoal(8, new AMISurroundEntity(cockroach));
            cockroach.goalSelector.addGoal(9, new AMIFollowAsmon(cockroach));
        }

        if (mob instanceof EntityEmu emu) {
            if (common.EGG_ATTACK_ENABLED.get()) {
                emu.targetSelector.addGoal(6, new AMIEggHeldAttack<>(emu, LivingEntity.class, true));
            }
            if (common.RANGED_AGGRO_ENABLED.get()) {
                emu.targetSelector.addGoal(7, new AMIEmuRangedTrigger(emu, LivingEntity.class, true));
            }
        }

        if (mob instanceof EntityFrilledShark frilledShark) {
            if (common.BLEEDING_HUNGER_ENABLED.get()) {
                frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, Player.class, 50, true, true, livingEntity -> {
                    return livingEntity.hasEffect(AMEffectRegistry.EXSANGUINATION);
                }));
                frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, EntityGiantSquid.class, 300, false, true, livingEntity -> {
                    return livingEntity.getHealth() <= 0.25F * livingEntity.getMaxHealth();
                }));
            }
        }

        if ((mob instanceof IronGolem || mob instanceof SnowGolem) && common.UNSETTLING_BACKFIRE_ENABLED.get()) {
            mob.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(mob, Player.class, 10, true, false, (livingEntity) -> {
                return livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO.get());
            }));
        }

        if (mob instanceof EntityEnderiophage enderiophage && common.INFECT_INTERACTION_ENABLED.get()) {
            enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, EnderMan.class, 15, true, true, (livingEntity) -> {
                return !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
            }) {
                public boolean canUse() {
                    return enderiophage.isMissingEye() && super.canUse();
                }

                public boolean canContinueToUse() {
                    return enderiophage.isMissingEye() && super.canContinueToUse();
                }
            });
            enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, LivingEntity.class, 15, true, true, ENDERGRADE_OR_INFECTED) {
                public boolean canUse() {
                    Object fleeAfterSteal = AMIReflectionUtil.getField(enderiophage, "fleeAfterStealTime");
                    if (fleeAfterSteal == null) {
                        return false;
                    }
                    return !enderiophage.isMissingEye() && (int) fleeAfterSteal == 0  && super.canUse();
                }

                public boolean canContinueToUse() {
                    return !enderiophage.isMissingEye() && super.canContinueToUse();
                }
            });
        }

        if (mob instanceof EntityKomodoDragon komodoDragon) {
            komodoDragon.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(komodoDragon, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.KOMODO_DRAGON_TARGETS)){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && (!komodoDragon.isTame() || !common.TAMED_FRIENDLIES_ENABLED.get());
                }
            });
            if (common.TAMED_FRIENDLIES_ENABLED.get()) {
                komodoDragon.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(komodoDragon, EntityKomodoDragon.class, 50, true, false, (livingEntity) -> {
                    return livingEntity.isBaby() || livingEntity.getHealth() <= 0.7F * livingEntity.getMaxHealth();
                }){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !komodoDragon.isTame();
                    }
                });
                komodoDragon.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(komodoDragon, Player.class, 150, true, true, null){
                    @Override
                    public boolean canContinueToUse() {
                        return super.canContinueToUse() && !komodoDragon.isTame();
                    }
                });
            }
        }

        if (mob instanceof EntitySeagull seagull && common.SNATCH_INTERACTION_ENABLED.get()) {
            seagull.targetSelector.addGoal(2, new AMISeagullSteal(seagull){
                public boolean canUse() {
                    return super.canUse() && !(seagull.getHealth() <= 0.40F * seagull.getMaxHealth());
                }
            });
        }

        if (mob instanceof EntitySnowLeopard snowLeopard && common.LEOPARD_DESIRES_ENABLED.get()) {
            snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, EntityMoose.class, 100, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.35F * livingEntity.getMaxHealth();
            }));
            snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, Player.class, 100, true, false, (livingEntity) -> {
                return livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(AMItemRegistry.MOOSE_HEADGEAR.get());
            }));
        }

        if (mob instanceof EntitySoulVulture soulVulture) {
            soulVulture.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(soulVulture, Hoglin.class, true));
            soulVulture.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(soulVulture, EntityDropBear.class, true));
            soulVulture.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(soulVulture, EntityBoneSerpent.class, 0, true, false, (livingEntity) -> {
                return !livingEntity.isInLava();
            }));
        }

        if (mob instanceof EntityTiger tiger) {
            tiger.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(tiger, Pillager.class, true));
        }

        if (mob instanceof EntityWarpedToad warpedToad && common.TAMED_FRIENDLIES_ENABLED.get()) {
            warpedToad.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(warpedToad, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.WARPED_TOAD_TARGETS)){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !warpedToad.isTame();
                }
            });
        }

        if (mob instanceof EntityElephant elephant && common.TUSKED_TERRITORIAL_ENABLED.get()) {
            elephant.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(elephant, Player.class, 1000, true, true, (livingEntity -> {
                return livingEntity.isHolding(Ingredient.of(AMItemRegistry.ACACIA_BLOSSOM.get()));
            })) {
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && elephant.isTusked() && !elephant.isTame();
                }
            });
        }

        if (mob instanceof EntityCrimsonMosquito crimsonMosquito && common.HEMOGENICISM_ENABLED.get()) {
            crimsonMosquito.targetSelector.addGoal(2, new AMIBloodedAttraction(crimsonMosquito, Player.class, 10, true, false, (livingEntity) -> !livingEntity.hasEffect(AMEffectRegistry.MOSQUITO_REPELLENT)));
            crimsonMosquito.targetSelector.addGoal(2, new AMIBloodedAttraction(crimsonMosquito, LivingEntity.class, 30, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS)));
        }

        if (mob instanceof EntityLeafcutterAnt leafcutterAnt && common.ANT_WAR_ENABLED.get()) {
            leafcutterAnt.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(leafcutterAnt, EntityLeafcutterAnt.class, 100, false, true, livingEntity ->  {
                return livingEntity.getData(AMIAttachments.VARIANT) != leafcutterAnt.getData(AMIAttachments.VARIANT);
            }){
                public boolean canUse() {
                    return super.canUse() && !leafcutterAnt.hasLeaf();
                }
            });
        }

        if (mob instanceof EntityAlligatorSnappingTurtle snappingTurtle) {
            snappingTurtle.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(snappingTurtle, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.SIGNIFICANT_PREY)){
                @Override
                public boolean canUse() {
                    return snappingTurtle.isInWater() && super.canUse();
                }
                @Override
                protected AABB getTargetSearchArea(double targetDistance) {
                    return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
                }
            });
        }
    }

    private static final Predicate<LivingEntity> ENDERGRADE_OR_INFECTED = (entity) -> !entity.hasEffect(MobEffects.DAMAGE_RESISTANCE) && (entity instanceof EntityEndergrade || entity.hasEffect(AMEffectRegistry.ENDER_FLU));
}
