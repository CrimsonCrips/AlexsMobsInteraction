package com.crimsoncrips.alexsmobsinteraction.event;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.goal.AISeagullSteal;
import com.crimsoncrips.alexsmobsinteraction.goal.AISkelewagCircleGoal;
import com.crimsoncrips.alexsmobsinteraction.goal.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.goal.FollowNearestGoal;
import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.HummingbirdAIPollinate;
import com.github.alexthe666.alexsmobs.entity.ai.SeagullAIStealFromPlayers;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry.CROCODILE_BABY_KILL;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionEvents {


    @SubscribeEvent
    public void onEntityFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        final var entity = event.getEntity();
        //Vanilla Mobs
        if (AInteractionConfig.frogeatflies && entity instanceof final Frog frog) {
            frog.targetSelector.addGoal(4,
                    new NearestAttackableTargetGoal<>(frog, EntityFly.class, 1, true, false, null));
        }

        if (AInteractionConfig.spidereats && entity instanceof final Spider spider) {
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityCockroach.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Silverfish.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, Bee.class, 2, true, false, null));
            spider.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(spider, EntityFly.class, 2, true, false, null));
        }


        //AM Mobs
        if (entity instanceof EntityAnaconda anaconda){
            Predicate<LivingEntity> ANACONDA_BABY_TARGETS = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ANACONDA_BABY_KILL);
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
            if (AInteractionConfig.anacondacanibalize) {
                anaconda.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 2500, true, false, (livingEntity) -> {
                    return (livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth() || livingEntity.isBaby());
                }));
            }
        }

        if (AInteractionConfig.baldeaglecannibalize && entity instanceof final EntityBaldEagle baldEagle && !baldEagle.isTame()) {
            baldEagle.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(baldEagle, EntityBaldEagle.class, 1000, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
            }));
        }

        if(AInteractionConfig.preyfear) {
            if (entity instanceof final EntityBananaSlug bananaSlug) {
                bananaSlug.goalSelector.addGoal(3, new AvoidEntityGoal<>(bananaSlug, LivingEntity.class, 2.0F, 1.2, 1.5, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SMALLINSECTFEAR)));
            }
            if (entity instanceof final EntityRaccoon raccoon) {
                raccoon.goalSelector.addGoal(3, new AvoidEntityGoal<>(raccoon, LivingEntity.class, 8.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.RACCOON_FEAR)));
            }
            if (entity instanceof final EntityJerboa jerboa) {
                jerboa.goalSelector.addGoal(3, new AvoidEntityGoal<>(jerboa, LivingEntity.class, 7.0F, 1.7D, 1.4,AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.JERBOAFEAR)));
            }
            if (entity instanceof final EntityRainFrog rainFrog) {
                rainFrog.goalSelector.addGoal(3, new AvoidEntityGoal<>(rainFrog, LivingEntity.class, 10.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.RAINFROG_FEAR)));
            }
            if (entity instanceof final EntityBlobfish blobfish) {
                blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, Player.class, 5.0F, 1.2, 1.4));
                blobfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(blobfish, EntityGiantSquid.class, 5.0F, 1.2, 1.4));
            }
            if (entity instanceof final EntityCatfish catfish) {
                catfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(catfish, LivingEntity.class, 4.0F, 1.1, 1.3, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FISHFEAR)));
            }
            if (entity instanceof final EntityDevilsHolePupfish devilsHolePupfish){
                devilsHolePupfish.goalSelector.addGoal(3, new AvoidEntityGoal<>(devilsHolePupfish, LivingEntity.class, 2.0F, 1.1, 1.3, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FISHFEAR)));
            }
            if (entity instanceof final EntityFlyingFish flyingFish){
                flyingFish.goalSelector.addGoal(3, new AvoidEntityGoal<>(flyingFish, LivingEntity.class, 4.0F, 1.3, 1.7, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FISHFEAR)));
            }
            if (entity instanceof final EntitySugarGlider sugarGlider) {
                sugarGlider.goalSelector.addGoal(3, new AvoidEntityGoal<>(sugarGlider, LivingEntity.class, 2.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SMALLINSECTFEAR)));
            }
        }

        if (entity instanceof final EntityBoneSerpent boneSerpent ){
            boneSerpent.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(boneSerpent, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.BONESERPENT_KILL)));
            if(AInteractionConfig.boneserpentfear) {
                boneSerpent.goalSelector.addGoal(7, new AvoidEntityGoal<>(boneSerpent, EntityLaviathan.class, 10.0F, 1.6, 2));
            }
        }

        if (entity instanceof final EntityCaiman caiman){
            if(AInteractionConfig.caimanaggresive && !caiman.isTame()) {
                caiman.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(caiman, Player.class, 150, true, true, null));
            }
            if (AInteractionConfig.caimaneggattack && !caiman.isBaby()) {
                caiman.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(caiman, LivingEntity.class, 0, true, false, (livingEntity) -> {
                    return livingEntity.isHolding( Ingredient.of(AMBlockRegistry.CAIMAN_EGG.get()));
                }));
            }
        }

        if (entity instanceof final EntityCapuchinMonkey capuchinMonkey && AInteractionConfig.capuchinhunt && capuchinMonkey.isTame()) {
            capuchinMonkey.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(capuchinMonkey, LivingEntity.class, 400, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CAPUCHIN_KILL)));
        }

        if (entity instanceof final EntityCachalotWhale cachalotWhale && cachalotWhale.isSleeping() && !cachalotWhale.isBeached()){
            cachalotWhale.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(cachalotWhale, LivingEntity.class, 300, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CACHALOT_WHALE_KILL_CHANCE)));
        }

        if (entity instanceof final EntityCentipedeHead centipede){
            if (AInteractionConfig.centipedelightfear) {
                centipede.goalSelector.addGoal(1, new AvoidEntityGoal<>(centipede, LivingEntity.class, 4.0F, 1.5, 2, (livingEntity) -> {
                    return livingEntity.isHolding(Ingredient.of(AInteractionTagRegistry.CENTIPEDE_LIGHT_FEAR));
                }));
                centipede.goalSelector.addGoal(1, new AvoidBlockGoal(centipede, 4,1,1.2,(pos) -> {
                    BlockState state = centipede.level().getBlockState(pos);
                    return state.is(AInteractionTagRegistry.CENTIPEDE_BLOCK_FEAR);
                }));
            }
            centipede.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(centipede, LivingEntity.class, 55, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CAVE_CENTIPEDE_KILL)));

        }

        if (entity instanceof final EntityCrocodile crocodile && !crocodile.isBaby() && !crocodile.isTame()){
            crocodile.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(crocodile, LivingEntity.class, 5000, true, false,AMEntityRegistry.buildPredicateFromTag(CROCODILE_BABY_KILL)){
                protected AABB getTargetSearchArea(double targetDistance) {
                    return this.mob.getBoundingBox().inflate(25D, 1D, 25D);
                }
            });
        }

        if (entity instanceof final EntityCrow crow && !crow.isTame() && !crow.isBaby()){
            crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.CROW_KILL)));
            if (AInteractionConfig.crowcannibalize){
                crow.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(crow, EntityCrow.class, 500, true, true, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.10F * livingEntity.getMaxHealth();
                }));
            }
        }

        if (entity instanceof final EntityDropBear dropBear){
            dropBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(dropBear, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.DROPBEAR_KILL)){
                protected AABB getTargetSearchArea(double targetDistance) {
                    AABB bb = this.mob.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
                    return new AABB(bb.minX, 0.0, bb.minZ, bb.maxX, 256.0, bb.maxZ);
                }
            });
        }

        if (entity instanceof final EntityElephant elephant && elephant.isTusked() && !elephant.isTame()){
            elephant.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(elephant, Player.class, 1500, true, true,null));
        }

        if (entity instanceof final EntityEmu emu && !emu.isBaby()){
            if (AInteractionConfig.emueggattack){
                emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 5, true, false, (livingEntity) -> {
                    return livingEntity.isHolding( Ingredient.of(AMItemRegistry.EMU_EGG.get())) || livingEntity.isHolding( Ingredient.of(AMItemRegistry.BOILED_EMU_EGG.get()));
                }));
            }
            if (AInteractionConfig.emurangedattack){
                emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 70, true, false, (livingEntity) -> {
                    return livingEntity.isHolding(Ingredient.of(AInteractionTagRegistry.EMU_TRIGGER));
                }));
            }
            emu.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(emu, LivingEntity.class, 55, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.EMU_KILL)));
            if(AInteractionConfig.emuscuffle){
                emu.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(emu, EntityEmu.class, 1000, false, true, null) {
                    public boolean canUse() {
                        return !emu.isLeashed() && super.canUse() && emu.level().isDay();
                    }
                });
            }
        }

        if (entity instanceof final EntityFly fly){
            Predicate<LivingEntity> PESTERTARGET = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FLY_PESTER);
            if(AInteractionConfig.flyfearall) {
                fly.goalSelector.addGoal(7, new AvoidEntityGoal<>(fly, LivingEntity.class, 2.0F, 1.1, 1.3, (livingEntity) ->{
                    return !PESTERTARGET.test(livingEntity) && !(livingEntity instanceof EntityFly);
                }));
            }
            if(AInteractionConfig.flyfearcandles){
                fly.goalSelector.addGoal(3, new AvoidBlockGoal(fly, 4, 1, 1.2, (pos) -> {
                    BlockState state = fly.level().getBlockState(pos);
                    return state.is(BlockTags.CANDLES);
                }));
            }
            if(AInteractionConfig.flypester) {
                fly.goalSelector.addGoal(8, new FollowNearestGoal<>(fly, LivingEntity.class, 1, 0.8, PESTERTARGET));
            }
        }

        if (entity instanceof final EntityEnderiophage enderiophage){

            if(AInteractionConfig.enderioimmunity){
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
            if(AInteractionConfig.enderiophageplayer && !enderiophage.isMissingEye() && (int) ReflectionUtil.getField(enderiophage, "fleeAfterStealTime") == 0){
                enderiophage.goalSelector.removeAllGoals(goal -> {
                    return goal instanceof EntityAINearestTarget3D;
                });
                enderiophage.targetSelector.addGoal(1, new EntityAINearestTarget3D<>(enderiophage, Player.class, 15, true, true, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.40F * livingEntity.getMaxHealth() && !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
                }));
            }


        }

        else if (entity instanceof EntityFarseer farseer && AInteractionConfig.farseerhumanlikeattack){
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Raider.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, Villager.class, 3, false, true, null));
            farseer.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(farseer, WanderingTrader.class, 3, false, true, null));
        }

        else if(entity instanceof EntityFrilledShark frilledShark){
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, LivingEntity.class, 1, false, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FRILLED_KILL)));
            frilledShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(frilledShark, EntityGiantSquid.class, 1, false, true,(livingEntity) -> {
                return livingEntity.getHealth() <= 0.25F * livingEntity.getMaxHealth();
            }));
        }

        else if(AInteractionConfig.geladahunt && entity instanceof EntityGeladaMonkey geladaMonkey){
            geladaMonkey.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(geladaMonkey, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SMALLCRITTER)));
        }

        else if(entity instanceof EntityGiantSquid giantSquid && AInteractionConfig.giantsquidcannibalize && giantSquid.isInWaterOrBubble() && !giantSquid.isCaptured()) {
            giantSquid.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(giantSquid, EntityGiantSquid.class, 200, true, false, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
            }));
        }

        else if(entity instanceof EntityGorilla gorilla){
            gorilla.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(gorilla, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SMALLCRITTER)));
        }

        else if(entity instanceof EntityHummingbird hummingbird){
            if(AInteractionConfig.hummingbirdpolinate) {
            hummingbird.goalSelector.removeAllGoals(goal -> {
                return goal instanceof HummingbirdAIPollinate;
            });

            hummingbird.goalSelector.addGoal(4, new HummingbirdAIPollinate(hummingbird){
                public boolean canUse() {
                        return super.canUse() && hummingbird.level().isDay();
                    }
            });
            }
            if(AInteractionConfig.hummingfollowflutter) {
                hummingbird.goalSelector.addGoal(8, new FollowNearestGoal<>(hummingbird, EntityFlutter.class, 10, 1.2, (livingEntity) -> true) {
                    public boolean canContinueToUse() {
                        return hummingbird.level().isDay();
                    }
                });
            }
        }

        if (entity instanceof EntityHammerheadShark hammerheadShark){
            if (AInteractionConfig.hammerheadhuntmantisshrimp){
                hammerheadShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(hammerheadShark, EntityMantisShrimp.class, 50, true, false, (mob) -> {
                    return mob.getHealth() <= 0.2 * mob.getMaxHealth();
                }));
            }
            hammerheadShark.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(hammerheadShark, LivingEntity.class, 0, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.HAMMERHEAD_KILL)));
        }

        if (entity instanceof EntityMantisShrimp mantisShrimp && !mantisShrimp.isTame()){
            if(AInteractionConfig.mantisaggresive  && !mantisShrimp.isBaby()) {
                mantisShrimp.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mantisShrimp, Player.class, 150, true, true, null));
            }
            if(AInteractionConfig.mantiscannibalize) {
                mantisShrimp.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(mantisShrimp, EntityMantisShrimp.class, 200, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth();
                }));
            }
        }

        if (entity instanceof EntityMudskipper mudskipper && AInteractionConfig.mudskipperhunt) {
            mudskipper.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(mudskipper, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.MUDSKIPPER_KILL)));
        }

        if(entity instanceof EntityOrca orca && AInteractionConfig.orcahunt){
            orca.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(orca, LivingEntity.class, 200, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ORCA_KILL)));
            orca.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(orca, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ORCA_CHANCE_KILL)));
        }

        if(entity instanceof EntityPotoo potoo){
            potoo.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(potoo, LivingEntity.class, 600, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.POTOO_KILL)));
        }

        if (entity instanceof EntityRaccoon raccoon && AInteractionConfig.raccoonhunt) {
            raccoon.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(raccoon, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.RACCOON_KILL)) {
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
            rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, LivingEntity.class, 300, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.RATTLESNAKE_KILL)) {
                public void start(){
                    super.start();
                }
            });
            if (AInteractionConfig.rattlesnakecannibalize) {
                rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, EntityRattlesnake.class, 1500, true, true, (livingEntity) -> {
                    return (livingEntity.getHealth() <= 0.60F * livingEntity.getMaxHealth() || livingEntity.isBaby());
                }) {
                    public void start() {
                        super.start();
                    }
                });
            }
        }

        if (entity instanceof EntityRoadrunner roadrunner && AInteractionConfig.roadrunnerday){
            roadrunner.goalSelector.removeAllGoals(goal -> {
                return goal instanceof NearestAttackableTargetGoal;
            });
                roadrunner.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(roadrunner, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ROADRUNNER_KILL)) {
                    public boolean canUse() {
                        return super.canUse();
                    }
                });
        }

        if (entity instanceof EntitySeagull seagull && AInteractionConfig.seagullnotsnatch){
            seagull.goalSelector.removeAllGoals(goal -> {
                return goal instanceof SeagullAIStealFromPlayers;
            });
            seagull.targetSelector.addGoal(2, new AISeagullSteal(seagull){
                public boolean canUse() {
                        return super.canUse() && !(seagull.getHealth() <= 0.40F * seagull.getMaxHealth());
                }
            });
        }

        if (entity instanceof EntityShoebill shoebill){
            shoebill.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(shoebill, LivingEntity.class, 200, true, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SHOEBILL_BABY_KILL)));
            shoebill.targetSelector.addGoal(7, new EntityAINearestTarget3D<>(shoebill, EntityTerrapin.class, 100, false, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SHOEBILL_KILL)));
        }

        if (entity instanceof EntitySnowLeopard snowLeopard){
            if(AInteractionConfig.leoparddesires){
                snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, EntityMoose.class, 100, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth();
                }));
                snowLeopard.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(snowLeopard, Player.class, 100, true, false, (livingEntity) -> {
                    return livingEntity.getHealth() <= 0.15F * livingEntity.getMaxHealth() && livingEntity.getItemBySlot(EquipmentSlot.HEAD).is((Item)AMItemRegistry.MOOSE_HEADGEAR.get());
                }));
            }
        }

        if(entity instanceof EntityStraddler straddler && AInteractionConfig.stradllervengeance) {
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityBoneSerpent.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityCrimsonMosquito.class, true));
            straddler.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(straddler, EntityWarpedMosco.class, true));
        }

        if (entity instanceof EntityTasmanianDevil tasmanianDevil){
            tasmanianDevil.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(tasmanianDevil, LivingEntity.class, 10, false, true, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.TASMANIAN_KILL)));
        }

        if (entity instanceof EntityTusklin tusklin && AInteractionConfig.fleewarped){
            tusklin.goalSelector.addGoal(3, new AvoidBlockGoal(tusklin, 4, 1, 1.2, (pos) -> {
                BlockState state = tusklin.level().getBlockState(pos);
                return state.is(BlockTags.HOGLIN_REPELLENTS);
            }));
        }

        if (entity instanceof EntityWarpedMosco warpedMosco && AInteractionConfig.warpedcannibalism){
            warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityCrimsonMosquito.class, 1000, true, true, null));
            warpedMosco.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(warpedMosco, EntityWarpedMosco.class, 100, true, true, (livingEntity) -> {
                        return livingEntity.getHealth() <= 0.05F * livingEntity.getMaxHealth();
                    }));
        }


        if (entity instanceof EntityWarpedToad warpedToad && AInteractionConfig.warpedtarantula){
            warpedToad.goalSelector.removeAllGoals(goal -> {
                return goal instanceof EntityAINearestTarget3D;
            });
        }

    }

    public void onInteractWithEntity(PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getItemStack().getItem().getDefaultInstance();
        Player player = event.getEntity();
        if (event.getTarget() instanceof LivingEntity living) {
            //Banana Slug
            if (living instanceof EntityBananaSlug bananaSlug && itemStack.getItem() == Items.SHEARS && AInteractionConfig.bananaslugsheared) {
                bananaSlug.gameEvent(GameEvent.ENTITY_INTERACT);
                bananaSlug.playSound(SoundEvents.SHEEP_SHEAR, 1, bananaSlug.getVoicePitch());
                bananaSlug.spawnAtLocation(AMItemRegistry.BANANA.get());
                if (!player.isCreative()) itemStack.hurtAndBreak(1, bananaSlug, (p_233654_0_) -> {});
                bananaSlug.discard();
            }

            //Flutter
            if (living instanceof EntityFlutter flutter){
                if (itemStack.getItem() == Items.WITHER_ROSE && AInteractionConfig.flutterwither && !flutter.isTame()) {
                    if (!player.isCreative()) itemStack.hurtAndBreak(1, flutter, (p_233654_0_) -> {});
                    flutter.addEffect(new MobEffectInstance(MobEffects.WITHER, 900, 0));
                }
                if (itemStack.getItem() == Items.SHEARS && AInteractionConfig.fluttersheared) {
                    flutter.playSound(SoundEvents.SHEEP_SHEAR, 1, flutter.getVoicePitch());
                    flutter.spawnAtLocation(Items.SPORE_BLOSSOM);
                    flutter.spawnAtLocation(Items.AZALEA);
                    if (!player.isCreative()) itemStack.hurtAndBreak(6, flutter, (p_233654_0_) -> {});
                    flutter.discard();
                }
            }

            //Sugar Glider
            if (living instanceof EntitySugarGlider sugarGlider && AInteractionConfig.sugarrush && (itemStack.getItem() == Items.SUGAR || itemStack.getItem() == Items.SUGAR_CANE)) {
                if (!player.isCreative()) itemStack.shrink(1);
                sugarGlider.gameEvent(GameEvent.EAT);
                sugarGlider.playSound(SoundEvents.FOX_EAT, 1, sugarGlider.getVoicePitch());
                sugarGlider.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 900, 1));
            }


        }


    }



}
