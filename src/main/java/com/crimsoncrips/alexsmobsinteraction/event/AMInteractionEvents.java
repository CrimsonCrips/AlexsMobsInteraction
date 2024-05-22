package com.crimsoncrips.alexsmobsinteraction.event;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.goal.AMISeagullSteal;
import com.crimsoncrips.alexsmobsinteraction.goal.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.goal.AMIFollowNearestGoal;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.github.alexthe666.alexsmobs.entity.ai.MantisShrimpAIBreakBlocks;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

import static com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry.CROCODILE_BABY_KILL;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AMInteractionEvents {


    @SubscribeEvent
    public void onEntityFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        final var entity = event.getEntity();
        //Vanilla Mobs

        if (AMInteractionConfig.SPIDER_EAT_ENABLED && entity instanceof final Spider spider) {
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityCockroach.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Silverfish.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Bee.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityFly.class, 2, true, false, null));
        }


        //AM Mobs
        if (entity instanceof EntityAnaconda anaconda){
            Predicate<LivingEntity> ANACONDA_BABY_TARGETS = AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ANACONDA_BABY_KILL);
            anaconda.goalSelector.removeAllGoals(goal -> {
                return goal instanceof HurtByTargetGoal;
            });
            anaconda.targetSelector.addGoal(3, new HurtByTargetGoal(anaconda, EntityAnaconda.class));
            anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, LivingEntity.class, 1000, true, false, (livingEntity) -> {
                return ANACONDA_BABY_TARGETS.test(livingEntity) && anaconda.level().isNight() && livingEntity.isBaby();
            }) {
                protected AABB getTargetSearchArea(double targetDistance) {
                    return anaconda.getBoundingBox().inflate(25D, 1D, 25D);
                }
            });
            if (AMInteractionConfig.anacondacanibalize) {
                anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 2500, true, false, (livingEntity) -> {
                    return (livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth() || livingEntity.isBaby());
                }));
            }
        }

        if (AMInteractionConfig.EAGLE_CANNIBALIZE_ENABLED && entity instanceof final EntityBaldEagle baldEagle && !baldEagle.isTame()) {
            baldEagle.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(baldEagle, EntityBaldEagle.class, 1000, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
            }));
        }

        if(AMInteractionConfig.PREY_FEAR_ENABLED) {
            if (entity instanceof final EntityBananaSlug bananaSlug) {
                bananaSlug.goalSelector.addGoal(3, new AvoidEntityGoal<>(bananaSlug, LivingEntity.class, 2.0F, 1.2, 1.5, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SMALLINSECTFEAR)));
            }
            if (entity instanceof final EntityRaccoon raccoon) {
                raccoon.goalSelector.addGoal(3, new AvoidEntityGoal<>(raccoon, LivingEntity.class, 8.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RACCOON_FEAR)));
            }
            if (entity instanceof final EntityJerboa jerboa) {
                jerboa.goalSelector.addGoal(3, new AvoidEntityGoal<>(jerboa, LivingEntity.class, 7.0F, 1.7D, 1.4,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.JERBOAFEAR)));
            }
            if (entity instanceof final EntityRainFrog rainFrog) {
                rainFrog.goalSelector.addGoal(3, new AvoidEntityGoal<>(rainFrog, LivingEntity.class, 10.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RAINFROG_FEAR)));
            }
            if (entity instanceof final EntityBlobfish blobfish) {
                blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, Player.class, 5.0F, 1.2, 1.4));
                blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, EntityGiantSquid.class, 5.0F, 1.2, 1.4));
            }
            if (entity instanceof final EntityCatfish catfish) {
                catfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(catfish, LivingEntity.class, 4.0F, 1.1, 1.3, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.FISHFEAR)));
            }
            if (entity instanceof final EntityDevilsHolePupfish devilsHolePupfish){
                devilsHolePupfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(devilsHolePupfish, LivingEntity.class, 2.0F, 1.1, 1.3, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.FISHFEAR)));
            }
            if (entity instanceof final EntityFlyingFish flyingFish){
                flyingFish.goalSelector.addGoal(3, new AvoidEntityGoal<>(flyingFish, LivingEntity.class, 4.0F, 1.3, 1.7, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.FISHFEAR)));
            }
            if (entity instanceof final EntitySugarGlider sugarGlider) {
                sugarGlider.goalSelector.addGoal(3, new AvoidEntityGoal<>(sugarGlider, LivingEntity.class, 2.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SMALLINSECTFEAR)));
            }
        }

        if (entity instanceof final EntityBoneSerpent boneSerpent ){
            boneSerpent.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(boneSerpent, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.BONESERPENT_KILL)));
            if(AMInteractionConfig.SERPENT_FEAR_ENABLED) {
                boneSerpent.goalSelector.addGoal(7, new AvoidEntityGoal<>(boneSerpent, EntityLaviathan.class, 10.0F, 1.6, 2));
            }
        }

        if (entity instanceof final EntityCaiman caiman){
            if(AMInteractionConfig.CAIMAN_AGGRO_ENABLED && !caiman.isTame()) {
                caiman.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(caiman, Player.class, 150, true, true, null));
            }
            if (AMInteractionConfig.CAIMAN_EGG_ATTACK_ENABLED && !caiman.isBaby()) {
                caiman.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(caiman, LivingEntity.class, 0, true, false, (livingEntity) -> {
                    return livingEntity.isHolding( Ingredient.of(AMBlockRegistry.CAIMAN_EGG.get()));
                }));
            }
        }

        if (entity instanceof final EntityCapuchinMonkey capuchinMonkey && AMInteractionConfig.CAPUCHIN_HUNT_ENABLED && capuchinMonkey.isTame()) {
            capuchinMonkey.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(capuchinMonkey, LivingEntity.class, 400, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CAPUCHIN_KILL)));
        }

        if (entity instanceof final EntityCachalotWhale cachalotWhale && cachalotWhale.isSleeping() && !cachalotWhale.isBeached()){
            cachalotWhale.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(cachalotWhale, LivingEntity.class, 300, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CACHALOT_WHALE_KILL_CHANCE)));
        }

        if (entity instanceof final EntityCentipedeHead centipede){
            if (AMInteractionConfig.LIGHT_FEAR_ENABLED) {
                centipede.goalSelector.addGoal(1, new AvoidEntityGoal<>(centipede, LivingEntity.class, 4.0F, 1.5, 2, (livingEntity) -> {
                    return livingEntity.isHolding(Ingredient.of(AMInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR));
                }));
                centipede.goalSelector.addGoal(1, new AvoidBlockGoal(centipede, 4,1,1.2,(pos) -> {
                    BlockState state = centipede.level().getBlockState(pos);
                    return state.is(AMInteractionTagRegistry.CENTIPEDE_BLOCK_FEAR);
                }));
            }
            centipede.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(centipede, LivingEntity.class, 55, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CAVE_CENTIPEDE_KILL)));

        }

        if (entity instanceof final EntityCrocodile crocodile && !crocodile.isBaby() && !crocodile.isTame()){
            crocodile.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(crocodile, LivingEntity.class, 5000, true, false,AMEntityRegistry.buildPredicateFromTag(CROCODILE_BABY_KILL)){
                protected AABB getTargetSearchArea(double targetDistance) {
                    return this.mob.getBoundingBox().inflate(25D, 1D, 25D);
                }
            });
        }

        if (entity instanceof final EntityCrow crow && !crow.isTame() && !crow.isBaby()){
            crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.CROW_KILL)));
            if (AMInteractionConfig.CROW_CANNIBALIZE_ENABLED){
                crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, EntityCrow.class, 500, true, true, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.10F * livingEntity.getMaxHealth();
                }));
            }
        }

        if (entity instanceof final EntityDropBear dropBear){
            dropBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(dropBear, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.DROPBEAR_KILL)){
                protected AABB getTargetSearchArea(double targetDistance) {
                    AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
                    return new AABB(bb.minX, 0.0, bb.minZ, bb.maxX, 256.0, bb.maxZ);
                }
            });
        }

        if (entity instanceof final EntityElephant elephant && elephant.isTusked() && !elephant.isTame() && AMInteractionConfig.ELEPHANT_TERRITORIAL_ENABLED){
            elephant.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(elephant, Player.class, 1500, true, true,(entity1 -> {
                return entity1.isHolding(Ingredient.of(AMItemRegistry.ACACIA_BLOSSOM.get()));
            })));
        }

        if (entity instanceof final EntityEmu emu && !emu.isBaby()){
            if (AMInteractionConfig.EMU_EGG_ATTACK_ENABLED){
                emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 5, true, false, (livingEntity) -> {
                    return livingEntity.isHolding( Ingredient.of(AMItemRegistry.EMU_EGG.get())) || livingEntity.isHolding( Ingredient.of(AMItemRegistry.BOILED_EMU_EGG.get()));
                }));
            }
            if (AMInteractionConfig.RANGED_AGGRO_ENABLED){
                emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 70, true, false, (livingEntity) -> {
                    return livingEntity.isHolding(Ingredient.of(AMInteractionTagRegistry.EMU_TRIGGER));
                }));
            }
            emu.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.EMU_KILL)));
            if(AMInteractionConfig.EMU_SCUFFLE_ENABLED){
                emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, EntityEmu.class, 1000, false, true, null) {
                    public boolean canUse() {
                        return !emu.isLeashed() && super.canUse() && emu.level().isDay();
                    }
                });
            }
        }

        if (entity instanceof final EntityFly fly){
            Predicate<LivingEntity> PESTERTARGET = AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.FLY_PESTER);
            if(AMInteractionConfig.FLY_FEAR_ENABLED) {
                fly.goalSelector.addGoal(7, new AvoidEntityGoal<>(fly, LivingEntity.class, 2.0F, 1.1, 1.3, (livingEntity) ->{
                    return !PESTERTARGET.test(livingEntity) && !(livingEntity instanceof EntityFly);
                }));
            }
            if(AMInteractionConfig.CANDLE_REPEL_ENABLED){
                fly.goalSelector.addGoal(3, new AvoidBlockGoal(fly, 4, 1, 1.2, (pos) -> {
                    BlockState state = fly.level().getBlockState(pos);
                    return state.is(BlockTags.CANDLES);
                }));
            }
            if(AMInteractionConfig.FLY_PESTER_ENABLED) {
                fly.goalSelector.addGoal(8, new AMIFollowNearestGoal<>(fly, LivingEntity.class, 1, 0.8, PESTERTARGET));
            }
        }

        if (entity instanceof final EntityEnderiophage enderiophage){

            if(AMInteractionConfig.INFECT_IMMUNITY_ENABLED){
                enderiophage.goalSelector.removeAllGoals(goal -> {
                    return goal instanceof EntityAINearestTarget3D;
                });
                enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, EnderMan.class, 15, true, true, (livingEntity) -> {
                    return !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
                }) {
                    public boolean canUse() {
                        return enderiophage.isMissingEye() && super.canUse();
                    }
                });
                enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, LivingEntity.class, 15, true, true, (livingEntity) -> {
                        return (livingEntity instanceof EntityEndergrade || livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get())) && !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
                }) {
                    public boolean canUse() {
                        return !enderiophage.isMissingEye() && (int) ReflectionUtil.getField(enderiophage, "fleeAfterStealTime") == 0 && super.canUse();
                    }
                });
            }
            if(AMInteractionConfig.INFECT_WEAK_ENABLED && !enderiophage.isMissingEye() && (int) ReflectionUtil.getField(enderiophage, "fleeAfterStealTime") == 0){
                enderiophage.goalSelector.removeAllGoals(goal -> {
                    return goal instanceof EntityAINearestTarget3D;
                });
                enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, Player.class, 15, true, true, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.40F * livingEntity.getMaxHealth() && !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
                }));
            }


        }

        else if (entity instanceof EntityFarseer farseer && AMInteractionConfig.FARSEER_HUMANLIKE_ATTACK_ENABLED){
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Raider.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Villager.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, WanderingTrader.class, 3, false, true, null));
        }

        else if(entity instanceof EntityFrilledShark frilledShark){
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, LivingEntity.class, 1, false, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.FRILLED_KILL)));
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, EntityGiantSquid.class, 1, false, true,(livingEntity) -> {
                return livingEntity.getHealth() <= 0.25F * livingEntity.getMaxHealth();
            }));
        }

        else if(AMInteractionConfig.GELADA_HUNT_ENABLED && entity instanceof EntityGeladaMonkey geladaMonkey){
            geladaMonkey.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(geladaMonkey, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SMALLCRITTER)));
        }

        else if(entity instanceof EntityGiantSquid giantSquid && AMInteractionConfig.SQUID_CANNIBALIZE_ENABLED && giantSquid.isInWaterOrBubble() && !giantSquid.isCaptured()) {
            giantSquid.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(giantSquid, EntityGiantSquid.class, 200, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
            }));
        }

        else if(entity instanceof EntityGorilla gorilla){
            gorilla.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(gorilla, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SMALLCRITTER)));
        }

        else if(entity instanceof EntityHummingbird hummingbird){
            if(AMInteractionConfig.POLINATE_DAY_ENABLED) {
            hummingbird.goalSelector.removeAllGoals(goal -> {
                return goal instanceof HummingbirdAIPollinate;
            });

            hummingbird.goalSelector.addGoal(4, new HummingbirdAIPollinate(hummingbird){
                public boolean canUse() {
                        return super.canUse() && hummingbird.level().isDay();
                    }
            });
            }
            if(AMInteractionConfig.HUMMING_FOLLOW_ENABLED) {
                hummingbird.goalSelector.addGoal(8, new AMIFollowNearestGoal<>(hummingbird, EntityFlutter.class, 10, 1.2, (livingEntity) -> true) {
                    public boolean canContinueToUse() {
                        return hummingbird.level().isDay();
                    }
                });
            }
        }

        if (entity instanceof EntityHammerheadShark hammerheadShark){
            if (AMInteractionConfig.HAMMERHEAD_MANTIS_EAT_ENABLED){
                hammerheadShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(hammerheadShark, EntityMantisShrimp.class, 50, true, false, (mob) -> {
                    return mob.getHealth() <= 0.2 * mob.getMaxHealth();
                }));
            }
            hammerheadShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(hammerheadShark, LivingEntity.class, 0, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.HAMMERHEAD_KILL)));
        }

        if (entity instanceof EntityMantisShrimp mantisShrimp && !mantisShrimp.isTame()){
            if(AMInteractionConfig.MANTIS_AGGRO_ENABLED  && !mantisShrimp.isBaby()) {
                mantisShrimp.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mantisShrimp, Player.class, 150, true, true, null));
            }
            if(AMInteractionConfig.MANTIS_CANNIBALIZE_ENABLED) {
                mantisShrimp.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(mantisShrimp, EntityMantisShrimp.class, 200, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
                }));
            }
        }

        if (entity instanceof EntityMudskipper mudskipper && AMInteractionConfig.MUDSKIPPER_HUNT_ENABLED) {
            mudskipper.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(mudskipper, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.MUDSKIPPER_KILL)));
        }

        if(entity instanceof EntityOrca orca && AMInteractionConfig.ORCA_HUNT_ENABLED){
            orca.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(orca, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ORCA_KILL)));
            orca.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(orca, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ORCA_CHANCE_KILL)));
        }

        if(entity instanceof EntityPotoo potoo){
            potoo.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(potoo, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.POTOO_KILL)));
        }

        if (entity instanceof EntityRaccoon raccoon && AMInteractionConfig.RACOON_HUNT_ENABLED) {
            raccoon.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(raccoon, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RACCOON_KILL)) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !raccoon.isTame() && raccoon.level().isNight();
                }

                protected AABB getTargetSearchArea(double targetDistance) {
                    return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
                }
            });
        }

        if (entity instanceof EntityRattlesnake rattlesnake){
            rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, LivingEntity.class, 300, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.RATTLESNAKE_KILL)) {
                public void start(){
                    super.start();
                }
            });
            if (AMInteractionConfig.RATTLESNAKE_CANNIBALIZE_ENABLED) {
                rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, EntityRattlesnake.class, 1500, true, true, (livingEntity) -> {
                    return (livingEntity.getHealth() <= 0.60F * livingEntity.getMaxHealth() || livingEntity.isBaby());
                }) {
                    public void start() {
                        super.start();
                    }
                });
            }
        }

        if (entity instanceof EntityRoadrunner roadrunner && AMInteractionConfig.ROADRUNNER_DAY_ENABLED){
            roadrunner.goalSelector.removeAllGoals(goal -> {
                return goal instanceof NearestAttackableTargetGoal;
            });
                roadrunner.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(roadrunner, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.ROADRUNNER_KILL)) {
                    public boolean canUse() {
                        return super.canUse();
                    }
                });
        }

        if (entity instanceof EntitySeagull seagull && AMInteractionConfig.SEAGULL_WEAKEN_ENABLED){
            seagull.goalSelector.removeAllGoals(goal -> {
                return goal instanceof SeagullAIStealFromPlayers;
            });
            seagull.targetSelector.addGoal(2, new AMISeagullSteal(seagull){
                public boolean canUse() {
                        return super.canUse() && !(seagull.getHealth() <= 0.40F * seagull.getMaxHealth());
                }
            });
        }

        if (entity instanceof EntityShoebill shoebill){
            shoebill.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(shoebill, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SHOEBILL_BABY_KILL)));
            shoebill.targetSelector.addGoal(7, new EntityAINearestTarget3D<>(shoebill, EntityTerrapin.class, 100, false, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.SHOEBILL_KILL)));
        }

        if (entity instanceof EntitySnowLeopard snowLeopard){
            if(AMInteractionConfig.LEOPARD_DESIRES_ENABLED){
                snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, EntityMoose.class, 100, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
                }));
                snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, Player.class, 100, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth() && livingEntity.getItemBySlot(EquipmentSlot.HEAD).is((Item)AMItemRegistry.MOOSE_HEADGEAR.get());
                }));
            }
        }

        if(entity instanceof EntityStraddler straddler && AMInteractionConfig.STRADDLER_VENGEANCE_ENABLED) {
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityBoneSerpent.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityCrimsonMosquito.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityWarpedMosco.class, true));
        }

        if (entity instanceof EntityTasmanianDevil tasmanianDevil){
            tasmanianDevil.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(tasmanianDevil, LivingEntity.class, 10, false, true, AMEntityRegistry.buildPredicateFromTag(AMInteractionTagRegistry.TASMANIAN_KILL)));
        }

        if (entity instanceof EntityTusklin tusklin && AMInteractionConfig.TUSKLIN_FLEE_ENABLED){
            tusklin.goalSelector.addGoal(3, new AvoidBlockGoal(tusklin, 4, 1, 1.2, (pos) -> {
                BlockState state = tusklin.level().getBlockState(pos);
                return state.is(BlockTags.HOGLIN_REPELLENTS);
            }));
        }

        if (entity instanceof EntityWarpedMosco warpedMosco && AMInteractionConfig.MOSCO_CANNIBALISM_ENABLED){
            warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityCrimsonMosquito.class, 1000, true, true, null));
            warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityWarpedMosco.class, 100, true, true, (livingEntity) -> {
                        return livingEntity.getHealth() <= 0.05F * livingEntity.getMaxHealth();
                    }));
        }


        if (entity instanceof EntityWarpedToad warpedToad && AMInteractionConfig.WARPED_FRIENDLY_ENABLED){
            warpedToad.goalSelector.removeAllGoals(goal -> {
                return goal instanceof EntityAINearestTarget3D;
            });
        }

    }

    @SubscribeEvent
    public void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        RandomSource random = player.getRandom();
        if (event.getTarget() instanceof LivingEntity living) {
            //Banana Slug
            if (living instanceof EntityBananaSlug bananaSlug) {
                if (itemStack.getItem() == Items.SHEARS)
                    return;
                if(AMInteractionConfig.GOOFY_BANANA_SLIP_ENABLED)
                    return;
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(1, player, (p_233654_0_) -> {
                    });
                }
                bananaSlug.gameEvent(GameEvent.ENTITY_INTERACT);
                bananaSlug.playSound(SoundEvents.SHEEP_SHEAR, 1, bananaSlug.getVoicePitch());
                bananaSlug.spawnAtLocation(AMItemRegistry.BANANA.get());
                bananaSlug.discard();
            }

            //Flutter
            if (living instanceof EntityFlutter flutter){
                if (itemStack.getItem() == Items.WITHER_ROSE && AMInteractionConfig.FLUTTER_WITHERED_ENABLED && !flutter.isTame()) {
                    if (!player.isCreative()) itemStack.hurtAndBreak(1, flutter, (p_233654_0_) -> {});
                    flutter.addEffect(new MobEffectInstance(MobEffects.WITHER, 900, 0));
                }
                if (itemStack.getItem() == Items.SHEARS && AMInteractionConfig.FLUTTER_SHEAR_ENABLED) {
                    flutter.playSound(SoundEvents.SHEEP_SHEAR, 1, flutter.getVoicePitch());
                    flutter.spawnAtLocation(Items.SPORE_BLOSSOM);
                    flutter.spawnAtLocation(Items.AZALEA);
                    flutter.spawnAtLocation(Items.AZALEA);
                    if (!player.isCreative()) itemStack.hurtAndBreak(6, flutter, (p_233654_0_) -> {});
                    flutter.discard();
                }
            }

            //Sugar Glider
            if (living instanceof EntitySugarGlider sugarGlider && AMInteractionConfig.SUGAR_RUSH_ENABLED && (itemStack.getItem() == Items.SUGAR || itemStack.getItem() == Items.SUGAR_CANE)) {
                if (!player.isCreative()) itemStack.shrink(1);
                sugarGlider.gameEvent(GameEvent.EAT);
                sugarGlider.playSound(SoundEvents.FOX_EAT, 1, sugarGlider.getVoicePitch());
                sugarGlider.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 1));
            }

            //Grizzly Bear
            if (living instanceof EntityGrizzlyBear grizzlyBear && AMInteractionConfig.BRUSHED_ENABLED && itemStack.getItem() == Items.BRUSH) {
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(32, player, (p_233654_0_) -> {
                    });
                }
                grizzlyBear.spawnAtLocation(AMItemRegistry.BEAR_FUR.get());
                if(random.nextDouble() < 0.2) grizzlyBear.spawnAtLocation(AMItemRegistry.BEAR_FUR.get());
                if(random.nextDouble() < 0.0002)grizzlyBear.spawnAtLocation(AMItemRegistry.BEAR_DUST.get());
            }


        }


    }



}
